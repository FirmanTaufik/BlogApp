package com.time.yourguideapp.presentation.love

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
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material.icons.outlined.TravelExplore
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.time.yourguideapp.AppColors
import com.time.yourguideapp.core.platform.rememberMapLauncher
import com.time.yourguideapp.helper.glassmorphism
import com.time.yourguideapp.presentation.component.HorizontalSpacer
import com.time.yourguideapp.presentation.component.VerticalSpacer
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import yourguideapp.composeapp.generated.resources.Res
import yourguideapp.composeapp.generated.resources.continent_africa
import yourguideapp.composeapp.generated.resources.continent_all
import yourguideapp.composeapp.generated.resources.continent_asia
import yourguideapp.composeapp.generated.resources.continent_europe
import yourguideapp.composeapp.generated.resources.continent_north_america
import yourguideapp.composeapp.generated.resources.continent_oceania
import yourguideapp.composeapp.generated.resources.continent_south_america
import yourguideapp.composeapp.generated.resources.popular_places_title
import yourguideapp.composeapp.generated.resources.popular_places_try_again
import yourguideapp.composeapp.generated.resources.popular_places_unavailable

@Composable
fun PopularPlacesScreen(
    modifier: Modifier = Modifier,
    viewModel: PopularPlacesViewModel = koinViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()
    val openMap = rememberMapLauncher()
    var selectedContinent by remember { mutableStateOf<PopularPlaceContinent?>(null) }
    val filteredPlaces = remember(uiState.places, selectedContinent) {
        selectedContinent?.let { continent ->
            uiState.places.filter { place -> place.continent == continent }
        } ?: uiState.places
    }

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(start = 10.dp, end = 10.dp, top = 10.dp, bottom = 112.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        item {
            PopularPlacesHeader(onRefresh = viewModel::refresh)
        }

        item {
            ContinentFilter(
                selectedContinent = selectedContinent,
                onSelectContinent = { selectedContinent = it },
            )
        }

        when {
            uiState.isLoading -> {
                items(10) {
                    PopularPlaceLoadingCard()
                }
            }
            uiState.errorMessage != null -> {
                item {
                    PopularPlacesErrorCard(
                        message = uiState.errorMessage,
                        onRetry = viewModel::refresh,
                    )
                }
            }
            else -> {
                items(filteredPlaces) { place ->
                    PopularPlaceCard(
                        place = place,
                        onClick = {
                            openMap("${place.title}, ${place.location}")
                        },
                    )
                }
            }
        }
    }
}

@Composable
private fun ContinentFilter(
    selectedContinent: PopularPlaceContinent?,
    onSelectContinent: (PopularPlaceContinent?) -> Unit,
) {
    val options = listOf<PopularPlaceContinent?>(null) + PopularPlaceContinent.entries

    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        items(options) { continent ->
            val selected = selectedContinent == continent
            Text(
                text = continent.label(),
                color = if (selected) AppColors.white else AppColors.blue123060,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1,
                modifier = Modifier
                    .clip(CircleShape)
                    .background(
                        if (selected) AppColors.blue123060 else Color.White.copy(alpha = 0.68f),
                    )
                    .clickable { onSelectContinent(continent) }
                    .padding(horizontal = 14.dp, vertical = 9.dp),
            )
        }
    }
}

@Composable
private fun PopularPlaceContinent?.label(): String {
    return when (this) {
        null -> stringResource(Res.string.continent_all)
        PopularPlaceContinent.Asia -> stringResource(Res.string.continent_asia)
        PopularPlaceContinent.Europe -> stringResource(Res.string.continent_europe)
        PopularPlaceContinent.Africa -> stringResource(Res.string.continent_africa)
        PopularPlaceContinent.NorthAmerica -> stringResource(Res.string.continent_north_america)
        PopularPlaceContinent.SouthAmerica -> stringResource(Res.string.continent_south_america)
        PopularPlaceContinent.Oceania -> stringResource(Res.string.continent_oceania)
    }
}

@Composable
private fun PopularPlacesHeader(onRefresh: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Row(
            modifier = Modifier.weight(1f),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                imageVector = Icons.Outlined.TravelExplore,
                contentDescription = null,
                tint = AppColors.blue123060,
            )
            HorizontalSpacer(8)
            Text(
                text = stringResource(Res.string.popular_places_title),
                color = AppColors.blue123060,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
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
}

@Composable
private fun PopularPlaceCard(
    place: PopularPlace,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .glassmorphism(
                shape = RoundedCornerShape(20.dp),
                backgroundColor = Color.White.copy(alpha = 0.72f),
                borderColor = Color.White.copy(alpha = 0.86f),
            )
            .clickable(onClick = onClick)
            .padding(10.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (place.imageUrl.isBlank()) {
            Box(
                modifier = Modifier
                    .size(92.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(AppColors.blueaad2fb.copy(alpha = 0.45f)),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = Icons.Outlined.TravelExplore,
                    contentDescription = null,
                    tint = AppColors.blue123060,
                    modifier = Modifier.size(38.dp),
                )
            }
        } else {
            AsyncImage(
                model = place.imageUrl,
                contentDescription = place.title,
                modifier = Modifier
                    .size(92.dp)
                    .clip(RoundedCornerShape(16.dp)),
                contentScale = ContentScale.Crop,
            )
        }

        HorizontalSpacer(12)

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = place.title,
                color = AppColors.blue123060,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Outlined.LocationOn,
                    contentDescription = null,
                    tint = AppColors.blue123060.copy(alpha = 0.72f),
                    modifier = Modifier.size(15.dp),
                )
                HorizontalSpacer(4)
                Text(
                    text = place.location,
                    color = AppColors.blue123060.copy(alpha = 0.72f),
                    style = MaterialTheme.typography.labelMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
            VerticalSpacer(6)
            Text(
                text = place.description,
                color = AppColors.blue123060.copy(alpha = 0.82f),
                style = MaterialTheme.typography.bodySmall,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}

@Composable
private fun PopularPlaceLoadingCard() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(Color.White.copy(alpha = 0.42f))
            .padding(10.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(92.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(Color.White.copy(alpha = 0.55f)),
        )
        HorizontalSpacer(12)
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.62f)
                    .size(height = 18.dp, width = 1.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.58f)),
            )
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.44f)
                    .size(height = 14.dp, width = 1.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.50f)),
            )
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.92f)
                    .size(height = 36.dp, width = 1.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color.White.copy(alpha = 0.46f)),
            )
        }
    }
}

@Composable
private fun PopularPlacesErrorCard(
    message: String?,
    onRetry: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .glassmorphism(
                shape = RoundedCornerShape(20.dp),
                backgroundColor = Color.White.copy(alpha = 0.72f),
                borderColor = Color.White.copy(alpha = 0.86f),
            )
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        Text(
            text = message ?: stringResource(Res.string.popular_places_unavailable),
            color = AppColors.blue123060,
            fontWeight = FontWeight.Bold,
        )
        Button(
            onClick = onRetry,
            colors = ButtonDefaults.buttonColors(containerColor = AppColors.blue123060),
        ) {
            Text(stringResource(Res.string.popular_places_try_again), color = Color.White)
        }
    }
}
