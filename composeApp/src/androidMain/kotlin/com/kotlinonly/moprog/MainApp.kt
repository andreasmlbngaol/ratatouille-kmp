package com.kotlinonly.moprog

import android.app.Application
import com.google.firebase.Firebase
import com.google.firebase.initialize
import com.kotlinonly.moprog.di.initKoin
import org.koin.android.ext.koin.androidContext

class MainApp: Application() {
    override fun onCreate() {
        super.onCreate()
        initKoin {
            androidContext(this@MainApp)
        }
        Firebase.initialize(this)
    }
}