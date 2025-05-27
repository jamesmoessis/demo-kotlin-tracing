package com.example.demo_kotlin_tracing

import io.micrometer.core.instrument.kotlin.asContextElement
import io.micrometer.observation.ObservationRegistry
import kotlinx.coroutines.withContext
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.server.CoWebFilter
import org.springframework.web.server.CoWebFilterChain
import org.springframework.web.server.ServerWebExchange

@Configuration
class TracingContextWebFilter {

    @Bean
    fun coroutineContextFilter(observationRegistry: ObservationRegistry): CoWebFilter = object : CoWebFilter() {
        override suspend fun filter(exchange: ServerWebExchange, chain: CoWebFilterChain) {
            withContext(observationRegistry.asContextElement()){
                chain.filter(exchange)
            }
        }
    }
}