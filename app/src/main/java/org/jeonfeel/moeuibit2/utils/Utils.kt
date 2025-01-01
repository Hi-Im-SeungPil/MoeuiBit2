package org.jeonfeel.moeuibit2.utils

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.orhanobut.logger.Logger
import org.jeonfeel.moeuibit2.constants.*
import org.jeonfeel.moeuibit2.data.network.retrofit.model.upbit.CommonExchangeModel
import org.jeonfeel.moeuibit2.data.network.retrofit.response.upbit.UpbitMarketCodeRes
import org.jeonfeel.moeuibit2.ui.main.exchange.component.SortOrder
import org.jeonfeel.moeuibit2.ui.main.exchange.component.SortType
import org.jeonfeel.moeuibit2.ui.theme.decreaseColor
import org.jeonfeel.moeuibit2.ui.theme.increaseColor
import java.math.BigDecimal
import java.text.SimpleDateFormat
import java.util.*

object Utils {
    val gson = Gson()

    fun isNetworkAvailable(context: Context): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork ?: return false
        val networkCapabilities = connectivityManager.getNetworkCapabilities(network) ?: return false

        return networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }

    fun String.coinOrderIsKrwMarket(): String {
        return when (this.isTradeCurrencyKrw()) {
            true -> {
                this
            }

            false -> {
                "$this,KRW-BTC"
            }
        }
    }

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
        return if (marketState == SELECTED_BTC_MARKET) {
            "[$SYMBOL_BTC] $name"
        } else {
            name
        }
    }

    fun divideKrwResBtcRes(list: List<UpbitMarketCodeRes>): Pair<List<UpbitMarketCodeRes>, List<UpbitMarketCodeRes>> {
        val krwList = list.filter { it.market.contains(UPBIT_KRW_SYMBOL_PREFIX) }.toList()
        val btcList = list.filter { it.market.contains(UPBIT_BTC_SYMBOL_PREFIX) }.toList()
        return Pair(krwList, btcList)
    }

    fun filterTickerList(
        exchangeModelList: List<CommonExchangeModel>,
        searchStr: String
    ): List<CommonExchangeModel> {
        val strResult = searchStr.uppercase()
        return when {
            strResult.isEmpty() -> exchangeModelList
            else -> {
                exchangeModelList.filter {
                    it.symbol.uppercase().contains(strResult)
                            || it.koreanName.uppercase().contains(strResult)
                            || it.englishName.uppercase().contains(strResult)
                            || it.initialConstant.uppercase().contains(strResult)
                }
            }
        }
    }

    fun sortTickerList(
        tickerList: List<CommonExchangeModel>,
        sortType: SortType,
        sortOrder: SortOrder,
        btcPrice: BigDecimal? = null
    ): List<CommonExchangeModel> {
        return when (sortType) {
            SortType.DEFAULT -> {
                tickerList.sortedByDescending { it.accTradePrice24h }
            }

            SortType.PRICE -> {
                when (sortOrder) {
                    SortOrder.DESCENDING -> {
                        tickerList.sortedByDescending {
                            if (btcPrice != null
                                && it.market.startsWith(UPBIT_BTC_SYMBOL_PREFIX)
                            ) {
                                it.tradePrice * btcPrice
                            } else {
                                it.tradePrice
                            }
                        }
                    }

                    SortOrder.ASCENDING -> {
                        tickerList.sortedBy {
                            if (btcPrice != null
                                && it.market.startsWith(UPBIT_BTC_SYMBOL_PREFIX)
                            ) {
                                it.tradePrice * btcPrice
                            } else {
                                it.tradePrice
                            }
                        }
                    }

                    SortOrder.NONE -> {
                        tickerList.sortedByDescending {
                            if (btcPrice != null
                                && it.market.startsWith(UPBIT_BTC_SYMBOL_PREFIX)
                            ) {
                                it.accTradePrice24h * btcPrice
                            } else {
                                it.accTradePrice24h
                            }
                        }
                    }
                }
            }

            SortType.RATE -> {
                when (sortOrder) {
                    SortOrder.DESCENDING -> {
                        tickerList.sortedByDescending { it.signedChangeRate }
                    }

                    SortOrder.ASCENDING -> {
                        tickerList.sortedBy { it.signedChangeRate }
                    }

                    SortOrder.NONE -> {
                        tickerList.sortedByDescending {
                            if (btcPrice != null
                                && it.market.startsWith(UPBIT_BTC_SYMBOL_PREFIX)
                            ) {
                                it.accTradePrice24h * btcPrice
                            } else {
                                it.accTradePrice24h
                            }
                        }
                    }
                }
            }

            SortType.VOLUME -> {
                when (sortOrder) {
                    SortOrder.DESCENDING -> {
                        tickerList.sortedByDescending {
                            if (btcPrice != null
                                && it.market.startsWith(UPBIT_BTC_SYMBOL_PREFIX)
                            ) {
                                it.accTradePrice24h * btcPrice
                            } else {
                                it.accTradePrice24h
                            }
                        }
                    }

                    SortOrder.ASCENDING -> {
                        tickerList.sortedBy {
                            if (btcPrice != null
                                && it.market.startsWith(UPBIT_BTC_SYMBOL_PREFIX)
                            ) {
                                it.accTradePrice24h * btcPrice
                            } else {
                                it.accTradePrice24h
                            }
                        }
                    }

                    SortOrder.NONE -> {
                        tickerList.sortedByDescending {
                            if (btcPrice != null
                                && it.market.startsWith(UPBIT_BTC_SYMBOL_PREFIX)
                            ) {
                                it.accTradePrice24h * btcPrice
                            } else {
                                it.accTradePrice24h
                            }
                        }
                    }
                }
            }
        }
    }

    fun extractCryptoKeysWithGson(json: JsonObject): List<String> {
        val keyList = mutableListOf<String>()
        for ((key, _) in json.entrySet()) {
            if (key != "date") {
                keyList.add(key)
            }
        }

        return keyList
    }

    fun bitthumbMarketToUpbitMarket(market: String): String {
        val standard = market.indexOf("_")
        val temp = market.substring(0, standard)
        val temp2 = market.substring(standard + 1)
//        Logger.e("$temp $temp2")
        return "$temp2-$temp"
    }

    fun upbitMarketToBitthumbMarket(market: String): String {
        val standard = market.indexOf("-")
        val temp = market.substring(0, standard)
        val temp2 = market.substring(standard + 1)
        Logger.e("$temp $temp2")
        return "${temp2}_$temp"
    }

    fun millisToUpbitFormat(millis: Long): String {
        val formatter = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = millis
        return formatter.format(calendar.time)
    }

    fun upbitFormatToMillis(time: String): Long {

        val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
        val date: Date = dateFormat.parse(time)

        // Date 객체의 time 속성을 이용해 밀리초로 변환
        return date.time
    }

    private fun extractInitialConsonant(char: Char): Char {
        if (char in '가'..'힣') {
            val baseCode = char.code - 0xAC00
            val initialConsonantIndex = baseCode / 28 / 21
            // 초성 배열
            val initialConsonants = listOf(
                'ㄱ', 'ㄲ', 'ㄴ', 'ㄷ', 'ㄸ', 'ㄹ', 'ㅁ', 'ㅂ', 'ㅃ',
                'ㅅ', 'ㅆ', 'ㅇ', 'ㅈ', 'ㅉ', 'ㅊ', 'ㅋ', 'ㅌ', 'ㅍ', 'ㅎ'
            )
            return initialConsonants[initialConsonantIndex]
        }
        // 한글이 아닐 경우 입력 그대로 반환
        return char
    }

    fun extractInitials(input: String): String {
        return input.map { extractInitialConsonant(it) }.joinToString("")
    }

    fun getStandardMillis(candleType: String): Long {
        val oneMinute: Long = (60 * 1000).toLong()
        val threeMinutes: Long = 3 * oneMinute
        val fiveMinutes: Long = 5 * oneMinute
        val fifteenMinutes: Long = 15 * oneMinute
        val thirtyMinutes: Long = 30 * oneMinute
        val oneHour: Long = 60 * oneMinute
        val sixHours: Long = 6 * oneHour
        val twelveHours: Long = 12 * oneHour
        val oneDay: Long = 24 * oneHour
        return when (candleType) {
            "1m" -> {
                oneMinute
            }

            "3m" -> {
                threeMinutes
            }

            "5m" -> {
                fiveMinutes
            }

            "10m" -> {
                fifteenMinutes
            }

            "30m" -> {
                thirtyMinutes
            }

            "1h" -> {
                oneHour
            }

            "6h" -> {
                sixHours
            }

            "12h" -> {
                twelveHours
            }

            "24h" -> {
                oneDay
            }

            else -> {
                0
            }
        }
    }
}