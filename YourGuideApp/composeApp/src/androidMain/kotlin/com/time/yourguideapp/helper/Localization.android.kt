package com.time.yourguideapp.helper

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ProvidedValue
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import java.util.Locale

actual object LocalAppLocale {
    private var default: Locale? = null

    actual val current: String
        @Composable get() = Locale.getDefault().toString()

    @Composable
    actual infix fun provides(value: String?): ProvidedValue<*> {
        val configuration = LocalConfiguration.current

        if (default == null) {
            default = Locale.getDefault()
        }

        val newLocale = when (value) {
            null -> default!!
            else -> Locale(value)
        }

        Locale.setDefault(newLocale)
        configuration.setLocale(newLocale)
        val resources = LocalContext.current.resources
        resources.updateConfiguration(configuration, resources.displayMetrics)

        return LocalConfiguration.provides(configuration)
    }
}
