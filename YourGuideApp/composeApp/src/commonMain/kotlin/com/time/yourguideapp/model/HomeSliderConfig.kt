package com.time.yourguideapp.model

import kotlinx.serialization.Serializable

@Serializable
data class HomeSliderConfig(
    val images: List<String> = emptyList(),
)
