package com.time.yourguideapp.core.platform

import androidx.compose.runtime.Composable
import platform.Foundation.NSBundle
import platform.UIKit.UIDevice

class IOSPlatform: Platform {
    override val name: String = UIDevice.currentDevice.systemName() + " " + UIDevice.currentDevice.systemVersion
}

actual fun getPlatform(): Platform = IOSPlatform()

@Composable
actual fun getAppName(): String {
    val mainBundle = NSBundle.mainBundle
     return mainBundle.objectForInfoDictionaryKey("CFBundleDisplayName") as? String
         ?: mainBundle.objectForInfoDictionaryKey("CFBundleName") as? String
         ?: "KMP App (iOS)"
}
