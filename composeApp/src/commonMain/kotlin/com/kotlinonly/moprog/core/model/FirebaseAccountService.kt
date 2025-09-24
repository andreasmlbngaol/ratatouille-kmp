package com.kotlinonly.moprog.core.model

expect class FirebaseAccountService : AccountService {
    override suspend fun signInWithGoogle(idToken: String, onGetFirebaseIdToken: (String) -> Unit)
    override suspend fun signOut()
}