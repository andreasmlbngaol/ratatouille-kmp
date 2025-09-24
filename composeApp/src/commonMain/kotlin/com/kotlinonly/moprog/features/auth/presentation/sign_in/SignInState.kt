package com.kotlinonly.moprog.features.auth.presentation.sign_in

data class SignInState(
    val email: String = "",
    val password: String = "",
    val passwordVisible: Boolean = false,
    val isLoading: Boolean = false
)