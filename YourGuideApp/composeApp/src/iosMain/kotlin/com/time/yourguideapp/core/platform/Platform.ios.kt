package com.time.yourguideapp.core.platform

import androidx.compose.runtime.Composable
import platform.Foundation.NSArray
import platform.Foundation.NSBundle
import platform.Foundation.NSString
import platform.Foundation.create
import platform.UIKit.UIActivityViewController
import platform.UIKit.UIApplication
import platform.UIKit.UIDevice
import platform.UIKit.UIViewController

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

actual fun getAppVersion(): String {
    val mainBundle = NSBundle.mainBundle
    return mainBundle.objectForInfoDictionaryKey("CFBundleShortVersionString") as? String
        ?: mainBundle.objectForInfoDictionaryKey("CFBundleVersion") as? String
        ?: "1.0"
}

@Composable
actual fun rememberShareAppLauncher(): (String) -> Unit {
    return { message ->
        val activityItems = listOf(message)
        val controller = UIActivityViewController(
            activityItems = activityItems,
            applicationActivities = null,
        )
        val rootViewController = UIApplication.sharedApplication.keyWindow?.rootViewController
        rootViewController?.presentViewController(controller, animated = true, completion = null)
    }
}
