package org.jeonfeel.moeuibit2.utils

import org.jeonfeel.moeuibit2.constants.UPBIT_BTC_SYMBOL_PREFIX
import org.jeonfeel.moeuibit2.constants.UPBIT_KRW_SYMBOL_PREFIX
import java.text.DecimalFormat

val commaFormat = DecimalFormat("###,###")
val decimalFormat = DecimalFormat("###,###.##")
val eightDecimalFormat = DecimalFormat("###,###.########")
val percentFormat = DecimalFormat("0.00%")
val decimalPoint = DecimalFormat("0.######")

fun getPostposition(subject: String): String {
    val lastChar = subject.lastOrNull() ?: return "는" // 빈 문자열 처리
    val unicode = lastChar.code
    return if ((unicode - 0xAC00) % 28 == 0) "는" else "은"
}

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
    return if(this == 0.0) "0" else String.format("%.8f", this)
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