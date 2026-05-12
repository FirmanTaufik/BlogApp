package com.time.yourguideapp.data.repository

import com.time.yourguideapp.core.platform.Platform
import com.time.yourguideapp.data.remote.FirestoreGuideService
import com.time.yourguideapp.presentation.home.HomeData
import com.time.yourguideapp.presentation.state.UIState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map

class MainRepository(
    private val platform: Platform,
    private val firestoreService: FirestoreGuideService,
) {

    fun observeHomeState(): Flow<UIState> {
        return firestoreService.observeLabels()
            .map { labels ->
                UIState.Success(
                    HomeData(labels = labels)
                ) as UIState
            }
            .catch { error ->
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
}
