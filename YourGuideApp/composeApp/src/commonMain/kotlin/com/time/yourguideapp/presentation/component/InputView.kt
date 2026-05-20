package com.time.yourguideapp.presentation.component

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.LocalSee
import androidx.compose.material.icons.filled.PanoramaFishEye
import androidx.compose.material.icons.filled.PlusOne
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.RemoveRedEye
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.time.yourguideapp.AppColors
import com.time.yourguideapp.helper.glassmorphism
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.vectorResource

@Composable
fun InputView(input : String,
              placeHolder : String,
              leadingIcon : ImageVector,
              trailingIcon : ImageVector ?= null,
              isInputTypePassword : Boolean = false,
              onTextChanged:(String)-> Unit) {

    var passwordVisible by remember {
        mutableStateOf(false)
    }

    TextField(
        modifier = Modifier.fillMaxWidth(),

        value = input,
        onValueChange = {
            onTextChanged(it)
        },
        enabled = true,
        shape = RoundedCornerShape(50),
        colors = TextFieldDefaults.colors(
            focusedContainerColor = AppColors.white,
            unfocusedContainerColor = AppColors.white,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent
        ),
        placeholder = {
            Text(text = placeHolder, color = AppColors.white)
        },
        label = {
            Text(text = placeHolder, color = Color.Gray.copy(alpha = 0.5f))

        },
        leadingIcon = {
            Icon(
                imageVector =  leadingIcon,
                contentDescription = "",
                tint = Color.Gray.copy(alpha = 0.5f)
            )
        },
        trailingIcon = {
            if (isInputTypePassword) {
                IconButton(onClick = {
                    passwordVisible = !passwordVisible
                }){
                    Icon(imageVector =  if (passwordVisible) Icons.Default.Visibility
                    else Icons.Default.VisibilityOff,
                        contentDescription = null,
                        tint = Color.Gray.copy(alpha = 0.5f))
                }
            }else {

                if (trailingIcon!=null) {

                    Icon(
                        imageVector =  trailingIcon,
                        contentDescription = "",
                        tint = Color.Gray.copy(alpha = 0.5f)
                    )

                }


            }

        },
        maxLines = 1,
        singleLine = true,
        visualTransformation = if (passwordVisible) {
            VisualTransformation.None
        } else {
            PasswordVisualTransformation()
        },
        keyboardOptions = KeyboardOptions(
            keyboardType = if (isInputTypePassword) KeyboardType.Password else  KeyboardType.Text,
            imeAction = if (isInputTypePassword) ImeAction.Send else ImeAction.Next
        ),
    )
}

@Composable
@Preview(showSystemUi = true, showBackground = true)
private fun InputTextView() {
    MaterialTheme {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center
        ) {
            InputView(leadingIcon = Icons.Default.Search,
isInputTypePassword = true,
                trailingIcon = null, placeHolder = "ssssss", input =  ""){

            }
        }
    }
}