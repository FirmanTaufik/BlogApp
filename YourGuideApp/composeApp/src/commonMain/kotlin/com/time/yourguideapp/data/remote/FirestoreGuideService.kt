package com.time.yourguideapp.data.remote

import com.time.yourguideapp.model.GuideDocument
import com.time.yourguideapp.model.Label
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.firestore.firestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class FirestoreGuideService {
    suspend fun getFeaturedGuide(): GuideDocument {
        return Firebase.firestore
            .collection("posts")
            .document("ZHa3Wxl7K6P3od3Ygb5e")
            .get()
            .data(GuideDocument.serializer())
    }

    fun observeLabels(): Flow<List<Label>> {
        return Firebase.firestore
            .collection("labels")
            .snapshots
            .map { snapshot ->
                snapshot.documents.map { document ->
                    document.data(Label.serializer())
                }
            }
    }
}
