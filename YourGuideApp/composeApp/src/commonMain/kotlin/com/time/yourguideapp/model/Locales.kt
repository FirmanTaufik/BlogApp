package com.time.yourguideapp.model

import kotlinx.serialization.Serializable

@Serializable
data class Locales(
    var idLocales : String = "",
    val code : String,
    val createdAt: String,
    val name : String,
    val updatedAt : String
)