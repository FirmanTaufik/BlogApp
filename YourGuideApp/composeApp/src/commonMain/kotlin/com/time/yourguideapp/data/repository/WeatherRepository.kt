package com.time.yourguideapp.data.repository

import com.time.yourguideapp.presentation.weather.DailyWeather
import com.time.yourguideapp.presentation.weather.WeatherForecast
import com.time.yourguideapp.presentation.weather.WeatherLocation
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get

class WeatherRepository(
    private val client: HttpClient,
) {
    suspend fun loadWeatherForecast(location: WeatherLocation): WeatherForecast {
        val url = "https://api.open-meteo.com/v1/forecast" +
            "?latitude=${location.latitude}" +
            "&longitude=${location.longitude}" +
            "&current=temperature_2m,weather_code,wind_speed_10m" +
            "&daily=weather_code,temperature_2m_max,temperature_2m_min,precipitation_probability_max" +
            "&timezone=auto" +
            "&forecast_days=7"

        val response = client.get(url).body<String>()

        val dates = response.stringArray("time")
        val weatherCodes = response.numberArray("weather_code").map { it.toInt() }
        val maxTemperatures = response.numberArray("temperature_2m_max")
        val minTemperatures = response.numberArray("temperature_2m_min")
        val rainChances = response.numberArray("precipitation_probability_max").map { it.toInt() }

        val days = dates.mapIndexed { index, date ->
            DailyWeather(
                date = date,
                weatherCode = weatherCodes.getOrElse(index) { 0 },
                maxTemperature = maxTemperatures.getOrElse(index) { 0.0 },
                minTemperature = minTemperatures.getOrElse(index) { 0.0 },
                rainChance = rainChances.getOrElse(index) { 0 },
            )
        }

        return WeatherForecast(
            locationName = location.name,
            temperature = response.currentNumber("temperature_2m"),
            windSpeed = response.currentNumber("wind_speed_10m"),
            weatherCode = response.currentNumber("weather_code").toInt(),
            days = days,
        )
    }
}

private fun String.currentNumber(field: String): Double {
    val currentBlock = Regex(""""current"\s*:\s*\{([^}]*)\}""")
        .find(this)
        ?.groupValues
        ?.get(1)
        .orEmpty()

    return Regex(""""$field"\s*:\s*(-?\d+(?:\.\d+)?)""")
        .find(currentBlock)
        ?.groupValues
        ?.get(1)
        ?.toDoubleOrNull()
        ?: 0.0
}

private fun String.numberArray(field: String): List<Double> {
    val dailyBlock = Regex(
        pattern = """"daily"\s*:\s*\{(.*?)\}""",
        option = RegexOption.DOT_MATCHES_ALL,
    )
        .find(this)
        ?.groupValues
        ?.get(1)
        .orEmpty()

    val rawValues = Regex(""""$field"\s*:\s*\[([^]]*)]""")
        .find(dailyBlock)
        ?.groupValues
        ?.get(1)
        .orEmpty()

    return rawValues
        .split(",")
        .mapNotNull { it.trim().toDoubleOrNull() }
}

private fun String.stringArray(field: String): List<String> {
    val dailyBlock = Regex(
        pattern = """"daily"\s*:\s*\{(.*?)\}""",
        option = RegexOption.DOT_MATCHES_ALL,
    )
        .find(this)
        ?.groupValues
        ?.get(1)
        .orEmpty()

    val rawValues = Regex(""""$field"\s*:\s*\[([^]]*)]""")
        .find(dailyBlock)
        ?.groupValues
        ?.get(1)
        .orEmpty()

    return Regex(""""([^"]+)"""")
        .findAll(rawValues)
        .map { it.groupValues[1] }
        .toList()
}
