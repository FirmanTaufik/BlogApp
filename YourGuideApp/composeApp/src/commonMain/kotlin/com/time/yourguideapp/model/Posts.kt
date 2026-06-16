package com.time.yourguideapp.model

import com.time.yourguideapp.helper.AppManager
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
    val locales : Map<String, LocaleData> = emptyMap(),
){

    fun getCurrentLocaleData(): LocaleData {
        return getLocaleData(AppManager.currentLanguage) ?: LocaleData()
    }

    fun hasCurrentLocaleContent(): Boolean {
        return hasLocaleContent(AppManager.currentLanguage)
    }

    fun hasLocaleContent(locale: String): Boolean {
        val data = getLocaleData(locale) ?: return false
        return data.title.isNotBlank() || data.content.isNotBlank()
    }

    private fun getLocaleData(locale: String): LocaleData? {
        return locales[locale.lowercase()]
    }

    @Serializable
    data class LocaleData(
        val title : String = "",
        val content : String = "",
    )
}
