package com.time.yourguideapp.presentation.ads

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

const val ANDROID_BANNER_AD_UNIT_ID = "ca-app-pub-3940256099942544/6300978111"
const val IOS_BANNER_AD_UNIT_ID = "ca-app-pub-3940256099942544/2435281174"
const val TEST_BANNER_AD_UNIT_ID = ANDROID_BANNER_AD_UNIT_ID

fun iosBannerAdUnitId(): String = IOS_BANNER_AD_UNIT_ID

@Composable
expect fun AdMobBanner(
    modifier: Modifier,
    adUnitId: String,
)
