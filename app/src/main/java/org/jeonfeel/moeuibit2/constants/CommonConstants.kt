package org.jeonfeel.moeuibit2.constants

import kotlinx.coroutines.Dispatchers
import java.util.*

val ioDispatcher = Dispatchers.IO
val mainDispatcher = Dispatchers.Main
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

const val UPBIT_BASE_URL = "https://api.upbit.com/"
const val upbitWebSocketBaseUrl = "wss://api.upbit.com/websocket/v1"

const val bitthumbWebSocketBaseUrl = "wss://pubwss.bithumb.com/pub/ws"
const val bitthumbCoinNameUrl =
    "https://raw.githubusercontent.com/Hi-Im-SeungPil/BitthumbCoinName/main/bitthumb_coin_name.json"

const val USD_BASE_URL = "https://cdn.jsdelivr.net/"
const val COIN_CAP_IO_BASE_URL = "https://api.coincap.io/"
const val ALTERNATIVE_BASE_URL = "https://api.alternative.me/"

const val PLAY_STORE_URL = "https://play.google.com/store/apps/details?id=org.jeonfeel.moeuibit2"
const val COIN_IMAGE_BASE_URL =
    "https://raw.githubusercontent.com/Hi-Im-SeungPil/moeuibitImg/main/coinlogo2/"

fun upbitOrderBookWebSocketMessage(market: String): String =
    """[{"ticket":"${UUID.randomUUID()}"},{"type":"orderbook","codes":[${market}]}]"""

fun upbitTickerWebSocketMessage(market: String): String =
    """[{"ticket":"${UUID.randomUUID()}"},{"type":"ticker","codes":[${market}]}]"""

fun bitthumbTickerWebSocketMessage(market: String): String =
    """{"type":"ticker","symbols":[$market],"tickTypes":["MID"]}"""

fun bitthumbOrderBookWebSocketMessage(market: String): String =
    """{"type":"orderbooksnapshot","symbols":[$market]}"""

fun twitterUrl(href: String): String =
    "<a class=\"twitter-timeline\" href=\"${href}?ref_src=twsrc%5Etfw\" target=\"_blank\">Tweets</a> <script async src=\"https://platform.twitter.com/widgets.js\" charset=\"utf-8\"></script>"

val chartMinuteArray = arrayOf("1", "3", "5", "10", "15", "30", "60", "240")
val chartMinuteStrArray = arrayOf("1분", "3분", "5분", "10분", "15분", "30분", "60분", "240분")
val bitthumbChartMinuteArray = arrayOf("1m", "3m", "5m", "10m", "30m", "1h", "6h", "12h")
val bitthumbChartMinuteStrArray = arrayOf("1분", "3분", "5분", "10분", "30분", "60분", "6시간", "12시간")
val menuTitleArray = arrayOf("거래소", "코인사이트", "부가기능", "투자내역", "설정")
val movingAverageLineArray = arrayOf(5, 10, 20, 60, 120)
val movingAverageLineColorArray =
    arrayOf("#B3FF36FF", "#B30000B7", "#B3DBC000", "#B3FF4848", "#B3BDBDBD")
val darkMovingAverageLineColorArray =
    arrayOf("#B3FF36FF", "#B3B2CCFF", "#B3DBC000", "#B3FF4848", "#B3BDBDBD")