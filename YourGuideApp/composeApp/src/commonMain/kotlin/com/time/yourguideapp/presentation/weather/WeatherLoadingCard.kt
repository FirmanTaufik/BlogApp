package com.time.yourguideapp.presentation.weather

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.time.yourguideapp.AppColors
import com.time.yourguideapp.helper.glassmorphism
import com.time.yourguideapp.presentation.component.HorizontalSpacer
import com.time.yourguideapp.presentation.component.VerticalSpacer
import com.valentinilk.shimmer.ShimmerBounds
import com.valentinilk.shimmer.rememberShimmer
import com.valentinilk.shimmer.shimmer

@Composable
internal fun WeatherLoadingCard() {
    val shimmer = rememberShimmer(shimmerBounds = ShimmerBounds.View)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .glassmorphism(
                shape = RoundedCornerShape(28.dp),
                backgroundColor = Color.White.copy(alpha = 0.72f),
                borderColor = Color.White.copy(alpha = 0.90f),
            )
            .padding(18.dp)
            .shimmer(shimmer),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(68.dp)
                    .clip(CircleShape)
                    .background(AppColors.blueaad2fb.copy(alpha = 0.48f)),
            )
            HorizontalSpacer(14)
            Column(modifier = Modifier.weight(1f)) {
                LoadingBlock(
                    widthFraction = 0.62f,
                    height = 24.dp,
                )
                VerticalSpacer(8)
                LoadingBlock(
                    widthFraction = 0.42f,
                    height = 16.dp,
                )
            }
        }

        VerticalSpacer(22)

        LoadingBlock(
            widthFraction = 0.38f,
            height = 44.dp,
        )

        VerticalSpacer(16)

        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            WeatherMetricPlaceholder(modifier = Modifier.weight(1f))
            WeatherMetricPlaceholder(modifier = Modifier.weight(1f))
        }
    }
}

@Composable
private fun WeatherMetricPlaceholder(modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(18.dp))
            .background(Color.White.copy(alpha = 0.44f))
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(24.dp)
                .clip(CircleShape)
                .background(AppColors.blue123060.copy(alpha = 0.16f)),
        )
        HorizontalSpacer(8)
        Column(modifier = Modifier.weight(1f)) {
            LoadingBlock(
                widthFraction = 0.52f,
                height = 12.dp,
            )
            VerticalSpacer(6)
            LoadingBlock(
                widthFraction = 0.72f,
                height = 16.dp,
            )
        }
    }
}

@Composable
private fun LoadingBlock(
    widthFraction: Float,
    height: androidx.compose.ui.unit.Dp,
) {
    Box(
        modifier = Modifier
            .fillMaxWidth(widthFraction)
            .height(height)
            .clip(RoundedCornerShape(10.dp))
            .background(AppColors.blue123060.copy(alpha = 0.14f)),
    )
}
