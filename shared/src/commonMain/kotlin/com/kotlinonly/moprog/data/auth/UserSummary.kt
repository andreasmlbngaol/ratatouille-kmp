package com.kotlinonly.moprog.data.auth

import kotlinx.serialization.Serializable

@Serializable
data class UserSummary(
    val id: String = "",
    val name: String = "",
    val profilePictureUrl: String? = null
)