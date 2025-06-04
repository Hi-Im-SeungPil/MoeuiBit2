package org.jeonfeel.moeuibit2.utils

import org.jeonfeel.moeuibit2.constants.BTC_SYMBOL_PREFIX
import org.jeonfeel.moeuibit2.constants.KRW_SYMBOL_PREFIX
import java.text.DecimalFormat

object DecimalFormats {
    val COMMA: DecimalFormat by lazy { DecimalFormat("###,###") }
    val DECIMAL: DecimalFormat by lazy { DecimalFormat("###,###.##") }
    val EIGHT_DECIMAL: DecimalFormat by lazy { DecimalFormat("###,###.########") }
    val PERCENT: DecimalFormat by lazy { DecimalFormat("0.00%") }
    val DECIMAL_POINT: DecimalFormat by lazy { DecimalFormat("0.######") }
}

/**
 * Returns the correct Korean postposition for the given subject string.
 */
fun String.getKoreanPostPosition(): String {
    if (this.isEmpty()) return "는"
    val lastCharCode = this.last().code
    return if ((lastCharCode - 0xAC00) % 28 == 0) "는" else "은"
}

/**
 * Extension to detect if a symbol indicates a KRW trading currency.
 */
fun String.isKrwTradeCurrency(): Boolean =
    startsWith(KRW_SYMBOL_PREFIX) || !startsWith(BTC_SYMBOL_PREFIX)

// -- Numeric formatting extensions --

fun Long.formatWithComma(): String = DecimalFormats.COMMA.format(this)
fun Int.formatWithComma(): String = DecimalFormats.COMMA.format(this)
fun String.formatWithComma(): String = this.toLongOrNull()?.formatWithComma() ?: this

fun Double.formatWithComma(): String = DecimalFormats.COMMA.format(this)
fun Double.formatDecimal(): String = DecimalFormats.DECIMAL.format(this)
fun Double.formatEightDecimal(): String =
    if (this == 0.0) "0" else DecimalFormats.EIGHT_DECIMAL.format(this)
fun Double.formatDecimalPoint(): String = DecimalFormats.DECIMAL_POINT.format(this)
fun Double.formatPercent(): String = DecimalFormats.PERCENT.format(this)

/**
 * Formats this Double with the specified number of decimal places.
 */
fun Double.formatPrecision(places: Int): String = "%.$places${ 'f' }".format(this)
fun Double.firstDecimal(): String = formatPrecision(1)
fun Double.secondDecimal(): String = formatPrecision(2)
fun Double.thirdDecimal(): String = formatPrecision(3)
fun Double.fourthDecimal(): String = formatPrecision(4)
fun Double.fifthDecimal(): String = formatPrecision(5)
fun Double.sixthDecimal(): String = formatPrecision(6)
fun Double.seventhDecimal(): String = formatPrecision(7)
fun Double.eighthDecimal(): String = formatPrecision(8)

fun Float.formatPrecision(places: Int): String = "%.$places${ 'f' }".format(this)
fun Float.firstDecimal(): String = formatPrecision(1)
fun Float.secondDecimal(): String = formatPrecision(2)
fun Float.thirdDecimal(): String = formatPrecision(3)
fun Float.fourthDecimal(): String = formatPrecision(4)
fun Float.fifthDecimal(): String = formatPrecision(5)
fun Float.sixthDecimal(): String = formatPrecision(6)
fun Float.seventhDecimal(): String = formatPrecision(7)
fun Float.eighthDecimal(): String = formatPrecision(8)