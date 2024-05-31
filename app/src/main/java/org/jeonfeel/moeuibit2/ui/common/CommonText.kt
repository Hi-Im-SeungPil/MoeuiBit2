package org.jeonfeel.moeuibit2.ui.common

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp

@Composable
fun CommonText(
    text: String,
    textStyle: TextStyle,
    fontSize: Dp,
    modifier: Modifier = Modifier
) {
    Text(text = text, style = textStyle, fontSize = DpToSp(dp = fontSize), modifier = modifier)
}