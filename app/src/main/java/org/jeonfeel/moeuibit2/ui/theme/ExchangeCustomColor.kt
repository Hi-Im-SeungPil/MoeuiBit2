package org.jeonfeel.moeuibit2.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import org.jeonfeel.moeuibit2.R

@Composable
fun decreaseColor(): Color {
    return if (!isSystemInDarkTheme()) {
        Color.Blue

    } else {
        Color(0xFF6E6EFA)

    }
}

@Composable
fun increaseColor(): Color {
    return if (!isSystemInDarkTheme()) {
        Color.Red
    } else {
        Color(0xFFFF4646)
    }
}

@Composable
fun lazyColumnItemUnderLineColor(): Color {
    return if (!isSystemInDarkTheme()) {
        Color.LightGray
    } else {
        Color(0xFF35363A)
    }
}

@Composable
fun loadingColor(): Color {
    return if (!isSystemInDarkTheme()) {
        colorResource(id = R.color.CECECEC)
    } else {
        Color(0xFF35363A)
    }
}