package org.jeonfeel.moeuibit2.data.remote.websocket

import okhttp3.OkHttpClient
import okhttp3.Request
import org.jeonfeel.moeuibit2.constants.*
import org.jeonfeel.moeuibit2.data.remote.websocket.listener.UpBitCoinDetailWebSocketListener

object UpBitCoinDetailWebSocket {

    var currentSocketState = SOCKET_IS_CONNECTED
    private var client = OkHttpClient().newBuilder().retryOnConnectionFailure(true)
        .connectTimeout(timeOutDuration)
        .callTimeout(timeOutDuration)
        .readTimeout(readTimeOutDuration)
        .writeTimeout(timeOutDuration)
        .build()
    private val request = Request.Builder().url(webSocketBaseUrl).build()
    private val socketListener = UpBitCoinDetailWebSocketListener()
    private var socket = client.newWebSocket(request, socketListener)
    var market = ""

    fun getListener(): UpBitCoinDetailWebSocketListener {
        return socketListener
    }

    fun requestCoinDetailData(market: String) {
        socketRebuild()
        socket.send(tickerWebSocketMessage(market))
    }

    private fun socketRebuild() {
        if (currentSocketState != SOCKET_IS_CONNECTED) {
            socket = client.newWebSocket(
                request, socketListener
            )
            currentSocketState = SOCKET_IS_CONNECTED
        }
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