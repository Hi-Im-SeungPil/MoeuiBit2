package org.jeonfeel.moeuibit2.ui.theme

import androidx.compose.ui.graphics.Color
import org.jeonfeel.moeuibit2.MoeuiBitDataStore.isKor

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

val increase_order_book_color = if (isKor) {
    Color(0x1AFF3030)
} else {
    Color(0x1A0ECB81)
}

val decrease_order_book_color = if (isKor) {
    Color(0x1A3654FF)
} else {
    Color(0x1AFF3030)
}

val increase_order_book_block_color = if (isKor) {
    Color(0x33FF3030)
} else {
    Color(0x4D0ECB81)
}

val decrease_order_book_block_color = if (isKor) {
    Color(0x333654FF)
} else {
    Color(0x33FF3030)
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