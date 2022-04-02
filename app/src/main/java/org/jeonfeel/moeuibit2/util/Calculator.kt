package org.jeonfeel.moeuibit2.util

import java.text.DecimalFormat
import kotlin.math.abs
import kotlin.math.round

object Calculator {

    private val decimalFormat = DecimalFormat("###,###")

    fun tradePriceCalculator(tradePrice: Double): String {
        return if (tradePrice >= 100) {
            round(tradePrice).toInt().toString()
        } else if (tradePrice < 100 && tradePrice > 1) {
            String.format("%.2f", tradePrice)
        } else {
            String.format("%.4f", tradePrice)
        }
    }

    fun signedChangeRateCalculator(signedChangeRate: Double): String {
        return String.format("%.2f", signedChangeRate * 100)
    }

    fun accTradePrice24hCalculator(accTradePrice24h: Double): String {
        return decimalFormat.format(round(accTradePrice24h * 0.000001))
    }

    fun changePriceCalculator(changePrice: Double): String {
        val absChangePrice = abs(changePrice)
        var result = ""
        if (absChangePrice >= 100) {
            result = decimalFormat.format(round(changePrice).toInt())
        } else if (absChangePrice < 100 && absChangePrice >= 0) {
            result =  String.format("%.2f", changePrice)
        } else {
            result = String.format("%.4f", changePrice)
        }

        if(changePrice > 0.0) {
            return "+".plus(result)
        }
        return result
    }

    fun getDecimalFormat(): DecimalFormat {
        return decimalFormat
    }
}