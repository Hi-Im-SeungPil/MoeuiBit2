package org.jeonfeel.moeuibit2.constant

import java.util.*

const val retrofitBaseUrl = "https://api.upbit.com/"
const val webSocketBaseUrl = "wss://api.upbit.com/websocket/v1"
const val playStoreUrl = "https://play.google.com/store/apps/details?id=org.jeonfeel.moeuibit2"
const val noticeBoard = "https://moeui-bit-announcement.tistory.com/"
const val coinImageUrl = "https://raw.githubusercontent.com/Hi-Im-SeungPil/moeuibitImg/main/coinlogo2/"
fun twitterUrl(href: String): String =
    "<a class=\"twitter-timeline\" href=\"${href}?ref_src=twsrc%5Etfw\" target=\"_blank\">Tweets</a> <script async src=\"https://platform.twitter.com/widgets.js\" charset=\"utf-8\"></script>"
fun orderBookWebSocketMessage(market: String): String = """[{"ticket":"${UUID.randomUUID()}"},{"type":"orderbook","codes":[${market}]},{"format":"SIMPLE"}]"""
fun tickerWebSocketMessage(market: String): String = """[{"ticket":"${UUID.randomUUID()}"},{"type":"ticker","codes":[${market}]},{"format":"SIMPLE"}]"""