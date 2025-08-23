package com.kotlinonly.moprog.core.database.reactions

import com.kotlinonly.moprog.core.database.recipes.Recipes
import com.kotlinonly.moprog.core.database.users.Users
import com.kotlinonly.moprog.data.reactions.ReactionType
import org.jetbrains.exposed.v1.core.dao.id.LongIdTable

object Reactions: LongIdTable("reactions") {
    val recipeId = reference("recipe_id", Recipes.id)
    val userId = reference("user_id", Users.id)
    val type = enumerationByName<ReactionType>("type", 10)
}