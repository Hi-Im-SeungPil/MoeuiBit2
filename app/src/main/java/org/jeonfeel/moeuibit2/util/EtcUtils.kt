package org.jeonfeel.moeuibit2.util

import org.jeonfeel.moeuibit2.constant.SELECTED_BTC_MARKET
import org.jeonfeel.moeuibit2.constant.SELECTED_KRW_MARKET
import org.jeonfeel.moeuibit2.constant.SYMBOL_BTC
import org.jeonfeel.moeuibit2.constant.SYMBOL_KRW

object EtcUtils {

    fun getSelectedMarket(market: String): Int {
        return if (market.startsWith(SYMBOL_KRW)) {
            SELECTED_KRW_MARKET
        } else if (market.startsWith(SYMBOL_BTC)) {
            SELECTED_BTC_MARKET
        } else {
            -999
        }
    }

    fun getUnit(marketState: Int): String =
        if (marketState == SELECTED_KRW_MARKET) {
            SYMBOL_KRW
        } else {
            SYMBOL_BTC
        }

    fun getCoinDetailScreenInfo(marketState: Int): List<String> {
        return if (marketState == SELECTED_KRW_MARKET) {
            listOf("5000 $SYMBOL_KRW", "0.05%")
        } else {
            listOf("0.0005 $SYMBOL_BTC", "0.25%")
        }
    }
}