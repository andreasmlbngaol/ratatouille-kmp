package com.kotlinonly.moprog.data.auth

import com.kotlinonly.moprog.data.core.now
import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.Serializable

@Serializable
data class User(
    val createdAt: LocalDateTime = now,
    val email: String = "",
    val id: String = "",
    val isEmailVerified: Boolean = false,
    val method: AuthMethod = AuthMethod.EMAIL_AND_PASSWORD,
    val name: String = "",
    val profilePictureUrl: String? = null,
    val coverPictureUrl: String? = null,
    val bio: String? = null
)

