package com.kotlinonly.moprog.data.auth

import com.kotlinonly.moprog.data.core.now
import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.Serializable
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
@Serializable
data class User(
    val createdAt: LocalDateTime = now,
    val email: String? = null,
    val id: String = "",
    val isEmailVerified: Boolean = false,
    val method: AuthMethod = AuthMethod.EMAIL_AND_PASSWORD,
    val name: String = "",
    val profilePictureUrl: String? = null,
)

