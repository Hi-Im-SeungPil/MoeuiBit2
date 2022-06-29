package org.jeonfeel.moeuibit2.data.remote.websocket

import okhttp3.OkHttpClient
import okhttp3.Request
import org.jeonfeel.moeuibit2.constant.*
import org.jeonfeel.moeuibit2.data.remote.websocket.listener.UpBitOrderBookWebSocketListener

object UpBitOrderBookWebSocket {

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
    private val socketListener = UpBitOrderBookWebSocketListener()
    private var socket = client.newWebSocket(request, socketListener)
    var market = ""

    fun getListener(): UpBitOrderBookWebSocketListener {
        return socketListener
    }

    fun requestOrderBookList(market: String) {
        socketRebuild()
        socket.send(orderBookWebSocketMessage(market))
    }

    private fun socketRebuild() {
        if (currentSocketState != SOCKET_IS_CONNECTED) {
            socket = client.newWebSocket(
                request,
                socketListener
            )
            currentSocketState = SOCKET_IS_CONNECTED
        }
    }

    fun onPause() {
        try {
            client.dispatcher.cancelAll()
            client.connectionPool.evictAll()
        }catch (e: Exception) {
            e.printStackTrace()
        }
        currentSocketState = SOCKET_IS_ON_PAUSE
    }

    fun onResume(market: String) {
        requestOrderBookList(market)
        currentSocketState = SOCKET_IS_CONNECTED
    }
}