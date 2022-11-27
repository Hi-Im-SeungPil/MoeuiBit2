package org.jeonfeel.moeuibit2.util

import org.jeonfeel.moeuibit2.constant.*

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

    fun getCoinDetailScreenInfo(marketState: Int, selectedTab: Int): List<String> {
        return if (marketState == SELECTED_KRW_MARKET && selectedTab == ASK_BID_SCREEN_BID_TAB) {
            listOf("5000 $SYMBOL_KRW", "0")
        } else if (marketState == SELECTED_KRW_MARKET && selectedTab == ASK_BID_SCREEN_ASK_TAB) {
            listOf("5000 $SYMBOL_KRW", "1")
        } else if(marketState == SELECTED_BTC_MARKET && selectedTab == ASK_BID_SCREEN_BID_TAB) {
            listOf("0.0005 $SYMBOL_BTC", "2")
        } else {
            listOf("0.0005 $SYMBOL_BTC", "3")
        }
    }
}