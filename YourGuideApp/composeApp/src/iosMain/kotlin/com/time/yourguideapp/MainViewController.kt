package com.time.yourguideapp

import androidx.compose.ui.window.ComposeUIViewController
import com.time.yourguideapp.helper.AppLogger
import kotlin.experimental.ExperimentalNativeApi
import kotlin.native.Platform

@OptIn(ExperimentalNativeApi::class)
fun MainViewController() = ComposeUIViewController {
    AppLogger.setUp(isDebug = Platform.isDebugBinary)
    App()
}
