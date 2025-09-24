package com.kotlinonly.moprog.core.data

import com.kotlinonly.moprog.core.model.AuthDataStoreManager
import com.kotlinonly.moprog.data.auth.AuthTokens
import com.kotlinonly.moprog.data.auth.User

expect class AuthDataStoreManagerImpl: AuthDataStoreManager {
    override suspend fun saveTokens(accessToken: String, refreshToken: String)
    override suspend fun saveUser(user: User)
    override suspend fun saveAuth(
        tokens: AuthTokens,
        user: User
    )

    override val user: User?
    override suspend fun getUser(): User?
    override suspend fun getTokens(): AuthDataStore
    override suspend fun clearDataStore()
}