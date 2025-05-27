package com.example.demo_kotlin_tracing

import io.opentelemetry.api.trace.Span
import kotlinx.coroutines.delay
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class Controller {

    @GetMapping("/delay")
    suspend fun delay(): String? {
        println("before " + Span.current().spanContext.traceId)
        delay(100)
        println("after " + Span.current().spanContext.traceId)
        return Span.current().spanContext.traceId
    }
}