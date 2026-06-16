package com.time.yourguideapp.presentation.ads

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.appopen.AppOpenAd

@Composable
actual fun AppOpenAdEffect(
    adUnitId: String,
    enabled: Boolean,
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val appOpenAd = remember { mutableStateOf<AppOpenAd?>(null) }
    val isLoading = remember { mutableStateOf(false) }
    val isShowing = remember { mutableStateOf(false) }
    val lastShownAt = remember { mutableLongStateOf(0L) }
    lateinit var showAdIfAvailable: () -> Unit

    fun loadAd() {
        if (!enabled || adUnitId.isBlank() || isLoading.value || appOpenAd.value != null) {
            return
        }

        isLoading.value = true
        AppOpenAd.load(
            context,
            adUnitId,
            AdRequest.Builder().build(),
            object : AppOpenAd.AppOpenAdLoadCallback() {
                override fun onAdLoaded(ad: AppOpenAd) {
                    Log.d("AppOpenAd", "App open ad loaded")
                    appOpenAd.value = ad
                    isLoading.value = false
                    if (lifecycleOwner.lifecycle.currentState.isAtLeast(Lifecycle.State.STARTED)) {
                        showAdIfAvailable()
                    }
                }

                override fun onAdFailedToLoad(error: LoadAdError) {
                    Log.e(
                        "AppOpenAd",
                        "App open ad failed to load: code=${error.code}, domain=${error.domain}, message=${error.message}",
                    )
                    isLoading.value = false
                }
            },
        )
    }

    showAdIfAvailable = {
        val activity = context.findActivity()
        val ad = appOpenAd.value

        if (activity == null) {
            Unit
        } else if (!enabled || adUnitId.isBlank() || ad == null || isShowing.value) {
            loadAd()
        } else {
            val now = System.currentTimeMillis()
            if (now - lastShownAt.longValue >= MIN_APP_OPEN_INTERVAL_MS) {
                ad.fullScreenContentCallback = object : FullScreenContentCallback() {
                    override fun onAdDismissedFullScreenContent() {
                        appOpenAd.value = null
                        isShowing.value = false
                        loadAd()
                    }

                    override fun onAdFailedToShowFullScreenContent(error: com.google.android.gms.ads.AdError) {
                        Log.e("AppOpenAd", "App open ad failed to show: ${error.message}")
                        appOpenAd.value = null
                        isShowing.value = false
                        loadAd()
                    }

                    override fun onAdShowedFullScreenContent() {
                        lastShownAt.longValue = System.currentTimeMillis()
                        isShowing.value = true
                    }
                }
                ad.show(activity)
            }
        }
    }

    DisposableEffect(adUnitId, enabled, lifecycleOwner) {
        loadAd()
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_START) {
                showAdIfAvailable()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)

        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }
}

private const val MIN_APP_OPEN_INTERVAL_MS = 60_000L
