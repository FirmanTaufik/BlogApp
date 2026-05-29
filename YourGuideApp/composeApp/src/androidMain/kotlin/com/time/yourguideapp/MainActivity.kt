package com.time.yourguideapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import chaintech.videoplayer.util.PlaybackPreference
import com.google.android.gms.ads.MobileAds
import com.time.yourguideapp.helper.AppLogger
import com.time.yourguideapp.helper.LanguageStorage
import com.time.yourguideapp.helper.AppManager

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        PlaybackPreference.initialize(this)
        MobileAds.initialize(this)
        AppLogger.setUp(isDebug = BuildConfig.DEBUG)
        LanguageStorage.initialize(this)
        AppManager.initializeLanguage()
        setContent {
            App()
        }
    }
}

@Preview
@Composable
fun AppAndroidPreview() {
    App()
}
