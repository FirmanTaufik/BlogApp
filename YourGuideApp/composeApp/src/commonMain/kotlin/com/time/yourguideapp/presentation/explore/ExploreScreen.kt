package com.time.yourguideapp.presentation.explore

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import chaintech.videoplayer.host.MediaPlayerHost
import chaintech.videoplayer.model.VideoPlayerConfig
import chaintech.videoplayer.ui.youtube.YouTubePlayerComposable
import com.time.yourguideapp.helper.rootBackground

private data class ExploreVideo(
    val id: String,
    val youtubeId: String,
    val height: Int,
)

private val dummyExploreVideos = listOf(
    ExploreVideo(id = "bali-beach", youtubeId = "QFxN2oDKk0E", height = 420),
    ExploreVideo(id = "kyoto-street", youtubeId = "hZ1Rb9hC4JY", height = 300),
    ExploreVideo(id = "swiss-view", youtubeId = "linlz7-Pnvw", height = 460),
    ExploreVideo(id = "city-food", youtubeId = "M7lc1UVf-VE", height = 340),
    ExploreVideo(id = "desert-road", youtubeId = "ScMzIvxBSi4", height = 390),
)

@Composable
fun ExploreScreen(modifier: Modifier = Modifier) {
    LazyVerticalStaggeredGrid(
        columns = StaggeredGridCells.Fixed(2),
        modifier = modifier
            .fillMaxSize()
            .rootBackground()
            .padding(horizontal = 10.dp),
        contentPadding = PaddingValues(top = 10.dp, bottom = 92.dp),
        verticalItemSpacing = 8.dp,
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        items(
            items = dummyExploreVideos,
            key = { it.id },
        ) { video ->
            ExploreVideoPreview(video = video)
        }
    }
}

@Composable
private fun ExploreVideoPreview(video: ExploreVideo) {
    val playerHost = remember(video.youtubeId) {
        MediaPlayerHost(mediaUrl = video.youtubeId)
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(video.height.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(Color.White.copy(alpha = 0.32f)),
    ) {
        BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
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
                playerConfig = VideoPlayerConfig(showControls = false),
            )
        }
    }
}
