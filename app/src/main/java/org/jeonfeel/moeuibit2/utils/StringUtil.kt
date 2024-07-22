package org.jeonfeel.moeuibit2.utils

import android.content.Context
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import com.orhanobut.logger.Logger
import org.jeonfeel.moeuibit2.R
import org.jeonfeel.moeuibit2.constants.CAUTION
import org.jeonfeel.moeuibit2.constants.UPBIT_BTC_SYMBOL_PREFIX
import org.jeonfeel.moeuibit2.constants.UPBIT_KRW_SYMBOL_PREFIX
import java.text.DecimalFormat

val commaFormat = DecimalFormat("###,###")
val decimalFormat = DecimalFormat("###,###.##")
val eightDecimalFormat = DecimalFormat("###,###.########")
val percentFormat = DecimalFormat("0.00%")
val decimalPoint = DecimalFormat("0.######")

fun Long.commaFormat(): String {
    return commaFormat.format(this)
}

fun String.isTradeCurrencyKrw(): Boolean {
    return when {
        this.startsWith(UPBIT_KRW_SYMBOL_PREFIX) -> true
        this.startsWith(UPBIT_BTC_SYMBOL_PREFIX) -> false
        else -> true
    }
}

fun String.commaFormat(): String {
    return commaFormat.format(this)
}

fun Int.commaFormat(): String {
    return commaFormat.format(this)
}

fun Double.eightDecimalCommaFormat(): String {
    return eightDecimalFormat.format(this)
}

fun Double.commaDecimalFormat(): String {
    return decimalFormat.format(this)
}

fun Double.commaFormat(): String {
    return commaFormat.format(this)
}

fun Double.firstDecimal(): String {
    return String.format("%.1f", this)
}

fun Double.secondDecimal(): String {
    return String.format("%.2f", this)
}

fun Double.thirdDecimal(): String {
    return String.format("%.3f", this)
}

fun Double.forthDecimal(): String {
    return String.format("%.4f", this)
}

fun Double.fiveDecimal(): String {
    return String.format("%.5f", this)
}

fun Double.sixthDecimal(): String {
    return String.format("%.6f", this)
}

fun Double.sevenDecimal(): String {
    return String.format("%.7f", this)
}

fun Double.eighthDecimal(): String {
    return String.format("%.8f", this)
}

fun Float.firstDecimal(): String {
    return String.format("%.1f", this)
}

fun Float.secondDecimal(): String {
    return String.format("%.2f", this)
}

fun Float.thirdDecimal(): String {
    return String.format("%.3f", this)
}

fun Float.forthDecimal(): String {
    return String.format("%.4f", this)
}

fun Float.fiveDecimal(): String {
    return String.format("%.5f", this)
}

fun Float.sixthDecimal(): String {
    return String.format("%.6f", this)
}

fun Float.sevenDecimal(): String {
    return String.format("%.7f", this)
}

fun Float.eighthDecimal(): String {
    return String.format("%.8f", this)
}

fun Double.decimalPoint(): String {
    return decimalPoint.format(this)
}

fun getCoinDetailTitle(warning: Boolean, context: Context, koreanCoinName: String) {
//    Logger.e("koreanCoinName3 $koreanCoinName")
//    return buildAnnotatedString {
//        if (warning) {
//            withStyle(
//                style = SpanStyle(
//                    color = Color.Yellow,
//                    fontWeight = FontWeight.Bold
//                )
//            ) {
//                append(context.getString(R.string.CAUTION_KOREAN))
//            }
//        }
//        append(koreanCoinName)
//    }
}