package com.time.yourguideapp.presentation.ads

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback

@Composable
actual fun AdMobInterstitialEffect(
    adUnitId: String,
    enabled: Boolean,
    requestKey: Int,
    onAdFinished: () -> Unit,
) {
    val context = LocalContext.current

    LaunchedEffect(adUnitId, enabled, requestKey) {
        Log.d("AdMobInterstitial", "AdMobInterstitialEffect: $adUnitId $enabled $requestKey")
        if (!enabled || adUnitId.isBlank() || requestKey <= 0) {
            return@LaunchedEffect
        }

        val activity = context.findActivity()
        if (activity == null) {
            onAdFinished()
            return@LaunchedEffect
        }

        InterstitialAd.load(
            context,
            adUnitId,
            AdRequest.Builder().build(),
            object : InterstitialAdLoadCallback() {
                override fun onAdLoaded(ad: InterstitialAd) {
                    Log.d("AdMobInterstitial", "Interstitial loaded")
                    ad.fullScreenContentCallback = object : FullScreenContentCallback() {
                        override fun onAdDismissedFullScreenContent() {
                            onAdFinished()
                        }

                        override fun onAdFailedToShowFullScreenContent(error: com.google.android.gms.ads.AdError) {
                            Log.e("AdMobInterstitial", "Interstitial failed to show: ${error.message}")
                            onAdFinished()
                        }
                    }
                    ad.show(activity)
                }

                override fun onAdFailedToLoad(error: LoadAdError) {
                    Log.e(
                        "AdMobInterstitial",
                            "Interstitial failed to load: code=${error.code}, domain=${error.domain}, message=${error.message}",
                    )
                    onAdFinished()
                }
            },
        )
    }
}

internal tailrec fun Context.findActivity(): Activity? {
    return when (this) {
        is Activity -> this
        is ContextWrapper -> baseContext.findActivity()
        else -> null
    }
}
