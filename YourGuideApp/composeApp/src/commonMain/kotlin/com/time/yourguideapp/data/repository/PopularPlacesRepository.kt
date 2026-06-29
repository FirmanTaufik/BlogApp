package com.time.yourguideapp.data.repository

import com.time.yourguideapp.presentation.love.PopularPlace
import com.time.yourguideapp.presentation.love.PopularPlaceContinent
import com.time.yourguideapp.presentation.love.PopularPlaceSeed
import com.time.yourguideapp.presentation.love.popularPlaceSeeds
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.http.HttpHeaders
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

class PopularPlacesRepository(
    private val client: HttpClient,
) {
    private val cacheMutex = Mutex()
    private var cachedPlaces: List<PopularPlace>? = null

    fun getCachedPopularPlaces(): List<PopularPlace>? = cachedPlaces

    suspend fun loadPopularPlaces(forceRefresh: Boolean = false): List<PopularPlace> {
        if (!forceRefresh) {
            cachedPlaces?.let { return it }
        }

        return cacheMutex.withLock {
            if (!forceRefresh) {
                cachedPlaces?.let { return@withLock it }
            }

            fetchPopularPlaces().also { places ->
                cachedPlaces = places
            }
        }
    }

    private suspend fun fetchPopularPlaces(): List<PopularPlace> {
        val apiPlaces = popularPlaceQueries
            .flatMap { query ->
                runCatching { searchPlaces(query) }.getOrDefault(emptyList())
            }
            .distinctBy { place -> place.continent to place.title.lowercase() }
            .filter { place -> place.title.isUsefulPlaceTitle() }
            .groupBy { place -> place.continent }
            .let { placesByContinent ->
                PopularPlaceContinent.entries.flatMap { continent ->
                    placesByContinent[continent]
                        .orEmpty()
                        .sortedBy { place -> place.title }
                        .take(30)
                }
            }

        val populatedContinents = apiPlaces.mapTo(mutableSetOf()) { place -> place.continent }
        val fallbackPlaces = popularPlaceSeeds
            .filter { seed -> seed.continent !in populatedContinents }
            .map { seed ->
                runCatching { loadPlace(seed) }
                    .getOrElse { seed.toFallbackPlace() }
            }

        return (apiPlaces + fallbackPlaces)
            .sortedWith(compareBy<PopularPlace> { it.continent.ordinal }.thenBy { it.title })
    }

    private suspend fun searchPlaces(query: PopularPlaceQuery): List<PopularPlace> {
        print("loadPopularPlaces $query")
        val response = client.get(
            "https://en.wikipedia.org/w/rest.php/v1/search/page?q=${query.text.queryValue()}&limit=30",
        ) {
            header(HttpHeaders.UserAgent, "YourGuideApp/1.0")
        }.body<String>()

        return response.findArrayBlock("pages")
            .splitTopLevelObjects()
            .mapNotNull { page ->
                val title = page.jsonString("title")
                val key = page.jsonString("key").ifBlank {
                    title.replace(" ", "_")
                }
                val description = page.jsonString("description")
                val excerpt = page.jsonString("excerpt").cleanSearchExcerpt()
                val imageUrl = page.findObjectBlock("thumbnail")
                    .jsonString("url")
                    .withHttpsScheme()

                if (title.isBlank() || excerpt.isBlank()) {
                    null
                } else {
                    PopularPlace(
                        title = title,
                        location = description.ifBlank { "Popular destination" },
                        description = excerpt,
                        imageUrl = imageUrl,
                        sourceUrl = "https://en.wikipedia.org/wiki/$key",
                        continent = query.continent,
                    )
                }
            }
    }

    private suspend fun loadPlace(seed: PopularPlaceSeed): PopularPlace {
        val response = client.get(
            "https://en.wikipedia.org/api/rest_v1/page/summary/${seed.pageTitle}",
        ) {
            header(HttpHeaders.UserAgent, "YourGuideApp/1.0")
        }.body<String>()

        return PopularPlace(
            title = response.jsonString("title").ifBlank {
                seed.pageTitle.replace("_", " ")
            },
            location = response.jsonString("description").ifBlank {
                seed.fallbackLocation
            },
            description = response.jsonString("extract"),
            imageUrl = response.thumbnailSource(),
            sourceUrl = response.contentUrl().ifBlank {
                "https://en.wikipedia.org/wiki/${seed.pageTitle}"
            },
            continent = seed.continent,
        )
    }
}

private fun PopularPlaceSeed.toFallbackPlace() = PopularPlace(
    title = pageTitle.replace("_", " "),
    location = fallbackLocation,
    description = "Popular destination in $fallbackLocation",
    imageUrl = "",
    sourceUrl = "https://en.wikipedia.org/wiki/$pageTitle",
    continent = continent,
)

private data class PopularPlaceQuery(
    val text: String,
    val continent: PopularPlaceContinent,
)

private val popularPlaceQueries = listOf(
    PopularPlaceQuery("best places to visit in Asia", PopularPlaceContinent.Asia),
    PopularPlaceQuery("famous landmarks Asia tourist attraction", PopularPlaceContinent.Asia),
    PopularPlaceQuery("famous temples Asia tourist attraction", PopularPlaceContinent.Asia),
    PopularPlaceQuery("famous islands Asia tourist destination", PopularPlaceContinent.Asia),
    PopularPlaceQuery("UNESCO World Heritage Asia tourism", PopularPlaceContinent.Asia),
    PopularPlaceQuery("best places to visit in Europe", PopularPlaceContinent.Europe),
    PopularPlaceQuery("famous landmarks Europe tourist attraction", PopularPlaceContinent.Europe),
    PopularPlaceQuery("famous castles Europe tourist attraction", PopularPlaceContinent.Europe),
    PopularPlaceQuery("historic cities Europe tourism", PopularPlaceContinent.Europe),
    PopularPlaceQuery("UNESCO World Heritage Europe tourism", PopularPlaceContinent.Europe),
    PopularPlaceQuery("best places to visit in Africa", PopularPlaceContinent.Africa),
    PopularPlaceQuery("famous landmarks Africa tourist attraction", PopularPlaceContinent.Africa),
    PopularPlaceQuery("famous national parks Africa tourism", PopularPlaceContinent.Africa),
    PopularPlaceQuery("famous ancient ruins Africa tourism", PopularPlaceContinent.Africa),
    PopularPlaceQuery("UNESCO World Heritage Africa tourism", PopularPlaceContinent.Africa),
    PopularPlaceQuery("best places to visit in North America", PopularPlaceContinent.NorthAmerica),
    PopularPlaceQuery("famous landmarks North America tourist attraction", PopularPlaceContinent.NorthAmerica),
    PopularPlaceQuery("famous national parks North America tourism", PopularPlaceContinent.NorthAmerica),
    PopularPlaceQuery("famous beaches North America tourist destination", PopularPlaceContinent.NorthAmerica),
    PopularPlaceQuery("UNESCO World Heritage North America tourism", PopularPlaceContinent.NorthAmerica),
    PopularPlaceQuery("best places to visit in South America", PopularPlaceContinent.SouthAmerica),
    PopularPlaceQuery("famous landmarks South America tourist attraction", PopularPlaceContinent.SouthAmerica),
    PopularPlaceQuery("famous ancient ruins South America tourism", PopularPlaceContinent.SouthAmerica),
    PopularPlaceQuery("famous national parks South America tourism", PopularPlaceContinent.SouthAmerica),
    PopularPlaceQuery("UNESCO World Heritage South America tourism", PopularPlaceContinent.SouthAmerica),
    PopularPlaceQuery("best places to visit in Oceania", PopularPlaceContinent.Oceania),
    PopularPlaceQuery("famous landmarks Oceania tourist attraction", PopularPlaceContinent.Oceania),
    PopularPlaceQuery("famous islands Oceania tourist destination", PopularPlaceContinent.Oceania),
    PopularPlaceQuery("famous national parks Oceania tourism", PopularPlaceContinent.Oceania),
    PopularPlaceQuery("UNESCO World Heritage Oceania tourism", PopularPlaceContinent.Oceania),
)

private fun String.jsonString(field: String): String {
    return Regex(""""$field"\s*:\s*"((?:\\.|[^"])*)"""")
        .find(this)
        ?.groupValues
        ?.getOrNull(1)
        ?.decodeJsonText()
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
                if (depth == 0) {
                    return substring(start + 1, index)
                }
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
                if (depth == 0) {
                    return substring(start + 1, index)
                }
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

private fun String.thumbnailSource(): String {
    val thumbnailBlock = Regex(
        pattern = """"thumbnail"\s*:\s*\{(.*?)\}""",
        option = RegexOption.DOT_MATCHES_ALL,
    )
        .find(this)
        ?.groupValues
        ?.getOrNull(1)
        .orEmpty()

    return thumbnailBlock.jsonString("source")
}

private fun String.contentUrl(): String {
    val contentUrlsBlock = Regex(
        pattern = """"content_urls"\s*:\s*\{(.*?)\}\s*,\s*"extract"""",
        option = RegexOption.DOT_MATCHES_ALL,
    )
        .find(this)
        ?.groupValues
        ?.getOrNull(1)
        .orEmpty()

    val desktopBlock = Regex(
        pattern = """"desktop"\s*:\s*\{(.*?)\}""",
        option = RegexOption.DOT_MATCHES_ALL,
    )
        .find(contentUrlsBlock)
        ?.groupValues
        ?.getOrNull(1)
        .orEmpty()

    return desktopBlock.jsonString("page")
}

private fun String.decodeJsonText(): String {
    return replace("\\/", "/")
        .replace("\\\"", "\"")
        .replace("\\n", " ")
        .replace("\\t", " ")
        .replace("\\u2013", "-")
        .replace("\\u2014", "-")
        .replace("\\u2019", "'")
        .replace("\\u00e9", "e")
        .replace("\\u00ed", "i")
        .replace("\\u00f3", "o")
}

private fun String.cleanSearchExcerpt(): String {
    return decodeJsonText()
        .replace(Regex("<[^>]+>"), "")
        .replace("&quot;", "\"")
        .replace("&amp;", "&")
        .replace("&#039;", "'")
        .replace(Regex("\\s+"), " ")
        .trim()
}

private fun String.withHttpsScheme(): String {
    return when {
        startsWith("//") -> "https:$this"
        else -> this
    }
}

private fun String.queryValue(): String {
    return trim()
        .replace(" ", "%20")
        .replace("&", "%26")
}

private fun String.isUsefulPlaceTitle(): Boolean {
    val value = lowercase()
    return !value.startsWith("list of") &&
        "disambiguation" !in value &&
        "tourism in" !in value &&
        "tourist attraction" != value &&
        "world heritage site" != value
}
