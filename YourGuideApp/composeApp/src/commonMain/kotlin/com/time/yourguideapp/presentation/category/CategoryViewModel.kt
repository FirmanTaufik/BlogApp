package com.time.yourguideapp.presentation.category

import androidx.lifecycle.ViewModel
import com.time.yourguideapp.data.repository.MainRepository
import com.time.yourguideapp.model.Label
import com.time.yourguideapp.model.Posts
import com.time.yourguideapp.presentation.state.UIState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class CategoryViewModel (
    private val repository: MainRepository,
) : ViewModel() {

    private var _uiState = MutableStateFlow<UIState>(UIState.Loading)
    val uiState = _uiState.asStateFlow()

    fun getListByLabel(label : Label, listPost : List<Posts>) {
        val list = repository.getListByLabel(label, listPost)
        _uiState.value = UIState.Success(list)
    }

}