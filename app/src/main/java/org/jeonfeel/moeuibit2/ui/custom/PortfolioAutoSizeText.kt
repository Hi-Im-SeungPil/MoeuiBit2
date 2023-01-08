package org.jeonfeel.moeuibit2.ui.custom

import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle

@Composable
fun PortfolioAutoSizeText(
    text: String,
    textStyle: TextStyle,
    modifier: Modifier = Modifier,
    color: Color = Color.Black
) {
    val scaledTextStyle = remember { mutableStateOf(textStyle) }
    val readyToDraw = remember { mutableStateOf(false) }
    scaledTextStyle.value = textStyle

    Text(
        text,
        modifier.drawWithContent {
            if (readyToDraw.value) {
                drawContent()
            }
        },
        style = scaledTextStyle.value.copy(color = color),
        softWrap = false,
        onTextLayout = { textLayoutResult ->
            if (textLayoutResult.didOverflowWidth) {
                scaledTextStyle.value =
                    scaledTextStyle.value.copy(fontSize = scaledTextStyle.value.fontSize * 0.9)
            } else {
                readyToDraw.value = true
            }
        }
    )
}