package com.time.yourguideapp.presentation.currency

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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CurrencyExchange
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material.icons.outlined.TrendingUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.time.yourguideapp.AppColors
import com.time.yourguideapp.helper.glassmorphism
import com.time.yourguideapp.presentation.component.HorizontalSpacer
import com.time.yourguideapp.presentation.component.VerticalSpacer
import org.koin.compose.viewmodel.koinViewModel
import kotlin.math.pow
import kotlin.math.round

@Composable
fun CurrencyScreen(
    modifier: Modifier = Modifier,
    viewModel: CurrencyViewModel = koinViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()
    val topRate = uiState.rates.firstOrNull()

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(start = 10.dp, end = 10.dp, top = 10.dp, bottom = 112.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        item {
            CurrencyHeaderCard(
                updatedDate = uiState.updatedDate,
                onRefresh = viewModel::refresh,
            )
        }

        item {
            when {
                uiState.isLoading -> CurrencyLoadingCard()
                uiState.errorMessage != null -> CurrencyErrorCard(
                    message = uiState.errorMessage.orEmpty(),
                    onRetry = viewModel::refresh,
                )
                topRate != null -> CurrencySummaryCard(rate = topRate)
            }
        }

        if (uiState.rates.isNotEmpty()) {
            item {
                Text(
                    text = "USD to Major Currencies",
                    color = AppColors.blue123060,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 4.dp),
                )
            }
            items(uiState.rates) { rate ->
                CurrencyRateItem(rate = rate)
            }
        }
    }
}

@Composable
private fun CurrencyHeaderCard(
    updatedDate: String?,
    onRefresh: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .glassmorphism(
                shape = RoundedCornerShape(28.dp),
                backgroundColor = Color.White.copy(alpha = 0.72f),
                borderColor = Color.White.copy(alpha = 0.90f),
            )
            .padding(18.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(58.dp)
                .clip(CircleShape)
                .background(AppColors.blueaad2fb.copy(alpha = 0.46f)),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = Icons.Outlined.CurrencyExchange,
                contentDescription = null,
                tint = AppColors.blue123060,
                modifier = Modifier.size(30.dp),
            )
        }

        HorizontalSpacer(14)

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = "USD Exchange Rates",
                color = AppColors.blue123060,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
            )
            VerticalSpacer(4)
            Text(
                text = updatedDate?.let { "Updated: $it" } ?: "Live daily reference rates",
                color = AppColors.blue123060.copy(alpha = 0.78f),
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
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
private fun CurrencySummaryCard(rate: CurrencyRate) {
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
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .clip(CircleShape)
                    .background(AppColors.blue123060.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = Icons.Outlined.TrendingUp,
                    contentDescription = null,
                    tint = AppColors.blue123060,
                )
            }
            HorizontalSpacer(12)
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = rate.target.name,
                    color = AppColors.blue123060,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                )
                Text(
                    text = rate.target.code,
                    color = AppColors.blue123060.copy(alpha = 0.7f),
                    style = MaterialTheme.typography.bodySmall,
                )
            }
            Text(
                text = rate.value.prettyRate(),
                color = AppColors.blue123060,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
            )
        }

        VerticalSpacer(12)

        Text(
            text = "1 USD = ${rate.value.prettyRate()} ${rate.target.code}",
            color = AppColors.blue123060.copy(alpha = 0.82f),
            style = MaterialTheme.typography.bodyMedium,
        )
    }
}

@Composable
private fun CurrencyRateItem(rate: CurrencyRate) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(22.dp))
            .background(Color.White.copy(alpha = 0.62f))
            .padding(horizontal = 14.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(AppColors.blue123060.copy(alpha = 0.12f)),
            contentAlignment = Alignment.Center,
        ) {
            AsyncImage(
                model = flagUrl(rate.target.flagCode),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape),
            )
        }

        HorizontalSpacer(12)

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = rate.target.name,
                color = AppColors.blue123060,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Text(
                text = rate.target.code,
                color = AppColors.blue123060.copy(alpha = 0.68f),
                style = MaterialTheme.typography.bodySmall,
            )
        }

        Text(
            text = rate.value.prettyRate(),
            color = AppColors.blue123060,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
        )
    }
}

@Composable
private fun CurrencyErrorCard(
    message: String,
    onRetry: () -> Unit,
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
        Text(
            text = "Unable to load exchange rates",
            color = AppColors.blue123060,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
        )
        VerticalSpacer(8)
        Text(
            text = message,
            color = AppColors.blue123060.copy(alpha = 0.78f),
            style = MaterialTheme.typography.bodyMedium,
        )
        VerticalSpacer(12)
        Text(
            text = "Try again",
            color = AppColors.blue123060,
            modifier = Modifier
                .clip(RoundedCornerShape(16.dp))
                .background(AppColors.blue123060.copy(alpha = 0.10f))
                .clickable(onClick = onRetry)
                .padding(horizontal = 14.dp, vertical = 10.dp),
            fontWeight = FontWeight.SemiBold,
        )
    }
}

private fun Double.prettyRate(): String {
    val decimals = when {
        this >= 1000.0 -> 0
        this >= 100.0 -> 1
        this >= 10.0 -> 2
        this >= 1.0 -> 3
        else -> 4
    }
    val factor = 10.0.pow(decimals)
    val rounded = round(this * factor) / factor
    return rounded
        .toString()
        .trimEnd('0')
        .trimEnd('.')
}

private fun flagUrl(flagCode: String): String {
    return "https://flagcdn.com/h40/$flagCode.png"
}
