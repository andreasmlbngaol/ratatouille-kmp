package com.kotlinonly.moprog.database.comments

import com.kotlinonly.moprog.database.comments_images.CommentsImagesRepository
import com.kotlinonly.moprog.database.ratings.RatingsRepository
import com.kotlinonly.moprog.database.users.UsersRepository
import com.kotlinonly.moprog.data.auth.UserSummary
import com.kotlinonly.moprog.data.comments.CommentSummary
import org.jetbrains.exposed.v1.core.ResultRow

fun ResultRow.toCommentSummary(): CommentSummary {
    val commentId = this[Comments.id].value
    val recipeId = this[Comments.recipeId].value

    val authorId = this[Comments.userId]?.value
    var author: UserSummary? = null

    if(authorId != null) {
        UsersRepository.findById(authorId)?.let { authorFull ->
            author = UserSummary(
                id = authorFull.id,
                name = authorFull.name,
                profilePictureUrl = authorFull.profilePictureUrl
            )
        }
    }

    val rating = if(author == null) null else RatingsRepository.findByRecipeIdAndAuthorId(recipeId, author.id)

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