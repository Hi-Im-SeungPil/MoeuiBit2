package org.jeonfeel.moeuibit2.constant

import kotlinx.coroutines.Dispatchers
import java.time.Duration

const val SOCKET_IS_CONNECTED = 0
const val SOCKET_IS_NO_CONNECTION = -1
const val SOCKET_IS_ON_PAUSE = -2

const val INTERNET_CONNECTION = 0
const val NO_INTERNET_CONNECTION = -1
const val NETWORK_ERROR = -2

const val SELECTED_KRW_MARKET = 1
//const val SELECTED_BTC_MARKET = 2
const val SELECTED_FAVORITE = 3
const val SELECTED_KIMP = 4

const val rewardFullScreenAdId = "ca-app-pub-8481465476603755/3905762551"
const val rewardVideoAdId = "ca-app-pub-8481465476603755/3753512134"
const val fullScreenAdId = "ca-app-pub-8481465476603755/8814267120"

val ioDispatcher = Dispatchers.IO
val mainDispatcher = Dispatchers.Main
val timeOutDuration = Duration.ofMillis(10000L)