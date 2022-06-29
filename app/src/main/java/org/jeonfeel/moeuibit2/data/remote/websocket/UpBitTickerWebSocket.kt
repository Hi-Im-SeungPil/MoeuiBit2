package org.jeonfeel.moeuibit2.data.remote.websocket

import okhttp3.OkHttpClient
import okhttp3.Request
import org.jeonfeel.moeuibit2.constant.*
import org.jeonfeel.moeuibit2.data.remote.websocket.listener.UpBitTickerWebSocketListener

object UpBitTickerWebSocket {

    private var krwMarkets = ""
    var currentSocketState = SOCKET_IS_CONNECTED
    private var client = OkHttpClient().newBuilder().retryOnConnectionFailure(true)
        .connectTimeout(timeOutDuration)
        .callTimeout(timeOutDuration)
        .readTimeout(timeOutDuration)
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

    fun requestKrwCoinList() {
        socketRebuild()
        socket.send(tickerWebSocketMessage(krwMarkets))
    }

    private fun socketRebuild() {
        if (currentSocketState != SOCKET_IS_CONNECTED) {
            socket = client.newWebSocket(request, socketListener)
            currentSocketState = SOCKET_IS_CONNECTED
        }
    }

    fun setKrwMarkets(markets: String) {
        krwMarkets = markets
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