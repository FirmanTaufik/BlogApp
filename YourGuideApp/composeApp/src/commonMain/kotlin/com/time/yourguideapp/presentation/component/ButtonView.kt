package com.time.yourguideapp.presentation.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.time.yourguideapp.helper.glassmorphism
import com.time.yourguideapp.helper.rootBackground
import org.jetbrains.compose.resources.stringResource
import yourguideapp.composeapp.generated.resources.*

@Composable
fun ButtonView(text : String,
                roundShape : Int = 30,
               onClick :() -> Unit){
    Button(onClick = {
        onClick()
    }, colors = ButtonDefaults.buttonColors(
        containerColor =  Color.White.copy(alpha = 0.15f),
    ) , shape =RoundedCornerShape(roundShape),
        border = BorderStroke(width = 1.dp, Color.White.copy(alpha = 0.25f))
    ){
        Text(text, color = Color.White)
    }
}

@Composable
@Preview(showBackground = true, showSystemUi = true)
private fun ButtonViewPreview(){
    MaterialTheme {
        Column (modifier = Modifier.fillMaxSize()
            .rootBackground(),
            verticalArrangement = Arrangement.Center){
            ButtonView(text = stringResource(Res.string.common_skip)){}
        }

    }
}
