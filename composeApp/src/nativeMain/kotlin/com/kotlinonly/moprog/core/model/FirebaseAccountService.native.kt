package com.kotlinonly.moprog.core.model

actual class FirebaseAccountService : AccountService {
    actual override suspend fun signInWithGoogle(
        idToken: String,
        onGetFirebaseIdToken: (String) -> Unit
    ) {
    }

    actual override suspend fun signOut() {
    }
}