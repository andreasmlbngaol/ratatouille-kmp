package com.kotlinonly.moprog.data.users

import com.kotlinonly.moprog.data.recipes.RecipeSimpleSummary
import kotlinx.serialization.Serializable

@Serializable
data class UserDetail(
    val id: String = "",
    val email: String = "",
    val name: String = "",
    val profilePictureUrl: String? = null,
    val coverPictureUrl: String? = null,
    val bio: String? = null,
    val totalFollower: Long = 0L,
    val totalFollowing: Long = 0L,
    val totalRecipes: Long = 0L,
    val topRecipes: List<RecipeSimpleSummary> = emptyList(),
    val isMe: Boolean = false,
    val isFollowed: Boolean? = null
)