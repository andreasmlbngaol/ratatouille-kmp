package com.kotlinonly.moprog.data.auth

import kotlinx.serialization.Serializable

@Serializable
data class LoginResponse(
    val tokens: RefreshTokenResponse,
    val user: User
)