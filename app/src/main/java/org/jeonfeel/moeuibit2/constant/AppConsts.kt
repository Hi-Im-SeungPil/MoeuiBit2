package org.jeonfeel.moeuibit2.constant

import kotlinx.coroutines.Dispatchers
import java.time.Duration

val ioDispatcher = Dispatchers.IO
val mainDispatcher = Dispatchers.Main
val defaultDispatcher = Dispatchers.Default
val timeOutDuration: Duration = Duration.ofMillis(50000L)
val readTimeOutDuration: Duration = Duration.ofMillis(50000L)

const val ROOM_DATABASE_NAME = "MoeuiBitDatabase"
const val rewardFullScreenAdId = "ca-app-pub-8481465476603755/3905762551"
const val rewardVideoAdId = "ca-app-pub-8481465476603755/3753512134"
const val fullScreenAdId = "ca-app-pub-8481465476603755/8814267120"

const val SOCKET_IS_CONNECTED = 0
const val SOCKET_IS_NO_CONNECTION = -1
const val SOCKET_IS_ON_PAUSE = -2

const val INTERNET_CONNECTION = 0
const val NO_INTERNET_CONNECTION = -1
const val NETWORK_ERROR = -2

const val SELECTED_KRW_MARKET = 0
const val SELECTED_BTC_MARKET = 1
const val SELECTED_FAVORITE = 2
const val SELECTED_KIMP = 3

const val BTC_MARKET = "KRW-BTC"

const val SORT_DEFAULT = -1
const val SORT_PRICE_DEC = 0
const val SORT_PRICE_ASC = 1
const val SORT_RATE_DEC = 2
const val SORT_RATE_ASC = 3
const val SORT_AMOUNT_DEC = 4
const val SORT_AMOUNT_ASC = 5

const val ASK_BID_SCREEN_BID_TAB = 1
const val ASK_BID_SCREEN_ASK_TAB = 2
const val ASK_BID_SCREEN_TRANSACTION_TAB = 3

const val COIN_DETAIL_MAIN_TAB_ROW_ITEM_ORDER = "주문"
const val COIN_DETAIL_MAIN_TAB_ROW_ITEM_CHART = "차트"
const val COIN_DETAIL_MAIN_TAB_ROW_ITEM_COIN_INFO = "정보"
const val CAUTION = "CAUTION"
const val SYMBOL_KRW = "KRW"
const val SYMBOL_BTC = "BTC"
const val ASK = "ask"
const val BID = "bid"