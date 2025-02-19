package org.jeonfeel.moeuibit2.ui.theme.newtheme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

val APP_PRIMARY_COLOR = Color(0xff7b5ed6)

@Composable
fun commonBackground(): Color = if (!isSystemInDarkTheme()) {
    Color.White
} else {
    Color(0xff080808)
}

@Composable
fun commonBottomNavBackground(): Color = if (!isSystemInDarkTheme()) {
    Color.White
} else {
    Color(0xFF181818)
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
    Color(0xffe84a4a)
}

@Composable
fun commonFallColor(): Color = if (!isSystemInDarkTheme()) {
    Color(0xff007fff)
} else {
    Color(0xff3a78d4)
}

@Composable
fun commonUnSelectedColor(): Color = if (!isSystemInDarkTheme()) {
    Color(0xffa0a0a0)
} else {
    Color(0xff6d6d6d)
}

@Composable
fun commonDividerColor(): Color = if (!isSystemInDarkTheme()) {
    Color(0xFFEFEFEF)
} else {
    Color(0xff303030)
}

@Composable
fun commonSkeletonColor(): Color = if (!isSystemInDarkTheme()) {
    Color(0xffd6d6d6)
} else {
    Color(0xFF464646)
}

@Composable
fun commonHintTextColor(): Color = if (!isSystemInDarkTheme()) {
    Color(0xffa0a0a0)
} else {
    Color(0xff6e6e6e)
}