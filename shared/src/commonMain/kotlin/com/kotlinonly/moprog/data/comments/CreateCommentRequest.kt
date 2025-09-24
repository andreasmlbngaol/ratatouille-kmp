package com.kotlinonly.moprog.data.comments

import kotlinx.serialization.Serializable

@Serializable
data class CreateCommentRequest(
    val content: String
)
