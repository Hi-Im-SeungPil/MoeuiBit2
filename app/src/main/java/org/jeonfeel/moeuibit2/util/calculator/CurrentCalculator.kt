package org.jeonfeel.moeuibit2.util.calculator

import org.jeonfeel.moeuibit2.constant.SELECTED_BTC_MARKET
import org.jeonfeel.moeuibit2.constant.SELECTED_KRW_MARKET
import org.jeonfeel.moeuibit2.util.*
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
}