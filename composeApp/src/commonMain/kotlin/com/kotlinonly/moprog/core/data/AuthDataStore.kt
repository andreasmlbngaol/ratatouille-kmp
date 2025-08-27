package com.kotlinonly.moprog.core.data

import com.kotlinonly.moprog.data.auth.AuthTokens
import com.kotlinonly.moprog.data.auth.User
import kotlinx.serialization.Serializable

@Serializable
data class AuthDataStore(
    val tokens: AuthTokens? = null,
    val user: User? = null
)
