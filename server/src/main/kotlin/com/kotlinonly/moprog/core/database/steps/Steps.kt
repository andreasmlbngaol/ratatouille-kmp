package com.kotlinonly.moprog.core.database.steps

import com.kotlinonly.moprog.core.database.recipes.Recipes
import org.jetbrains.exposed.v1.core.dao.id.LongIdTable

object Steps: LongIdTable("steps") {
    val recipeId = reference("recipe_id", Recipes.id)
    val stepNumber = integer("step_number")
    val content = text("content")
}