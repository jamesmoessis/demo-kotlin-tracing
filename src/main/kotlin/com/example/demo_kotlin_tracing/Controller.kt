package com.example.demo_kotlin_tracing

import io.opentelemetry.api.trace.Span
import io.opentelemetry.api.trace.SpanContext
import io.opentelemetry.api.trace.TraceFlags
import io.opentelemetry.api.trace.TraceState
import io.opentelemetry.context.Context
import io.opentelemetry.extension.kotlin.asContextElement
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController


@RestController
class Controller {

    val coroutinesScope = CoroutineScope(Dispatchers.IO)

    /**
     * This endpoint demonstrates how, without coroutineContextFilter CoWebFilter bean,
     * tracing context is lost on coroutine resumption.
     *
     * Compare the logs and return value before and after commenting out TracingContextWebFilter#coroutineContextFilter bean.
     */
    @GetMapping("/delay")
    suspend fun delay(): String? {
        println("before " + Span.current().spanContext.traceId)
        delay(100)
        println("after " + Span.current().spanContext.traceId)
        return Span.current().spanContext.traceId
    }

    /**
     * This endpoint demonstrates how tracing context is lost if it is not explicitly passed when
     * launching a coroutine in a new CoroutineScope.
     *
     * Compare the log lines.
     */
    @GetMapping("/newScope")
    suspend fun newScope(): String? {
        coroutinesScope.launch() {
            println("launch 1 " + Span.current().spanContext.traceId)
        }
        coroutinesScope.launch(Context.current().asContextElement()) {
            println("launch 2 " + Span.current().spanContext.traceId)
        }
        return Span.current().spanContext.traceId
    }

    /**
     * The endpoint demonstrates that makeCurrent should NOT be used to set the tracing context when using coroutines.
     * This is because the tracing context is lost on coroutine resumption. Use withContext instead.
     *
     * Compare the log lines.
     */
    @GetMapping("/makeCurrent")
    suspend fun makeCurrent(): String? {
        val newContext = SpanContext.create(
            "12312312312312312312312312312312",
            "1234567890123456",
            TraceFlags.getDefault(),
            TraceState.getDefault()
        )
        val ctx = Context.current().with(Span.wrap(newContext))

        ctx.makeCurrent().use {
            println("before1 " + Span.current().spanContext.traceId)
            delay(100)
            println("after1 " + Span.current().spanContext.traceId)
        }

        withContext(ctx.asContextElement()) {
            println("before2 " + Span.current().spanContext.traceId)
            delay(100)
            println("after2 " + Span.current().spanContext.traceId)
        }

        return Span.current().spanContext.traceId
    }
}