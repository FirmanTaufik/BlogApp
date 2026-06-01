package com.time.yourguideapp

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import chaintech.videoplayer.util.PlaybackPreference
import com.google.android.gms.ads.MobileAds
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.time.yourguideapp.helper.AppLogger
import com.time.yourguideapp.helper.LanguageStorage
import com.time.yourguideapp.helper.AppManager
import com.time.yourguideapp.helper.UserProfileStorage
import java.util.UUID

class MainActivity : ComponentActivity() {
    private val mainHandler = Handler(Looper.getMainLooper())
    private val nativeAuthStateListener = FirebaseAuth.AuthStateListener { auth ->
        logNativeFirebaseAuth(
            label = "native authStateChanged",
            auth = auth,
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        PlaybackPreference.initialize(this)
        MobileAds.initialize(this)
        AppLogger.setUp(isDebug = BuildConfig.DEBUG)
        val firebaseApp = FirebaseApp.initializeApp(this)
        LanguageStorage.initialize(this)
        UserProfileStorage.initialize(this)
        AppManager.initializeLanguage()
        AppManager.initializeUserProfile()
        Log.d(
            "AuthStateNative",
            "firebaseAppInitialized=${firebaseApp != null} name=${firebaseApp?.name.orEmpty()}",
        )
        logAppStorageSentinel()
        logNativeFirebaseAuth("onCreate before setContent")
        FirebaseAuth.getInstance().addAuthStateListener(nativeAuthStateListener)
        logNativeFirebaseAuthDelayed("onCreate delayed 1s", delayMillis = 1_000L)
        logNativeFirebaseAuthDelayed("onCreate delayed 3s", delayMillis = 3_000L)
        logNativeFirebaseAuthDelayed("onCreate delayed 8s", delayMillis = 8_000L)
        setContent {
            App()
        }
    }

    override fun onStart() {
        super.onStart()
        logNativeFirebaseAuth("onStart")
    }

    override fun onResume() {
        super.onResume()
        logNativeFirebaseAuth("onResume")
        logNativeFirebaseAuthDelayed("onResume delayed 1s", delayMillis = 1_000L)
    }

    override fun onDestroy() {
        FirebaseAuth.getInstance().removeAuthStateListener(nativeAuthStateListener)
        super.onDestroy()
    }

    private fun logNativeFirebaseAuthDelayed(
        label: String,
        delayMillis: Long,
    ) {
        mainHandler.postDelayed(
            { logNativeFirebaseAuth(label) },
            delayMillis,
        )
    }

    private fun logNativeFirebaseAuth(
        label: String,
        auth: FirebaseAuth = FirebaseAuth.getInstance(),
    ) {
        val user = auth.currentUser
        Log.d(
            "AuthStateNative",
            "$label currentUser=" + if (user == null) {
                "null"
            } else {
                "uid=${user.uid} email=${user.email.orEmpty()} isAnonymous=${user.isAnonymous}"
            }
        )
    }

    private fun logAppStorageSentinel() {
        val preferences = getSharedPreferences("auth_debug_storage", MODE_PRIVATE)
        val installId = preferences.getString(KEY_INSTALL_ID, null)
            ?: UUID.randomUUID().toString().also { generatedId ->
                preferences.edit().putString(KEY_INSTALL_ID, generatedId).apply()
            }
        val launchCount = preferences.getInt(KEY_LAUNCH_COUNT, 0) + 1
        preferences.edit().putInt(KEY_LAUNCH_COUNT, launchCount).apply()

        Log.d(
            "AuthStateNative",
            "storage installId=$installId launchCount=$launchCount package=$packageName",
        )
    }

    private companion object {
        const val KEY_INSTALL_ID = "install_id"
        const val KEY_LAUNCH_COUNT = "launch_count"
    }
}

@Preview
@Composable
fun AppAndroidPreview() {
    App()
}
