package com.time.yourguideapp

class MainRepository(
    private val platform: Platform,
    private val firestoreService: FirestoreGuideService,
) {
    suspend fun getHomeState(): UIState {
        return runCatching {
            val guide = firestoreService.getFeaturedGuide()

            UIState.Success(
                title = guide.title,
                message = guide.summary,
                highlights = buildList {
                    add("Platform aktif: ${platform.name}")
                    add("Kategori: ${guide.category}")
                    addAll(guide.highlights)
                },
            )
        }.getOrElse { error ->
            UIState.Error(
                message = buildString {
                    append("Gagal mengambil data Firestore.")
                    error.message
                        ?.takeIf { it.isNotBlank() }
                        ?.let { append(" $it") }
                }
            )
        }
    }
}
