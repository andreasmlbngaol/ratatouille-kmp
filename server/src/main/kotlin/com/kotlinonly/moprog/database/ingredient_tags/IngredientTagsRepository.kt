package com.kotlinonly.moprog.database.ingredient_tags

import com.kotlinonly.moprog.database.utils.ilike
import com.kotlinonly.moprog.database.utils.insertWithTimestamps
import org.jetbrains.exposed.v1.jdbc.selectAll
import org.jetbrains.exposed.v1.jdbc.transactions.transaction

object IngredientTagsRepository {
    fun findByNameILikeWithLimit(
        name: String,
        limit: Int
    ) = transaction {
        IngredientTags
            .selectAll()
            .where { IngredientTags.name ilike "%$name%" }
            .limit(limit)
            .map { it.toIngredientTag() }
    }

    fun findByName(name: String) = transaction {
        IngredientTags
            .selectAll()
            .where { IngredientTags.name eq name.uppercase() }
            .firstOrNull()
    }

    fun save(name: String) = transaction {
        IngredientTags
            .insertWithTimestamps {
                it[IngredientTags.name] = name.uppercase()
            }
    }
}