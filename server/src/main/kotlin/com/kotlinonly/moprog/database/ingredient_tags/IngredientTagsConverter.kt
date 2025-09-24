package com.kotlinonly.moprog.database.ingredient_tags

import com.kotlinonly.moprog.core.utils.uppercaseEachWord
import com.kotlinonly.moprog.data.ingredient.IngredientTag
import org.jetbrains.exposed.v1.core.ResultRow

fun ResultRow.toIngredientTag() = IngredientTag(
    id = this[IngredientTags.id].value,
    name = this[IngredientTags.name].uppercaseEachWord()
)