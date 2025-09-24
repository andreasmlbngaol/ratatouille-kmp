package com.kotlinonly.moprog.data.reactions

import kotlinx.serialization.Serializable

@Serializable
data class CreateReactionRequest(
    val reaction: ReactionType? = null
)