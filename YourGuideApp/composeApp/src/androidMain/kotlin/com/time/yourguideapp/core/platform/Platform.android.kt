package com.time.yourguideapp.core.platform

import android.content.Intent
import android.os.Build
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import com.time.yourguideapp.BuildConfig

class AndroidPlatform : Platform {
    override val name: String = "Android ${Build.VERSION.SDK_INT}"
}

actual fun getPlatform(): Platform = AndroidPlatform()

@Composable
actual fun getAppName(): String {
    val context = LocalContext.current
    return context.applicationInfo.loadLabel(context.packageManager).toString()
}

actual fun getAppVersion(): String = BuildConfig.VERSION_NAME

@Composable
actual fun rememberShareAppLauncher(): (String) -> Unit {
    val context = LocalContext.current
    val chooserTitle = getAppName()
    return {
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, it)
        }
        context.startActivity(Intent.createChooser(intent, chooserTitle))
    }
}
