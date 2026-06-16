package com.time.yourguideapp.model

import kotlinx.serialization.Serializable

@Serializable
data class AdMobConfig(
    val enabled: Boolean = false,
    val showBanner: Boolean = true,
    val showInterstitial: Boolean = true,
    val showAppOpen: Boolean = true,
    val appId: String = "",
    val bannerAdUnitId: String = "",
    val interstitialAdUnitId: String = "",
    val interstitialInterval: Int = 3,
    val appOpenAdUnitId: String = "",
) {
    val canShowBanner: Boolean
        get() = enabled && showBanner && bannerAdUnitId.isNotBlank()

    val canShowInterstitial: Boolean
        get() = enabled && showInterstitial && interstitialAdUnitId.isNotBlank()

    val canShowAppOpen: Boolean
        get() = enabled && showAppOpen && appOpenAdUnitId.isNotBlank()
}
