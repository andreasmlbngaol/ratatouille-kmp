package com.kotlinonly.moprog.users

import com.kotlinonly.moprog.auth.config.userId
import com.kotlinonly.moprog.core.utils.respondJson
import com.kotlinonly.moprog.data.recipes.RecipeFilter
import com.kotlinonly.moprog.database.follows.FollowsRepository
import com.kotlinonly.moprog.database.users.UsersRepository
import io.ktor.http.HttpStatusCode
import io.ktor.server.auth.jwt.JWTPrincipal
import io.ktor.server.auth.principal
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.delete
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.route

fun Route.otherUserRoute() {
    route("/others") {
        route("{userId}") {
            get {
                val otherUserId = call.parameters["userId"]
                    ?: return@get call.respondJson(HttpStatusCode.BadRequest, "Missing or malformed userId")
                val userId = call.principal<JWTPrincipal>()!!.userId

                if (otherUserId == userId) return@get call.respondJson(
                    HttpStatusCode.Forbidden,
                    "You cannot get your own detail with this endpoint"
                )

                // Kalau sempat dan niat ini bisa dipisah untuk query user dan recipe nya
                val userDetail = UsersRepository.findDetailById(otherUserId, RecipeFilter(isPublic = true))
                    ?: return@get call.respondJson(HttpStatusCode.NotFound, "User not found")

                val isFollowed = FollowsRepository.isFollowed(followerId = userId, followedId = otherUserId)

                call.respond(
                    userDetail.copy(
                        isMe = false,
                        isFollowed = isFollowed
                    )
                )
            }

            route("/follows") {
                post {
                    val otherUserId = call.parameters["userId"]
                        ?: return@post call.respondJson(HttpStatusCode.BadRequest, "Missing or malformed userId")
                    val userId = call.principal<JWTPrincipal>()!!.userId
                    if (otherUserId == userId) return@post call.respondJson(
                        HttpStatusCode.Forbidden,
                        "You cannot follow yourself"
                    )

                    val isFollowed = FollowsRepository.isFollowed(followerId = userId, followedId = otherUserId)

                    if(!isFollowed) {
                        FollowsRepository.saveWithTimestamps(followerId = userId, followedId = otherUserId).let {
                            if (it < 1) return@post call.respondJson(
                                HttpStatusCode.InternalServerError,
                                "Database error"
                            )
                        }
                        call.respond(HttpStatusCode.Created)
                    }
                    call.respond(HttpStatusCode.OK)
                }

                delete {
                    val otherUserId = call.parameters["userId"]
                        ?: return@delete call.respondJson(HttpStatusCode.BadRequest, "Missing or malformed userId")
                    val userId = call.principal<JWTPrincipal>()!!.userId
                    if (otherUserId == userId) return@delete call.respondJson(
                        HttpStatusCode.Forbidden,
                        "You cannot unfollow yourself"
                    )

                    val isFollowed = FollowsRepository.isFollowed(followerId = userId, followedId = otherUserId)
                    if(isFollowed) {
                        FollowsRepository.delete(followerId = userId, followedId = otherUserId)
                    }

                    call.respond(HttpStatusCode.OK)
                }
            }
        }
    }
}