package com.kotlinonly.moprog.data.recipes

import com.kotlinonly.moprog.data.Step
import com.kotlinonly.moprog.data.auth.UserSummary
import com.kotlinonly.moprog.data.comments.CommentSummary
import com.kotlinonly.moprog.data.core.now
import com.kotlinonly.moprog.data.ingredient.Ingredient
import com.kotlinonly.moprog.data.ratings.AverageRating
import com.kotlinonly.moprog.data.reactions.ReactionType
import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.Serializable

@Serializable
data class RecipeDetailSummary(
    val author: UserSummary = UserSummary(),
    val comments: List<CommentSummary> = emptyList(),
    val category: RecipeCategory = RecipeCategory.OTHERS,
    val createdAt: LocalDateTime = now,
    val description: String? = null,
    val estTimeInMinutes: Int? = null,
    val id: Long = 0L,
    val images: List<String> = emptyList(),
    val ingredients: List<Ingredient> = emptyList(),
    val isBookmarked: Boolean = false,
    val isPublic: Boolean = true,
    val name: String = "",
    val updatedAt: LocalDateTime = now,
    val rating: AverageRating? = null,
    val reaction: Map<ReactionType, Int>? = null,
    val steps: List<Step> = emptyList(),
    val totalBookmarks: Long = 0L,
    val totalComments: Int = 0,
    val totalReactions: Int = 0
)