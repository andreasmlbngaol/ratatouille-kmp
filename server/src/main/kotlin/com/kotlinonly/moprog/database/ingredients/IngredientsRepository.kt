package com.kotlinonly.moprog.database.ingredients

import com.kotlinonly.moprog.database.batchInsertWithTimestamps
import com.kotlinonly.moprog.data.ingredient.IngredientRequest
import org.jetbrains.exposed.v1.jdbc.selectAll
import org.jetbrains.exposed.v1.jdbc.transactions.transaction

object IngredientsRepository {
    fun findAllByRecipeId(recipeId: Long) = transaction {
        Ingredients
            .selectAll()
            .where { Ingredients.recipeId eq recipeId }
            .orderBy(Ingredients.id)
            .map { it.toIngredient() }
    }

    fun saveAll(
        recipeId: Long,
        ingredients: List<IngredientRequest>
    ) = transaction {
        Ingredients
            .batchInsertWithTimestamps(ingredients) { ingredient ->
                this[Ingredients.recipeId] = recipeId
                this[Ingredients.name] = ingredient.name
                this[Ingredients.amount] = ingredient.amount
                this[Ingredients.unit] = ingredient.unit
            }
    }
}