package com.time.yourguideapp.presentation.ads

import android.util.Log
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.LoadAdError

@Composable
actual fun AdMobBanner(
    modifier: Modifier,
    adUnitId: String,
) {
    AndroidView(
        modifier = modifier.height(50.dp),
        factory = { context ->
            AdView(context).apply {
                setAdSize(AdSize.BANNER)
                this.adUnitId = adUnitId
                adListener = object : AdListener() {
                    override fun onAdLoaded() {
                        Log.d("AdMobBanner", "Banner loaded")
                    }

                    override fun onAdFailedToLoad(error: LoadAdError) {
                        Log.e(
                            "AdMobBanner",
                            "Banner failed to load: code=${error.code}, domain=${error.domain}, message=${error.message}",
                        )
                    }
                }
                loadAd(AdRequest.Builder().build())
            }
        },
    )
}
