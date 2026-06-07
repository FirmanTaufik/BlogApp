package com.time.yourguideapp.core.platform

import android.content.Intent
import android.net.Uri
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

@Composable
actual fun rememberMapLauncher(): (String) -> Unit {
    val context = LocalContext.current
    return { query ->
        val encodedQuery = Uri.encode(query)
        val geoIntent = Intent(
            Intent.ACTION_VIEW,
            Uri.parse("geo:0,0?q=$encodedQuery"),
        )
        val webIntent = Intent(
            Intent.ACTION_VIEW,
            Uri.parse("https://www.google.com/maps/search/?api=1&query=$encodedQuery"),
        )

        runCatching {
            context.startActivity(geoIntent)
        }.getOrElse {
            context.startActivity(webIntent)
        }
    }
}

actual fun getPixabayApiKey(): String = BuildConfig.PIXABAY_API_KEY
