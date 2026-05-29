package com.time.yourguideapp.data.repository

import com.time.yourguideapp.core.platform.Platform
import com.time.yourguideapp.data.remote.FirestoreGuideService
import com.time.yourguideapp.model.Label
import com.time.yourguideapp.model.Posts
import com.time.yourguideapp.presentation.home.HomeData
import com.time.yourguideapp.presentation.state.UIState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map

class MainRepository(
    private val platform: Platform,
    private val firestoreService: FirestoreGuideService,
) {

    fun observeHomeState(userId: String?): Flow<UIState> {
        val bookmarkIdsFlow = userId
            ?.let { firestoreService.observeUserBookmarkPostIds(it) }
            ?: flowOf(emptyList())

        return combine(
            firestoreService.getFeaturedGuide(),
            firestoreService.observeLabels(),
            firestoreService.getObserverLocales(),
            bookmarkIdsFlow,
        ) { posts, labels, locales, bookmarkPostIds ->
            UIState.Success(
                    HomeData(labels = labels, posts, locales, bookmarkPostIds)
                ) as UIState
        }.catch { error ->
                emit(
                    UIState.Error(
                        message = buildString {
                            append("Gagal mengambil data Firestore.")
                            error.message
                                ?.takeIf { it.isNotBlank() }
                                ?.let { append(" $it") }
                        }
                    )
                )
            }
    }

    suspend fun addBookmark(userId: String, postId: String) {
        firestoreService.addBookmark(userId, postId)
    }

    suspend fun removeBookmark(userId: String, postId: String) {
        firestoreService.removeBookmark(userId, postId)
    }


    fun getListByLabel(label : Label, listPost : List<Posts>) : List<Posts>{
        val list = arrayListOf<Posts>()

        listPost.forEach { it
            if (it.labelIds.contains(label.idLabel)){
                list.add(it)
            }
        }
        return list
    }

}
