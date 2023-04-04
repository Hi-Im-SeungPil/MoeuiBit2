package org.jeonfeel.moeuibit2.utils

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import org.jeonfeel.moeuibit2.MoeuiBitDataStore
import org.jeonfeel.moeuibit2.constants.*
import org.jeonfeel.moeuibit2.ui.theme.decreaseColor
import org.jeonfeel.moeuibit2.ui.theme.decrease_color
import org.jeonfeel.moeuibit2.ui.theme.increaseColor
import org.jeonfeel.moeuibit2.ui.theme.increase_color
import java.util.*

object Utils {

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
        } else if (marketState == SELECTED_BTC_MARKET && selectedTab == ASK_BID_SCREEN_BID_TAB) {
            listOf("0.0005 $SYMBOL_BTC", "2")
        } else {
            listOf("0.0005 $SYMBOL_BTC", "3")
        }
    }

    fun removeComma(price: String): String {
        val temp = price.split(",")
        var krwPrice = ""
        for (i in temp.indices) {
            krwPrice += temp[i]
        }
        return krwPrice
    }

    @Composable
    fun getIncreaseOrDecreaseColor(value: Float): Color {
        return when {
            value > 0 -> {
                increaseColor()
            }
            value < 0 -> {
                decreaseColor()
            }
            else -> {
                MaterialTheme.colorScheme.onBackground
            }
        }
    }

    fun getPortfolioName(marketState: Int, name: String): String {
        return if(marketState == SELECTED_BTC_MARKET) {
            "[$SYMBOL_BTC] $name"
        } else {
            name
        }
    }

    fun getLocale() {
        MoeuiBitDataStore.isKor = Locale.getDefault().language == "ko"
    }
}