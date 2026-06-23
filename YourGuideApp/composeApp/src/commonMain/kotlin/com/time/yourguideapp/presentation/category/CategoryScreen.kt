package com.time.yourguideapp.presentation.category

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIos
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import cafe.adriel.voyager.core.screen.Screen
import com.time.yourguideapp.AppColors
import com.time.yourguideapp.helper.AppManager
import com.time.yourguideapp.LocalMainViewModel
import com.time.yourguideapp.helper.glassmorphism
import com.time.yourguideapp.helper.rootBackground
import com.time.yourguideapp.model.AdMobConfig
import com.time.yourguideapp.model.Label
import com.time.yourguideapp.model.Posts
import com.time.yourguideapp.presentation.ads.AdMobBanner
import com.time.yourguideapp.presentation.ads.AdMobInterstitialEffect
import com.time.yourguideapp.presentation.detail.DetailScreen
import com.time.yourguideapp.presentation.home.HomeData
import com.time.yourguideapp.presentation.home.PostItem
import com.time.yourguideapp.presentation.main.MainViewModel
import com.time.yourguideapp.presentation.state.UIState
import org.jetbrains.compose.resources.stringResource
import yourguideapp.composeapp.generated.resources.*
import org.koin.compose.viewmodel.koinViewModel

class CategoryScreen(
    val label: Label, val data: List<Posts>,
    val listLabel: List<Label>,
    val adMobConfig: AdMobConfig,
    val mainVM: MainViewModel,
    var interstitialRequestKey: Int,
    val onClickBack: () -> Unit,
    val onOpenDetail: (Posts, List<Label>) -> Unit
) : Screen {

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Content() {
        val viewModel = koinViewModel<CategoryViewModel>()
        val uiState by viewModel.uiState.collectAsStateWithLifecycle()
        val mainState by mainVM.state.collectAsState()
        var pendingDetail by remember { mutableStateOf<Pair<Posts, List<Label>>?>(null) }

        val bookmarkPostIds = ((mainState as? UIState.Success<*>)?.data as? HomeData)
            ?.bookmarkPostIds
            .orEmpty()
        val currentLanguage = AppManager.currentLanguage

        LaunchedEffect(Unit){
            viewModel.getListByLabel(label, data)
        }


        fun openDetail(data: Posts, labels: List<Label>) {
            onOpenDetail(data, labels)
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

        Scaffold(modifier = Modifier.fillMaxSize()
            .rootBackground(),
            containerColor = Color.Transparent,
            contentColor = Color.Transparent,
            topBar = {
                TopAppBar(
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color.Transparent,
                    ),
                    title = {
                        Text(
                            text = label.getCurrentLanguage(),
                            color = AppColors.blue123060,
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = {
                            onClickBack()
                        }, modifier = Modifier.glassmorphism(
                            shape = androidx.compose.foundation.shape.CircleShape,
                            backgroundColor = Color.White.copy(alpha = 0.30f),
                        )) {
                            Icon(
                                imageVector = Icons.Default.ArrowBackIos,
                                contentDescription = null,
                                tint = AppColors.blue123060,
                            )
                        }
                    }
                )
            }) {

            Column {
                Box(modifier = Modifier.weight(1f)
                    .fillMaxWidth()
                    .padding(it),
                    contentAlignment = Alignment.Center){
                    when (uiState){
                        is UIState.Success<*> -> {
                            val listPostByLabel = ((uiState as UIState.Success<*>).data as List<Posts>)
                                .filter { post -> post.hasLocaleContent(currentLanguage) }
                            LazyColumn(modifier = Modifier.fillMaxSize()) {
                                itemsIndexed(listPostByLabel){ index, item ->
                                    val labelName = label.getCurrentLanguage()
                                    PostItem(item, labelName, Modifier.fillMaxWidth()
                                        .padding(vertical = 10.dp)
                                        .clickable{
                                            val interval = adMobConfig
                                                ?.interstitialInterval
                                                ?.takeIf { it > 0 }
                                                ?: 0
                                            val shouldShowInterstitial =
                                                adMobConfig?.canShowInterstitial == true && interval > 0

                                            if (!shouldShowInterstitial) {
                                                onOpenDetail(item, listLabel)
                                            } else if (mainVM.shouldShowHomeDetailInterstitial(interval)) {
                                                pendingDetail = item to listLabel
                                                interstitialRequestKey += 1
                                            } else {
                                                onOpenDetail(item, listLabel)
                                            }

                                        },
                                        isLoved = bookmarkPostIds.contains(item.idPost),
                                        onToggleLove = { mainVM.toggleBookmark(item.idPost) },
                                    )
                                }
                            }
                        }

                        is UIState.Loading -> {
                            CircularProgressIndicator()
                        }
                        else -> {
                            Text(stringResource(Res.string.category_error))
                        }
                    }
                }
                if (adMobConfig.enabled) {
                    AdMobBanner(
                        modifier = Modifier.fillMaxWidth(),
                        adUnitId = adMobConfig.bannerAdUnitId,
                    )
                }
            }







        }
    }
}
