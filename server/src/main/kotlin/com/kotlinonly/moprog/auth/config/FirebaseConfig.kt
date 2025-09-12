package com.kotlinonly.moprog.auth.config

import com.google.auth.oauth2.GoogleCredentials
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions

object FirebaseConfig {
    fun init() {
        if (FirebaseApp.getApps().isEmpty()) {
//            val serviceAccount = FileInputStream("serviceAccountKey.json")
            val options = FirebaseOptions.builder()
//                .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                .setCredentials(GoogleCredentials.getApplicationDefault())
                .build()

            FirebaseApp.initializeApp(options)
        }

    }
}