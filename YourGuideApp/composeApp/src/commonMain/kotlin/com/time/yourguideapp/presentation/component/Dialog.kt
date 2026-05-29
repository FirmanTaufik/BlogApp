package com.time.yourguideapp.presentation.component

import androidx.compose.runtime.Composable
import androidx.compose.ui.window.Dialog

@Composable
fun GeneralDialog(onDismissRequest: () -> Unit){
    Dialog(onDismissRequest = onDismissRequest) {

    }
}
