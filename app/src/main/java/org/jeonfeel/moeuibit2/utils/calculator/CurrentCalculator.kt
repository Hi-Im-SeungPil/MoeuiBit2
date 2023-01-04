package org.jeonfeel.moeuibit2.utils.calculator

import org.jeonfeel.moeuibit2.constants.SELECTED_BTC_MARKET
import org.jeonfeel.moeuibit2.constants.SELECTED_KRW_MARKET
import org.jeonfeel.moeuibit2.utils.*
import kotlin.math.abs
import kotlin.math.round

object CurrentCalculator {

    fun tradePriceCalculator(tradePrice: Double, marketState: Int): String {
        return if(marketState == SELECTED_KRW_MARKET) {
            if(tradePrice == 0.0 || tradePrice < 0.00000001) {
                "0"
            } else {
                if (tradePrice >= 100) {
                    round(tradePrice).toLong().commaFormat()
                } else if (tradePrice < 100 && tradePrice >= 1) {
                    tradePrice.secondDecimal()
                } else {
                    tradePrice.forthDecimal()
                }
            }
        } else if(marketState == SELECTED_BTC_MARKET) {
                tradePrice.eighthDecimal()
        } else {
            ""
        }
    }

    fun tradePriceCalculator(tradePrice: Float, marketState: Int): String {
        return if(marketState == SELECTED_KRW_MARKET) {
            if (tradePrice >= 100) {
                round(tradePrice).toLong().commaFormat()
            } else if (tradePrice < 100 && tradePrice >= 1) {
                tradePrice.secondDecimal()
            } else {
                tradePrice.forthDecimal()
            }
        } else if(marketState == SELECTED_BTC_MARKET) {
            tradePrice.eighthDecimal()
        } else {
            ""
        }
    }

    fun tradePriceCalculatorNoStringFormat(tradePrice: Double, marketState: Int): Double {
        return if(marketState == SELECTED_KRW_MARKET) {
            if(tradePrice == 0.0 || tradePrice < 0.00000001) {
                0.0
            } else {
                if (tradePrice >= 100) {
                    round(tradePrice)
                } else if (tradePrice < 100 && tradePrice >= 1) {
                    tradePrice.secondDecimal().toDouble()
                } else {
                    tradePrice.forthDecimal().toDouble()
                }
            }
        } else if(marketState == SELECTED_BTC_MARKET) {
            tradePrice.eighthDecimal().toDouble()
        } else {
            -1.0
        }
    }

    fun signedChangeRateCalculator(signedChangeRate: Double): String {
        return (signedChangeRate * 100).secondDecimal()
    }

    fun accTradePrice24hCalculator(accTradePrice24h: Double, marketState: Int): String {
        return when (marketState) {
            SELECTED_KRW_MARKET -> {
                round(accTradePrice24h * 0.000001).commaFormat()
            }
            SELECTED_BTC_MARKET -> {
                accTradePrice24h.thirdDecimal()
            }
            else -> {
                ""
            }
        }
    }

    fun krwToUsd(krw: Double,usd: Double): String {
        return if (krw == 0.0) {
            "0"
        } else {
            val tradePrice = krw / usd
            val absTradePrice = abs(tradePrice)
            if (absTradePrice >= 1000) {
                tradePrice.secondDecimal().toDouble().commaDecimalFormat()
            } else if (absTradePrice < 1000 && absTradePrice >= 1) {
                tradePrice.secondDecimal()
            } else if(absTradePrice < 1 && absTradePrice >= 0.0001 ){
                tradePrice.sixthDecimal()
            } else {
                tradePrice.eighthDecimal()
            }
        }

    }
}