package com.time.yourguideapp.presentation.splash

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.time.yourguideapp.AppColors

@Composable
fun SplashScreen(modifier: Modifier = Modifier) {
    val transition = rememberInfiniteTransition(label = "splash")
    val pulse by transition.animateFloat(
        initialValue = 0.92f,
        targetValue = 1.08f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1100, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "pulse",
    )
    val rotation by transition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2800, easing = LinearEasing),
        ),
        label = "rotation",
    )
    val progress by transition.animateFloat(
        initialValue = 0.18f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1400, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "progress",
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        AppColors.blueaad2fb,
                        Color(0xFFEAF6FF),
                        Color.White,
                    ),
                ),
            ),
        contentAlignment = Alignment.Center,
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawCircle(
                color = AppColors.blue4789d7.copy(alpha = 0.16f),
                radius = size.minDimension * 0.32f,
                center = Offset(size.width * 0.18f, size.height * 0.18f),
            )
            drawCircle(
                color = Color(0xFFFFC857).copy(alpha = 0.18f),
                radius = size.minDimension * 0.26f,
                center = Offset(size.width * 0.86f, size.height * 0.72f),
            )
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Box(
                modifier = Modifier.size(174.dp),
                contentAlignment = Alignment.Center,
            ) {
                Canvas(
                    modifier = Modifier
                        .size(174.dp)
                        .rotate(rotation),
                ) {
                    val strokeWidth = 7.dp.toPx()
                    drawArc(
                        color = AppColors.blue123060.copy(alpha = 0.22f),
                        startAngle = 0f,
                        sweepAngle = 360f,
                        useCenter = false,
                        style = Stroke(width = strokeWidth),
                    )
                    drawArc(
                        color = AppColors.blue4789d7,
                        startAngle = -90f,
                        sweepAngle = 98f,
                        useCenter = false,
                        style = Stroke(width = strokeWidth, cap = StrokeCap.Round),
                    )
                    drawCircle(
                        color = Color(0xFFFFC857),
                        radius = 6.dp.toPx(),
                        center = Offset(size.width / 2f, 10.dp.toPx()),
                    )
                    drawCircle(
                        color = Color(0xFF2CB67D),
                        radius = 5.dp.toPx(),
                        center = Offset(size.width - 20.dp.toPx(), size.height / 2f),
                    )
                }

                Box(
                    modifier = Modifier
                        .size(112.dp)
                        .scale(pulse)
                        .clip(CircleShape)
                        .background(
                            Brush.radialGradient(
                                colors = listOf(
                                    Color.White,
                                    Color(0xFFE9F5FF),
                                ),
                            ),
                        ),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = "YG",
                        color = AppColors.blue123060,
                        fontSize = 34.sp,
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = 0.sp,
                    )
                }
            }

            Spacer(modifier = Modifier.height(22.dp))

            Text(
                text = "YourGuide",
                color = AppColors.blue123060,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Explore with confidence",
                color = AppColors.blue123060.copy(alpha = 0.72f),
                style = MaterialTheme.typography.bodyMedium,
            )
            Spacer(modifier = Modifier.height(28.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                repeat(4) { index ->
                    Box(
                        modifier = Modifier
                            .size(width = 34.dp, height = 5.dp)
                            .clip(CircleShape)
                            .alpha(if (progress * 4 > index) 1f else 0.28f)
                            .background(AppColors.blue4789d7),
                    )
                }
            }
        }
    }
}
