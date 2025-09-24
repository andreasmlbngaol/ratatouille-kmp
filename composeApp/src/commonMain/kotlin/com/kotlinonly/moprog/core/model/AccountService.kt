package com.kotlinonly.moprog.core.model

interface AccountService {
    suspend fun signInWithGoogle(
        idToken: String,
        onGetFirebaseIdToken: (String) -> Unit
    )

    suspend fun signOut()
}