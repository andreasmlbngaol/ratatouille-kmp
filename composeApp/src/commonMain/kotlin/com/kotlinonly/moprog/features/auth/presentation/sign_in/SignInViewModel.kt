package com.kotlinonly.moprog.features.auth.presentation.sign_in

import androidx.lifecycle.viewModelScope
import com.kotlinonly.moprog.core.model.AccountService
import com.kotlinonly.moprog.core.model.AuthDataStoreManager
import com.kotlinonly.moprog.core.repository.MoprogRepository
import com.kotlinonly.moprog.data.core.logD
import com.kotlinonly.moprog.features.auth.presentation.AuthViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SignInViewModel(
    auth: AccountService,
    repository: MoprogRepository,
    authDataStoreManager: AuthDataStoreManager
): AuthViewModel(auth, repository, authDataStoreManager) {
    private val _state = MutableStateFlow(SignInState())
    val state = _state.asStateFlow()

    fun setEmail(email: String) {
        _state.update { it.copy(email = email.trim()) }
    }

    fun setPassword(password: String) {
        _state.update { it.copy(password = password) }
    }

    fun togglePasswordVisibility() {
        _state.update { it.copy(passwordVisible = !it.passwordVisible) }
    }

    fun signInWithEmailAndPassword() {
        viewModelScope.launch {
            logD("SignInViewModel", "Email: ${_state.value.email}, Password: ${_state.value.password}")
        }
    }
}