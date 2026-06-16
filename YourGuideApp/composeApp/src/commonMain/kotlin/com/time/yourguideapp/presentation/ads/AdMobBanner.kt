package com.time.yourguideapp.presentation.ads

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
expect fun AdMobBanner(
    modifier: Modifier,
    adUnitId: String,
)

@Composable
expect fun AdMobInterstitialEffect(
    adUnitId: String,
    enabled: Boolean,
    requestKey: Int,
    onAdFinished: () -> Unit,
)

@Composable
expect fun AppOpenAdEffect(
    adUnitId: String,
    enabled: Boolean,
)
