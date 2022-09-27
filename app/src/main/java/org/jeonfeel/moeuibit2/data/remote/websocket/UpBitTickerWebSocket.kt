package org.jeonfeel.moeuibit2.data.remote.websocket

import okhttp3.OkHttpClient
import okhttp3.Request
import org.jeonfeel.moeuibit2.constant.*
import org.jeonfeel.moeuibit2.data.remote.websocket.listener.UpBitTickerWebSocketListener

object UpBitTickerWebSocket {

    var currentMarket = 0
    private var krwMarkets = ""
    private var btcMarkets = ""
    var currentSocketState = SOCKET_IS_CONNECTED
    private var client = OkHttpClient().newBuilder().retryOnConnectionFailure(true)
        .connectTimeout(timeOutDuration)
        .callTimeout(timeOutDuration)
        .readTimeout(readTimeOutDuration)
        .writeTimeout(timeOutDuration)
        .build()
    private val request = Request.Builder()
        .url(webSocketBaseUrl)
        .build()
    private val socketListener = UpBitTickerWebSocketListener()
    private var socket = client.newWebSocket(request, socketListener)

    fun getListener(): UpBitTickerWebSocketListener {
        return socketListener
    }

    fun requestKrwCoinList(marketState: Int) {
        socketRebuild()
        if (marketState == SELECTED_KRW_MARKET) {
            socket.send(tickerWebSocketMessage(krwMarkets))
            currentMarket = SELECTED_KRW_MARKET
        } else if (marketState == SELECTED_BTC_MARKET) {
            socket.send(tickerWebSocketMessage(btcMarkets))
            currentMarket = SELECTED_BTC_MARKET
        }
    }

    private fun socketRebuild() {
        if (currentSocketState != SOCKET_IS_CONNECTED) {
            socket = client.newWebSocket(request, socketListener)
            currentSocketState = SOCKET_IS_CONNECTED
        }
    }

    fun setMarkets(krwMarkets: String, btcMarkets: String) {
        this.krwMarkets = krwMarkets
        this.btcMarkets = btcMarkets
    }

    fun onPause() {
        try {
            client.dispatcher.cancelAll()
            client.connectionPool.evictAll()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        currentSocketState = SOCKET_IS_ON_PAUSE
    }
}