package com.kotlinonly.moprog.features.splash

import androidx.lifecycle.ViewModel
import com.kotlinonly.moprog.core.model.AuthDataStoreManager
import com.kotlinonly.moprog.core.repository.MoprogRepository
import com.kotlinonly.moprog.data.core.logD

class SplashViewModel(
    private val repository: MoprogRepository,
    private val dataStoreManager: AuthDataStoreManager,
): ViewModel() {
    suspend fun checkAuthUser(
        onAuthenticated: () -> Unit,
        onUnauthenticated: () -> Unit
    ) {
        if(dataStoreManager.user == null) {
            logD("SplashViewModel", "Refresh Token and Access Token not found")
            dataStoreManager.clearDataStore()
            onUnauthenticated()
            return
        }

        logD("SplashViewModel", "Refresh token and Access Token found")
        val result = repository.ping()

        if (result) {
            logD("SplashViewModel", "User is authenticated")
            onAuthenticated()
            return
        }

        logD("SplashViewModel", "Access Token and Refresh Token invalid")
        dataStoreManager.clearDataStore()
        onUnauthenticated()
    }
}