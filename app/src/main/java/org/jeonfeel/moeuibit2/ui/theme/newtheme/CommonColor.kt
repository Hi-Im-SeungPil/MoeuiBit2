package org.jeonfeel.moeuibit2.ui.theme.newtheme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

val APP_PRIMARY_COLOR = Color(0xffF7A600)

@Composable
fun commonBackground(): Color = if (!isSystemInDarkTheme()) {
    Color.White
} else {
    Color(0xff080808)
}

@Composable
fun commonTextColor(): Color = if (!isSystemInDarkTheme()) {
    Color(0xff191919)
} else {
    Color(0xfff2f2f2)
}

