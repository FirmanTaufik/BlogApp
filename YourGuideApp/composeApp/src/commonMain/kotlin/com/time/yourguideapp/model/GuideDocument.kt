package com.time.yourguideapp.model

import kotlinx.serialization.Serializable

@Serializable
data class GuideDocument(
    val title: String = "Your Guide",
    val summary: String = "Data ini datang dari dokumen Firestore.",
    val category: String = "General",
    val highlights: List<String> = emptyList(),
)
