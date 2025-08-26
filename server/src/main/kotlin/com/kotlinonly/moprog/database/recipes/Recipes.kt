package com.kotlinonly.moprog.database.recipes

import com.kotlinonly.moprog.data.recipes.RecipeCategory
import com.kotlinonly.moprog.database.utils.LongBaseTable
import com.kotlinonly.moprog.database.users.Users
import org.jetbrains.exposed.v1.core.ReferenceOption

object Recipes: LongBaseTable("recipes") {
    val name = varchar("name", 100)
    val authorId = reference("author", Users.id, onDelete = ReferenceOption.CASCADE)
    val description = text("description").nullable()
    val category = enumerationByName<RecipeCategory>("recipes", 64).default(RecipeCategory.OTHERS)
    val estTimeInMinutes = integer("est_time_in_minutes").nullable()
    val isPublic = bool("is_public").default(true)
}