package org.jeonfeel.moeuibit2.ui.theme.newtheme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// FF181818 FFF7F7F7
@Composable
fun portfolioMainBackground(): Color = if (!isSystemInDarkTheme()) {
    Color(0xffF7F7F7)
} else {
    Color(0xFF181818)
}