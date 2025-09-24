package com.kotlinonly.moprog.features.auth.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kotlinonly.moprog.core.model.AccountService
import com.kotlinonly.moprog.core.model.AuthDataStoreManager
import com.kotlinonly.moprog.core.repository.MoprogRepository
import com.kotlinonly.moprog.data.auth.AuthMethod
import com.kotlinonly.moprog.data.auth.AuthTokens
import com.kotlinonly.moprog.data.auth.LoginRequest
import com.kotlinonly.moprog.data.core.logD
import kotlinx.coroutines.launch

abstract class AuthViewModel(
    private val auth: AccountService,
    private val repository: MoprogRepository,
    private val authDataStoreManager: AuthDataStoreManager
): ViewModel() {
    suspend fun signInWithGoogle(
        idToken: String,
        onSuccess: () -> Unit
    ) {
        auth.signInWithGoogle(idToken) { firebaseIdToken ->
            viewModelScope.launch {
                logD("LoginViewModel", "Success sign in with Google with Firebase: $idToken")
                val success = loginToBackend(firebaseIdToken, AuthMethod.GOOGLE)
                if(success) {
                    logD("LoginViewModel", "Login to backend success")
                    onSuccess()
                }
            }
        }
    }

    private suspend fun loginToBackend(
        firebaseIdToken: String,
        method: AuthMethod
    ): Boolean {
        logD("LoginViewModel", "Logging in to backend")
        val response = repository.login(
            LoginRequest(firebaseIdToken, method)
        ) ?: return false

        authDataStoreManager.saveAuth(
            tokens = AuthTokens(
                accessToken = response.tokens.accessToken,
                refreshToken = response.tokens.refreshToken
            ),
            user = response.user
        )

        return true
    }

}
