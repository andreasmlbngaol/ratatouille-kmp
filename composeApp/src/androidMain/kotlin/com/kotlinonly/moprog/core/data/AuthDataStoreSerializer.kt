package com.kotlinonly.moprog.core.data

import android.util.Log
import androidx.datastore.core.Serializer
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import java.io.InputStream
import java.io.OutputStream

object AuthDataStoreSerializer: Serializer<AuthDataStore> {

    private val json = Json {
        ignoreUnknownKeys = true
        prettyPrint = true
        isLenient = true
    }
    override val defaultValue: AuthDataStore
        get() = AuthDataStore()

    override suspend fun readFrom(input: InputStream): AuthDataStore {
        return try {
            json.decodeFromString(
                deserializer = AuthDataStore.serializer(),
                string = input.readBytes().decodeToString()
            )
        } catch (e: SerializationException) {
            Log.e("AuthTokenSerializer", "Error deserializing AuthTokens: ${e.message}")
            defaultValue
        }
    }

    override suspend fun writeTo(
        t: AuthDataStore,
        output: OutputStream
    ) {
        output.write(
            json.encodeToString(
                serializer = AuthDataStore.serializer(),
                value = t
            ).encodeToByteArray()
        )
    }
}