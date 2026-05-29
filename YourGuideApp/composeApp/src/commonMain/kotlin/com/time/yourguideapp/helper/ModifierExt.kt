package com.time.yourguideapp.helper

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.shape.GenericShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.paint
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import com.time.yourguideapp.AppColors.black
import org.jetbrains.compose.resources.painterResource
import yourguideapp.composeapp.generated.resources.Res
import yourguideapp.composeapp.generated.resources.root_background

/*fun Modifier.rootBackground(): Modifier{
    return this.background(
        Brush.verticalGradient(
            colors = listOf(
                blue245bc8,
                black
            )
        )
    )
}*/

fun Modifier.profileCardShape(): Modifier {
    return clip(
        GenericShape { size, _ ->

            val corner = 40f
            val notchRadius = 80f
            val centerX = size.width / 2f

            moveTo(0f, corner)

            quadraticBezierTo(0f, 0f, corner, 0f)

            lineTo(centerX - notchRadius, 0f)

            cubicTo(
                centerX - notchRadius / 2f, 0f,
                centerX - notchRadius / 2f, -notchRadius,
                centerX, -notchRadius
            )

            cubicTo(
                centerX + notchRadius / 2f, -notchRadius,
                centerX + notchRadius / 2f, 0f,
                centerX + notchRadius, 0f
            )

            lineTo(size.width - corner, 0f)

            quadraticBezierTo(size.width, 0f, size.width, corner)

            lineTo(size.width, size.height - corner)

            quadraticBezierTo(
                size.width,
                size.height,
                size.width - corner,
                size.height
            )

            lineTo(corner, size.height)

            quadraticBezierTo(0f, size.height, 0f, size.height - corner)

            close()
        }
    )
}

@Composable
fun Modifier.rootBackground(): Modifier = this.then(
    Modifier.paint(
        painter = painterResource(Res.drawable.root_background),
        contentScale = ContentScale.Crop
    )
)

fun Modifier.glassmorphism(
    shape: Shape = RoundedCornerShape(20.dp),
    backgroundColor : Color =  Color.White.copy(alpha = 0.15f),
    borderColor : Color = Color.White.copy(alpha = 0.25f),
    widthBorder : Int = 1
): Modifier {

    return this
        .clip(shape)
        .background(
           backgroundColor
        )
        .border(
            widthBorder.dp,
            borderColor,
            shape
        )
}
