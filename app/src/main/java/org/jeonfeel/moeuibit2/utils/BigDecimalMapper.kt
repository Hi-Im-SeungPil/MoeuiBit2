package org.jeonfeel.moeuibit2.utils

import org.jeonfeel.moeuibit2.constants.UPBIT_KRW_SYMBOL_PREFIX
import org.jeonfeel.moeuibit2.ui.main.exchange.ExchangeViewModel.Companion.ROOT_EXCHANGE_BITTHUMB
import org.jeonfeel.moeuibit2.ui.main.exchange.ExchangeViewModel.Companion.ROOT_EXCHANGE_UPBIT
import java.math.BigDecimal
import java.math.RoundingMode
import java.text.DecimalFormat

object BigDecimalMapper {
    private val format: DecimalFormat = DecimalFormat("###,###")
    private val decimalFormatQuantity: DecimalFormat = DecimalFormat("#,###.########")
    private const val MILLION = 1_000_000L
    private const val THOUSAND = 100_000_000L
    fun Double.newBigDecimal(rootExchange: String, market: String): BigDecimal {
        return when (rootExchange) {
            ROOT_EXCHANGE_UPBIT -> {
                when {
                    market.startsWith(UPBIT_KRW_SYMBOL_PREFIX) -> {
                        when {
                            this >= 1_000 -> BigDecimal(this).setScale(0, RoundingMode.HALF_UP)
                            this >= 100 -> BigDecimal(this).setScale(1, RoundingMode.HALF_UP)
                            this >= 10 -> BigDecimal(this).setScale(2, RoundingMode.HALF_UP)
                            this >= 1 -> BigDecimal(this).setScale(3, RoundingMode.HALF_UP)
                            this >= 0.1 -> BigDecimal(this).setScale(4, RoundingMode.HALF_UP)
                            this >= 0.01 -> BigDecimal(this).setScale(5, RoundingMode.HALF_UP)
                            this >= 0.001 -> BigDecimal(this).setScale(6, RoundingMode.HALF_UP)
                            this >= 0.0001 -> BigDecimal(this).setScale(7, RoundingMode.HALF_UP)
                            else -> BigDecimal(this).setScale(8, RoundingMode.HALF_UP)
                        }
                    }

                    else -> {
                        BigDecimal(this).setScale(8, RoundingMode.HALF_UP)
                    }
                }
            }

            ROOT_EXCHANGE_BITTHUMB -> {
                BigDecimal(this).setScale(0, RoundingMode.HALF_UP)
            }

            else -> {
                BigDecimal(this).setScale(0, RoundingMode.HALF_UP)
            }
        }
    }

    fun Double.accBigDecimal(
        scale: Int = 0,
        roundingMode: RoundingMode = RoundingMode.HALF_UP
    ): BigDecimal {
        return when {
            this >= 1_000 -> BigDecimal(this).setScale(1, RoundingMode.HALF_UP)
            this >= 100 -> BigDecimal(this).setScale(3, RoundingMode.HALF_UP)
            this >= 10 -> BigDecimal(this).setScale(3, RoundingMode.HALF_UP)
            this >= 1 -> BigDecimal(this).setScale(3, RoundingMode.HALF_UP)
            this >= 0.1 -> BigDecimal(this).setScale(3, RoundingMode.HALF_UP)
            this >= 0.01 -> BigDecimal(this).setScale(3, RoundingMode.HALF_UP)
            this >= 0.001 -> BigDecimal(this).setScale(3, RoundingMode.HALF_UP)
            this >= 0.0001 -> BigDecimal(this).setScale(4, RoundingMode.HALF_UP)
            else -> BigDecimal(this).setScale(5, RoundingMode.HALF_UP)
        }
    }

    fun Float.newBigDecimal(
        scale: Int = 0,
        roundingMode: RoundingMode = RoundingMode.FLOOR
    ): BigDecimal {
        return BigDecimal(this.toString()).setScale(scale, roundingMode)
    }

    fun Double.newBigDecimal(
        scale: Int = 0,
        roundingMode: RoundingMode = RoundingMode.FLOOR
    ): BigDecimal {
        return BigDecimal(this.toString()).setScale(scale, roundingMode)
    }

    fun BigDecimal.formattedString(): String {
        return when {
            this >= BigDecimal("1000") -> format.format(this)
            else -> this.toPlainString()
        }
    }

    fun BigDecimal.formattedStringForBtc(): String {
        return when {
            this >= BigDecimal("1000") -> format.format(this.setScale(0, RoundingMode.HALF_UP))
            this >= BigDecimal("100") -> this.setScale(1, RoundingMode.HALF_UP).toPlainString()
            this >= BigDecimal("10") -> this.setScale(2, RoundingMode.HALF_UP).toPlainString()
            this >= BigDecimal("1") -> this.setScale(3, RoundingMode.HALF_UP).toPlainString()
            this >= BigDecimal("0.1") -> this.setScale(4, RoundingMode.HALF_UP).toPlainString()
            this >= BigDecimal("0.01") -> this.setScale(5, RoundingMode.HALF_UP).toPlainString()
            this >= BigDecimal("0.001") -> this.setScale(6, RoundingMode.HALF_UP).toPlainString()
            this >= BigDecimal("0.0001") -> this.setScale(7, RoundingMode.HALF_UP).toPlainString()
            else -> this.setScale(8, RoundingMode.HALF_UP).toPlainString()
        }
    }

    fun BigDecimal.formattedUnitString(): String {
        return if (this >= BigDecimal.valueOf(MILLION)) {
            divide(BigDecimal.valueOf(MILLION))
                .setScale(0, RoundingMode.HALF_UP).formattedString() + " 백만"
        } else {
            toString()
        }
    }

    fun BigDecimal.formattedUnitStringForBtc(): String {
        return if (this >= BigDecimal.valueOf(MILLION)) {
            divide(BigDecimal.valueOf(MILLION))
                .setScale(0, RoundingMode.HALF_UP).formattedString() + " 백만"
        } else {
            format.format(this)
        }
    }

    fun BigDecimal.formattedStringForQuantity(): String {
        return decimalFormatQuantity.format(this)
    }

    fun Float.formattedFluctuateString(): String {
        return newBigDecimal(scale = 2).toPlainString()
    }
}