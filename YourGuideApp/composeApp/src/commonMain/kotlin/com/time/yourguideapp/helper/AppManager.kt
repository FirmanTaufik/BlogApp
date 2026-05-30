package com.time.yourguideapp.helper

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

object AppManager {
    private const val DEFAULT_LANGUAGE = "en"

    private var initialized = false
    private var _currentLanguage by mutableStateOf(DEFAULT_LANGUAGE)

    var currentLanguage: String
        get() {
            ensureInitialized()
            return _currentLanguage
        }
        set(value) {
            ensureInitialized()
            _currentLanguage = value
            LanguageStorage.saveLanguage(value)
        }

    fun initializeLanguage() {
        if (initialized) return
        _currentLanguage = LanguageStorage.loadLanguage() ?: DEFAULT_LANGUAGE
        initialized = true
    }

    private fun ensureInitialized() {
        if (!initialized) {
            initializeLanguage()
        }
    }
}
