package com.kotlinonly.moprog.database.comments_images

import com.kotlinonly.moprog.database.comments.Comments
import com.kotlinonly.moprog.database.images.Images
import org.jetbrains.exposed.v1.core.ReferenceOption
import org.jetbrains.exposed.v1.core.dao.id.LongIdTable

object CommentsImages: LongIdTable("comments_images") {
    val commentId = reference("comment_id", Comments.id, onDelete = ReferenceOption.CASCADE)
    val imageId = reference("image_id", Images.id)
}