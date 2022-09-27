package org.jeonfeel.moeuibit2.util

import org.jeonfeel.moeuibit2.constant.SELECTED_BTC_MARKET
import org.jeonfeel.moeuibit2.constant.SELECTED_KRW_MARKET

object EtcUtils {

    fun getSelectedMarket(market: String): Int {
        return if (market.startsWith("KRW-")) {
            SELECTED_KRW_MARKET
        } else if (market.startsWith("BTC-")) {
            SELECTED_BTC_MARKET
        } else {
            -999
        }
    }

    fun getUnit(marketState: Int): String =
        if (marketState == SELECTED_KRW_MARKET) {
            "KRW"
        } else {
            "BTC"
        }
}