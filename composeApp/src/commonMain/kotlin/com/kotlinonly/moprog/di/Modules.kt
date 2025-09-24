package com.kotlinonly.moprog.di

import com.kotlinonly.moprog.core.model.HttpClientFactory
import com.kotlinonly.moprog.core.repository.MoprogRepository
import com.kotlinonly.moprog.features.auth.presentation.sign_in.SignInViewModel
import com.kotlinonly.moprog.features.splash.SplashViewModel
import io.ktor.client.*
import io.ktor.client.engine.*
import io.ktor.client.engine.cio.*
import org.koin.core.module.Module
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

expect val platformModule: Module

val sharedModule = module {
    single<HttpClientEngine> { CIO.create() }
    // AuthDataStoreManager di platformModule
    single<HttpClient> { HttpClientFactory.create(get(), get()) }
    single { MoprogRepository(get())  }

    viewModelOf(::SplashViewModel)
    viewModelOf(::SignInViewModel)
}