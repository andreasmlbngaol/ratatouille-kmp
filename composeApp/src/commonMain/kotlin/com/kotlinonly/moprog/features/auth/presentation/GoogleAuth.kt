package com.kotlinonly.moprog.features.auth.presentation

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

@Composable
expect fun GoogleButton(
    modifier: Modifier = Modifier,
    onGetCredential: suspend (idToken: String) -> Unit
)

@Composable
fun GoogleButtonContent() {
    Text(
        text = "Google",
        fontSize = 14.sp,
        fontWeight = FontWeight.SemiBold,
    )
}