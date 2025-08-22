package com.kotlinonly.moprog.core.controller

import io.ktor.server.response.respond
import io.ktor.server.routing.Routing
import io.ktor.server.routing.get
import io.ktor.server.routing.route
import io.micrometer.prometheusmetrics.PrometheusMeterRegistry

fun Routing.metricRoute(
    appMicrometerRegistry: PrometheusMeterRegistry
) {
    route("/metrics") {
        get {
            call.respond(appMicrometerRegistry.scrape())
        }
    }
}