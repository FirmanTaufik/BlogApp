package com.time.yourguideapp.presentation.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
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
import com.time.yourguideapp.presentation.component.VerticalSpacer
import com.time.yourguideapp.presentation.state.UIState

@Composable
fun HomeScreen(
    state: UIState,
    onReload: () -> Unit,
    onOpenDetail: (Posts, List<Label>) -> Unit,
    onOpenCategory:  (Label, List<Posts>) -> Unit,
    modifier: Modifier = Modifier,
) {

    Column(
        modifier = modifier
            .fillMaxSize() ,
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        when (state) {
            UIState.Loading -> {
                CircularProgressIndicator()
                Text("Memuat data dari MainViewModel...")
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
                                            onOpenCategory(label,data.posts)
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
                            val label = data.labels.find { it.idLabel == item.labelIds.first()  }?.getCurrentLanguage() ?: ""
                            AppLogger.d (tag = "HomeTAG"){ "Ini Label $label ${item.labelIds.first()} ${data.labels.size}" }
                            PostItem(item, label, Modifier.fillMaxWidth()
                                .padding(vertical = 10.dp)
                                .clickable{
                                    onOpenDetail(item, data.labels)
                                }
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
                onOpenCategory = {lable, post-> },
                modifier = Modifier
            )
        }

    }
}
