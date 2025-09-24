package com.kotlinonly.moprog.core.data

import androidx.datastore.core.DataStore
import com.kotlinonly.moprog.core.model.AuthDataStoreManager
import com.kotlinonly.moprog.data.auth.AuthTokens
import com.kotlinonly.moprog.data.auth.User
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

actual class AuthDataStoreManagerImpl(
    private val dataStore: DataStore<AuthDataStore>
) : AuthDataStoreManager {
    actual override suspend fun saveTokens(accessToken: String, refreshToken: String) {
        dataStore.updateData { store ->
            store.copy(
                tokens = AuthTokens(
                    accessToken = accessToken,
                    refreshToken = refreshToken
                )
            )
        }
    }

    actual override suspend fun saveUser(user: User) {
        dataStore.updateData { store ->
            store.copy(
                user = user
            )
        }
    }

    actual override suspend fun saveAuth(
        tokens: AuthTokens,
        user: User
    ) {
        saveTokens(tokens.accessToken, tokens.refreshToken)
        saveUser(user)
    }

    actual override val user: User?
        get() = runBlocking { getUser() }

    actual override suspend fun getUser(): User? {
        val store = dataStore.data.first()

        store.user?.let {
            return User(
                id = it.id,
                name = it.name,
                email = it.email,
                profilePictureUrl = it.profilePictureUrl,
                method = it.method,
                isEmailVerified = it.isEmailVerified,
                createdAt = it.createdAt
            )
        }

        return null
    }

    actual override suspend fun getTokens(): AuthDataStore {
        return dataStore.data.first()
    }

    actual override suspend fun clearDataStore() {
        dataStore.updateData { store ->
            store.copy(
                tokens = null,
                user = null
            )
        }
    }
}