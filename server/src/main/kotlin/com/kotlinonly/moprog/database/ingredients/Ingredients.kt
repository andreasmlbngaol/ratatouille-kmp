package com.kotlinonly.moprog.database.ingredients

import com.kotlinonly.moprog.database.utils.LongBaseTable
import com.kotlinonly.moprog.database.recipes.Recipes
import org.jetbrains.exposed.v1.core.ReferenceOption

object Ingredients: LongBaseTable("ingredients") {
    val recipeId = reference("recipe_id", Recipes.id, onDelete = ReferenceOption.CASCADE)
    val name = varchar("name", 64)
    val amount = double("amount").nullable()
    val unit = varchar("unit", 16).nullable()
}