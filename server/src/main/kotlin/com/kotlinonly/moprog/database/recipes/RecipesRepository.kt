package com.kotlinonly.moprog.database.recipes

import com.kotlinonly.moprog.data.recipes.RecipeCategory
import com.kotlinonly.moprog.data.recipes.RecipeFilter
import com.kotlinonly.moprog.data.recipes.SortType
import com.kotlinonly.moprog.database.utils.insertWithTimestamps
import com.kotlinonly.moprog.database.users.Users
import com.kotlinonly.moprog.database.utils.ilike
import org.jetbrains.exposed.v1.core.and
import org.jetbrains.exposed.v1.jdbc.andWhere
import org.jetbrains.exposed.v1.jdbc.select
import org.jetbrains.exposed.v1.jdbc.selectAll
import org.jetbrains.exposed.v1.jdbc.transactions.transaction

object RecipesRepository {
    fun findById(id: Long, userId: String) = transaction {
        // Join dengan User langsung karena one-to-one
        (Recipes innerJoin Users).selectAll()
            .where { Recipes.id eq id }
            .map { it.toRecipeDetailSummary(userId) }
            .singleOrNull()
    }

    fun findAll(filter: RecipeFilter) = transaction {
        val query = Recipes innerJoin Users
        var stmt = query.selectAll()
        filter.name?.let { name ->
            stmt = stmt.andWhere { Recipes.name ilike "%$name%" }
        }

        if(filter.category != RecipeCategory.ALL)
            stmt = stmt.andWhere { Recipes.category eq filter.category }

        stmt = stmt
            .limit(filter.limit)
            .offset(filter.offset)

        val unsorted = stmt.map { it.toRecipeSimpleSummary() }

        return@transaction when(filter.sort) {
            SortType.POPULAR -> unsorted.sortedBy { it.rating?.value }
            else -> unsorted.sortedBy { it.updatedAt }
        }
    }

    fun save(
        category: RecipeCategory,
        name: String,
        authorId: String,
        estTimeInMinutes: Int?,
        description: String?,
        isPublic: Boolean,
    ) = transaction {
        Recipes
            .insertWithTimestamps {
                it[Recipes.category] = category
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