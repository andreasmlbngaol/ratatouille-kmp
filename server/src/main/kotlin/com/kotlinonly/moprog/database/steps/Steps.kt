package com.kotlinonly.moprog.database.steps

import com.kotlinonly.moprog.database.LongBaseTable
import com.kotlinonly.moprog.database.recipes.Recipes
import org.jetbrains.exposed.v1.core.ReferenceOption

object Steps: LongBaseTable("steps") {
    val recipeId = reference("recipe_id", Recipes.id, onDelete = ReferenceOption.CASCADE)
    val stepNumber = integer("step_number")
    val content = text("content")
}