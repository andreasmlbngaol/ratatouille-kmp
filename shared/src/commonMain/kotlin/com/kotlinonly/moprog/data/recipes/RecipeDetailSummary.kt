package com.kotlinonly.moprog.data.recipes

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
    val id: Long = 0L,
    val author: UserSummary = UserSummary(),
    val name: String = "",
    val description: String? = null,
    val estTimeInMinutes: Int? = null,
    val isPublic: Boolean = true,
    val createdAt: LocalDateTime = now,
    val updatedAt: LocalDateTime = now,
    val rating: AverageRating? = null,
    val reaction: Map<ReactionType, Int>? = null,
    val totalReactions: Int = 0,
    val comments: List<CommentSummary> = emptyList(),
    val totalComments: Int = 0,
    val images: List<String> = emptyList(),
    val ingredients: List<Ingredient> = emptyList(),
    val steps: List<String> = emptyList()
)