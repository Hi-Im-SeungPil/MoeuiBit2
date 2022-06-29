package org.jeonfeel.moeuibit2.util

import java.text.DecimalFormat

val commaFormat = DecimalFormat("###,###")
val decimalFormat = DecimalFormat("#.00000000")
val percentFormat = DecimalFormat("0.00%")

fun Long.commaFormat(): String {
    return commaFormat.format(this)
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

fun Double.eighthDecimal(): String {
    return String.format("%.8f", this)
}

fun Float.firstDecimal(): String {
    return String.format("%.1f", this)
}

fun Float.secondDecimal(): String {
    return String.format("%.2f", this)
}

fun Float.forthDecimal(): String {
    return String.format("%.4f", this)
}

fun Float.eighthDecimal(): String {
    return String.format("%.8f", this)
}

