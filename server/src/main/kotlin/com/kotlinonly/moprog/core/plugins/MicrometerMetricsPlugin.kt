package com.kotlinonly.moprog.core.plugins

import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.metrics.micrometer.MicrometerMetrics
import io.micrometer.prometheusmetrics.PrometheusMeterRegistry

fun Application.micrometerMetricsPlugin(
    appMicrometerRegistry: PrometheusMeterRegistry
) {
    install(MicrometerMetrics) {
        registry = appMicrometerRegistry
    }
}