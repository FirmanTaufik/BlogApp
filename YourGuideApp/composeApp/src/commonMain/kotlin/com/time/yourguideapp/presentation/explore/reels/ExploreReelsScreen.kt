package com.time.yourguideapp.presentation.explore.reels

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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.automirrored.filled.VolumeOff
import androidx.compose.material.icons.filled.ChatBubbleOutline
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
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
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import chaintech.videoplayer.host.MediaPlayerHost
import chaintech.videoplayer.model.VideoPlayerConfig
import chaintech.videoplayer.ui.video.VideoPlayerComposable
import coil3.compose.AsyncImage
import com.time.yourguideapp.presentation.component.VerticalSpacer
import com.time.yourguideapp.presentation.explore.ExploreVideo

data class ExploreReelsScreen(
    val videos: List<ExploreVideo>,
    val initialPage: Int,
) : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow

        ExploreReelsPager(
            videos = videos,
            initialPage = initialPage,
            modifier = Modifier.fillMaxSize(),
            onBack = { navigator.pop() },
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ExploreReelsPager(
    videos: List<ExploreVideo>,
    initialPage: Int,
    modifier: Modifier = Modifier,
    onBack: () -> Unit,
) {
    var commentVideo by remember { mutableStateOf<ExploreVideo?>(null) }
    val commentSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val pagerState = rememberPagerState(initialPage = initialPage) {
            videos.size
    }
    VerticalPager(
        state = pagerState,
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black),
    ) { page ->
        Box(modifier = Modifier.fillMaxSize()) {
            ExploreReelPage(
                video = videos[page],
                isActive = page == pagerState.currentPage,
                onOpenComments = {
                    commentVideo = videos[page]
                },
            )
            Column(modifier = Modifier.fillMaxSize()) {
                VerticalSpacer(20)
                Box(
                    modifier = Modifier
                        .padding(start = 16.dp, top = 16.dp)
                        .size(42.dp)
                        .clip(CircleShape)
                        .background(Color.Black.copy(alpha = 0.42f))
                        .clickable(onClick = onBack),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(24.dp),
                    )
                }
            }
        }
    }

    commentVideo?.let { video ->
        ModalBottomSheet(
            onDismissRequest = { commentVideo = null },
            sheetState = commentSheetState,
            containerColor = Color.White,
        ) {
            CommentsSheetContent(video = video)
        }
    }
}

@Composable
private fun ExploreReelPage(
    video: ExploreVideo,
    isActive: Boolean,
    onOpenComments: () -> Unit,
) {
    val playerHost = remember(video.videoUrl) {
        MediaPlayerHost(
            mediaUrl = video.videoUrl,
            autoPlay = false,
            isMuted = false,
            isLooping = true,
        )
    }
    var isPaused by remember(video.videoUrl) { mutableStateOf(false) }

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
            CroppedVideoPlayer(
                playerHost = playerHost,
                modifier = Modifier.fillMaxSize(),
            )
        } else {
            AsyncImage(
                model = video.thumbnailUrl,
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
                .align(Alignment.TopEnd)
                .padding(end = 16.dp, top = 46.dp)
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
            views = video.views,
            downloads = video.downloads,
            onOpenComments = onOpenComments,
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
private fun CroppedVideoPlayer(
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

        VideoPlayerComposable(
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
                if (video.creatorAvatarUrl.isBlank()) {
                    Text(
                        text = video.creatorName.firstOrNull()?.toString().orEmpty(),
                        color = Color.Black,
                        fontWeight = FontWeight.Bold,
                    )
                } else {
                    AsyncImage(
                        model = video.creatorAvatarUrl,
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop,
                    )
                }
            }
            Spacer(modifier = Modifier.width(10.dp))
            Column {
                Text(
                    text = video.creatorName,
                    color = Color.White,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    text = listOfNotNull(
                        video.duration.takeIf { it.isNotBlank() },
                        video.views.takeIf { it.isNotBlank() }?.let { "$it views" },
                    ).joinToString(" • "),
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
            maxLines = 3,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

@Composable
private fun ReelActions(
    likes: String,
    comments: String,
    views: String,
    downloads: String,
    onOpenComments: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(18.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        ReelActionButton(Icons.Default.FavoriteBorder, likes)
        ReelActionButton(
            icon = Icons.Default.ChatBubbleOutline,
            label = comments,
            onClick = onOpenComments,
        )
        ReelActionButton(Icons.Default.PlayArrow, views)
        ReelActionButton(Icons.Default.MoreHoriz, downloads)
        ReelActionButton(Icons.AutoMirrored.Filled.Send, "Share")
    }
}

@Composable
private fun CommentsSheetContent(video: ExploreVideo) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 20.dp, end = 20.dp, bottom = 28.dp),
    ) {
        Text(
            text = "Comments",
            color = Color.Black,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "${video.comments.ifBlank { "0" }} comments on Pixabay",
            color = Color.Black.copy(alpha = 0.62f),
            style = MaterialTheme.typography.bodyMedium,
        )

        Spacer(modifier = Modifier.height(18.dp))

        CommentSheetRow(
            avatarText = video.creatorName.firstOrNull()?.toString().orEmpty(),
            avatarUrl = video.creatorAvatarUrl,
            author = video.creatorName,
            body = "Video tags: ${video.tags.ifBlank { video.title }}",
        )

        Spacer(modifier = Modifier.height(14.dp))

        CommentSheetRow(
            avatarText = "P",
            avatarUrl = "",
            author = "Pixabay API",
            body = "Pixabay menyediakan jumlah komentar, tetapi tidak menyediakan isi daftar komentar pada response videos API.",
        )
    }
}

@Composable
private fun CommentSheetRow(
    avatarText: String,
    avatarUrl: String,
    author: String,
    body: String,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top,
    ) {
        Box(
            modifier = Modifier
                .size(42.dp)
                .clip(CircleShape)
                .background(Color(0xFFEAF1FB)),
            contentAlignment = Alignment.Center,
        ) {
            if (avatarUrl.isBlank()) {
                Text(
                    text = avatarText,
                    color = Color.Black,
                    fontWeight = FontWeight.Bold,
                )
            } else {
                AsyncImage(
                    model = avatarUrl,
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop,
                )
            }
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = author,
                color = Color.Black,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Spacer(modifier = Modifier.height(3.dp))
            Text(
                text = body,
                color = Color.Black.copy(alpha = 0.74f),
                style = MaterialTheme.typography.bodyMedium,
            )
        }
    }
}

@Composable
private fun ReelActionButton(
    icon: ImageVector,
    label: String,
    onClick: (() -> Unit)? = null,
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(Color.Black.copy(alpha = 0.32f))
                .clickable(enabled = onClick != null) { onClick?.invoke() },
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
