package com.time.yourguideapp.helper

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ProvidedValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.InternalComposeUiApi
import platform.Foundation.NSUserDefaults

@OptIn(InternalComposeUiApi::class)
actual object LocalAppLocale {
    private const val LANG_KEY = "AppleLanguages"
    private val default = "en"
    private val LocalAppLocale = staticCompositionLocalOf { default }

    actual val current: String
        @Composable get() = LocalAppLocale.current

    @Composable
    actual infix fun provides(value: String?): ProvidedValue<*> {
        val newValue = value ?: default
        if (value == null) {
            NSUserDefaults.standardUserDefaults.removeObjectForKey(LANG_KEY)
        } else {
            NSUserDefaults.standardUserDefaults.setObject(arrayListOf(newValue), LANG_KEY)
        }

        return LocalAppLocale.provides(newValue)
    }
}
