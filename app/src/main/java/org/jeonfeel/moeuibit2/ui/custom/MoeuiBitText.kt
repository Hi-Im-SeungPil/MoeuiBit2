package org.jeonfeel.moeuibit2.ui.custom

import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import org.jeonfeel.moeuibit2.R

val NotoSans_light = FontFamily(
    Font(R.font.notosans_black)
)
val NotoSans_medium = FontFamily(
    Font(R.font.notosans_medium)
)
val NotoSans_bold = FontFamily(
    Font(R.font.notosans_bold)
)

@Composable
fun FontLightText(
    text: String,
    modifier: Modifier = Modifier,
    fontSize: Int = 14,
    textStyle: TextStyle = TextStyle()
) {
    Text(
        text = text,
        modifier = modifier,
        style = textStyle.copy(fontFamily = NotoSans_light, fontSize = DpToSp(fontSize.dp))
    )
}

@Composable
fun FontMediumText(
    text: String,
    modifier: Modifier,
    fontSize: Int = 14,
    textStyle: TextStyle = TextStyle()
) {
    Text(
        text = text,
        modifier = modifier,
        style = textStyle.copy(fontFamily = NotoSans_medium, fontSize = DpToSp(fontSize.dp))
    )
}

@Composable
fun FontBoldText(
    text: String,
    modifier: Modifier,
    fontSize: Int = 14,
    textStyle: TextStyle = TextStyle()
) {
    Text(
        text = text,
        modifier = modifier,
        style = textStyle.copy(fontFamily = NotoSans_bold, fontSize = DpToSp(fontSize.dp))
    )
}