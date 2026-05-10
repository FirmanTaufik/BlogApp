package com.time.yourguideapp

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun HomeScreen(
    state: UIState,
    onReload: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        when (state) {
            UIState.Loading -> {
                CircularProgressIndicator()
                Text("Memuat data dari MainViewModel...")
            }

            is UIState.Success -> {
                Text(
                    text = state.title,
                    style = MaterialTheme.typography.headlineMedium,
                )
                Text(
                    text = state.message,
                    style = MaterialTheme.typography.bodyLarge,
                )

                state.highlights.forEach { item ->
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            text = item,
                            modifier = Modifier.padding(16.dp),
                        )
                    }
                }
            }

            is UIState.Error -> {
                Text(
                    text = state.message,
                    color = MaterialTheme.colorScheme.error,
                )
            }
        }

        Button(onClick = onReload) {
            Text("Reload State")
        }
    }
}
