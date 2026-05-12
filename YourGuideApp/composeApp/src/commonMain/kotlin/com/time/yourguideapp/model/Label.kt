package com.time.yourguideapp.model

import kotlinx.serialization.Serializable

@Serializable
data class Label(
    var imageUrl : String = "",
    var names :Language
){
    @Serializable
    data class Language(
        var id : String = "",
        var en : String =""
    )
}
