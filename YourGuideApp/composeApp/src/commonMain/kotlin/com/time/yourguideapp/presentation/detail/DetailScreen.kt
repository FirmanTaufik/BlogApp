package com.time.yourguideapp.presentation.detail

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.AddLocation
import androidx.compose.material.icons.outlined.ArrowForwardIos
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Map
import androidx.compose.material.icons.outlined.ShareLocation
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FloatingActionButtonMenu
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import coil3.compose.AsyncImage
import com.time.yourguideapp.AppColors
import com.time.yourguideapp.LocalMainViewModel
import com.time.yourguideapp.helper.Dummy
import com.time.yourguideapp.helper.glassmorphism
import com.time.yourguideapp.model.Label
import com.time.yourguideapp.model.Posts
import com.time.yourguideapp.presentation.ads.ANDROID_BANNER_AD_UNIT_ID
import com.time.yourguideapp.presentation.ads.AdMobBanner
import com.time.yourguideapp.presentation.component.CustomItemBar
import com.time.yourguideapp.presentation.component.HorizontalSpacer
import com.time.yourguideapp.presentation.component.VerticalSpacer
import com.time.yourguideapp.presentation.home.HomeData
import com.time.yourguideapp.presentation.state.UIState

data class DetailScreen(
    val data: Posts,
    val labels : List<Label>
) : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val mainViewModel = LocalMainViewModel.current
        val mainState by mainViewModel.state.collectAsState()
        val bookmarkPostIds = ((mainState as? UIState.Success<*>)?.data as? HomeData)
            ?.bookmarkPostIds
            .orEmpty()

        DetailContent(
            onBack = { navigator.pop() },
            isLoved = bookmarkPostIds.contains(data.idPost),
            onBookmark = { mainViewModel.toggleBookmark(data.idPost) },
            onShare = {},
        )
    }


    @OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
    @Composable
    private fun DetailContent(
        onBack: () -> Unit,
        isLoved: Boolean,
        onBookmark: () -> Unit,
        onShare: () -> Unit,
    ) {
        val state = rememberScrollState()

        Scaffold(
            topBar = {
                TopAppBar(
                    colors = TopAppBarDefaults.topAppBarColors(

                        containerColor = Color.Transparent,

                        ),
                    title = { },

                    navigationIcon = {
                        IconButton(
                            onClick = onBack,
                            modifier = Modifier.glassmorphism(
                                CircleShape,
                                backgroundColor = Color.White.copy(alpha = 0.30f),
                            ),
                        ) {
                            Icon(
                                imageVector = Icons.Default.ArrowBackIosNew,
                                contentDescription = null,
                                tint = AppColors.blue123060,
                            )
                        }
                    },

                    actions = {
                        IconButton(
                            onClick = onBookmark,
                            modifier = Modifier.glassmorphism(
                                CircleShape,
                                backgroundColor = Color.White.copy(alpha = 0.30f),
                            ),
                        ) {
                            Icon(
                                imageVector = if (isLoved) {
                                    Icons.Filled.Favorite
                                } else {
                                    Icons.Outlined.FavoriteBorder
                                },
                                contentDescription = null,
                                tint = if (isLoved) Color(0xFFE94B6A) else AppColors.blue123060,
                            )
                        }
                        HorizontalSpacer(15)
                        IconButton(
                            onClick = onShare,
                            modifier = Modifier.glassmorphism(
                                CircleShape,
                                backgroundColor = Color.White.copy(alpha = 0.30f),
                            ),
                        ) {
                            Icon(
                                imageVector = Icons.Default.Share,
                                contentDescription = null,
                                tint = AppColors.blue123060,
                            )
                        }
                    }
                )
            },
            bottomBar = {
                Column {
                    BottomAppBar(containerColor = Color.Transparent,) {
                        Row (modifier = Modifier.fillMaxWidth()
                            .padding(horizontal = 15.dp),
                            horizontalArrangement = Arrangement.SpaceBetween){
                            LazyRow(
                                horizontalArrangement = Arrangement.End,
                                modifier = Modifier.wrapContentWidth()
                                    .glassmorphism(CircleShape, widthBorder = 1, backgroundColor = Color.White.copy(alpha = 0.95f) ),
                                contentPadding = PaddingValues(5.dp)
                            ) {
                                items(2) {
                                    CustomItemBar(rememberVectorPainter(Icons.Outlined.Map)) {

                                    }
                                }

                            }
                            Box( modifier = Modifier.wrapContentWidth()
                                .padding(5.dp)
                                .glassmorphism(CircleShape, widthBorder = 1, backgroundColor = Color.White.copy(alpha = 0.95f) ),
                            ){

                                CustomItemBar(rememberVectorPainter(Icons.Outlined.ArrowForwardIos)) {

                                }
                            }
                        }


                    }
                    AdMobBanner(
                        modifier = Modifier.fillMaxWidth(),
                        adUnitId = ANDROID_BANNER_AD_UNIT_ID,
                    )
                }
            }
        ) {
            BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
                val viewportHeight = maxHeight

                AsyncImage(
                    model = data.coverImageUrl,
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .background(Color.Black)
                        .fillMaxSize(),
                )

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    Color.Transparent,
                                    AppColors.white.copy(alpha = 0.5f),
                                    AppColors.white.copy(alpha = 1f),
                                    AppColors.white
                                )
                            )
                        )
                )

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(state)
                        .padding(horizontal = 0.dp),
                ) {

                    VerticalSpacer(350)
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(min = viewportHeight)
                            .border(
                                1.dp,
                                color = AppColors.white,
                                shape = RoundedCornerShape(topEnd = 30.dp, topStart = 30.dp)
                            )

                    ) {
                        VerticalSpacer(15)
                        LazyRow() {
                            itemsIndexed(
                                data.labelIds
                            ){  index, item ->
                                Box(modifier = Modifier.padding(10.dp)) {
                                    Box(
                                        modifier = Modifier
                                            .glassmorphism(
                                                shape = CircleShape,
                                                backgroundColor = AppColors.blueaad2fb
                                            )
                                            .padding(horizontal = 6.dp, vertical = 2.dp),
                                    ) {
                                        Text(
                                            text = labels.find { it.idLabel == item }?.getCurrentLanguage() ?: "",
                                            color = AppColors.blue4789d7,
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }
                        }
                        Text(
                            text = data.getCurrentLocaleData().title,
                            color = AppColors.blue123060,
                            fontSize = 25.sp,
                            fontWeight = FontWeight.Bold,
                            lineHeight = 30.sp,
                            modifier = Modifier.padding(15.dp)
                        )
                        Text(
                            text = data.getCurrentLocaleData().content,
                            color = AppColors.blue123060,
                            fontSize = 13.sp,
                            lineHeight = 14.sp,
                            modifier = Modifier.padding(horizontal = 15.dp)
                        )
                        VerticalSpacer(32)
                    }
                }

            }
        }


    }
}


@Composable
@Preview(showSystemUi = true)
private fun PreviewDetailScreen() {
    MaterialTheme {
//        DetailContent(
//            onBack = {},
//            onBookmark = {},
//            onShare = {},
//        )
    }
}
