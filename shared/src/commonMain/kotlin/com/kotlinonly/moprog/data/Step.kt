package com.kotlinonly.moprog.data

import com.kotlinonly.moprog.data.image.Image
import kotlinx.serialization.Serializable

@Serializable
data class Step(
    val id: Long,
    val stepNumber: Int,
    val content: String,
    val images: List<Image>
)
