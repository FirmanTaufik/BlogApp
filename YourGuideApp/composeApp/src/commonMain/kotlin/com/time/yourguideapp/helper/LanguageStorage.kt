package com.time.yourguideapp.helper

expect object LanguageStorage {
    fun initialize(context: Any? = null)
    fun loadLanguage(): String?
    fun saveLanguage(language: String)
}
