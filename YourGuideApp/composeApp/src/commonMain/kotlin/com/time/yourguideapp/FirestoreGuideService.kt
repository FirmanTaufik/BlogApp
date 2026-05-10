package com.time.yourguideapp

import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.firestore.firestore

class FirestoreGuideService {
    suspend fun getFeaturedGuide(): GuideDocument {
        return Firebase.firestore
            .collection("guides")
            .document("featured")
            .get()
            .data(GuideDocument.serializer())
    }
}
