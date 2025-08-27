package com.kotlinonly.moprog.core.repository

import com.kotlinonly.moprog.data.auth.LoginRequest
import com.kotlinonly.moprog.data.auth.LoginResponse
import com.kotlinonly.moprog.data.core.logE
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.isSuccess

class MoprogRepository(
    private val httpClient: HttpClient
) {
    @Suppress("PrivatePropertyName")
    private val BASE_URL = "https://moprog.sanalab.live/api"

    suspend fun ping(): Boolean {
        try {
            val response = httpClient.get("$BASE_URL/ping-protected")
            return response.status.isSuccess()
        } catch (e: Exception) {
            logE("MoprogRepository", "Error occurred while ping: ${e.message}")
            return false
        }
    }

    suspend fun login(body: LoginRequest): LoginResponse? {
        try {
            val response = httpClient.post("$BASE_URL/auth/login") {
                setBody(body)
            }

            if(response.status.isSuccess()) {
                return response.body<LoginResponse>()
            }
            return null
        } catch (e: Exception) {
            logE("MoprogRepository", "Error occurred while login: ${e.message}")
            return null
        }
    }
}