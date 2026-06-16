package com.time.yourguideapp.data.remote

import com.time.yourguideapp.model.AdMobConfig
import com.time.yourguideapp.model.Bookmark
import com.time.yourguideapp.model.GuideDocument
import com.time.yourguideapp.model.HomeSliderConfig
import com.time.yourguideapp.model.Label
import com.time.yourguideapp.model.Locales
import com.time.yourguideapp.model.Posts
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.firestore.firestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class FirestoreGuideService {
     fun getFeaturedGuide(): Flow<List<Posts>> {
        return Firebase.firestore
            .collection("posts")
            .snapshots
            .map {
                it.documents.map { doc ->
                    doc.data(Posts.serializer())
                        .copy(idPost = doc.id)
                }
            }
    }

    fun observeLabels(): Flow<List<Label>> {
        return Firebase.firestore
            .collection("labels")
            .snapshots
            .map { snapshot ->
                snapshot.documents.map { document ->
                    document.data(Label.serializer()).copy(
                        idLabel = document.id
                    )
                }
            }
    }

    fun getObserverLocales(): Flow<List<Locales>> {
        return Firebase.firestore
            .collection("locales")
            .snapshots
            .map { snapshot ->
                snapshot.documents.map { document ->
                    document.data(Locales.serializer())
                        .copy(
                            idLocales = document.id
                        )
                }
            }
    }

    fun observeAdMobConfig(): Flow<AdMobConfig> {
        return Firebase.firestore
            .collection("settings")
            .document("admob")
            .snapshots
            .map { snapshot ->
                runCatching {
                    snapshot.data(AdMobConfig.serializer())
                }.getOrDefault(AdMobConfig())
            }
    }

    fun observeHomeSliderConfig(): Flow<HomeSliderConfig> {
        return Firebase.firestore
            .collection("homeSlider")
            .document("main")
            .snapshots
            .map { snapshot ->
                runCatching {
                    snapshot.data(HomeSliderConfig.serializer())
                }.getOrDefault(HomeSliderConfig())
            }
    }

    fun observeUserBookmarkPostIds(userId: String): Flow<List<String>> {
        return Firebase.firestore
            .collection("users")
            .document(userId)
            .collection("bookmarks")
            .snapshots
            .map { snapshot ->
                snapshot.documents.map { document -> document.id }
            }
    }

    suspend fun addBookmark(userId: String, postId: String) {
        Firebase.firestore
            .collection("users")
            .document(userId)
            .collection("bookmarks")
            .document(postId)
            .set(
                Bookmark(
                    postId = postId,
                    createdAt = "",
                )
            )
    }

    suspend fun removeBookmark(userId: String, postId: String) {
        Firebase.firestore
            .collection("users")
            .document(userId)
            .collection("bookmarks")
            .document(postId)
            .delete()
    }
}
