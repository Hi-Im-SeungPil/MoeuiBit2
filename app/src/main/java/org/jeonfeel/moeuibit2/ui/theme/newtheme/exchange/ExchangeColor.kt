package org.jeonfeel.moeuibit2.ui.theme.newtheme.exchange

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

@Composable
fun exchangeCautionColor(): Color = if (!isSystemInDarkTheme()) {
    Color(0xffF2AD24)
} else {
    Color(0xffF2AD24)
}

@Composable
fun exchangeWarningColor(): Color = if (!isSystemInDarkTheme()) {
    Color.Red
} else {
    Color.Red
}

@Composable
fun exchangeSortColor(): Color = if (!isSystemInDarkTheme()) {
    Color(0xff2454E6)
} else {
    Color(0xff2454E6)
}

@Composable
fun exchangeUnselectSortColor(): Color = if (!isSystemInDarkTheme()) {
    Color(0xff91959E)
} else {
    Color(0xff91959E)
}

@Composable
fun exchangeBTCtoKRWColor(): Color = if (!isSystemInDarkTheme()) {
    Color(0xff91959E)
} else {
    Color(0xff91959E)
}

@Composable
fun exchangeRiseColor(): Color = if (!isSystemInDarkTheme()) {
    Color(0xFFE15241)
} else {
    Color(0xFFE15241)
}

@Composable
fun exchangeFallColor(): Color = if (!isSystemInDarkTheme()) {
    Color(0xFF4A80EA)
} else {
    Color(0xFF4A80EA)
}

@Composable
fun exchangeEvenColor(): Color = if (!isSystemInDarkTheme()) {
    Color(0xFF000000)
} else {
    Color(0xFFFFFFFF)
}

@Composable
fun exchangeCoinListDividerColor(): Color = if (!isSystemInDarkTheme()) {
    Color(0xfff0f0f2)
} else {
    Color(0xfff0f0f2)
}