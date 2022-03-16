package org.jeonfeel.moeuibit2.util

import java.text.DecimalFormat
import kotlin.math.round

object Calculator {

    private val decimalFormat = DecimalFormat("###,###")

    fun tradePriceCalculator(tradePrice: Double): String {
        return if (tradePrice >= 100) {
            round(tradePrice).toString()
        } else if (tradePrice < 100 && tradePrice > 1) {
            String.format("%.2f",tradePrice)
        } else {
            String.format("%.4f",tradePrice)
        }
    }

    fun signedChangeRateCalculator(signedChangeRate: Double): String {
        return String.format("%.2f", signedChangeRate * 100)
    }

    fun accTradePrice24hCalculator(accTradePrice24h: Double): String {
        return decimalFormat.format(round(accTradePrice24h* 0.000001))
    }

}