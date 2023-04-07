package org.jeonfeel.moeuibit2.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import org.jeonfeel.moeuibit2.R

@Composable
fun tabRowSelectedColor(): Color {
    return if(!isSystemInDarkTheme()) {
        colorResource(id = R.color.C0F0F5C)
    } else {
        Color(0xFF6464FF)
    }
}

@Composable
fun orderBookLineColor(): Color {
    return if(!isSystemInDarkTheme()) {
        Color.White
    } else {
        Color(0xFF35363A)
    }
}

@Composable
fun orderBookTabBackground(): Color {
    return if(!isSystemInDarkTheme()) {
        colorResource(id = R.color.CF6F6F6)
    } else {
        Color(0xFF35363A)
    }
}

@Composable
fun orderBookTabTextColor(): Color {
    return if(!isSystemInDarkTheme()) {
        colorResource(id = R.color.C0F0F5C)
    } else {
        Color(0xFF6464FF)
    }
}