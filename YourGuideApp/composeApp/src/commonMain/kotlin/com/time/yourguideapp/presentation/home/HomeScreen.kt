package com.time.yourguideapp.presentation.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import coil3.compose.AsyncImage
import com.time.yourguideapp.AppColors
import com.time.yourguideapp.helper.AppLogger
import com.time.yourguideapp.helper.glassmorphism
import com.time.yourguideapp.helper.rootBackground
import com.time.yourguideapp.model.Label
import com.time.yourguideapp.model.Posts
import com.time.yourguideapp.presentation.category.CategoryScreen
import com.time.yourguideapp.presentation.component.BannerSlider
import com.time.yourguideapp.presentation.weather.WeatherLoadingCard
import com.time.yourguideapp.presentation.component.VerticalSpacer
import com.time.yourguideapp.presentation.state.UIState

@Composable
fun HomeScreen(
    state: UIState,
    onReload: () -> Unit,
    onOpenDetail: (Posts, List<Label>) -> Unit,
    onOpenCategory:  (Label, List<Posts>, List<Label>) -> Unit,
    onToggleLove: (Posts) -> Unit,
    modifier: Modifier = Modifier,
) {

    Column(
        modifier = modifier
            .fillMaxSize() ,
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        when (state) {
            UIState.Loading -> {
                HomeLoadingSkeleton()
            }

            is UIState.Success<*> -> {
                val data = state.data as HomeData
                LaunchedEffect(data){
                    print("Hallo this loge ${data.posts.size}")
                }
                Column {


                    LazyColumn(modifier = Modifier.fillMaxWidth(),
                        contentPadding = PaddingValues(10.dp)
                    ) {
                        item{
                            BannerSlider(listOf("https://picsum.photos/seed/blog-label-tech/512/512",
                                "https://picsum.photos/seed/blog-label-tech/512/512","https://picsum.photos/seed/blog-label-tech/512/512","https://picsum.photos/seed/blog-label-tech/512/512"))
                            VerticalSpacer(20)
                        }
                        item {
                            LazyRow(
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                items(data.labels) { label ->
                                    Box(modifier= Modifier.padding(horizontal = 10.dp)
                                        .clickable{
                                            onOpenCategory(label,data.posts, data.labels)
                                        }
                                    ){
                                        Row(
                                            modifier = Modifier
                                                .glassmorphism(CircleShape,  backgroundColor = Color.White.copy(alpha = 0.50f))
                                                .padding(horizontal = 10.dp, vertical = 8.dp),
                                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                                            verticalAlignment = Alignment.CenterVertically,
                                        ) {
                                            if (label.imageUrl.isNotBlank()) {
                                                AsyncImage(
                                                    model = label.imageUrl,
                                                    contentDescription = label.names.id,
                                                    modifier = Modifier
                                                        .size(28.dp)
                                                        .clip(CircleShape),
                                                    contentScale = ContentScale.Crop,
                                                )
                                            }
                                            Text(
                                                text = label.getCurrentLanguage(),
                                                color = AppColors.blue123060,
                                            )
                                        }
                                    }
                                }
                            }
                            VerticalSpacer(20)
                        }


                        itemsIndexed(data.posts){ index, item ->
                            val labelId = item.labelIds.firstOrNull()
                            val label = data.labels.find { it.idLabel == labelId }?.getCurrentLanguage() ?: ""
                            AppLogger.d (tag = "HomeTAG"){ "Ini Label $label ${labelId.orEmpty()} ${data.labels.size}" }
                            PostItem(item, label, Modifier.fillMaxWidth()
                                .padding(vertical = 10.dp)
                                .clickable{
                                    onOpenDetail(item, data.labels)
                                },
                                isLoved = data.bookmarkPostIds.contains(item.idPost),
                                onToggleLove = { onToggleLove(item) },
                            )
                        }


                    }
                }



            }

            is UIState.Error -> {
                Text(
                    text = state.message,
                    color = MaterialTheme.colorScheme.error,
                )
            }
        }

    }
}

@Composable
private fun HomeLoadingSkeleton() {
    LazyColumn(
        modifier = Modifier.fillMaxWidth(),
        contentPadding = PaddingValues(10.dp),
    ) {
        item {
            WeatherLoadingCard()
            VerticalSpacer(20)
        }
        item {
            androidx.compose.foundation.layout.Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                repeat(4) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(36.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.50f)),
                    )
                }
            }
            VerticalSpacer(20)
        }
        items(3) {
            HomePostSkeleton()
        }
    }
}

@Composable
private fun HomePostSkeleton() {
    androidx.compose.foundation.layout.Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp)
            .clip(androidx.compose.foundation.shape.RoundedCornerShape(10.dp))
            .height(150.dp)
            .background(Color.White.copy(alpha = 0.40f)),
    ) {
        Box(
            modifier = Modifier
                .size(150.dp)
                .padding(5.dp)
                .clip(androidx.compose.foundation.shape.RoundedCornerShape(10.dp))
                .background(Color.White.copy(alpha = 0.50f)),
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .weight(1f)
                .padding(horizontal = 15.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            androidx.compose.foundation.layout.Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Box(
                    modifier = Modifier
                        .size(width = 64.dp, height = 22.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.52f)),
                )
                Box(
                    modifier = Modifier
                        .size(24.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.52f)),
                )
            }
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.85f)
                    .height(18.dp)
                    .clip(androidx.compose.foundation.shape.RoundedCornerShape(8.dp))
                    .background(Color.White.copy(alpha = 0.54f)),
            )
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.65f)
                    .height(14.dp)
                    .clip(androidx.compose.foundation.shape.RoundedCornerShape(8.dp))
                    .background(Color.White.copy(alpha = 0.48f)),
            )
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.92f)
                    .height(14.dp)
                    .clip(androidx.compose.foundation.shape.RoundedCornerShape(8.dp))
                    .background(Color.White.copy(alpha = 0.46f)),
            )
        }
    }
}

@Composable
@Preview(showBackground = true, showSystemUi = true)
fun HomeViewPreview() {

    val list = ArrayList<Label>()
    for (i in 0 until  10){
        list.add(
            Label(
                imageUrl = "https://picsum.photos/seed/label-$i/120/120",
                names = Label.Language(
                    id = "Indonesia",
                    en = "Indonesia",
                )
            ),)
    }

    MaterialTheme {
        Box(modifier = Modifier.rootBackground()){
            HomeScreen(
                state = UIState.Success(
                    data = HomeData(
                        labels = list,
                        emptyList(), emptyList()
                    )
                ),
                onReload = {},
                onOpenDetail ={ data, lablel ->

                },
                onOpenCategory = {lable, post, lables -> },
                onToggleLove = {},
                modifier = Modifier
            )
        }

    }
}
