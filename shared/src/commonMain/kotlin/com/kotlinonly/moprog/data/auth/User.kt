package com.kotlinonly.moprog.data.auth

import com.kotlinonly.moprog.data.core.now
import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.Serializable
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
@Serializable
data class User(
    val id: String = "",
    val name: String = "",
    val email: String? = null,
    val profilePictureUrl: String? = null,
    val method: AuthMethod = AuthMethod.EMAIL_AND_PASSWORD,
    val isEmailVerified: Boolean = false,
    val createdAt: LocalDateTime = now
)

