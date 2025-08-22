package com.kotlinonly.moprog.auth

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.kotlinonly.moprog.core.config.JwtConfig
import com.kotlinonly.moprog.core.utils.respondJson
import com.kotlinonly.moprog.data.auth.LoginRequest
import com.kotlinonly.moprog.data.auth.LoginResponse
import com.kotlinonly.moprog.data.auth.RefreshTokenRequest
import com.kotlinonly.moprog.data.auth.RefreshTokenResponse
import com.kotlinonly.moprog.data.auth.User
import com.kotlinonly.moprog.data.core.logE
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

        post("/login") {
            val payload = call.receive<LoginRequest>()

            val idToken = payload.idToken
            val method = payload.method

            val decoded = try {
                FirebaseAuth.getInstance().verifyIdToken(idToken)
            } catch (e: FirebaseAuthException) {
                logE("authRoute/login", "FirebaseAuthException: ${e.message}")
                return@post call.respondJson(HttpStatusCode.BadRequest, "Invalid id token")
            }

            val uid = decoded.uid

            val user = UsersRepository.findById(uid) ?: run {
                logE("authRoute/login", "User with ID $uid not found, creating new user.")

                User(
                    id = uid,
                    email = decoded.email ?: "",
                    name = decoded.name ?: "",
                    profilePictureUrl = decoded.picture ?: "",
                    isEmailVerified = decoded.isEmailVerified,
                    method = method
                ).also { UsersRepository.save(it) }
            }

            val accessToken = JwtConfig.generateAccessToken(user)
            val refreshToken = JwtConfig.generateRefreshToken(user.id)

            call.respond(
                LoginResponse(
                    user = user,
                    tokens = RefreshTokenResponse(
                        accessToken = accessToken,
                        refreshToken = refreshToken
                    )
                )
            )
        }
    }
}