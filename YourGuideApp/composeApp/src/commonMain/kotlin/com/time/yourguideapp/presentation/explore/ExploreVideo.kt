package com.time.yourguideapp.presentation.explore

data class ExploreVideo(
    val id: String,
    val title: String,
    val creatorName: String,
    val creatorAvatarUrl: String,
    val tags: String,
    val description: String,
    val thumbnailUrl: String,
    val videoUrl: String,
    val pageUrl: String,
    val likes: String,
    val comments: String,
    val views: String,
    val downloads: String,
    val duration: String,
)

data class ExploreUiState(
    val videos: List<ExploreVideo> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
)
