package com.kotlinonly.moprog.core.data

actual class AuthDataStoreManagerImpl : com.kotlinonly.moprog.core.model.AuthDataStoreManager {
    actual override suspend fun saveTokens(accessToken: String, refreshToken: String) {
    }

    actual override suspend fun saveUser(user: com.kotlinonly.moprog.data.auth.User) {
    }

    actual override suspend fun saveAuth(
        tokens: com.kotlinonly.moprog.data.auth.AuthTokens,
        user: com.kotlinonly.moprog.data.auth.User
    ) {
    }

    actual override val user: com.kotlinonly.moprog.data.auth.User?
        get() = TODO("Not yet implemented")

    actual override suspend fun getUser(): com.kotlinonly.moprog.data.auth.User? {
        TODO("Not yet implemented")
    }

    actual override suspend fun getTokens(): AuthDataStore {
        TODO("Not yet implemented")
    }

    actual override suspend fun clearDataStore() {
    }
}