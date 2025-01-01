package org.jeonfeel.moeuibit2.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

val increase_color =
    Color.Red


val decrease_color =
    Color.Blue


@Composable
fun decreaseOrderBookColor(): Color {
    return if (!isSystemInDarkTheme()) {
        Color(0x1A3654FF)

    } else {
        Color(0x403654FF)

    }
}

@Composable
fun increaseOrderBookColor(): Color {
    return if (!isSystemInDarkTheme()) {
        Color(0x1AFF3030)

    } else {
        Color(0x40FF3030)

    }
}

@Composable
fun increaseOrderBookBlockColor(): Color {
    return if (!isSystemInDarkTheme()) {
        Color(0x33FF3030)

    } else {
        Color(0x59FF3030)

    }
}

@Composable
fun decreaseOrderBookBlockColor(): Color {
    return if (!isSystemInDarkTheme()) {
        Color(0x333654FF)

    } else {
        Color(0x593654FF)

    }
}

val increase_candle_color =
    android.graphics.Color.RED


val decrease_candle_color =
    android.graphics.Color.BLUE


val increase_bar_color =
    android.graphics.Color.parseColor("#4DFF0000")


val decrease_bar_color =
    android.graphics.Color.parseColor("#4D0100FF")
