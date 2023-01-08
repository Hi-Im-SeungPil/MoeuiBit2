package org.jeonfeel.moeuibit2.ui.custom

import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import org.jeonfeel.moeuibit2.R

val GmarketSans_light = FontFamily(
    Font(R.font.gmarket_sans_light)
)
val GmarketSans_medium = FontFamily(
    Font(R.font.gmarket_sans_medium)
)
val GmarketSans_bold = FontFamily(
    Font(R.font.gmarket_sans_bold)
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
        style = textStyle.copy(fontFamily = GmarketSans_light, fontSize = DpToSp(fontSize))
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
        style = textStyle.copy(fontFamily = GmarketSans_medium, fontSize = DpToSp(fontSize))
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
        style = textStyle.copy(fontFamily = GmarketSans_bold, fontSize = DpToSp(fontSize))
    )
}