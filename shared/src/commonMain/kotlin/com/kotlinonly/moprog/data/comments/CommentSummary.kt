package com.kotlinonly.moprog.data.comments

import com.kotlinonly.moprog.data.auth.UserSummary
import com.kotlinonly.moprog.data.core.now
import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.Serializable

@Serializable
data class CommentSummary(
    val author: UserSummary? = null,
    val content: String = "",
    val createdAt: LocalDateTime = now,
    val id: Long = 0L,
    val imageUrls: List<String> = emptyList(),
    val rating: Double? = null
)
