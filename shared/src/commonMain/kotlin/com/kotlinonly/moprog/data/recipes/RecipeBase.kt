package com.kotlinonly.moprog.data.recipes

import kotlinx.serialization.Serializable

@Serializable
data class RecipeBase(
    val id: Long = 0L,
    val name: String = "",
    val authorId: String = "",
    val description: String? = null,
    val category: RecipeCategory = RecipeCategory.OTHERS,
    val isPublic: Boolean = true,
    val status: RecipeStatus = RecipeStatus.Draft,
    val imageUrls: Map<Long, String> = emptyMap()
)
