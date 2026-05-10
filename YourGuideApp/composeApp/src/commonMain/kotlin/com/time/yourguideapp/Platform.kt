package com.time.yourguideapp

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform