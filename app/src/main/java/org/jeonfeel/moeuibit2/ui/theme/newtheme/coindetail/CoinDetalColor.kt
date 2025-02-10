package org.jeonfeel.moeuibit2.ui.theme.newtheme.coindetail

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

@Composable
fun orderBookBidColor(): Color = if (!isSystemInDarkTheme()) {
    Color(0x0Dff2b2b)
} else {
    Color(0x26ff3838)
}

@Composable
fun orderBookAskColor(): Color = if (!isSystemInDarkTheme()) {
    Color(0x0D007fff)
} else {
    Color(0x261e6fdc)
}

@Composable
fun orderBookBidBlockColor(): Color = if (!isSystemInDarkTheme()) {
    Color(0x1Aff2b2b)
} else {
    Color(0x33ff3838)
}

@Composable
fun orderBookAskBlockColor(): Color = if (!isSystemInDarkTheme()) {
    Color(0x1A007fff)
} else {
    Color(0x331e6fdc)
}

@Composable
fun boxTextColor(): Color = if (!isSystemInDarkTheme()) {
    Color.DarkGray
} else {
    Color.LightGray
}

@Composable
fun unSelectedOrderTabColor(): Color = if (!isSystemInDarkTheme()) {
    Color(0xFFF5F5F5)
} else {
    Color(0xFF121212)
}

@Composable
fun unselectedOrderTabTextColor(): Color = if (!isSystemInDarkTheme()) {
    Color(0xFF616161)
} else {
    Color(0xFFBDBDBD)
}
