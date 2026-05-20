package com.time.yourguideapp.presentation.component

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.SecondaryTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.time.yourguideapp.AppColors
import com.time.yourguideapp.AppColors.blue4789d7
import com.time.yourguideapp.AppColors.blueaad2fb
import com.time.yourguideapp.helper.glassmorphism

@Composable
fun TabView(tabsList : List<String>,
            selectedTabIndex : Int,
            onSelectTab:(Int)-> Unit ){
    SecondaryTabRow(
        selectedTabIndex = selectedTabIndex,
        modifier = Modifier
            .height(56.dp)
            .padding(horizontal = 20.dp)
            .border(2.dp, color = Color.White, shape = RoundedCornerShape(50))
            .clip( RoundedCornerShape(50)),
        containerColor = blueaad2fb.copy(0.7f),
        divider = {},
        indicator = {
            TabRowDefaults.SecondaryIndicator(
                Modifier
                    .tabIndicatorOffset(selectedTabIndex, matchContentSize = false)
                    .padding(5.dp)
                    .fillMaxHeight()
                    .glassmorphism(widthBorder = 2, )
                    .zIndex(1f), //this should be lower than Tab zIndex,
                color = AppColors.white.copy(alpha = 0.15f),
            )
        },
        tabs = {
            tabsList.forEachIndexed { index, tab ->
                Tab(
                    selected = selectedTabIndex == index,
                    modifier = Modifier.zIndex(2f), // And this needed too
                    onClick = {
                        onSelectTab(index)
                    },
                    selectedContentColor = blue4789d7,
                    text = {
                        Text(
                            text = tab,
                            textAlign = TextAlign.Center,
                            color = AppColors.white
                        )
                    },
                )
            }
        },
    )
}