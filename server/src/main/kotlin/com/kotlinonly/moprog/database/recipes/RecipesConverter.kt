package com.kotlinonly.moprog.database.recipes

import com.kotlinonly.moprog.database.bookmarks.BookmarksRepository
import com.kotlinonly.moprog.database.comments.CommentsRepository
import com.kotlinonly.moprog.database.ingredients.IngredientsRepository
import com.kotlinonly.moprog.database.ratings.RatingsRepository
import com.kotlinonly.moprog.database.reactions.ReactionsRepository
import com.kotlinonly.moprog.database.recipes_images.RecipesImagesRepository
import com.kotlinonly.moprog.database.steps.StepsRepository
import com.kotlinonly.moprog.database.users.Users
import com.kotlinonly.moprog.data.auth.UserSummary
import com.kotlinonly.moprog.data.ratings.AverageRating
import com.kotlinonly.moprog.data.recipes.RecipeBase
import com.kotlinonly.moprog.data.recipes.RecipeDetailSummary
import com.kotlinonly.moprog.data.recipes.RecipeSimpleSummary
import org.jetbrains.exposed.v1.core.ResultRow

fun ResultRow.toRecipeDetailSummary(userId: String): RecipeDetailSummary {
    // 1. Ambil author dari hasil yang sudah di join
    val author = UserSummary(
        id = this[Users.id].value,
        name = this[Users.name],
        profilePictureUrl = this[Users.profilePictureUrl]
    )

    val recipeId = this[Recipes.id].value

    // 2. Ambil rating
    val ratingList = RatingsRepository.findAllByRecipeId(recipeId)
    val rating = if(ratingList.isEmpty()) null else AverageRating(
        value = ratingList.average(),
        count = ratingList.size
    )

    // 3 Ambil reaction
    val reactionList = ReactionsRepository.findAllByRecipeId(recipeId)
    val reactions = if(reactionList.isEmpty()) null else reactionList.groupingBy { it }.eachCount()

    // 4. Ambil gambar
    val imageUrls = RecipesImagesRepository.findAllByRecipeId(recipeId)
        .map { it.url }

    // 5. Ambil bahan
    val ingredients = IngredientsRepository.findAllByRecipeId(recipeId)

    // 6. Ambil langkah pembuatan
    val steps = StepsRepository.findAllByRecipeId(recipeId)

    // 7. Ambil komentar
    val comments = CommentsRepository.findAllByRecipeId(recipeId)

    // 8. Cek bookmark
    val totalBookmarks = BookmarksRepository.countByRecipeId(recipeId)
    val isBookmarked = BookmarksRepository.isBookmarked(recipeId, userId)

    return RecipeDetailSummary(
        id = recipeId,
        author = author,
        category = this[Recipes.category],
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
        images = imageUrls,
        ingredients = ingredients,
        steps = steps,
        totalBookmarks = totalBookmarks,
        isBookmarked = isBookmarked
    )
}

fun ResultRow.toRecipeSimpleSummary(): RecipeSimpleSummary {
    // 1. Ambil author dari hasil yang sudah di join
    val author = UserSummary(
        id = this[Users.id].value,
        name = this[Users.name],
        profilePictureUrl = this[Users.profilePictureUrl]
    )

    val recipeId = this[Recipes.id].value

    // 2. Ambil gambar
    val image = RecipesImagesRepository.findFirstByRecipeId(recipeId)

    // 3. Ambil rating
    val ratingList = RatingsRepository.findAllByRecipeId(recipeId)
    val rating = if(ratingList.isEmpty()) null else AverageRating(
        value = ratingList.average(),
        count = ratingList.size
    )

    // 4. Ambil reaction
    val reactionList = ReactionsRepository.findAllByRecipeId(recipeId)
    val reactions = if(reactionList.isEmpty()) null else reactionList.groupingBy { it }.eachCount()

    // 5. Cek bookmark
    val totalBookmarks = BookmarksRepository.countByRecipeId(recipeId)

    return RecipeSimpleSummary(
        author = author,
        category = this[Recipes.category],
        createdAt = this[Recipes.createdAt],
        estTimeInMinutes = this[Recipes.estTimeInMinutes],
        id = recipeId,
        image = image,
        name = this[Recipes.name],
        updatedAt = this[Recipes.updatedAt],
        rating = rating,
        reactions = reactions,
        totalBookmarks = totalBookmarks
    )
}

fun ResultRow.toRecipeBase(): RecipeBase {
    val imagesUrls = RecipesImagesRepository.findAllByRecipeId(this[Recipes.id].value)
    return RecipeBase(
        id = this[Recipes.id].value,
        name = this[Recipes.name],
        authorId = this[Recipes.authorId].value,
        description = this[Recipes.description],
        estTimeInMinutes = this[Recipes.estTimeInMinutes],
        category = this[Recipes.category],
        isPublic = this[Recipes.isPublic],
        status = this[Recipes.status],
        images = imagesUrls
    )
}