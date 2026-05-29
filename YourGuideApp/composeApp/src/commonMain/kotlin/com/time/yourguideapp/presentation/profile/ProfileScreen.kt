package com.time.yourguideapp.presentation.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import coil3.compose.AsyncImage
import com.time.yourguideapp.AppColors
import com.time.yourguideapp.helper.glassmorphism
import com.time.yourguideapp.helper.profileCardShape
import com.time.yourguideapp.helper.rootBackground
import com.time.yourguideapp.presentation.component.HorizontalSpacer
import com.time.yourguideapp.presentation.component.VerticalSpacer


class ProfileScreen(

) : Screen{
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Content() {

        val navigator = LocalNavigator.currentOrThrow

        Box(modifier = Modifier.rootBackground().fillMaxSize()
            .padding(10.dp)){
            Scaffold(modifier = Modifier, containerColor = Color.Transparent,

                topBar = {
                    TopAppBar(
                        colors = TopAppBarDefaults.topAppBarColors(

                            containerColor = Color.Transparent,

                            ),
                        title = {
                            Text("Profile",color = AppColors.blue123060)
                        },

                        navigationIcon = {
                            IconButton(
                                onClick = {
                                    navigator.pop()
                                          },
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
                    )
                },
                ) {

                Column(modifier = Modifier
                    .padding(it)
                    .fillMaxSize(), ) {
                    VerticalSpacer(10)
                    Row (modifier = Modifier.fillMaxWidth()
                        .padding(horizontal = 10.dp),
                        verticalAlignment = Alignment.CenterVertically){
                        Box(modifier = Modifier.wrapContentSize()) {
                            Box(
                                modifier = Modifier
                                    .size(90.dp)
                                    .align(Alignment.TopCenter)
                                    .clip(CircleShape)
                                    .background(color = Color.White)
                            ) {
                                AsyncImage(
                                    model = "https://picsum.photos/seed/blog-label-tech/512/512",
                                    contentDescription = null,
                                    modifier = Modifier
                                        .size(80.dp)
                                        .align(Alignment.Center)
                                        .clip(CircleShape)
                                        .background(color = AppColors.blueaad2fb.copy(alpha = 0.5f)),
                                )
                            }
                        }

                        HorizontalSpacer(10)
                        Column(modifier = Modifier.weight(1f)
                            .padding()
                            .glassmorphism()
                            .padding(15.dp)) {
                            Text("Abdulah Bin Jaenudin",color = AppColors.blue123060, fontWeight = FontWeight.Bold)
                            Text("abdulah@masil.com",color = AppColors.blue123060.copy(alpha = 0.8f))
                        }
                    }

                    VerticalSpacer(20)

                }
            }
        }
    }

    @Composable
    private fun ContentProfile(){

    }

}
@Composable
@Preview(showBackground = true, showSystemUi = true)
fun ProfileScreenPreview(){
    MaterialTheme {
        ProfileScreen().Content()
    }
}