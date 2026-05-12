package com.time.yourguideapp.core.platform

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform
