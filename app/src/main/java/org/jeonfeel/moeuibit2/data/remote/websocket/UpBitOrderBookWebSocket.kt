package org.jeonfeel.moeuibit2.data.remote.websocket

import com.orhanobut.logger.Logger
import okhttp3.OkHttpClient
import okhttp3.Request
import org.jeonfeel.moeuibit2.constants.*
import org.jeonfeel.moeuibit2.data.remote.websocket.listener.UpBitOrderBookWebSocketListener
import org.jeonfeel.moeuibit2.utils.NetworkMonitorUtil

object UpBitOrderBookWebSocket {

    var currentSocketState = SOCKET_IS_CONNECTED
    var temp = IS_ANOTHER_SCREEN

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

    fun rebuildSocket() {
        if (NetworkMonitorUtil.currentNetworkState == INTERNET_CONNECTION && currentSocketState == SOCKET_IS_FAILURE) {
            currentSocketState = SOCKET_IS_CONNECTED
            socket = client.newWebSocket(request, socketListener)
            if (temp != IS_ANOTHER_SCREEN) {
                requestOrderBookList(market = market)
            }
        }
    }

    fun onPause() {
        socket.send(orderBookWebSocketMessage("pause"))
        currentSocketState = SOCKET_IS_ON_PAUSE
    }
}