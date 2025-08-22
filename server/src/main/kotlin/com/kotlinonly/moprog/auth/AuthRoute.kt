package com.kotlinonly.moprog.auth

import com.kotlinonly.moprog.core.config.JwtConfig
import com.kotlinonly.moprog.core.utils.respondJson
import com.kotlinonly.moprog.data.auth.RefreshTokenRequest
import com.kotlinonly.moprog.data.auth.RefreshTokenResponse
import io.ktor.http.HttpStatusCode
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.post
import io.ktor.server.routing.route

fun Route.authRoute() {
    route("/auth") {
        post("/refresh") {
            val payload = call.receive<RefreshTokenRequest>()
            val tokenValid = JwtConfig.validateRefreshToken(payload.refreshToken)
            if(!tokenValid) return@post call.respondJson(HttpStatusCode.BadRequest, "Invalid refresh token")

            val userId = JwtConfig.getUserIdFromToken(payload.refreshToken)
                ?: return@post call.respondJson(HttpStatusCode.BadRequest, "Invalid refresh token")

            val user = UsersRepository.findById(userId)
                ?: return@post call.respondJson(HttpStatusCode.NotFound, "User not found")

            val accessToken = JwtConfig.generateAccessToken(user)
            val refreshToken = JwtConfig.generateRefreshToken(user.id)

            call.respond(
                RefreshTokenResponse(
                    accessToken = accessToken,
                    refreshToken = refreshToken
                )
            )
        }
    }
}