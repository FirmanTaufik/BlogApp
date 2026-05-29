package com.time.yourguideapp.presentation.search

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import cafe.adriel.voyager.core.screen.Screen
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.time.yourguideapp.AppColors
import com.time.yourguideapp.LocalMainViewModel
import com.time.yourguideapp.LocalRootNavigator
import com.time.yourguideapp.helper.glassmorphism
import com.time.yourguideapp.helper.rootBackground
import com.time.yourguideapp.model.Label
import com.time.yourguideapp.model.Posts
import com.time.yourguideapp.presentation.detail.DetailScreen
import com.time.yourguideapp.presentation.home.HomeData
import com.time.yourguideapp.presentation.home.PostItem
import com.time.yourguideapp.presentation.state.UIState
import kotlinx.coroutines.delay
import org.jetbrains.compose.resources.stringResource
import yourguideapp.composeapp.generated.resources.*

data class SearchScreen(val listPost : List<Posts>, val labels : List<Label>) : Screen {
    @Composable
    override fun Content() {
        var query by remember { mutableStateOf("") }
        val rootNavigator = LocalRootNavigator.current
        val mainViewModel = LocalMainViewModel.current
        val mainState by mainViewModel.state.collectAsState()
        val bookmarkPostIds = ((mainState as? UIState.Success<*>)?.data as? HomeData)
            ?.bookmarkPostIds
            .orEmpty()
        val focusRequester = remember { FocusRequester() }
        val filteredPosts = remember(query, listPost, labels) {
            val keyword = query.trim()

            if (keyword.isBlank()) {
                listPost
            } else {
                listPost.filter { post ->
                    val localeData = post.getCurrentLocaleData()
                    val labelNames = post.labelIds
                        .mapNotNull { labelId -> labels.find { it.idLabel == labelId } }
                        .joinToString(" ") { "${it.names.id} ${it.names.en}" }

                    listOf(
                        localeData.title,
                        localeData.content,
                        labelNames,
                    ).any { value ->
                        value.contains(keyword, ignoreCase = true)
                    }
                }
            }
        }

        LaunchedEffect(Unit) {
            delay(300)
            focusRequester.requestFocus()
        }

        Scaffold(
            modifier = Modifier
                .fillMaxSize()
                .rootBackground(),
            containerColor = Color.Transparent,
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 16.dp),
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 12.dp, bottom = 16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    IconButton(
                        onClick = { rootNavigator.pop() },
                        modifier = Modifier.glassmorphism(
                            shape = CircleShape,
                            backgroundColor = Color.White.copy(alpha = 0.55f),
                        ),
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = null,
                            tint = AppColors.blue123060,
                        )
                    }

                    TextField(
                        value = query,
                        onValueChange = { query = it },
                        modifier = Modifier
                            .weight(1f)
                            .focusRequester(focusRequester),
                        singleLine = true,
                        shape = RoundedCornerShape(50),
                        placeholder = {
                            Text(
                                text = stringResource(Res.string.search_placeholder),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                            )
                        },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = null,
                                tint = AppColors.blue4789d7,
                            )
                        },
                        trailingIcon = {
                            if (query.isNotBlank()) {
                                IconButton(onClick = { query = "" }) {
                                    Icon(
                                        imageVector = Icons.Default.Close,
                                        contentDescription = null,
                                        tint = AppColors.blue123060.copy(alpha = 0.65f),
                                    )
                                }
                            }
                        },
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.White.copy(alpha = 0.92f),
                            unfocusedContainerColor = Color.White.copy(alpha = 0.78f),
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent,
                            focusedTextColor = AppColors.blue123060,
                            unfocusedTextColor = AppColors.blue123060,
                        ),
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                    )
                }

                Text(
                    text = if (query.isBlank()) {
                        stringResource(Res.string.search_all_destinations)
                    } else {
                        stringResource(Res.string.search_results_found, filteredPosts.size)
                    },
                    color = AppColors.white,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 10.dp),
                )

                if (filteredPosts.isEmpty()) {
                    EmptySearchResult(query)
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(bottom = 24.dp),
                    ) {
                        items(filteredPosts) { post ->
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
                                    .padding(vertical = 6.dp)
                                    .clickable {
                                        rootNavigator.push(DetailScreen(post, labels))
                                    },
                                isLoved = bookmarkPostIds.contains(post.idPost),
                                onToggleLove = { mainViewModel.toggleBookmark(post.idPost) },
                            )
                        }
                    }
                }
            }
        }
    }

@Composable
private fun EmptySearchResult(query: String) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier
                .fillMaxWidth()
                .glassmorphism(
                    shape = RoundedCornerShape(18.dp),
                    backgroundColor = Color.White.copy(alpha = 0.35f),
                )
                .padding(horizontal = 24.dp, vertical = 28.dp),
        ) {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = null,
                tint = AppColors.white,
            )
            Text(
                text = stringResource(Res.string.search_no_results_found),
                color = AppColors.white,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
            )
            Text(
                text = stringResource(Res.string.search_try_another_keyword, query),
                color = AppColors.white.copy(alpha = 0.82f),
                style = MaterialTheme.typography.bodyMedium,
            )
        }
    }
}
}
