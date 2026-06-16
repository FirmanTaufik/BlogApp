package com.time.yourguideapp.presentation.home

import com.time.yourguideapp.model.AdMobConfig
import com.time.yourguideapp.model.HomeSliderConfig
import com.time.yourguideapp.model.Label
import com.time.yourguideapp.model.Locales
import com.time.yourguideapp.model.Posts

data class HomeData(
    val labels: List<Label>,
    val posts : List<Posts>,
    val locales : List<Locales>,
    val bookmarkPostIds: List<String> = emptyList(),
    val adMobConfig: AdMobConfig = AdMobConfig(),
    val homeSliderConfig: HomeSliderConfig = HomeSliderConfig(),
)
