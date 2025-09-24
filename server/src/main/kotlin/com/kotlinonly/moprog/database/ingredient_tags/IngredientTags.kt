package com.kotlinonly.moprog.database.ingredient_tags

import com.kotlinonly.moprog.database.utils.LongBaseTable

object IngredientTags: LongBaseTable("ingredient_tags") {
    val name = varchar("name", 64)

    init {
        uniqueIndex(name)
    }
}