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