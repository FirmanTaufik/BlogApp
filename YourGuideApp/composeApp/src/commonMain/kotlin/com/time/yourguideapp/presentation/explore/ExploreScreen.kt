package com.time.yourguideapp.presentation.explore

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.time.yourguideapp.LocalRootNavigator
import com.time.yourguideapp.helper.rootBackground
import com.time.yourguideapp.presentation.explore.reels.ExploreReelsScreen

@Composable
fun ExploreScreen(modifier: Modifier = Modifier) {
    val rootNavigator = LocalRootNavigator.current

    ExploreThumbnailGrid(
        videos = dummyExploreVideos,
        modifier = modifier,
        onVideoClick = { index ->
            rootNavigator.push(ExploreReelsScreen(initialPage = index))
        },
    )
}

@Composable
private fun ExploreThumbnailGrid(
    videos: List<ExploreVideo>,
    modifier: Modifier = Modifier,
    onVideoClick: (Int) -> Unit,
) {
    LazyVerticalStaggeredGrid(
        columns = StaggeredGridCells.Fixed(2),
        modifier = modifier
            .fillMaxSize()
            .rootBackground(),
        contentPadding = PaddingValues(10.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalItemSpacing = 10.dp,
    ) {
        itemsIndexed(
            items = videos,
           // key = { _, video -> video.id },
        ) { index, video ->
            ExploreThumbnailCard(
                video = video,
                tall = index % 3 == 0,
                onClick = { onVideoClick( index) },
            )
        }
    }
}

@Composable
private fun ExploreThumbnailCard(
    video: ExploreVideo,
    tall: Boolean,
    onClick: () -> Unit,
) {
    val aspectRatio = if (tall) 0.68f else 0.82f

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(aspectRatio)
            .clip(RoundedCornerShape(14.dp))
            .background(Color(0xFF111111))
            .clickable(onClick = onClick),
    ) {
        AsyncImage(
            model = "https://img.youtube.com/vi/${video.youtubeId}/hqdefault.jpg",
            contentDescription = video.title,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop,
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.Transparent,
                            Color.Black.copy(alpha = 0.74f),
                        ),
                    ),
                ),
        )

        Box(
            modifier = Modifier
                .align(Alignment.Center)
                .size(44.dp)
                .clip(CircleShape)
                .background(Color.Black.copy(alpha = 0.46f)),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = Icons.Default.PlayArrow,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(30.dp),
            )
        }

        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .fillMaxWidth()
                .padding(10.dp),
        ) {
            Text(
                text = video.title,
                color = Color.White,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
            Text(
                text = video.location,
                color = Color.White.copy(alpha = 0.78f),
                style = MaterialTheme.typography.labelMedium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}
