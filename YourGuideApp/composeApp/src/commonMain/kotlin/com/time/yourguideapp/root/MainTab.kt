package com.time.yourguideapp.root

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.Explore
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Menu
import androidx.compose.material.icons.outlined.Save
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
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
import com.time.yourguideapp.presentation.detail.DetailScreen
import com.time.yourguideapp.presentation.home.HomeScreen

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
                    Profile -> Icons.Outlined.Explore
                    Category -> Icons.Outlined.Menu
                    else -> Icons.Outlined.Save
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
                onOpenDetail = { text ->
                    rootNavigator.push(DetailScreen(text))
                },
            )
        }
    }

    data object Profile : MainTab(index = 1u, title = "Profile") {
        @Composable
        override fun Content() {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
            ) {
                Text(
                    text = "Profile",
                    style = MaterialTheme.typography.headlineSmall,
                )
            }
        }
    }

    data object Category: MainTab(index = 2u, title = "Category"){
        @Composable
        override fun Content() {
             Text("Category")
        }
    }


    data object Loves: MainTab(index = 3u, title = "Loves"){
        @Composable
        override fun Content() {
            Text("Loves")
        }

    }

}
