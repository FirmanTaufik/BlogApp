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
import androidx.compose.ui.draw.clip
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
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.Favorite
import androidx.compose.material.icons.outlined.Save
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.tab.CurrentTab
import cafe.adriel.voyager.navigator.tab.LocalTabNavigator
import cafe.adriel.voyager.navigator.tab.Tab
import cafe.adriel.voyager.navigator.tab.TabNavigator
import cafe.adriel.voyager.navigator.tab.TabOptions
import coil3.compose.AsyncImage
import com.time.yourguideapp.AppColors
import com.time.yourguideapp.LocalMainViewModel
import com.time.yourguideapp.LocalRootNavigator
import com.time.yourguideapp.helper.glassmorphism
import com.time.yourguideapp.helper.rootBackground
import com.time.yourguideapp.model.Label
import com.time.yourguideapp.model.Posts
import com.time.yourguideapp.presentation.component.CustomItemBar
import com.time.yourguideapp.presentation.component.HorizontalSpacer
import com.time.yourguideapp.presentation.component.VerticalSpacer
import com.time.yourguideapp.presentation.home.HomeData
import com.time.yourguideapp.presentation.profile.ProfileScreen
import com.time.yourguideapp.presentation.search.SearchScreen
import com.time.yourguideapp.presentation.state.UIState
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.auth.auth
import kotlinx.coroutines.launch
import org.koin.core.instance.InstanceFactory

data object RootScreen : Screen {
    @Composable
    override fun Content() {
        val viewModel = LocalMainViewModel.current
        val state by viewModel.state.collectAsState()
        val posts = ((state as? UIState.Success<*>)?.data as? HomeData)?.posts.orEmpty()
        val labels = ((state as? UIState.Success<*>)?.data as? HomeData)?.labels.orEmpty()
        val currentUser by Firebase.auth.authStateChanged.collectAsState(Firebase.auth.currentUser)

        RootContent(
            initialTab = MainTab.Home,
            tabs = listOf(MainTab.Home, MainTab.Weather, MainTab.Explore, MainTab.Currency, MainTab.Loves),
            searchPosts = posts,
            labels = labels,
            userName = currentUser?.displayName ?: "Traveler",
            userEmail = currentUser?.email ?: currentUser?.uid.orEmpty(),
            userPhotoUrl = currentUser?.photoURL,
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
    searchPosts: List<Posts>,
    labels : List<Label>,
    userName: String,
    userEmail: String,
    userPhotoUrl: String?,
    content: @Composable () -> Unit,
) {

    var showDialogConfirmation by remember { mutableStateOf(false) }
    val rootNavigator = LocalRootNavigator.current
    val coroutineScope = rememberCoroutineScope()

    TabNavigator(initialTab) {
        Scaffold(
            topBar = {
                TopAppBar(
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color.Transparent,
                    ),
                    title = {
                        Column(verticalArrangement = Arrangement.Center) {
                            Text(
                                text = userName,
                                color = AppColors.blue123060,
                                style = MaterialTheme.typography.titleMedium,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                            )
                            Text(
                                text = userEmail,
                                color = AppColors.blue123060.copy(alpha = 0.8f),
                                style = MaterialTheme.typography.bodySmall,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                            )
                        }
                    },

                    navigationIcon = {

                        IconButton(
                            onClick = {
                                rootNavigator.push(ProfileScreen())
                            },
                            modifier = Modifier.glassmorphism(CircleShape, backgroundColor = Color.White.copy(alpha = 0.30f))
                        ) {
                            if (userPhotoUrl.isNullOrBlank()) {
                                Icon(
                                    imageVector = Icons.Outlined.AccountCircle,
                                    contentDescription = null,
                                    tint = AppColors.blue123060
                                )
                            } else {
                                AsyncImage(
                                    model = userPhotoUrl,
                                    contentDescription = null,
                                    modifier = Modifier
                                        .size(38.dp)
                                        .clip(CircleShape),
                                    contentScale = ContentScale.Crop,
                                )
                            }
                        }
                    },

                    actions = {
                        HorizontalSpacer(10)

                        IconButton(
                            onClick = {
                                rootNavigator.push(SearchScreen(searchPosts, labels = labels))
                            },
                            modifier = Modifier.glassmorphism(CircleShape,   backgroundColor = Color.White.copy(alpha = 0.30f))
                        ) {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = null,
                                tint = AppColors.blue123060
                            )
                        }

                        HorizontalSpacer(10)

                        IconButton(
                            onClick = {
                                showDialogConfirmation = true
                            },
                            modifier = Modifier.glassmorphism(CircleShape,   backgroundColor = Color.White.copy(alpha = 0.30f))
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.Logout,
                                contentDescription = null,
                                tint = AppColors.blue123060
                            )
                        }

                        HorizontalSpacer(10)

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

    if (showDialogConfirmation) {
        AlertDialog(
            modifier = Modifier.glassmorphism(),
            containerColor = Color.Transparent,
            onDismissRequest = {
            showDialogConfirmation = false
        }, title = {
            Text("Confirmation", color = AppColors.white)
        }, text = {
            Text(text = "Are you sure want to logout?", color = AppColors.white.copy(alpha = 0.8f))
        }, confirmButton = {
                Button(onClick = {
                    showDialogConfirmation = false
                    coroutineScope.launch {
                        Firebase.auth.signOut()
                    }
                }, modifier = Modifier.glassmorphism(),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent)){
                    Text("Yes")
                }
        }, dismissButton = {
            Button(onClick = {
                showDialogConfirmation = false
            }, modifier = Modifier.glassmorphism(),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent)){
                Text("No")
            }
        })
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
            CustomItemBar( "Loves", rememberVectorPainter(Icons.Outlined.Favorite),
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
            tabs = listOf(MainTab.Home, MainTab.Weather, MainTab.Explore, MainTab.Currency, MainTab.Loves),
            searchPosts = emptyList(),
            labels = emptyList(),
            userName = "Traveler",
            userEmail = "traveler@example.com",
            userPhotoUrl = null,
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
