package org.jeonfeel.moeuibit2.utils

import java.text.DecimalFormat

val commaFormat = DecimalFormat("###,###")
val decimalFormat = DecimalFormat("###,###.##")
val eightDecimalFormat = DecimalFormat("###,###.########")
val percentFormat = DecimalFormat("0.00%")

fun Long.commaFormat(): String {
    return commaFormat.format(this)
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