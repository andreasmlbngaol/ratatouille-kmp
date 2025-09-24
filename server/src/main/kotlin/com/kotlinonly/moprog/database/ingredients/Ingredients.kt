package com.kotlinonly.moprog.database.ingredients

import com.kotlinonly.moprog.database.ingredient_tags.IngredientTags
import com.kotlinonly.moprog.database.utils.LongBaseTable
import com.kotlinonly.moprog.database.recipes.Recipes
import org.jetbrains.exposed.v1.core.ReferenceOption

object Ingredients: LongBaseTable("ingredients") {
    val recipeId = reference("recipe_id", Recipes.id, onDelete = ReferenceOption.CASCADE)
    val tagId = reference("tag_id", IngredientTags.id, onDelete = ReferenceOption.CASCADE)
    val alternative = varchar("alternative", 64).nullable()
    val amount = double("amount").nullable()
    val unit = varchar("unit", 16).nullable()
}