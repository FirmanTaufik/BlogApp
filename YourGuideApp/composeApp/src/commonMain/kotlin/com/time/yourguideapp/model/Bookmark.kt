package com.time.yourguideapp.model

import kotlinx.serialization.Serializable

@Serializable
data class Bookmark(
    val postId: String = "",
    val createdAt: String = "",
)
