package org.jeonfeel.moeuibit2.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import org.jeonfeel.moeuibit2.MoeuiBitDataStore.isKor
import org.jeonfeel.moeuibit2.R

val increase_color = if (isKor) {
    Color.Red
} else {
    Color(0xFF0ECB81)
}

val decrease_color = if (isKor) {
    Color.Blue
} else {
    Color.Red
}

@Composable
fun decreaseOrderBookColor(): Color {
    return if (!isSystemInDarkTheme()) {
        if (isKor) {
            Color(0x1A3654FF)
        } else {
            Color(0x1AFF3030)
        }
    } else {
        if (isKor) {
            Color(0x403654FF)
        } else {
            Color(0x40FF3030)
        }
    }
}

@Composable
fun increaseOrderBookColor(): Color {
    return if (!isSystemInDarkTheme()) {
        if (isKor) {
            Color(0x1AFF3030)
        } else {
            Color(0x1A0ECB81)
        }
    } else {
        if (isKor) {
            Color(0x40FF3030)
        } else {
            Color(0x400ECB81)
        }
    }
}

@Composable
fun increaseOrderBookBlockColor(): Color {
    return if (!isSystemInDarkTheme()) {
        if (isKor) {
            Color(0x33FF3030)
        } else {
            Color(0x330ECB81)
        }
    } else {
        if (isKor) {
            Color(0x59FF3030)
        } else {
            Color(0x590ECB81)
        }
    }
}

@Composable
fun decreaseOrderBookBlockColor(): Color {
    return if (!isSystemInDarkTheme()) {
        if (isKor) {
            Color(0x333654FF)
        } else {
            Color(0x33FF3030)
        }
    } else {
        if (isKor) {
            Color(0x593654FF)
        } else {
            Color(0x59FF3030)
        }
    }
}

val increase_candle_color = if (isKor) {
    android.graphics.Color.RED
} else {
    android.graphics.Color.parseColor("#FF0ECB81")
}

val decrease_candle_color = if (isKor) {
    android.graphics.Color.BLUE
} else {
    android.graphics.Color.RED
}

val increase_bar_color = if (isKor) {
    android.graphics.Color.parseColor("#4DFF0000")
} else {
    android.graphics.Color.parseColor("#4D0ECB81")
}

val decrease_bar_color = if (isKor) {
    android.graphics.Color.parseColor("#4D0100FF")
} else {
    android.graphics.Color.parseColor("#4DFF0000")
}