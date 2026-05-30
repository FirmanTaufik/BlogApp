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
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.time.yourguideapp.AppColors
import kotlin.math.PI
import kotlin.math.atan2

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
    val routeProgress by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2600, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Restart,
        ),
        label = "routeProgress",
    )
    val floatOffset by transition.animateFloat(
        initialValue = -10f,
        targetValue = 10f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "floatOffset",
    )
    val sparkle by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1300, easing = LinearEasing),
            repeatMode = RepeatMode.Restart,
        ),
        label = "sparkle",
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
            TravelAnimation(
                rotation = rotation,
                pulse = pulse,
                routeProgress = routeProgress,
                floatOffset = floatOffset,
                sparkle = sparkle,
            )

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

@Composable
private fun TravelAnimation(
    rotation: Float,
    pulse: Float,
    routeProgress: Float,
    floatOffset: Float,
    sparkle: Float,
) {
    Box(
        modifier = Modifier.size(246.dp),
        contentAlignment = Alignment.Center,
    ) {
        Canvas(modifier = Modifier.size(246.dp)) {
            val center = Offset(size.width / 2f, size.height / 2f)
            val ringStroke = 7.dp.toPx()
            rotate(rotation, pivot = center) {
                drawArc(
                    color = AppColors.blue123060.copy(alpha = 0.16f),
                    startAngle = 0f,
                    sweepAngle = 360f,
                    useCenter = false,
                    style = Stroke(width = ringStroke),
                )
                drawArc(
                    color = AppColors.blue4789d7,
                    startAngle = -90f,
                    sweepAngle = 86f,
                    useCenter = false,
                    style = Stroke(width = ringStroke, cap = StrokeCap.Round),
                )
                drawCircle(
                    color = Color(0xFFFFC857),
                    radius = 6.dp.toPx(),
                    center = Offset(center.x, 10.dp.toPx()),
                )
            }

            val cardTopLeft = Offset(size.width * 0.16f, size.height * 0.22f)
            val cardWidth = size.width * 0.68f
            val cardHeight = size.height * 0.56f
            drawRoundRect(
                brush = Brush.verticalGradient(
                    colors = listOf(Color.White, Color(0xFFEAF6FF)),
                    startY = cardTopLeft.y,
                    endY = cardTopLeft.y + cardHeight,
                ),
                topLeft = cardTopLeft,
                size = Size(cardWidth, cardHeight),
                cornerRadius = CornerRadius(26.dp.toPx()),
            )
            drawRoundRect(
                color = AppColors.blue4789d7.copy(alpha = 0.12f),
                topLeft = cardTopLeft,
                size = Size(cardWidth, cardHeight),
                cornerRadius = CornerRadius(26.dp.toPx()),
                style = Stroke(width = 1.5.dp.toPx()),
            )

            val start = Offset(size.width * 0.28f, size.height * 0.66f)
            val midOne = Offset(size.width * 0.43f, size.height * 0.48f + floatOffset)
            val midTwo = Offset(size.width * 0.61f, size.height * 0.58f - floatOffset)
            val end = Offset(size.width * 0.74f, size.height * 0.38f)

            drawRouteProgress(
                points = listOf(start, midOne, midTwo, end),
                progress = routeProgress,
            )

            drawMapPin(start, Color(0xFF2CB67D), floatOffset * 0.15f)
            drawMapPin(end, Color(0xFFFF5A5F), -floatOffset * 0.22f)

            val planeState = pointOnRoute(listOf(start, midOne, midTwo, end), routeProgress)
            drawPlane(
                position = planeState.position,
                angleDegrees = planeState.angleDegrees,
                color = AppColors.blue123060,
            )

            drawSparkles(sparkle)
        }

        Box(
            modifier = Modifier
                .size(86.dp)
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
                fontSize = 28.sp,
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = 0.sp,
            )
        }
    }
}

private data class RoutePoint(
    val position: Offset,
    val angleDegrees: Float,
)

private fun DrawScope.drawRouteProgress(points: List<Offset>, progress: Float) {
    val strokeWidth = 4.dp.toPx()
    for (index in 0 until points.lastIndex) {
        drawLine(
            color = AppColors.blue123060.copy(alpha = 0.10f),
            start = points[index],
            end = points[index + 1],
            strokeWidth = strokeWidth,
            cap = StrokeCap.Round,
        )
    }

    val segmentProgress = progress * points.lastIndex
    for (index in 0 until points.lastIndex) {
        val localProgress = (segmentProgress - index).coerceIn(0f, 1f)
        if (localProgress > 0f) {
            drawLine(
                color = AppColors.blue4789d7,
                start = points[index],
                end = lerp(points[index], points[index + 1], localProgress),
                strokeWidth = strokeWidth,
                cap = StrokeCap.Round,
            )
        }
    }
}

private fun DrawScope.drawMapPin(center: Offset, color: Color, bounce: Float) {
    val pinCenter = center.copy(y = center.y + bounce)
    val radius = 11.dp.toPx()
    val pinPath = Path().apply {
        moveTo(pinCenter.x, pinCenter.y + radius * 2.1f)
        cubicTo(
            pinCenter.x - radius * 1.5f,
            pinCenter.y + radius * 0.65f,
            pinCenter.x - radius,
            pinCenter.y - radius * 1.2f,
            pinCenter.x,
            pinCenter.y - radius * 1.2f,
        )
        cubicTo(
            pinCenter.x + radius,
            pinCenter.y - radius * 1.2f,
            pinCenter.x + radius * 1.5f,
            pinCenter.y + radius * 0.65f,
            pinCenter.x,
            pinCenter.y + radius * 2.1f,
        )
        close()
    }
    drawCircle(
        color = color.copy(alpha = 0.16f),
        radius = radius * 1.6f,
        center = center.copy(y = center.y + radius * 1.7f),
    )
    drawPath(path = pinPath, color = color)
    drawCircle(color = Color.White, radius = radius * 0.38f, center = pinCenter)
}

private fun DrawScope.drawPlane(position: Offset, angleDegrees: Float, color: Color) {
    rotate(angleDegrees, pivot = position) {
        val scale = 1.1.dp.toPx()
        val plane = Path().apply {
            moveTo(position.x + 15f * scale, position.y)
            lineTo(position.x - 12f * scale, position.y - 9f * scale)
            lineTo(position.x - 5f * scale, position.y)
            lineTo(position.x - 12f * scale, position.y + 9f * scale)
            close()
        }
        drawCircle(color = Color.White, radius = 18.dp.toPx(), center = position)
        drawPath(path = plane, color = color)
        drawLine(
            color = AppColors.blue4789d7,
            start = Offset(position.x - 15f * scale, position.y),
            end = Offset(position.x - 28f * scale, position.y),
            strokeWidth = 2.dp.toPx(),
            cap = StrokeCap.Round,
        )
    }
}

private fun DrawScope.drawSparkles(progress: Float) {
    val sparklePoints = listOf(
        Offset(size.width * 0.29f, size.height * 0.30f),
        Offset(size.width * 0.74f, size.height * 0.28f),
        Offset(size.width * 0.77f, size.height * 0.72f),
    )
    sparklePoints.forEachIndexed { index, point ->
        val phase = ((progress + index * 0.28f) % 1f)
        val alpha = if (phase < 0.5f) phase * 2f else (1f - phase) * 2f
        val radius = (3.dp.toPx() + 5.dp.toPx() * phase)
        drawCircle(
            color = Color(0xFFFFC857).copy(alpha = alpha.coerceIn(0.12f, 0.9f)),
            radius = radius,
            center = point,
        )
    }
}

private fun pointOnRoute(points: List<Offset>, progress: Float): RoutePoint {
    val segmentProgress = progress.coerceIn(0f, 0.999f) * points.lastIndex
    val segment = segmentProgress.toInt().coerceIn(0, points.lastIndex - 1)
    val localProgress = segmentProgress - segment
    val start = points[segment]
    val end = points[segment + 1]
    val position = lerp(start, end, localProgress)
    val angle = atan2(end.y - start.y, end.x - start.x) * 180f / PI.toFloat()
    return RoutePoint(position = position, angleDegrees = angle)
}

private fun lerp(start: Offset, end: Offset, fraction: Float): Offset {
    val value = fraction.coerceIn(0f, 1f)
    return Offset(
        x = start.x + (end.x - start.x) * value,
        y = start.y + (end.y - start.y) * value,
    )
}
