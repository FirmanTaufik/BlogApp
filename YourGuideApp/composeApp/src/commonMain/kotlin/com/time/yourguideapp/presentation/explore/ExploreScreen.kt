package com.time.yourguideapp.presentation.explore

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.VerticalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.automirrored.filled.VolumeOff
import androidx.compose.material.icons.filled.ChatBubbleOutline
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import chaintech.videoplayer.host.MediaPlayerHost
import chaintech.videoplayer.model.VideoPlayerConfig
import chaintech.videoplayer.ui.youtube.YouTubePlayerComposable
import coil3.compose.AsyncImage

private data class ExploreVideo(
    val id: String,
    val youtubeId: String,
    val title: String,
    val location: String,
    val description: String,
    val likes: String,
    val comments: String,
)

private val dummyExploreVideos = listOf(
    ExploreVideo(
        id = "bali-beach",
        youtubeId = "QFxN2oDKk0E",
        title = "Bali Beach Walk",
        location = "Canggu, Bali",
        description = "Sunset route, beach clubs, and a quiet corner near the shore.",
        likes = "12.4K",
        comments = "348",
    ),
    ExploreVideo(
        id = "kyoto-street",
        youtubeId = "hZ1Rb9hC4JY",
        title = "Kyoto Street Notes",
        location = "Kyoto, Japan",
        description = "Narrow alleys, warm lights, and small stops for your next itinerary.",
        likes = "8.9K",
        comments = "192",
    ),
    ExploreVideo(
        id = "swiss-view",
        youtubeId = "linlz7-Pnvw",
        title = "Swiss Viewpoint",
        location = "Lauterbrunnen, Switzerland",
        description = "A quick look at the valley before the morning crowd arrives.",
        likes = "21K",
        comments = "571",
    ),
    ExploreVideo(
        id = "city-food",
        youtubeId = "M7lc1UVf-VE",
        title = "City Food Stop",
        location = "Night Market",
        description = "Fast bites worth saving before you move to the next district.",
        likes = "6.1K",
        comments = "84",
    ),
    ExploreVideo(
        id = "desert-road",
        youtubeId = "ScMzIvxBSi4",
        title = "Desert Road",
        location = "Open Route",
        description = "Long road energy with a few stops that make the drive worth it.",
        likes = "15.7K",
        comments = "421",
    ),
)

@Composable
fun ExploreScreen(modifier: Modifier = Modifier) {
    val pagerState = rememberPagerState {
        dummyExploreVideos.size
    }

    VerticalPager(
        state = pagerState,
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black),
    ) { page ->
        ExploreReelPage(
            video = dummyExploreVideos[page],
            isActive = page == pagerState.currentPage,
        )
    }
}

@Composable
private fun ExploreReelPage(
    video: ExploreVideo,
    isActive: Boolean,
) {
    val playerHost = remember(video.youtubeId) {
        MediaPlayerHost(
            mediaUrl = video.youtubeId,
            autoPlay = false,
            isMuted = false,
            isLooping = true,
        )
    }
    var isPaused by remember(video.youtubeId) { mutableStateOf(false) }

    LaunchedEffect(isActive) {
        if (isActive) {
            isPaused = false
            playerHost.play()
        } else {
            isPaused = true
            playerHost.pause()
        }
    }

    DisposableEffect(playerHost) {
        onDispose { playerHost.pause() }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .clickable {
                isPaused = !isPaused
                playerHost.togglePlayPause()
            },
    ) {
        if (isActive) {
            CroppedYoutubePlayer(
                playerHost = playerHost,
                modifier = Modifier.fillMaxSize(),
            )
        } else {
            AsyncImage(
                model = "https://img.youtube.com/vi/${video.youtubeId}/hqdefault.jpg",
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop,
            )
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.Black.copy(alpha = 0.12f),
                            Color.Transparent,
                            Color.Black.copy(alpha = 0.78f),
                        ),
                    ),
                ),
        )

        Row(
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(start = 16.dp, top = 16.dp)
                .clip(CircleShape)
                .background(Color.Black.copy(alpha = 0.38f))
                .padding(horizontal = 10.dp, vertical = 7.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.VolumeOff,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(16.dp),
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = "Muted",
                color = Color.White,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.SemiBold,
            )
        }

        ReelInfo(
            video = video,
            modifier = Modifier
                .align(Alignment.BottomStart)
                .fillMaxWidth()
                .padding(start = 16.dp, end = 88.dp, bottom = 118.dp),
        )

        ReelActions(
            likes = video.likes,
            comments = video.comments,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 16.dp, bottom = 118.dp),
        )

        if (isPaused) {
            Box(
                modifier = Modifier
                    .align(Alignment.Center)
                    .size(78.dp)
                    .clip(CircleShape)
                    .background(Color.Black.copy(alpha = 0.42f)),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = Icons.Default.PlayArrow,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(52.dp),
                )
            }
        }
    }
}

@Composable
private fun CroppedYoutubePlayer(
    playerHost: MediaPlayerHost,
    modifier: Modifier = Modifier,
) {
    BoxWithConstraints(modifier = modifier.clip(RoundedCornerShape(0.dp))) {
        val videoAspectRatio = 16f / 9f
        val containerAspectRatio = maxWidth / maxHeight
        val playerWidth = if (containerAspectRatio > videoAspectRatio) {
            maxWidth
        } else {
            maxHeight * videoAspectRatio
        }
        val playerHeight = if (containerAspectRatio > videoAspectRatio) {
            maxWidth / videoAspectRatio
        } else {
            maxHeight
        }

        YouTubePlayerComposable(
            modifier = Modifier
                .width(playerWidth)
                .height(playerHeight)
                .align(Alignment.Center),
            playerHost = playerHost,
            playerConfig = VideoPlayerConfig(
                showControls = false,
                isMuteControlEnabled = false,
                isFullScreenEnabled = false,
                isSpeedControlEnabled = false,
                isScreenLockEnabled = false,
                isFastForwardBackwardEnabled = false,
                isGestureVolumeControlEnabled = false,
                isZoomEnabled = false,
                autoPlayNextReel = false,
                enableResumePlayback = false,
            ),
        )
    }
}

@Composable
private fun ReelInfo(
    video: ExploreVideo,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.92f)),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = video.title.first().toString(),
                    color = Color.Black,
                    fontWeight = FontWeight.Bold,
                )
            }
            Spacer(modifier = Modifier.width(10.dp))
            Column {
                Text(
                    text = video.title,
                    color = Color.White,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    text = video.location,
                    color = Color.White.copy(alpha = 0.82f),
                    style = MaterialTheme.typography.bodySmall,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = video.description,
            color = Color.White,
            style = MaterialTheme.typography.bodyMedium,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

@Composable
private fun ReelActions(
    likes: String,
    comments: String,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(18.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        ReelActionButton(Icons.Default.FavoriteBorder, likes)
        ReelActionButton(Icons.Default.ChatBubbleOutline, comments)
        ReelActionButton(Icons.AutoMirrored.Filled.Send, "Share")
        ReelActionButton(Icons.Default.MoreHoriz, "")
    }
}

@Composable
private fun ReelActionButton(
    icon: ImageVector,
    label: String,
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(Color.Black.copy(alpha = 0.32f)),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(28.dp),
            )
        }

        if (label.isNotBlank()) {
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = label,
                color = Color.White,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
            )
        }
    }
}
