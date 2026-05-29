package com.time.yourguideapp.presentation.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.time.yourguideapp.AppColors
import com.time.yourguideapp.helper.glassmorphism
import com.time.yourguideapp.helper.rootBackground

@Composable
fun CustomItemBar(
                  icon: Painter ,
                  iconSize : Int = 30,
                  onClick : () -> Unit){
    Box {
        Box(
            modifier = Modifier
                .clickable{
                    onClick()
                }
                .padding(5.dp)
                .glassmorphism(CircleShape),

            contentAlignment = Alignment.Center
        ) {
            Box(modifier = Modifier.padding(10.dp),
                contentAlignment = Alignment.Center) {
               // Text(text, color = AppColors.white)
                Icon(
                    painter = icon,
                    contentDescription = null,
                    tint = AppColors.blue123060,
                    modifier = Modifier.size(iconSize.dp)
                )
            }

        }
    }
}

@Composable
@Preview(showSystemUi = true, showBackground = true)
fun CustomItemBarPreview(){
    MaterialTheme {
        Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()
            .rootBackground()) {
            CustomItemBar(rememberVectorPainter( Icons.Outlined.Home)){}
        }

    }
}
