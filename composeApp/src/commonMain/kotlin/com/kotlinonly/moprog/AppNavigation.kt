package com.kotlinonly.moprog

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.kotlinonly.moprog.features.auth.presentation.sign_in.SignInScreen
import com.kotlinonly.moprog.features.splash.SplashScreen

@Composable
fun Navigation() {
    val navController = rememberNavController()

    fun onBack() { navController.navigateUp() }

    NavHost(
        startDestination = SplashKey,
        navController = navController
    ) {
        composable<SplashKey> {
            SplashScreen(
                onNavigateToHome = { navController.navigate(HomeKey) {
                    popUpTo(SplashKey) { inclusive = true }
                    launchSingleTop = true
                } },
                onNavigateToSignIn = { navController.navigate(SignInKey) {
                    popUpTo(SplashKey) { inclusive = true }
                    launchSingleTop = true
                } }
            )
        }

        composable<HomeKey> {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.fillMaxSize()
            ) {
                Text("Home")
            }
        }

        composable<SignInKey> {
            SignInScreen(
                onNavigateToHome = { navController.navigate(HomeKey) {
                    popUpTo(SignInKey) { inclusive = true }
                    launchSingleTop = true
                } },
                onNavigateToSignUp = { navController.navigate(SignUpKey) {
                    popUpTo(SignInKey) { inclusive = true }
                    launchSingleTop = true
                } }
            )
        }
    }

}