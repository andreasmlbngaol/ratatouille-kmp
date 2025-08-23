package com.kotlinonly.moprog.core.database.recipes

import com.kotlinonly.moprog.core.database.comments.CommentsRepository
import com.kotlinonly.moprog.core.database.ingredients.IngredientsRepository
import com.kotlinonly.moprog.core.database.ratings.RatingsRepository
import com.kotlinonly.moprog.core.database.reactions.ReactionsRepository
import com.kotlinonly.moprog.core.database.recipes_images.RecipesImagesRepository
import com.kotlinonly.moprog.core.database.steps.StepsRepository
import com.kotlinonly.moprog.core.database.users.Users
import com.kotlinonly.moprog.data.auth.UserSummary
import com.kotlinonly.moprog.data.ratings.AverageRating
import com.kotlinonly.moprog.data.recipes.RecipeDetailSummary
import org.jetbrains.exposed.v1.core.ResultRow

fun ResultRow.toRecipeDetailSummary(): RecipeDetailSummary {
    // 1. Ambil author dari hasil yang sudah di join
    val author = UserSummary(
        id = this[Users.id],
        name = this[Users.name],
        profilePictureUrl = this[Users.profilePictureUrl]
    )

    val id = this[Recipes.id].value

    // 2. Ambil rating
    val ratingList = RatingsRepository.findAllByRecipeId(id)
    val rating = if(ratingList.isEmpty()) null else AverageRating(ratingList.average(), ratingList.size)

    // 3 Ambil reaction
    val reactionList = ReactionsRepository.findAllByRecipeId(id)
    val reactions = if(reactionList.isEmpty()) null else reactionList.groupingBy { it }.eachCount()

    // 4. Ambil gambar
    val images = RecipesImagesRepository.findAllByRecipeId(id)

    // 5. Ambil bahan
    val ingredients = IngredientsRepository.findAllByRecipeId(id)

    // 6. Ambil langkah pembuatan
    val steps = StepsRepository.findAllByRecipeId(id)

    // 7. Ambil komentar
    val comments = CommentsRepository.findAllByRecipeId(id)

    return RecipeDetailSummary(
        id = id,
        author = author,
        name = this[Recipes.name],
        description = this[Recipes.description],
        estTimeInMinutes = this[Recipes.estTimeInMinutes],
        isPublic = this[Recipes.isPublic],
        createdAt = this[Recipes.createdAt],
        updatedAt = this[Recipes.updatedAt],
        rating = rating,
        reaction = reactions,
        totalReactions = reactionList.size,
        comments = comments,
        totalComments = comments.size,
        images = images,
        ingredients = ingredients,
        steps = steps
    )
}