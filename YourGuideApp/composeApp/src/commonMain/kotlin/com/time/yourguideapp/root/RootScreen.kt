package com.time.yourguideapp.root

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.CollectionsBookmark
import androidx.compose.material.icons.outlined.Save
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.tab.CurrentTab
import cafe.adriel.voyager.navigator.tab.LocalTabNavigator
import cafe.adriel.voyager.navigator.tab.Tab
import cafe.adriel.voyager.navigator.tab.TabNavigator
import cafe.adriel.voyager.navigator.tab.TabOptions
import com.time.yourguideapp.AppColors
import com.time.yourguideapp.helper.glassmorphism
import com.time.yourguideapp.helper.rootBackground
import com.time.yourguideapp.presentation.component.CustomItemBar
import com.time.yourguideapp.presentation.component.HorizontalSpacer
import com.time.yourguideapp.presentation.component.VerticalSpacer

data object RootScreen : Screen {
    @Composable
    override fun Content() {
        RootContent(
            initialTab = MainTab.Home,
            tabs = listOf(MainTab.Home, MainTab.Category, MainTab.Explore, MainTab.Loves),
        ) {
            CurrentTab()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun RootContent(
    initialTab: Tab,
    tabs: List<Tab>,
    content: @Composable () -> Unit,
) {
    TabNavigator(initialTab) {
        Scaffold(
            topBar = {
                TopAppBar(
                    colors = TopAppBarDefaults.topAppBarColors(

                        containerColor = Color.Transparent,

                    ),
                    title = {
                        Text("Home", color = AppColors.blue123060)
                    },

                    navigationIcon = {

                        IconButton(
                            onClick = {
                                // back
                            },
                            modifier = Modifier.glassmorphism(CircleShape, backgroundColor = Color.White.copy(alpha = 0.30f))
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.AccountCircle,
                                contentDescription = null,
                                tint = AppColors.blue123060
                            )
                        }
                    },

                    actions = {

                        IconButton(
                            onClick = {
                                // search
                            },
                            modifier = Modifier.glassmorphism(CircleShape,   backgroundColor = Color.White.copy(alpha = 0.30f))
                        ) {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = null,
                                tint = AppColors.blue123060
                            )
                        }
                    }
                )
            }
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .rootBackground()
                    // .safeContentPadding()
                    .padding(innerPadding)
                    .fillMaxSize(),
            ) {
                content()
               Box(modifier = Modifier.wrapContentSize()
                   .align(alignment = Alignment.BottomCenter),){
                   MainNavigationBar(tabs = tabs)
               }
            }
        }
    }
}

@Composable
private fun MainNavigationBar(tabs: List<Tab>) {
    val tabNavigator = LocalTabNavigator.current

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth()
    ) {

        Row (verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween){
            HorizontalSpacer(15)
            LazyRow(
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.wrapContentWidth()
                    .glassmorphism(CircleShape, widthBorder = 1, backgroundColor = Color.White.copy(alpha = 0.80f) ),
                contentPadding = PaddingValues(5.dp)
            ) {
                itemsIndexed(tabs) { index, item ->
                    if (item.options.icon != null) {
                        if (index!= tabs.size-1) {
                            CustomItemBar(item.options.title, item.options.icon!!) {
                                tabNavigator.current = tabs[index]
                            }

                        }

                    }
                }

            }

            Spacer(modifier = Modifier.weight(1f))
            CustomItemBar( "Loves", rememberVectorPainter(Icons.Outlined.CollectionsBookmark),
                iconSize = 40) {
                tabNavigator.current = tabs[tabs.size-1]
            }
            HorizontalSpacer(15)
        }


        VerticalSpacer(50)

    }
}

@Composable
@Preview(showBackground = true, showSystemUi = true)
private fun RootScreenPreview() {
    MaterialTheme {
        RootContent(
            initialTab = MainTab.Home,
            tabs = listOf(MainTab.Home, MainTab.Category, MainTab.Explore, MainTab.Loves),
        ) {
            Text("Home")
        }
    }
}

private sealed class PreviewTab(
    private val index: UShort,
    private val title: String,
) : Tab {
    override val options: TabOptions
        @Composable
        get() {
            val icon = rememberVectorPainter(
                image = when (this) {
                    Home -> Icons.Default.Home
                    Profile -> Icons.Default.AccountCircle
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

    data object Home : PreviewTab(index = 0u, title = "Home") {
        @Composable
        override fun Content() = Unit
    }

    data object Profile : PreviewTab(index = 1u, title = "Profile") {
        @Composable
        override fun Content() = Unit
    }
}
