package com.kotlinonly.moprog.data.recipes

import kotlinx.serialization.Serializable

@Serializable
data class DeleteRecipeBaseImageRequest(
    val imageId: Long,
    val url: String
)
