package com.time.yourguideapp.data.repository

import com.time.yourguideapp.core.platform.getPixabayApiKey
import com.time.yourguideapp.presentation.explore.ExploreVideo
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get

class PixabayVideoRepository(
    private val client: HttpClient,
) {
    private var cachedTravelVideos: List<ExploreVideo> = emptyList()

    suspend fun loadTravelVideos(): List<ExploreVideo> {
        if (cachedTravelVideos.isNotEmpty()) {
            return cachedTravelVideos
        }

        return fetchTravelVideos()
    }

    suspend fun refreshTravelVideos(): List<ExploreVideo> {
        cachedTravelVideos = emptyList()
        return fetchTravelVideos()
    }

    private suspend fun fetchTravelVideos(): List<ExploreVideo> {
        val apiKey = "56206107-288efd6168b9be96d68694d98" // getPixabayApiKey()
        require(apiKey.isNotBlank()) {
            "Pixabay API key belum diisi. Tambahkan PIXABAY_API_KEY di local.properties."
        }

        val videos = pixabayTravelQueries
            .flatMap { query ->
                runCatching { searchVideos(apiKey, query) }.getOrDefault(emptyList())
            }
            .distinctBy { video -> video.id }
            .take(120)

        require(videos.isNotEmpty()) { "Video wisata dari Pixabay tidak tersedia." }
        cachedTravelVideos = videos
        return videos
    }

    private suspend fun searchVideos(
        apiKey: String,
        query: String,
    ): List<ExploreVideo> {
        val response = client.get(
            "https://pixabay.com/api/videos/" +
                "?key=${apiKey.queryValue()}" +
                "&q=${query.queryValue()}" +
                "&category=travel" +
                "&video_type=film" +
                "&safesearch=true" +
                "&per_page=30",
        ).body<String>()

        return response.findArrayBlock("hits")
            .splitTopLevelObjects()
            .mapNotNull { item ->
                val id = item.jsonNumber("id").ifBlank { item.jsonString("id") }
                val tags = item.jsonString("tags")
                val pageUrl = item.jsonString("pageURL")
                val likes = item.jsonNumber("likes").compactCount()
                val comments = item.jsonNumber("comments").compactCount()
                val views = item.jsonNumber("views").compactCount()
                val downloads = item.jsonNumber("downloads").compactCount()
                val duration = item.jsonNumber("duration").toDurationLabel()
                val user = item.jsonString("user").ifBlank { "Pixabay" }
                val userImageUrl = item.jsonString("userImageURL")
                val mediumVideo = item.findObjectBlock("medium")
                val smallVideo = item.findObjectBlock("small")
                val tinyVideo = item.findObjectBlock("tiny")
                val videoUrl = mediumVideo.jsonString("url")
                    .ifBlank { smallVideo.jsonString("url") }
                    .ifBlank { tinyVideo.jsonString("url") }
                val thumbnailUrl = mediumVideo.jsonString("thumbnail")
                    .ifBlank { smallVideo.jsonString("thumbnail") }
                    .ifBlank { tinyVideo.jsonString("thumbnail") }

                if (id.isBlank() || videoUrl.isBlank()) {
                    null
                } else {
                    ExploreVideo(
                        id = id,
                        title = tags.toTitle(),
                        creatorName = user,
                        creatorAvatarUrl = userImageUrl,
                        tags = tags,
                        description = if (tags.isBlank()) {
                            "Pixabay travel video by $user."
                        } else {
                            tags.split(",")
                                .map { it.trim() }
                                .filter { it.isNotBlank() }
                                .joinToString("  #", prefix = "#")
                        },
                        thumbnailUrl = thumbnailUrl,
                        videoUrl = videoUrl,
                        pageUrl = pageUrl,
                        likes = likes,
                        comments = comments,
                        views = views,
                        downloads = downloads,
                        duration = duration,
                    )
                }
            }
    }
}

private val pixabayTravelQueries = listOf(
    "travel",
    "beach travel",
    "city travel",
    "mountain travel",
    "island travel",
    "nature travel",
    "landmark travel",
    "bali",
    "paris",
    "japan travel",
)

private fun String.jsonString(field: String): String {
    return Regex(""""$field"\s*:\s*"((?:\\.|[^"])*)"""")
        .find(this)
        ?.groupValues
        ?.getOrNull(1)
        ?.decodeJsonText()
        .orEmpty()
}

private fun String.jsonNumber(field: String): String {
    return Regex(""""$field"\s*:\s*([0-9]+)""")
        .find(this)
        ?.groupValues
        ?.getOrNull(1)
        .orEmpty()
}

private fun String.findArrayBlock(field: String): String {
    val fieldIndex = indexOf(""""$field"""")
    if (fieldIndex < 0) return ""

    val start = indexOf('[', startIndex = fieldIndex)
    if (start < 0) return ""

    var depth = 0
    var inString = false
    var escaped = false
    for (index in start until length) {
        val char = this[index]
        when {
            escaped -> escaped = false
            char == '\\' && inString -> escaped = true
            char == '"' -> inString = !inString
            !inString && char == '[' -> depth++
            !inString && char == ']' -> {
                depth--
                if (depth == 0) return substring(start + 1, index)
            }
        }
    }

    return ""
}

private fun String.findObjectBlock(field: String): String {
    val fieldIndex = indexOf(""""$field"""")
    if (fieldIndex < 0) return ""

    val start = indexOf('{', startIndex = fieldIndex)
    if (start < 0) return ""

    var depth = 0
    var inString = false
    var escaped = false
    for (index in start until length) {
        val char = this[index]
        when {
            escaped -> escaped = false
            char == '\\' && inString -> escaped = true
            char == '"' -> inString = !inString
            !inString && char == '{' -> depth++
            !inString && char == '}' -> {
                depth--
                if (depth == 0) return substring(start + 1, index)
            }
        }
    }

    return ""
}

private fun String.splitTopLevelObjects(): List<String> {
    val objects = mutableListOf<String>()
    var start = -1
    var depth = 0
    var inString = false
    var escaped = false

    for (index in indices) {
        val char = this[index]
        when {
            escaped -> escaped = false
            char == '\\' && inString -> escaped = true
            char == '"' -> inString = !inString
            !inString && char == '{' -> {
                if (depth == 0) start = index + 1
                depth++
            }
            !inString && char == '}' -> {
                depth--
                if (depth == 0 && start >= 0) {
                    objects += substring(start, index)
                    start = -1
                }
            }
        }
    }

    return objects
}

private fun String.decodeJsonText(): String {
    return replace("\\/", "/")
        .replace("\\\"", "\"")
        .replace("\\n", " ")
        .replace("\\t", " ")
}

private fun String.queryValue(): String {
    return trim()
        .replace("%", "%25")
        .replace(" ", "+")
        .replace("&", "%26")
}

private fun String.toTitle(): String {
    return split(",")
        .map { it.trim() }
        .filter { it.isNotBlank() }
        .take(3)
        .joinToString(" • ") { value ->
            value.replaceFirstChar { char ->
                if (char.isLowerCase()) char.titlecase() else char.toString()
            }
        }
        .ifBlank { "Travel Video" }
}

private fun String.compactCount(): String {
    val value = toLongOrNull() ?: return ""
    return when {
        value >= 1_000_000 -> "${value / 1_000_000}M"
        value >= 1_000 -> "${value / 1_000}K"
        else -> value.toString()
    }
}

private fun String.toDurationLabel(): String {
    val seconds = toIntOrNull() ?: return ""
    val minutes = seconds / 60
    val remainingSeconds = seconds % 60
    return "$minutes:${remainingSeconds.toString().padStart(2, '0')}"
}
