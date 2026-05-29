package com.time.yourguideapp.model

import com.time.yourguideapp.helper.AppManager
import kotlinx.serialization.Serializable

@Serializable
data class Label(
    var idLabel : String  = "",
    var imageUrl : String = "",
    var names :Language
){

    fun getCurrentLanguage(): String {
        return if (AppManager.currentLanguage== "id")
            names.id
        else names.en
    }

    @Serializable
    data class Language(
        var id : String = "",
        var en : String =""
    )

}
