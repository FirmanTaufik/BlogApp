package com.time.yourguideapp.presentation.detail

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
expect fun HtmlContent(
    html: String,
    modifier: Modifier = Modifier,
)
