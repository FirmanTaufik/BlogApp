package com.time.yourguideapp.helper

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ProvidedValue
import androidx.compose.runtime.key

expect object LocalAppLocale {
    val current: String

    @Composable
    infix fun provides(value: String?): ProvidedValue<*>
}

@Composable
fun ProvideAppLanguage(content: @Composable () -> Unit) {
    CompositionLocalProvider(
        LocalAppLocale provides AppManager.currentLanguage,
    ) {
        key(AppManager.currentLanguage) {
            content()
        }
    }
}
