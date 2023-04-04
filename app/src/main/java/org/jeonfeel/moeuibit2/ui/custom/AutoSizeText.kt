package org.jeonfeel.moeuibit2.ui.custom

import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle

@Composable
fun AutoSizeText(
    modifier: Modifier = Modifier,
    text: String,
    textStyle: TextStyle? = MaterialTheme.typography.body1,
    color: Color =  androidx.compose.material3.MaterialTheme.colorScheme.onBackground
) {
    val readyToDraw = remember { mutableStateOf(false) }
    val rememberTextStyle = remember {
        mutableStateOf(textStyle)
    }

    Text(
        text,
        modifier.drawWithContent {
            if (readyToDraw.value) {
                drawContent()
            }
        },
        style = rememberTextStyle.value!!.copy(color = color),
        softWrap = false,
        onTextLayout = { textLayoutResult ->
            if (textLayoutResult.didOverflowWidth) {
                rememberTextStyle.value =
                    rememberTextStyle.value!!.copy(fontSize = rememberTextStyle.value!!.fontSize * 0.9)
            } else {
                readyToDraw.value = true
            }
        }
    )
}
