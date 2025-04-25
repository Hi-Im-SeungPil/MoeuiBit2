package org.jeonfeel.moeuibit2.constants

import kotlinx.coroutines.Dispatchers
import java.util.*

const val EXCHANGE_UPBIT = "UpBit"
const val EXCHANGE_BITTHUMB = "BitThumb"

val ioDispatcher = Dispatchers.IO
val defaultDispatcher = Dispatchers.Default

const val KRW_COMMISSION_FEE = 0.0005
const val BTC_COMMISSION_FEE = 0.0025

const val UPBIT_KRW_SYMBOL_PREFIX = "KRW-"
const val UPBIT_BTC_SYMBOL_PREFIX = "BTC-"

const val ROOM_DATABASE_NAME = "MoeuiBitDatabase"
const val PREFRENCE_NAME = "MoeuiBitPrefrence"
const val AD_ID_REWARD_FULL_SCREEN = "ca-app-pub-8481465476603755/3905762551"
const val AD_ID_REWARD_VIDEO = "ca-app-pub-8481465476603755/3753512134"
const val AD_ID_FULL_SCREEN = "ca-app-pub-8481465476603755/8814267120"
const val AD_ID_OPENING = "ca-app-pub-8481465476603755/4153375494"
const val AD_ID_NATIVE = "ca-app-pub-8481465476603755/9887286231"
const val AD_ID_TEST = "ca-app-pub-3940256099942544/3419835294"

const val SELECTED_KRW_MARKET = 0
const val SELECTED_BTC_MARKET = 1
const val SELECTED_FAVORITE = 2

const val BTC_MARKET = "KRW-BTC"

const val ASK_BID_SCREEN_BID_TAB = 1
const val ASK_BID_SCREEN_ASK_TAB = 2

const val COIN_DETAIL_MAIN_TAB_ROW_ITEM_ORDER = "주문"
const val COIN_DETAIL_MAIN_TAB_ROW_ITEM_CHART = "차트"
const val COIN_DETAIL_MAIN_TAB_ROW_ITEM_COIN_INFO = "정보"
const val SYMBOL_KRW = "KRW"
const val SYMBOL_BTC = "BTC"
const val ASK = "ask"
const val BID = "bid"

fun upbitOrderBookWebSocketMessage(market: String): String =
    """[{"ticket":"${UUID.randomUUID()}"},{"type":"orderbook","codes":[${market}]}]"""

fun upbitTickerWebSocketMessage(market: String): String =
    """[{"ticket":"${UUID.randomUUID()}"},{"type":"ticker","codes":[${market}]}]"""

val chartMinuteArray = arrayOf("1", "3", "5", "10", "15", "30", "60", "240")
val chartMinuteStrArray = arrayOf("1분", "3분", "5분", "10분", "15분", "30분", "60분", "240분")
val bitthumbChartMinuteArray = arrayOf("1m", "3m", "5m", "10m", "30m", "1h", "6h", "12h")
val bitthumbChartMinuteStrArray = arrayOf("1분", "3분", "5분", "10분", "30분", "60분", "6시간", "12시간")
val menuTitleArray = arrayOf("거래소", "코인 정보", "유용한 기능", "투자내역", "설정")
val movingAverageLineArray = arrayOf(5, 10, 20, 60, 120)
val darkMovingAverageLineColorArray = arrayOf("#B3FF36FF", "#B3B2CCFF", "#B3DBC000", "#B3FF4848", "#B3BDBDBD")