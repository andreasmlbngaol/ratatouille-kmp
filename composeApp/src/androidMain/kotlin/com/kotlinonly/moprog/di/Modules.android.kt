package com.kotlinonly.moprog.di

import androidx.datastore.core.DataStore
import androidx.datastore.core.DataStoreFactory
import com.kotlinonly.moprog.core.data.AuthDataStore
import com.kotlinonly.moprog.core.data.AuthDataStoreManagerImpl
import com.kotlinonly.moprog.core.data.AuthDataStoreSerializer
import com.kotlinonly.moprog.core.model.AccountService
import com.kotlinonly.moprog.core.model.AuthDataStoreManager
import com.kotlinonly.moprog.core.model.FirebaseAccountService
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.Module
import org.koin.dsl.module
import java.io.File

actual val platformModule: Module
    get() = module {
        single<DataStore<AuthDataStore>> {
            DataStoreFactory.create(
                serializer = AuthDataStoreSerializer,
                produceFile = { File(androidContext().filesDir, "auth_data_store.json") }
            )
        }
        single<AuthDataStoreManager> { AuthDataStoreManagerImpl(get()) }

        single<AccountService> { FirebaseAccountService() }
    }