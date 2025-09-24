package com.kotlinonly.moprog.database.comments_images

import com.kotlinonly.moprog.MY_DOMAIN
import com.kotlinonly.moprog.database.images.Images
import org.jetbrains.exposed.v1.jdbc.insertAndGetId
import org.jetbrains.exposed.v1.jdbc.select
import org.jetbrains.exposed.v1.jdbc.transactions.transaction

object CommentsImagesRepository {
    fun save(
        commentId: Long,
        imageId: Long
    ) = transaction {
        CommentsImages
            .insertAndGetId {
                it[CommentsImages.commentId] = commentId
                it[CommentsImages.imageId] = imageId
            }
    }.value

    fun findAllByCommentId(commentId: Long) = transaction {
        (CommentsImages innerJoin Images)
            .select(Images.url)
            .where { CommentsImages.commentId eq commentId }
            .map { "$MY_DOMAIN/${it[Images.url]}" }
    }
}