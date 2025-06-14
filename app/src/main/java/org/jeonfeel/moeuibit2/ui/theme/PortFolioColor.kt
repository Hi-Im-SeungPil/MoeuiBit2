package org.jeonfeel.moeuibit2.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import org.jeonfeel.moeuibit2.R

@Composable
fun portfolioSortButtonSelectedBackgroundColor(): Color {
    return if (!isSystemInDarkTheme()) {
        colorResource(id = R.color.C0F0F5C)
    } else {
        Color(0xFF6464FF)
    }
}