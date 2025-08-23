package com.kotlinonly.moprog.data.comments

import com.kotlinonly.moprog.data.auth.UserSummary
import com.kotlinonly.moprog.data.core.now
import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.Serializable

@Serializable
data class CommentSummary(
    val id: Long = 0L,
    val author: UserSummary = UserSummary(),
    val content: String = "",
    val createdAt: LocalDateTime = now,
    val rating: Double? = null
)
