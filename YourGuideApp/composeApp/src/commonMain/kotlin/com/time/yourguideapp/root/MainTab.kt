package com.time.yourguideapp.root

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Explore
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Favorite
import androidx.compose.material.icons.outlined.AttachMoney
import androidx.compose.material.icons.outlined.TravelExplore
import androidx.compose.material.icons.outlined.WbSunny
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import cafe.adriel.voyager.navigator.tab.Tab
import cafe.adriel.voyager.navigator.tab.TabOptions
import org.jetbrains.compose.resources.stringResource
import com.time.yourguideapp.LocalMainViewModel
import com.time.yourguideapp.LocalRootNavigator
import com.time.yourguideapp.presentation.category.CategoryScreen
import com.time.yourguideapp.presentation.ads.AdMobInterstitialEffect
import com.time.yourguideapp.presentation.detail.DetailScreen
import com.time.yourguideapp.presentation.explore.ExploreScreen
import com.time.yourguideapp.presentation.home.HomeData
import com.time.yourguideapp.presentation.home.HomeScreen
import com.time.yourguideapp.presentation.love.LoveScreen
import com.time.yourguideapp.presentation.love.PopularPlacesScreen
import com.time.yourguideapp.presentation.currency.CurrencyScreen
import com.time.yourguideapp.presentation.state.UIState
import com.time.yourguideapp.presentation.weather.WeatherScreen
import com.time.yourguideapp.model.Label
import com.time.yourguideapp.model.Posts
import yourguideapp.composeapp.generated.resources.*

sealed class MainTab(
    private val index: UShort,
    private val title: String,
) : Tab {
    override val options: TabOptions
        @Composable
        get() {
            val icon = rememberVectorPainter(
                image = when (this) {
                    Home -> Icons.Outlined.Home
                    Explore -> Icons.Outlined.Explore
                    Weather -> Icons.Outlined.WbSunny
                    Currency -> Icons.Outlined.AttachMoney
                    PopularPlaces -> Icons.Outlined.TravelExplore
                    else -> Icons.Outlined.Favorite
                }
            )
            val tabTitle = when (this) {
                Home -> stringResource(Res.string.nav_home)
                Explore -> stringResource(Res.string.nav_explore)
                Weather -> stringResource(Res.string.nav_weather)
                Currency -> stringResource(Res.string.nav_currency)
                PopularPlaces -> stringResource(Res.string.nav_popular_places)
                else -> stringResource(Res.string.nav_loves)
            }

            return remember(tabTitle, icon) {
                TabOptions(
                    index = index,
                    title = tabTitle,
                    icon = icon,
                )
            }
        }

    data object Home : MainTab(index = 0u, title = "Home") {
        @Composable
        override fun Content() {
            val viewModel = LocalMainViewModel.current
            val rootNavigator = LocalRootNavigator.current
            val state by viewModel.state.collectAsState()
            val homeData = (state as? UIState.Success<*>)?.data as? HomeData
            val adMobConfig = homeData?.adMobConfig
            var interstitialRequestKey by rememberSaveable { mutableIntStateOf(0) }
            var pendingDetail by remember { mutableStateOf<Pair<Posts, List<Label>>?>(null) }

            fun openDetail(data: Posts, labels: List<Label>) {
                rootNavigator.push(DetailScreen(data, labels))
            }

            AdMobInterstitialEffect(
                adUnitId = adMobConfig?.interstitialAdUnitId.orEmpty(),
                enabled = pendingDetail != null && adMobConfig?.canShowInterstitial == true,
                requestKey = interstitialRequestKey,
                onAdFinished = {
                    val detail = pendingDetail ?: return@AdMobInterstitialEffect
                    pendingDetail = null
                    openDetail(detail.first, detail.second)
                },
            )

            HomeScreen(
                state = state,
                onReload = viewModel::refresh,
                onOpenDetail = { data, lables ->
                    val interval = adMobConfig
                        ?.interstitialInterval
                        ?.takeIf { it > 0 }
                        ?: 0
                    val shouldShowInterstitial =
                        adMobConfig?.canShowInterstitial == true && interval > 0

                    if (!shouldShowInterstitial) {
                        openDetail(data, lables)
                    } else if (viewModel.shouldShowHomeDetailInterstitial(interval)) {
                        pendingDetail = data to lables
                        interstitialRequestKey += 1
                    } else {
                        openDetail(data, lables)
                    }
                },
                onOpenCategory = { label, list , listLabel->

                    rootNavigator.push(CategoryScreen(label, list,
                        listLabel ,
                        onClickBack = {
                        rootNavigator.pop()

                    }, onOpenDetail = { data, labels ->
                        rootNavigator.push(DetailScreen(data, labels))
                    }))
                },
                onToggleLove = { post ->
                    viewModel.toggleBookmark(post.idPost)
                },
            )
        }
    }

    data object Explore : MainTab(index = 1u, title = "Explore") {
        @Composable
        override fun Content() {
            ExploreScreen()
        }
    }

    data object Weather: MainTab(index = 2u, title = "Weather"){
        @Composable
        override fun Content() {
             WeatherScreen()
        }
    }

    data object Currency: MainTab(index = 3u, title = "Dollar") {
        @Composable
        override fun Content() {
            CurrencyScreen()
        }
    }

    data object PopularPlaces: MainTab(index = 4u, title = "Popular") {
        @Composable
        override fun Content() {
            PopularPlacesScreen()
        }
    }

    data object Loves: MainTab(index = 5u, title = "Loves"){
        @Composable
        override fun Content() {
            val viewModel = LocalMainViewModel.current
            val rootNavigator = LocalRootNavigator.current
            val state by viewModel.state.collectAsState()
            val homeData = (state as? UIState.Success<*>)?.data as? HomeData
            val currentLanguage = com.time.yourguideapp.helper.AppManager.currentLanguage
            val labels = homeData?.labels.orEmpty()
            val lovedPosts = homeData?.posts
                ?.filter { post ->
                    homeData.bookmarkPostIds.contains(post.idPost) &&
                        post.hasLocaleContent(currentLanguage)
                }
                .orEmpty()

            LoveScreen(
                lovedPosts = lovedPosts,
                labels = labels,
                rootNavigator = rootNavigator,
                onToggleLove = { post ->
                    viewModel.toggleBookmark(post.idPost)
                },
            )
        }

    }

}
