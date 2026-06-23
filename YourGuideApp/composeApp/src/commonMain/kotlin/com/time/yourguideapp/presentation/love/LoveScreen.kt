package com.time.yourguideapp.presentation.love

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.navigator.Navigator
import com.time.yourguideapp.AppColors
import com.time.yourguideapp.model.Label
import com.time.yourguideapp.model.Posts
import com.time.yourguideapp.presentation.ads.AdMobInterstitialEffect
import com.time.yourguideapp.presentation.detail.DetailScreen
import com.time.yourguideapp.presentation.home.HomeData
import com.time.yourguideapp.presentation.home.PostItem
import com.time.yourguideapp.presentation.main.MainViewModel
import com.time.yourguideapp.presentation.state.UIState
import org.jetbrains.compose.resources.stringResource
import yourguideapp.composeapp.generated.resources.Res
import yourguideapp.composeapp.generated.resources.love_empty_body
import yourguideapp.composeapp.generated.resources.love_empty_title

@Composable
fun LoveScreen(
    lovedPosts: List<Posts>,
    labels: List<Label>,
    mainViewModel: MainViewModel,
    rootNavigator: Navigator,
    onToggleLove: (Posts) -> Unit,
    modifier: Modifier = Modifier,
) {

    val state by mainViewModel.state.collectAsState()
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

    if (lovedPosts.isEmpty()) {
        EmptyLoveContent(modifier = modifier)
    } else {
        LazyColumn(
            modifier = modifier.fillMaxSize(),
            contentPadding = PaddingValues(start = 10.dp, end = 10.dp, top = 10.dp, bottom = 112.dp),
        ) {
            items(lovedPosts) { post ->
                val label = post.labelIds
                    .firstOrNull()
                    ?.let { labelId -> labels.find { it.idLabel == labelId } }
                    ?.getCurrentLanguage()
                    .orEmpty()

                PostItem(
                    posts = post,
                    label = label,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 10.dp)
                        .clickable {
                            val interval = adMobConfig
                                ?.interstitialInterval
                                ?.takeIf { it > 0 }
                                ?: 0
                            val shouldShowInterstitial =
                                adMobConfig?.canShowInterstitial == true && interval > 0

                            if (!shouldShowInterstitial) {
                                openDetail(post, labels)
                            } else if (mainViewModel.shouldShowHomeDetailInterstitial(interval)) {
                                pendingDetail = post to labels
                                interstitialRequestKey += 1
                            } else {
                                openDetail(post, labels)
                            }

                        },
                    isLoved = true,
                    onToggleLove = { onToggleLove(post) },
                )
            }
        }
    }
}

@Composable
private fun EmptyLoveContent(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Icon(
                imageVector = Icons.Outlined.FavoriteBorder,
                contentDescription = null,
                tint = AppColors.blue123060,
            )
            Text(
                text = stringResource(Res.string.love_empty_title),
                color = AppColors.blue123060,
                fontWeight = FontWeight.Bold,
            )
            Text(
                text = stringResource(Res.string.love_empty_body),
                color = AppColors.blue123060.copy(alpha = 0.75f),
            )
        }
    }
}
