package com.kotlinonly.moprog.core.plugins

import com.kotlinonly.moprog.core.utils.respondJson
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.plugins.BadRequestException
import io.ktor.server.plugins.statuspages.StatusPages

fun Application.statusPagesPlugin() {
    install(StatusPages) {
        exception<BadRequestException> { call, cause ->
            println(cause.message)
            call.respondJson(HttpStatusCode.BadRequest, "Invalid Request Body")
        }
        exception<Throwable> { call, cause ->
            println(cause.message)
            call.respondJson(HttpStatusCode.InternalServerError, cause::class.qualifiedName ?: "Unknown error")
        }
    }
}