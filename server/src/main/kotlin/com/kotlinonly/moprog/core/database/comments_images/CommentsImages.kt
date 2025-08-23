package com.kotlinonly.moprog.core.database.comments_images

import com.kotlinonly.moprog.core.database.comments.Comments
import com.kotlinonly.moprog.core.database.images.Images
import org.jetbrains.exposed.v1.core.dao.id.LongIdTable

object CommentsImages: LongIdTable("comments_images") {
    val commentId = reference("comment_id", Comments.id)
    val imageId = reference("image_id", Images.id)
}