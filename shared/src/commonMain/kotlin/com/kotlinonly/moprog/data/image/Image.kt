package com.kotlinonly.moprog.data.image

import kotlinx.serialization.Serializable

@Serializable
data class Image(
    val id: Long,
    val url: String,
    val description: String? = null
)
