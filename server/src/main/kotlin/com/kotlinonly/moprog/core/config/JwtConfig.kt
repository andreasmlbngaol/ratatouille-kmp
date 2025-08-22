package com.kotlinonly.moprog.core.config

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import io.ktor.server.auth.jwt.JWTPrincipal
import io.ktor.server.config.ApplicationConfig
import java.util.Date

object JwtConfig {
    private lateinit var secretKey: String
    lateinit var algorithm: Algorithm
        private set

    const val ACCESS_TOKEN_EXPIRATION_DURATION_IN_SECOND = 60 * 60       // 1 jam
    const val REFRESH_TOKEN_EXPIRATION_DURATION_IN_SECOND = 60 * 60 * 24 * 30 // 30 hari

    fun init(config: ApplicationConfig) {
        secretKey = config.config("ktor.jwt").property("secretKey").getString()
        algorithm = Algorithm.HMAC256(secretKey)
    }

    fun generateRefreshToken(userId: String): String {
        val now = System.currentTimeMillis()
        return JWT.create()
            .withClaim("type", "refresh")
            .withClaim("sub", userId)
            .withIssuedAt(Date(now))
            .withExpiresAt(Date(now + REFRESH_TOKEN_EXPIRATION_DURATION_IN_SECOND * 1000L))
            .sign(algorithm)
    }

    fun getUserIdFromToken(token: String): String? {
        return try {
            val decodedJWT = JWT.require(algorithm).build().verify(token)
            decodedJWT.getClaim("sub").asString()
        } catch (_: Exception) {
            null
        }
    }

    fun validateRefreshToken(token: String) = validateToken(token, "refresh")

    fun validateToken(token: String, type: String): Boolean {
        return try {
            val decodedJWT = JWT.require(algorithm).build().verify(token)
            val expiration = decodedJWT.expiresAt
            val tokenType = decodedJWT.getClaim("type").asString()

            expiration.after(Date()) && tokenType == type
        } catch (_: Exception) {
            false
        }
    }
}

val JWTPrincipal.userId: String
    get() = this.payload.getClaim("sub").asString()
