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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.navigator.Navigator
import com.time.yourguideapp.AppColors
import com.time.yourguideapp.model.Label
import com.time.yourguideapp.model.Posts
import com.time.yourguideapp.presentation.detail.DetailScreen
import com.time.yourguideapp.presentation.home.PostItem
import org.jetbrains.compose.resources.stringResource
import yourguideapp.composeapp.generated.resources.Res
import yourguideapp.composeapp.generated.resources.love_empty_body
import yourguideapp.composeapp.generated.resources.love_empty_title

@Composable
fun LoveScreen(
    lovedPosts: List<Posts>,
    labels: List<Label>,
    rootNavigator: Navigator,
    onToggleLove: (Posts) -> Unit,
    modifier: Modifier = Modifier,
) {
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
                            rootNavigator.push(DetailScreen(post, labels))
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
