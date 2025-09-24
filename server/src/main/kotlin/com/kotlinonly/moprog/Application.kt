package com.kotlinonly.moprog

import com.kotlinonly.moprog.auth.authRoute
import com.kotlinonly.moprog.auth.config.FirebaseConfig
import com.kotlinonly.moprog.auth.config.JwtConfig
import com.kotlinonly.moprog.auth.config.userId
import com.kotlinonly.moprog.core.config.*
import com.kotlinonly.moprog.core.controller.metricRoute
import com.kotlinonly.moprog.auth.data.AuthNames
import com.kotlinonly.moprog.database.users.UsersRepository
import com.kotlinonly.moprog.core.plugins.*
import com.kotlinonly.moprog.core.utils.respondJson
import com.kotlinonly.moprog.recipes.recipeRoute
import com.kotlinonly.moprog.users.userRoute
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.config.*
import io.ktor.server.http.content.*
import io.ktor.server.netty.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.micrometer.prometheusmetrics.PrometheusConfig
import io.micrometer.prometheusmetrics.PrometheusMeterRegistry
import java.io.File

fun main(args: Array<String>): Unit = EngineMain.main(args)

lateinit var MY_DOMAIN: String

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
        staticFiles("/uploads", File("uploads"))

        route("/") {
            get {
                call.respondRedirect("/api")
            }
        }

        route("/api") {
            get { call.respondJson(HttpStatusCode.OK, "Mari kita ngetes CI/CD kita ini") }
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

                userRoute()
            }
        }
    }
}

fun initKtor(
    config: ApplicationConfig
) {
    FirebaseConfig.init()
//    EnvConfig.init()
    JwtConfig.init(config)
    DatabaseFactory.init(config)
    MY_DOMAIN = config.property("ktor.domain").getString()
}