package org.jeonfeel.moeuibit2.util.calculator

import org.jeonfeel.moeuibit2.util.commaFormat
import org.jeonfeel.moeuibit2.util.forthDecimal
import org.jeonfeel.moeuibit2.util.secondDecimal
import kotlin.math.round

object ExchangeCalculator {

    fun tradePriceCalculator(tradePrice: Double): String {
        return if (tradePrice >= 100) {
            round(tradePrice).toLong().commaFormat()
        } else if (tradePrice < 100 && tradePrice >= 1) {
            tradePrice.secondDecimal()
        } else {
            tradePrice.forthDecimal()
        }
    }

    fun signedChangeRateCalculator(signedChangeRate: Double): String {
        return (signedChangeRate * 100).secondDecimal()
    }

    fun accTradePrice24hCalculator(accTradePrice24h: Double): String {
        return round(accTradePrice24h * 0.000001).commaFormat()
    }
}