package com.kotlinonly.moprog.core.database.recipes

import com.kotlinonly.moprog.core.database.users.Users
import org.jetbrains.exposed.v1.core.and
import org.jetbrains.exposed.v1.jdbc.insertAndGetId
import org.jetbrains.exposed.v1.jdbc.select
import org.jetbrains.exposed.v1.jdbc.selectAll
import org.jetbrains.exposed.v1.jdbc.transactions.transaction

object RecipesRepository {
    fun findById(id: Long) = transaction {
        // Join dengan User langsung karena one-to-one
        (Recipes innerJoin Users).selectAll()
            .where { Recipes.id eq id }
            .map { it.toRecipeDetailSummary() }
            .singleOrNull()
    }

    fun save(
        name: String,
        authorId: String,
        estTimeInMinutes: Int?,
        description: String?,
        isPublic: Boolean,
    ) = transaction {
        Recipes
            .insertAndGetId {
                it[Recipes.name] = name
                it[Recipes.authorId] = authorId
                it[Recipes.estTimeInMinutes] = estTimeInMinutes
                it[Recipes.description] = description
                it[Recipes.isPublic] = isPublic
            }
    }

    fun isAuthor(recipeId: Long, userId: String) = transaction {
        Recipes
            .select(Recipes.id)
            .where { (Recipes.id eq recipeId) and  (Recipes.authorId eq userId) }
            .count() > 0
    }
}