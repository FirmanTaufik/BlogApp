package com.time.yourguideapp.core.greeting

import com.time.yourguideapp.core.platform.getPlatform

class Greeting {
    private val platform = getPlatform()

    fun greet(): String {
        return "Hello, ${platform.name}!"
    }
}
