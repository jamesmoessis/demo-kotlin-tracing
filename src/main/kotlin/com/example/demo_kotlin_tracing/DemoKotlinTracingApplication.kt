package com.example.demo_kotlin_tracing

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import reactor.core.publisher.Hooks

@SpringBootApplication
class DemoKotlinTracingApplication

fun main(args: Array<String>) {
	Hooks.enableAutomaticContextPropagation()
	runApplication<DemoKotlinTracingApplication>(*args)
}
