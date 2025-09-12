package com.kotlinonly.moprog.data.recipes

import kotlinx.serialization.Serializable

@Serializable
data class RecipeFilter(
    val name: String? = null,
    val category: RecipeCategory = RecipeCategory.ALL,
    val sort: SortType = SortType.POPULAR,
    val limit: Int = 10,
    val offset: Long = 0L,
    val isPublic: Boolean? = null,
    val status: RecipeStatus = RecipeStatus.Published
)
