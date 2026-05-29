package com.time.yourguideapp.presentation.currency

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
internal fun CurrencyLoadingCard() {
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
                    .size(58.dp)
                    .clip(CircleShape)
                    .background(AppColors.blueaad2fb.copy(alpha = 0.48f)),
            )
            HorizontalSpacer(14)
            Column(modifier = Modifier.weight(1f)) {
                LoadingBlock(widthFraction = 0.60f, height = 20.dp)
                VerticalSpacer(8)
                LoadingBlock(widthFraction = 0.42f, height = 14.dp)
            }
        }

        VerticalSpacer(18)

        LoadingBlock(widthFraction = 0.50f, height = 34.dp)

        VerticalSpacer(18)

        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            repeat(4) {
                RateRowPlaceholder()
            }
        }
    }
}

@Composable
private fun RateRowPlaceholder() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(Color.White.copy(alpha = 0.44f))
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(42.dp)
                .clip(CircleShape)
                .background(AppColors.blue123060.copy(alpha = 0.14f)),
        )
        HorizontalSpacer(12)
        Column(modifier = Modifier.weight(1f)) {
            LoadingBlock(widthFraction = 0.54f, height = 14.dp)
            VerticalSpacer(8)
            LoadingBlock(widthFraction = 0.36f, height = 12.dp)
        }
        HorizontalSpacer(12)
        LoadingBlock(widthFraction = 0.24f, height = 16.dp)
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
