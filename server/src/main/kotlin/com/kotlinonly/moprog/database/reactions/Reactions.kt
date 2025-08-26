package com.kotlinonly.moprog.database.reactions

import com.kotlinonly.moprog.database.utils.LongBaseTable
import com.kotlinonly.moprog.database.recipes.Recipes
import com.kotlinonly.moprog.database.users.Users
import com.kotlinonly.moprog.data.reactions.ReactionType
import org.jetbrains.exposed.v1.core.ReferenceOption

object Reactions: LongBaseTable("reactions") {
    val recipeId = reference("recipe_id", Recipes.id, onDelete = ReferenceOption.CASCADE)
    val userId = optReference("user_id", Users.id, onDelete = ReferenceOption.CASCADE)
    val type = enumerationByName<ReactionType>("type", 10)
}