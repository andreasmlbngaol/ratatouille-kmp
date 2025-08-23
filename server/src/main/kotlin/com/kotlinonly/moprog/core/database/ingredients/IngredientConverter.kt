package com.kotlinonly.moprog.core.database.ingredients

import com.kotlinonly.moprog.data.ingredient.Ingredient
import org.jetbrains.exposed.v1.core.ResultRow

fun ResultRow.toIngredient() = Ingredient(
    id = this[Ingredients.id].value,
    name = this[Ingredients.name],
    amount = this[Ingredients.amount],
    unit = this[Ingredients.unit]
)