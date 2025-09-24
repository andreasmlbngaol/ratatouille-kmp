package com.kotlinonly.moprog.database.users

import com.kotlinonly.moprog.data.auth.User
import com.kotlinonly.moprog.data.recipes.RecipeFilter
import com.kotlinonly.moprog.data.users.UserDetail
import com.kotlinonly.moprog.database.follows.FollowsRepository
import com.kotlinonly.moprog.database.recipes.RecipesRepository
import org.jetbrains.exposed.v1.core.ResultRow

fun ResultRow.toUser() = User(
    id = this[Users.id].value,
    name = this[Users.name],
    email = this[Users.email],
    profilePictureUrl = this[Users.profilePictureUrl],
    coverPictureUrl = this[Users.coverPictureUrl],
    method = this[Users.method],
    isEmailVerified = this[Users.isEmailVerified],
    createdAt = this[Users.createdAt],
    bio = this[Users.bio]
)

fun ResultRow.toUserDetail(filter: RecipeFilter): UserDetail {
    val userId = this[Users.id].value
    val totalFollower = FollowsRepository.countFollowerById(userId)
    val totalFollowing = FollowsRepository.countFollowingById(userId)

    val topRecipes = RecipesRepository.findAll(
        RecipeFilter(limit = 3)
    )
    val totalRecipes = RecipesRepository.countByAuthorId(userId, filter)

    return UserDetail(
        id = userId,
        email = this[Users.email],
        name = this[Users.name],
        profilePictureUrl = this[Users.profilePictureUrl],
        coverPictureUrl = this[Users.coverPictureUrl],
        bio = this[Users.bio],
        totalFollower = totalFollower,
        totalFollowing = totalFollowing,
        totalRecipes = totalRecipes,
        topRecipes = topRecipes
    )
}