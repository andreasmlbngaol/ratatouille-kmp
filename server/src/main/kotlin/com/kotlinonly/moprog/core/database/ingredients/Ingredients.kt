package com.kotlinonly.moprog.core.database.ingredients

import com.kotlinonly.moprog.core.database.recipes.Recipes
import org.jetbrains.exposed.v1.core.dao.id.LongIdTable

object Ingredients: LongIdTable("ingredients") {
    val recipeId = reference("recipe_id", Recipes.id)
    val name = varchar("name", 64)
    val amount = double("amount").nullable()
    val unit = varchar("unit", 16).nullable()
}