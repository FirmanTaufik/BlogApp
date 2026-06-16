package com.time.yourguideapp.presentation.ads

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier

@Composable
actual fun AdMobBanner(
    modifier: Modifier,
    adUnitId: String,
) = Unit

@Composable
actual fun AdMobInterstitialEffect(
    adUnitId: String,
    enabled: Boolean,
    requestKey: Int,
    onAdFinished: () -> Unit,
) {
    LaunchedEffect(enabled, requestKey) {
        if (enabled && requestKey > 0) {
            onAdFinished()
        }
    }
}

@Composable
actual fun AppOpenAdEffect(
    adUnitId: String,
    enabled: Boolean,
) = Unit
