package com.time.yourguideapp.presentation.weather

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Air
import androidx.compose.material.icons.outlined.Cloud
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material.icons.outlined.Thermostat
import androidx.compose.material.icons.outlined.WaterDrop
import androidx.compose.material.icons.outlined.WbSunny
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.time.yourguideapp.AppColors
import com.time.yourguideapp.helper.glassmorphism
import com.time.yourguideapp.presentation.component.HorizontalSpacer
import com.time.yourguideapp.presentation.component.VerticalSpacer
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun WeatherScreen(
    modifier: Modifier = Modifier,
    viewModel: WeatherViewModel = koinViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(start = 10.dp, end = 10.dp, top = 10.dp, bottom = 112.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        item {
            WeatherLocationSelector(
                locations = uiState.locations,
                selectedLocation = uiState.selectedLocation,
                onSelectLocation = viewModel::selectLocation,
            )
        }

        item {
            when {
                uiState.isLoading -> WeatherLoadingCard()
                uiState.errorMessage != null -> WeatherErrorCard(
                    message = uiState.errorMessage.orEmpty(),
                    onRetry = viewModel::refresh,
                )
                uiState.forecast != null -> CurrentWeatherCard(
                    forecast = uiState.forecast!!,
                    onRefresh = viewModel::refresh,
                )
            }
        }

        uiState.forecast?.let { forecast ->
            item {
                Text(
                    text = "7-Day Forecast",
                    color = AppColors.blue123060,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 4.dp),
                )
            }
            items(forecast.days) { day ->
                DailyWeatherItem(day = day)
            }
        }
    }
}

@Composable
private fun WeatherLocationSelector(
    locations: List<WeatherLocation>,
    selectedLocation: WeatherLocation,
    onSelectLocation: (WeatherLocation) -> Unit,
) {
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        items(locations) { location ->
            val selected = location == selectedLocation
            Row(
                modifier = Modifier
                    .clip(CircleShape)
                    .background(
                        if (selected) AppColors.blue123060 else Color.White.copy(alpha = 0.62f),
                    )
                    .clickable { onSelectLocation(location) }
                    .padding(horizontal = 14.dp, vertical = 9.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(
                    imageVector = Icons.Outlined.LocationOn,
                    contentDescription = null,
                    tint = if (selected) AppColors.white else AppColors.blue123060,
                    modifier = Modifier.size(18.dp),
                )
                HorizontalSpacer(6)
                Text(
                    text = location.name,
                    color = if (selected) AppColors.white else AppColors.blue123060,
                    fontWeight = FontWeight.SemiBold,
                )
            }
        }
    }
}

@Composable
private fun CurrentWeatherCard(
    forecast: WeatherForecast,
    onRefresh: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .glassmorphism(
                shape = RoundedCornerShape(28.dp),
                backgroundColor = Color.White.copy(alpha = 0.72f),
                borderColor = Color.White.copy(alpha = 0.90f),
            )
            .padding(18.dp),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier
                    .size(68.dp)
                    .clip(CircleShape)
                    .background(AppColors.blueaad2fb.copy(alpha = 0.46f)),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = weatherIcon(forecast.weatherCode),
                    contentDescription = null,
                    tint = AppColors.blue123060,
                    modifier = Modifier.size(38.dp),
                )
            }

            HorizontalSpacer(14)

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = forecast.locationName,
                    color = AppColors.blue123060,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    text = weatherDescription(forecast.weatherCode),
                    color = AppColors.blue123060.copy(alpha = 0.78f),
                    style = MaterialTheme.typography.bodyMedium,
                )
            }

            IconButton(onClick = onRefresh) {
                Icon(
                    imageVector = Icons.Outlined.Refresh,
                    contentDescription = null,
                    tint = AppColors.blue123060,
                )
            }
        }

        VerticalSpacer(18)

        Text(
            text = "${forecast.temperature.rounded()}°C",
            color = AppColors.blue123060,
            style = MaterialTheme.typography.displaySmall,
            fontWeight = FontWeight.Bold,
        )

        VerticalSpacer(14)

        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            WeatherMetric(
                icon = Icons.Outlined.Air,
                label = "Wind",
                value = "${forecast.windSpeed.rounded()} km/h",
                modifier = Modifier.weight(1f),
            )
            WeatherMetric(
                icon = Icons.Outlined.WaterDrop,
                label = "Rain",
                value = "${forecast.days.firstOrNull()?.rainChance ?: 0}%",
                modifier = Modifier.weight(1f),
            )
        }
    }
}

@Composable
private fun WeatherMetric(
    icon: ImageVector,
    label: String,
    value: String,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(18.dp))
            .background(Color.White.copy(alpha = 0.44f))
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = AppColors.blue123060,
        )
        HorizontalSpacer(8)
        Column {
            Text(
                text = label,
                color = AppColors.blue123060.copy(alpha = 0.72f),
                style = MaterialTheme.typography.labelMedium,
            )
            Text(
                text = value,
                color = AppColors.blue123060,
                fontWeight = FontWeight.Bold,
            )
        }
    }
}

@Composable
private fun DailyWeatherItem(day: DailyWeather) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .glassmorphism(
                shape = RoundedCornerShape(20.dp),
                backgroundColor = Color.White.copy(alpha = 0.62f),
                borderColor = Color.White.copy(alpha = 0.82f),
            )
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(46.dp)
                .clip(CircleShape)
                .background(Color.White.copy(alpha = 0.42f)),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = weatherIcon(day.weatherCode),
                contentDescription = null,
                tint = AppColors.blue123060,
            )
        }

        HorizontalSpacer(12)

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = day.date.displayDate(),
                color = AppColors.blue123060,
                fontWeight = FontWeight.Bold,
            )
            Text(
                text = weatherDescription(day.weatherCode),
                color = AppColors.blue123060.copy(alpha = 0.74f),
                style = MaterialTheme.typography.bodySmall,
            )
        }

        Column(horizontalAlignment = Alignment.End) {
            Text(
                text = "${day.maxTemperature.rounded()}° / ${day.minTemperature.rounded()}°",
                color = AppColors.blue123060,
                fontWeight = FontWeight.Bold,
            )
            Text(
                text = "Hujan ${day.rainChance}%",
                color = AppColors.blue123060.copy(alpha = 0.74f),
                style = MaterialTheme.typography.bodySmall,
            )
        }
    }
}

@Composable
private fun WeatherErrorCard(
    message: String,
    onRetry: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .glassmorphism(
                shape = RoundedCornerShape(28.dp),
                backgroundColor = Color.White.copy(alpha = 0.68f),
            )
            .padding(18.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = "Weather unavailable",
            color = AppColors.blue123060,
            fontWeight = FontWeight.Bold,
        )
        VerticalSpacer(6)
        Text(
            text = message,
            color = AppColors.blue123060.copy(alpha = 0.76f),
        )
        VerticalSpacer(12)
        Button(
            onClick = onRetry,
            colors = ButtonDefaults.buttonColors(containerColor = AppColors.blue123060),
        ) {
            Text("Try again", color = AppColors.white)
        }
    }
}

private fun weatherDescription(code: Int): String = when (code) {
    0 -> "Clear"
    1, 2 -> "Mostly clear"
    3 -> "Cloudy"
    45, 48 -> "Foggy"
    51, 53, 55 -> "Drizzle"
    61, 63, 65 -> "Rain"
    66, 67 -> "Freezing rain"
    71, 73, 75, 77 -> "Snow"
    80, 81, 82 -> "Rain showers"
    85, 86 -> "Snow showers"
    95, 96, 99 -> "Thunderstorm"
    else -> "Changing weather"
}

private fun weatherIcon(code: Int): ImageVector = when (code) {
    0, 1 -> Icons.Outlined.WbSunny
    2, 3, 45, 48 -> Icons.Outlined.Cloud
    51, 53, 55, 61, 63, 65, 80, 81, 82 -> Icons.Outlined.WaterDrop
    else -> Icons.Outlined.Thermostat
}

private fun Double.rounded(): String = if (this % 1.0 == 0.0) {
    toInt().toString()
} else {
    (kotlin.math.round(this * 10) / 10).toString()
}

private fun String.displayDate(): String {
    val parts = split("-")
    if (parts.size != 3) return this
    return "${parts[2]}/${parts[1]}"
}
