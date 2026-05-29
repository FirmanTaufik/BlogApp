package com.time.yourguideapp.root

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Explore
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Favorite
import androidx.compose.material.icons.outlined.WbSunny
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import cafe.adriel.voyager.navigator.tab.Tab
import cafe.adriel.voyager.navigator.tab.TabOptions
import com.time.yourguideapp.LocalMainViewModel
import com.time.yourguideapp.LocalRootNavigator
import com.time.yourguideapp.presentation.category.CategoryScreen
import com.time.yourguideapp.presentation.detail.DetailScreen
import com.time.yourguideapp.presentation.explore.ExploreScreen
import com.time.yourguideapp.presentation.home.HomeData
import com.time.yourguideapp.presentation.home.HomeScreen
import com.time.yourguideapp.presentation.love.LoveScreen
import com.time.yourguideapp.presentation.state.UIState
import com.time.yourguideapp.presentation.weather.WeatherScreen

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
                    else -> Icons.Outlined.Favorite
                }
            )

            return remember {
                TabOptions(
                    index = index,
                    title = title,
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

            HomeScreen(
                state = state,
                onReload = viewModel::refresh,
                onOpenDetail = { data, lables ->
                    rootNavigator.push(DetailScreen(data, lables))
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


    data object Loves: MainTab(index = 3u, title = "Loves"){
        @Composable
        override fun Content() {
            val viewModel = LocalMainViewModel.current
            val rootNavigator = LocalRootNavigator.current
            val state by viewModel.state.collectAsState()
            val homeData = (state as? UIState.Success<*>)?.data as? HomeData
            val labels = homeData?.labels.orEmpty()
            val lovedPosts = homeData?.posts
                ?.filter { post -> homeData.bookmarkPostIds.contains(post.idPost) }
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
