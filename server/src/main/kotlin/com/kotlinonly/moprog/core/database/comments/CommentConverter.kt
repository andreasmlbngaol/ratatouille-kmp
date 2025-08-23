package com.kotlinonly.moprog.core.database.comments

import com.kotlinonly.moprog.core.database.comments_images.CommentsImagesRepository
import com.kotlinonly.moprog.core.database.ratings.RatingsRepository
import com.kotlinonly.moprog.data.auth.UserSummary
import com.kotlinonly.moprog.data.comments.CommentSummary
import org.jetbrains.exposed.v1.core.ResultRow

fun ResultRow.toCommentSummary(): CommentSummary {
    val commentId = this[Comments.id].value
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

    val imageUrls = CommentsImagesRepository.findAllByCommentId(commentId)

    return CommentSummary(
        id = commentId,
        author = author,
        content = this[Comments.content],
        createdAt = this[Comments.createdAt],
        rating = rating,
        imageUrls = imageUrls
    )
}