package com.kotlinonly.moprog.features.auth.presentation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
actual fun GoogleButton(modifier: Modifier, onGetCredential: suspend (idToken: String) -> Unit) {
}

