package com.kotlinonly.moprog.database.recipes

import com.kotlinonly.moprog.data.recipes.RecipeCategory
import com.kotlinonly.moprog.data.recipes.RecipeFilter
import com.kotlinonly.moprog.data.recipes.RecipeStatus
import com.kotlinonly.moprog.data.recipes.SortType
import com.kotlinonly.moprog.database.utils.insertWithTimestamps
import com.kotlinonly.moprog.database.users.Users
import com.kotlinonly.moprog.database.utils.ilike
import com.kotlinonly.moprog.database.utils.updateWithTimestamps
import org.jetbrains.exposed.v1.core.and
import org.jetbrains.exposed.v1.jdbc.andWhere
import org.jetbrains.exposed.v1.jdbc.select
import org.jetbrains.exposed.v1.jdbc.selectAll
import org.jetbrains.exposed.v1.jdbc.transactions.transaction

object RecipesRepository {
    fun createDraft(userId: String) = transaction {
        Recipes.insertWithTimestamps {
            it[Recipes.name] = ""
            it[Recipes.authorId] = userId
            it[Recipes.description] = ""
            it[Recipes.category] = RecipeCategory.OTHERS
            it[Recipes.estTimeInMinutes] = null
            it[Recipes.isPublic] = false
            it[Recipes.status] = RecipeStatus.Draft
        }.value
    }

    fun findBaseById(id: Long) = transaction {
        Recipes
            .selectAll()
            .where { Recipes.id eq id }
            .map { it.toRecipeBase() }
            .singleOrNull()
    }

    fun findDraftBaseByAuthorId(userId: String) = transaction {
        Recipes
            .selectAll()
            .where { (Recipes.authorId eq userId) and (Recipes.status eq RecipeStatus.Draft) }
            .map { it.toRecipeBase() }
            .singleOrNull()
    }

    fun existByIdAndAuthorId(id: Long, userId: String) = transaction {
        Recipes
            .select(Recipes.id)
            .where { Recipes.id eq id }
            .andWhere { Recipes.authorId eq userId }
            .andWhere { Recipes.status eq RecipeStatus.Draft }
            .count() > 0
    }

    fun saveBase(
        id: Long,
        name: String,
        description: String?,
        category: RecipeCategory,
        estTimeInMinutes: Int?,
        isPublic: Boolean
    ) = transaction {
        Recipes
            .updateWithTimestamps({ Recipes.id eq id }) {
                it[Recipes.name] = name
                it[Recipes.description] = description
                it[Recipes.category] = category
                it[Recipes.estTimeInMinutes] = estTimeInMinutes
                it[Recipes.isPublic] = isPublic
            }
    }

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

        filter.isPublic?.let { isPublic ->
            stmt = stmt.andWhere { Recipes.isPublic eq isPublic }
        }

        stmt = stmt
            .andWhere { Recipes.status eq filter.status }
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

    fun countByAuthorId(authorId: String, filter: RecipeFilter) = transaction {
        var stmt = Recipes
            .select(Recipes.id)
            .where { Recipes.authorId eq authorId }

        filter.isPublic?.let { isPublic ->
            stmt = stmt.andWhere { Recipes.isPublic eq isPublic }
        }

        return@transaction stmt.count()
    }
}