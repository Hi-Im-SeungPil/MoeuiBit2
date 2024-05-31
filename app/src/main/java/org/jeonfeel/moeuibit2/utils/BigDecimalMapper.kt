package org.jeonfeel.moeuibit2.utils

import java.math.BigDecimal
import java.math.RoundingMode
import java.text.DecimalFormat

object BigDecimalMapper {
    private val format: DecimalFormat = DecimalFormat("###,###")
    private const val MILLION = 1_000_000L
    private const val THOUSAND = 100_000_000L
    fun Double.newBigDecimal(
        scale: Int = 0,
        roundingMode: RoundingMode = RoundingMode.FLOOR
    ): BigDecimal {
        return BigDecimal(this).setScale(scale, roundingMode)
    }

    fun Float.newBigDecimal(
        scale: Int = 0,
        roundingMode: RoundingMode = RoundingMode.FLOOR
    ): BigDecimal {
        return BigDecimal(this.toString()).setScale(scale, roundingMode)
    }

    fun BigDecimal.formattedString(): String {
        return format.format(this)
    }

    fun BigDecimal.formattedUnitString(): String {
        return if (this >= BigDecimal.valueOf(MILLION)) {
            divide(BigDecimal.valueOf(MILLION))
                .setScale(0, RoundingMode.FLOOR).formattedString() + "백만"
        } else {
            toString()
        }
    }

    fun Float.formattedFluctuateString(): String {
        return newBigDecimal(scale = 2).toPlainString()
    }
}