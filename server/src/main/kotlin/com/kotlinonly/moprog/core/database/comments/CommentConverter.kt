package com.kotlinonly.moprog.core.database.comments

import com.kotlinonly.moprog.core.database.ratings.RatingsRepository
import com.kotlinonly.moprog.data.auth.UserSummary
import com.kotlinonly.moprog.data.comments.CommentSummary
import org.jetbrains.exposed.v1.core.ResultRow

fun ResultRow.toCommentSummary(): CommentSummary {
    val id = this[Comments.id].value
    val recipeId = this[Comments.recipeId].value

    val authorId = this[Comments.userId]
    val authorFull = UsersRepository.findById(authorId)
        ?: return CommentSummary()
    val author = UserSummary(
        id = authorFull.id,
        name = authorFull.name,
        profilePictureUrl = authorFull.profilePictureUrl
    )

    val rating = RatingsRepository.findByRecipeIdAndAuthorId(recipeId, author.id)

    return CommentSummary(
        id = id,
        author = author,
        content = this[Comments.content],
        createdAt = this[Comments.createdAt],
        rating = rating
    )
}