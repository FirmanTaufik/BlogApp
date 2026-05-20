package com.time.yourguideapp.core.platform

import android.os.Build
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import com.google.api.Context

class AndroidPlatform : Platform {
    override val name: String = "Android ${Build.VERSION.SDK_INT}"
}

actual fun getPlatform(): Platform = AndroidPlatform()

@Composable
actual fun getAppName(): String {
    val context = LocalContext.current
    return context.applicationInfo.loadLabel(context.packageManager).toString()
}