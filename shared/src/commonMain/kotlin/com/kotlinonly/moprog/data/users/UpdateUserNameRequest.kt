package com.kotlinonly.moprog.data.users

import kotlinx.serialization.Serializable

@Serializable
data class UpdateUserNameRequest(val name: String)