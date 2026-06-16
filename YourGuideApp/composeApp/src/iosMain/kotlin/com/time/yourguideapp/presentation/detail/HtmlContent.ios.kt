package com.time.yourguideapp.presentation.detail

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
actual fun HtmlContent(
    html: String,
    modifier: Modifier,
) {
    Text(
        text = html,
        modifier = modifier,
    )
}
