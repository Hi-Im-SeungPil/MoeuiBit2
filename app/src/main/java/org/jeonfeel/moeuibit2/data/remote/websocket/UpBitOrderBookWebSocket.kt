package org.jeonfeel.moeuibit2.data.remote.websocket

import okhttp3.OkHttpClient
import okhttp3.Request
import org.jeonfeel.moeuibit2.constants.*
import org.jeonfeel.moeuibit2.data.remote.websocket.listener.UpBitOrderBookWebSocketListener

object UpBitOrderBookWebSocket {

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
    private val socketListener = UpBitOrderBookWebSocketListener()
    var socket = client.newWebSocket(request, socketListener)
    var market = ""

    fun getListener(): UpBitOrderBookWebSocketListener {
        return socketListener
    }

    fun requestOrderBookList(market: String) {
        currentSocketState = SOCKET_IS_CONNECTED
        socket.send(orderBookWebSocketMessage(market))
    }

    fun onPause() {
        socket.send(orderBookWebSocketMessage("pause"))
        currentSocketState = SOCKET_IS_ON_PAUSE
    }
}