package com.kotlinonly.moprog.data.recipes

import com.kotlinonly.moprog.data.auth.UserSummary
import com.kotlinonly.moprog.data.core.now
import com.kotlinonly.moprog.data.ratings.AverageRating
import com.kotlinonly.moprog.data.reactions.ReactionType
import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.Serializable

@Serializable
data class RecipeSimpleSummary(
    val author: UserSummary = UserSummary(),
    val category: RecipeCategory = RecipeCategory.OTHERS,
    val createdAt: LocalDateTime = now,
    val estTimeInMinutes: Int? = null,
    val id: Long = 0L,
    val image: String? = null,
    val name: String = "",
    val updatedAt: LocalDateTime = now,
    val rating: AverageRating? = null,
    val reactions: Map<ReactionType, Int>? = null,
    val totalBookmarks: Long = 0L
)