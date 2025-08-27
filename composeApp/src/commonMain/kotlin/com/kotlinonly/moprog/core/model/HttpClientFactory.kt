package com.kotlinonly.moprog.core.model

import com.kotlinonly.moprog.data.auth.RefreshTokenRequest
import com.kotlinonly.moprog.data.auth.RefreshTokenResponse
import com.kotlinonly.moprog.data.core.logD
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.plugins.HttpSend
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.plugins.logging.SIMPLE
import io.ktor.client.plugins.plugin
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonNamingStrategy

object HttpClientFactory {
    @OptIn(ExperimentalSerializationApi::class)
    fun create(
        engine: HttpClientEngine,
        dataStore: AuthDataStoreManager
    ): HttpClient {
        val client = HttpClient(engine) {
            install(Logging) {
                logger = Logger.SIMPLE
                level = LogLevel.ALL
            }

            install(ContentNegotiation) {
                json(
                    Json {
                        ignoreUnknownKeys = true
                        prettyPrint = true
                        isLenient = true
                        namingStrategy = JsonNamingStrategy.SnakeCase
                    }
                )
            }

            defaultRequest { contentType(ContentType.Application.Json) }
        }

        client.plugin(HttpSend.Plugin).intercept { request ->
            logD("HttpClientFactory", "Intercepting request to ${request.url}")

            val tokens = dataStore.getTokens().tokens
            val accessToken = tokens?.accessToken

            logD("HttpClientFactory", "Current stored token: ${accessToken?.take(20)}...")

            request.headers.remove(HttpHeaders.Authorization)

            if(!accessToken.isNullOrBlank()) {
                request.headers.append(HttpHeaders.Authorization, "Bearer $accessToken")
                logD("HttpClientFactory", "Authorization header set with fresh token")
            } else {
                logD("HttpClientFactory", "No access token available, proceeding without Authorization header")
            }

            val originalCall = execute(request)

            if(originalCall.response.status == HttpStatusCode.Unauthorized) {
                logD("HttpClientFactory", "Received 401, attempting token refresh")

                val refreshToken = tokens?.refreshToken
                if(!refreshToken.isNullOrBlank()) {
                    try {
                        val newTokens = refreshTokenFromServer(refreshToken)
                        dataStore.saveTokens(newTokens.accessToken, newTokens.refreshToken)

                        logD("HttpClientFactory", "Token refreshed successfully, retrying request")

                        request.headers.remove(HttpHeaders.Authorization)
                        request.headers.append(HttpHeaders.Authorization, "Bearer ${newTokens.accessToken}")

                        return@intercept execute(request)
                    } catch (e: Exception) {
                        logD("HttpClientFactory", "Token refresh failed: ${e.message}")
                        dataStore.clearDataStore()
                        return@intercept originalCall
                    }
                } else {
                    logD("HttpClientFactory", "No refresh token available")
                }
            }

            originalCall
        }
        return client
    }
}

@OptIn(ExperimentalSerializationApi::class)
private suspend fun refreshTokenFromServer(refreshToken: String): RefreshTokenResponse {
    val refreshClient = HttpClient {
        install(ContentNegotiation) {
            json(
                Json {
                    ignoreUnknownKeys = true
                    prettyPrint = true
                    isLenient = true
                    namingStrategy = JsonNamingStrategy.SnakeCase
                }
            )
        }
    }

    try {
        val response = refreshClient.post("https://moprog.sanalab.live/api/auth/refresh") {
            contentType(ContentType.Application.Json)
            setBody(RefreshTokenRequest(refreshToken))
        }

        return response.body<RefreshTokenResponse>()
    } finally {
        refreshClient.close()
    }
}