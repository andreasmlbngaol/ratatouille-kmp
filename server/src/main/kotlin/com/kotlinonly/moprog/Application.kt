package com.kotlinonly.moprog

import com.kotlinonly.moprog.auth.authRoute
import com.kotlinonly.moprog.core.config.DatabaseFactory
import com.kotlinonly.moprog.core.config.EnvConfig
import com.kotlinonly.moprog.core.config.FirebaseConfig
import com.kotlinonly.moprog.core.config.JwtConfig
import com.kotlinonly.moprog.core.config.userId
import com.kotlinonly.moprog.core.controller.metricRoute
import com.kotlinonly.moprog.core.data.AuthNames
import com.kotlinonly.moprog.core.plugins.authenticationPlugin
import com.kotlinonly.moprog.core.plugins.callLoggingPlugin
import com.kotlinonly.moprog.core.plugins.contentNegotiationPlugin
import com.kotlinonly.moprog.core.plugins.corsPlugin
import com.kotlinonly.moprog.core.plugins.micrometerMetricsPlugin
import com.kotlinonly.moprog.core.plugins.statusPagesPlugin
import com.kotlinonly.moprog.core.utils.respondJson
import com.kotlinonly.moprog.recipes.recipeRoute
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.*
import io.ktor.server.auth.authenticate
import io.ktor.server.auth.jwt.JWTPrincipal
import io.ktor.server.auth.principal
import io.ktor.server.config.ApplicationConfig
import io.ktor.server.netty.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.micrometer.prometheusmetrics.PrometheusConfig
import io.micrometer.prometheusmetrics.PrometheusMeterRegistry

fun main(args: Array<String>): Unit = EngineMain.main(args)

fun Application.module() {
    initKtor(environment.config)
    val appMicrometerRegistry = PrometheusMeterRegistry(PrometheusConfig.DEFAULT)

    corsPlugin()
    contentNegotiationPlugin()
    authenticationPlugin()
    statusPagesPlugin()
    callLoggingPlugin()
    micrometerMetricsPlugin(appMicrometerRegistry)


    routing {
        route("/") {
            get {
                call.respondRedirect("/api")
            }
        }

        route("/api") {
            get { call.respondJson(HttpStatusCode.OK, "Allo Woldeu") }
            metricRoute(appMicrometerRegistry)
            authRoute()

            // Protected Route
            authenticate(AuthNames.JWT_AUTH) {
                // Untuk mengecek token
                get("/ping-protected") {
                    val userId = call.principal<JWTPrincipal>()!!.userId
                    UsersRepository.findById(userId) ?: return@get call.respondJson(HttpStatusCode.Unauthorized, "User not found")

                    call.respond(HttpStatusCode.OK)
                }

                recipeRoute()
            }
        }
    }
}

fun initKtor(
    config: ApplicationConfig
) {
    FirebaseConfig.init()
    EnvConfig.init()
    JwtConfig.init(config)
    DatabaseFactory.init(config)
}