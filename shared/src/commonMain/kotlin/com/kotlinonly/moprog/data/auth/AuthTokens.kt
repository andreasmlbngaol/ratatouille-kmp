package com.kotlinonly.moprog.data.auth

import kotlinx.serialization.Serializable

@Serializable
data class AuthTokens(
    val accessToken: String = "",
    val refreshToken: String = ""
)
