package com.time.yourguideapp.presentation.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Bookmark
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material.icons.outlined.Save
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.time.yourguideapp.AppColors
import com.time.yourguideapp.helper.glassmorphism
import com.time.yourguideapp.presentation.component.CustomItemBar
import com.time.yourguideapp.presentation.component.VerticalSpacer

@Composable
fun PostItem(modifier: Modifier) {
    Row(
        modifier = modifier.fillMaxWidth()
            .padding(10.dp)
            .clip(RoundedCornerShape(10))
            .height(150.dp)
            .glassmorphism()
    ) {


        Box(modifier = Modifier.size(150.dp)) {

            AsyncImage(
                model = "https://picsum.photos/seed/blog-label-tech/512/512",
                contentDescription = null,
                modifier = Modifier.size(140.dp)
                    .align(alignment = Alignment.Center)
                    .clip(RoundedCornerShape(10))
                    .background(color = Color.Yellow),
                contentScale = ContentScale.Crop
            )
        }

        Column(modifier = Modifier
            .fillMaxHeight()
            .weight(1f).padding(horizontal = 15.dp, vertical = 0.dp)) {
            Row(
                modifier = Modifier.padding(0.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {

                Box(
                    modifier = Modifier
                        .glassmorphism(shape = CircleShape, backgroundColor = AppColors.blueaad2fb)
                        .padding(horizontal = 6.dp, vertical = 2.dp),
                ) {
                    Text(
                        text = "Teknologiaa",
                        color = AppColors.blue4789d7,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                IconButton(onClick = {

                }) {
                    Icon(
                        painter = rememberVectorPainter(Icons.Outlined.BookmarkBorder),
                        contentDescription = null,
                        tint = AppColors.blue123060
                    )
                }

            }

            Text(
                text = "10 Thing I Wish have to bla blssssa bla",
                color = AppColors.blue123060,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 2,
                lineHeight = 17.sp,
                overflow = TextOverflow.Ellipsis
            )

            VerticalSpacer(5)

            Text(
                text = "10 Thing I Wish have to bla blssssa sasasa sadakdak dmakdmakdmkdakmdakmdkadamkdmakdabla",
                color = AppColors.blue123060,
                fontSize = 13.sp,
                maxLines = 3,
                lineHeight = 14.sp,
                overflow = TextOverflow.Ellipsis
            )

        }


    }
}

@Composable
@Preview(showSystemUi = true)
private fun PostItemPreview() {
    MaterialTheme {
        Box(contentAlignment = Alignment.Center) {
            PostItem(Modifier)
        }

    }
}