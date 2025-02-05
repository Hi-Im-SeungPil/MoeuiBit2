package org.jeonfeel.moeuibit2.ui.theme.newtheme.coinsite

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

@Composable
fun coinSiteUnSelectTabColor() = if (!isSystemInDarkTheme()) {
    Color.LightGray
} else {
    Color.LightGray
}

@Composable
fun coinSiteIconBorderColor() = if (!isSystemInDarkTheme()) {
    Color(0xFFF1EFEF)
} else {
    Color(0xFFF1EFEF)
}

@Composable
fun coinSiteTitleDividerColor() = if (!isSystemInDarkTheme()) {
    Color(0xFFDFDFDF)
} else {
    Color(0xFFDFDFDF)
}