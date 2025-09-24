package com.kotlinonly.moprog.database.ingredients

import com.kotlinonly.moprog.data.ingredient.Ingredient
import com.kotlinonly.moprog.database.ingredient_tags.IngredientTags
import org.jetbrains.exposed.v1.core.ResultRow

fun ResultRow.toIngredient() = Ingredient(
    id = this[Ingredients.id].value,
    name = this[IngredientTags.name],
    alternative = this[Ingredients.alternative],
    amount = this[Ingredients.amount],
    unit = this[Ingredients.unit]
)