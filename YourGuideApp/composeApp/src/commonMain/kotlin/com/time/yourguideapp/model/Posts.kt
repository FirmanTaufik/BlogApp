package com.time.yourguideapp.model

import kotlinx.serialization.Serializable

@Serializable
data class Posts (
    val currentLocale : String = "id",
    var idPost : String = "",
    val contentFormat : String,
    val coverImageUrl : String,
    val createdAt : String,
    val defaultLocale : String,
    val imageName : String,
    val labelIds : List<String>,
    val updatedAt: String,
    val views: String,
    val locales : Locales

){

    fun getCurrentLocaleData(): Locales.LocalesData {
        return if (currentLocale == "id"){
            locales.id
        } else locales.en
    }

    @Serializable
    data class Locales (
       val en  : LocalesData,
       val id : LocalesData

    ) {

        @Serializable
        data class LocalesData(
            val title : String,
            val content : String,
        )
    }
}