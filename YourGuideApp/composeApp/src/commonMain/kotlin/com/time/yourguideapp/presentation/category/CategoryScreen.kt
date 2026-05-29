package com.time.yourguideapp.presentation.category

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIos
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import cafe.adriel.voyager.core.screen.Screen
import com.time.yourguideapp.helper.AppLogger
import com.time.yourguideapp.helper.rootBackground
import com.time.yourguideapp.model.Label
import com.time.yourguideapp.model.Posts
import com.time.yourguideapp.presentation.home.PostItem
import com.time.yourguideapp.presentation.state.UIState
import org.koin.compose.viewmodel.koinViewModel

class CategoryScreen(val label : Label, val data : List<Posts>,
                     val  onClickBack :() -> Unit,
    val onOpenDetail : (Posts, List<Label>)-> Unit) : Screen {
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Content() {
        val viewModel = koinViewModel<CategoryViewModel>()
        val uiState by viewModel.uiState.collectAsStateWithLifecycle()
        LaunchedEffect(Unit){
            viewModel.getListByLabel(label, data)
        }
        Scaffold(modifier = Modifier.fillMaxSize()
            .rootBackground(),
            containerColor = Color.Transparent,
            contentColor = Color.Transparent,
            topBar = {
                TopAppBar(
                    title = {
                        Text(label.getCurrentLanguage())
                    },
                    navigationIcon = {
                        IconButton(onClick = {
                            onClickBack()
                        }){
                            Icon(imageVector = Icons.Default.ArrowBackIos, contentDescription = null)
                        }
                    }
                )
            }) {

            Box(modifier = Modifier.fillMaxSize().padding(it),
                contentAlignment = Alignment.Center){
                when (uiState){
                    is UIState.Success<*> -> {
                        val listPostByLabel = (uiState as UIState.Success<*>).data as List<Posts>
                        LazyColumn(modifier = Modifier.fillMaxSize()) {
                            itemsIndexed(listPostByLabel){ index, item ->
                                val labelName = label.getCurrentLanguage()
                                PostItem(item, labelName, Modifier.fillMaxWidth()
                                    .padding(vertical = 10.dp)
                                    .clickable{
                                       // onOpenDetail(item, "label")
                                    }
                                )
                            }
                        }
                    }

                    is UIState.Loading -> {
                        CircularProgressIndicator()
                    }
                    else -> {
                        Text("Something went wrong..")
                    }
                }
            }





        }
    }
}