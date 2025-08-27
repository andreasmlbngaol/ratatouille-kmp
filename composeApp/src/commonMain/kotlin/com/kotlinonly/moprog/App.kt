package com.kotlinonly.moprog

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
@Preview
fun App(
    exitWithDoublePress: (@Composable (() -> Unit))? = null
) {
    MaterialTheme(
        colorScheme = if(isSystemInDarkTheme()) darkColorScheme() else lightColorScheme()
    ) {
        Surface(
            color = MaterialTheme.colorScheme.background,
            modifier = Modifier.fillMaxSize()
        ) {
            exitWithDoublePress?.invoke()
            Navigation()
        }
    }
}