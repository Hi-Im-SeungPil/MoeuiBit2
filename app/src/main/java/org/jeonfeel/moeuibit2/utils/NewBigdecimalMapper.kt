package org.jeonfeel.moeuibit2.utils

import org.jeonfeel.moeuibit2.GlobalState
import org.jeonfeel.moeuibit2.constants.EXCHANGE_BITTHUMB
import org.jeonfeel.moeuibit2.constants.EXCHANGE_UPBIT
import org.jeonfeel.moeuibit2.constants.UPBIT_KRW_SYMBOL_PREFIX
import java.math.BigDecimal
import java.math.RoundingMode
import java.text.DecimalFormat

object NewBigDecimalMapper {
    private val groupFormat = DecimalFormat("###,###")
    private val quantityFormat = DecimalFormat("#,##0.00000000")

    private val upbitKrwRules = listOf(
        1_000.0 to 0,
        100.0 to 1,
        10.0 to 2,
        1.0 to 3,
        0.1 to 4,
        0.01 to 5,
        0.001 to 6,
        0.0001 to 7,
        0.0 to 8
    )

    private const val MILLION = 1_000_000L

    /**
     * Convert a Double price to BigDecimal with appropriate scale
     * based on exchange and market.
     */
    fun Double.toPriceBigDecimal(rootExchange: String, market: String): BigDecimal {
        val bd = BigDecimal(this)
        return when (GlobalState.globalExchangeState.value) {
            EXCHANGE_UPBIT -> when {
                market.startsWith(UPBIT_KRW_SYMBOL_PREFIX) && !market.containsAny("USDT", "USDC") ->
                    bd.applyScale(this, upbitKrwRules)

                market.startsWith(UPBIT_KRW_SYMBOL_PREFIX) ->
                    bd.setScale(1, RoundingMode.HALF_UP)

                else ->
                    bd.setScale(8, RoundingMode.HALF_UP)
            }

            EXCHANGE_BITTHUMB ->
                bd.setScale(0, RoundingMode.HALF_UP)

            else ->
                bd.setScale(0, RoundingMode.HALF_UP)
        }
    }

    /**
     * Common accumulation formatting rules for quantities
     */
    fun Double.toQuantityBigDecimal(): BigDecimal =
        BigDecimal(this).applyScale(this, upbitKrwRules)

    /**
     * General extension for Float and Double with fixed scale
     */
    fun Number.toBigDecimal(scale: Int = 0, mode: RoundingMode = RoundingMode.FLOOR): BigDecimal =
        BigDecimal(this.toString()).setScale(scale, mode)

    /**
     * Format BigDecimal with group separators if >= 1,000
     */
    fun BigDecimal.format(): String =
        if (abs() >= BigDecimal(1_000)) groupFormat.format(this) else toPlainString()

    /**
     * Format quantity with fixed 8-decimal pattern
     */
    fun BigDecimal.formatQuantity(): String =
        if (compareTo(BigDecimal.ZERO) == 0) "0" else quantityFormat.format(this)

    /**
     * Format with unit (백만) when >= 1,000,000
     */
    fun BigDecimal.formatWithUnit(): String =
        if (this >= MILLION.toBigDecimal()) {
            (divide(MILLION.toBigDecimal())
                .setScale(0, RoundingMode.HALF_UP)
                .format()) + " 백만"
        } else groupFormat.format(this)

    /**
     * Apply scale based on threshold rules
     */
    private fun BigDecimal.applyScale(
        value: Double,
        rules: List<Pair<Double, Int>>,
        mode: RoundingMode = RoundingMode.HALF_UP,
    ): BigDecimal {
        val scale = rules.first { value >= it.first }.second
        return setScale(scale, mode)
    }

    /**
     * Helper to check multiple substrings
     */
    private fun String.containsAny(vararg tokens: String): Boolean =
        tokens.any { contains(it, ignoreCase = true) }
}