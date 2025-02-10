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

@Composable
fun commonRiseColor(): Color = if (!isSystemInDarkTheme()) {
    Color(0xffff2b2b)
} else {
    Color(0xffff3838)
}

@Composable
fun commonFallColor(): Color = if (!isSystemInDarkTheme()) {
    Color(0xff007fff)
} else {
    Color(0xff1e6fdc)
}

@Composable
fun commonUnSelectedColor(): Color = if (!isSystemInDarkTheme()) {
    Color(0xffa0a0a0)
} else {
    Color(0xff6d6d6d)
}