package com.kotlinonly.moprog.core.model

import com.kotlinonly.moprog.core.data.AuthDataStore
import com.kotlinonly.moprog.data.auth.AuthTokens
import com.kotlinonly.moprog.data.auth.User

interface AuthDataStoreManager {
    suspend fun saveTokens(accessToken: String, refreshToken: String)
    suspend fun saveUser(user: User)
    suspend fun saveAuth(
        tokens: AuthTokens,
        user: User
    )
    val user: User?
    suspend fun getUser(): User?
    suspend fun getTokens(): AuthDataStore
    suspend fun clearDataStore()
}