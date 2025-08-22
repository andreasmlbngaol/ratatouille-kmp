package com.kotlinonly.moprog.core.plugins

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.kotlinonly.moprog.core.data.AuthNames
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.auth.Authentication
import io.ktor.server.auth.jwt.JWTPrincipal
import io.ktor.server.auth.jwt.jwt

fun Application.authenticationPlugin() {
    val secretKey = environment.config.config("ktor.jwt").property("secretKey").getString()
    install(Authentication) {
        jwt(AuthNames.JWT_AUTH) {
            realm = "Access to application"
            verifier(
                JWT
                    .require(Algorithm.HMAC256(secretKey))
                    .build()
            )
            validate { credential ->
                val payload = credential.payload
                val id = payload.getClaim("sub")

                if (id != null) {
                    JWTPrincipal(credential.payload)
                } else {
                    null
                }
            }
        }
    }
}