package com.time.yourguideapp.core.platform

import androidx.compose.runtime.Composable

interface Platform {
    val name: String
}

interface AppName {
    val name : String
}

expect fun getPlatform(): Platform

@Composable
expect fun getAppName(): String

expect fun getAppVersion(): String

@Composable
expect fun rememberShareAppLauncher(): (String) -> Unit

@Composable
expect fun rememberMapLauncher(): (String) -> Unit
