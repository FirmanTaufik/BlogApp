package com.time.yourguideapp.presentation.component

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.time.yourguideapp.AppColors
import kotlinx.coroutines.delay
import kotlin.math.absoluteValue

@Composable
fun BannerSlider(
    images: List<String>
) {
    if (images.isEmpty()) return

    val pagerState = rememberPagerState {
        images.size
    }

    LaunchedEffect(pagerState, images.size) {
        if (images.size <= 1) return@LaunchedEffect

        while (true) {
            delay(3_500)
            val nextPage = (pagerState.currentPage + 1) % images.size
            pagerState.animateScrollToPage(nextPage)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(220.dp)
            .padding(horizontal = 16.dp)
            .clip(RoundedCornerShape(24.dp))
    ) {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize()
        ) { page ->
            val pageOffset = (
                (pagerState.currentPage - page) + pagerState.currentPageOffsetFraction
                ).absoluteValue
            val scale = animateFloatAsState(
                targetValue = 1f - (pageOffset.coerceIn(0f, 1f) * 0.08f),
                animationSpec = tween(durationMillis = 300, easing = LinearOutSlowInEasing),
                label = "bannerScale"
            )

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .scale(scale.value)
                    .alpha(1f - (pageOffset.coerceIn(0f, 1f) * 0.25f))
            ) {
                AsyncImage(
                    model = images[page],
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    Color.Transparent,
                                    Color.Black.copy(alpha = 0.35f)
                                )
                            )
                        )
                )
            }
        }

        BannerIndicator(
            pageCount = images.size,
            currentPage = pagerState.currentPage,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 14.dp)
        )
    }
}

@Composable
private fun BoxScope.BannerIndicator(
    pageCount: Int,
    currentPage: Int,
    modifier: Modifier = Modifier
) {
    if (pageCount <= 1) return

    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        repeat(pageCount) { index ->
            val selected = index == currentPage
            val indicatorWidth = animateDpAsState(
                targetValue = if (selected) 24.dp else 8.dp,
                animationSpec = tween(durationMillis = 300, easing = FastOutSlowInEasing),
                label = "indicatorWidth"
            )
            val indicatorColor = animateColorAsState(
                targetValue = if (selected) AppColors.white else AppColors.white.copy(alpha = 0.45f),
                animationSpec = tween(durationMillis = 300),
                label = "indicatorColor"
            )

            Box(
                modifier = Modifier
                    .height(8.dp)
                    .width(indicatorWidth.value)
                    .clip(CircleShape)
                    .background(indicatorColor.value)
            )
        }
    }
}
