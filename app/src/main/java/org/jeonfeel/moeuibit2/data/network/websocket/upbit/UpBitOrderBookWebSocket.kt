package org.jeonfeel.moeuibit2.data.network.websocket.upbit

import okhttp3.OkHttpClient
import okhttp3.Request
import org.jeonfeel.moeuibit2.constants.*
import org.jeonfeel.moeuibit2.data.network.websocket.listener.upbit.UpBitOrderBookWebSocketListener
import org.jeonfeel.moeuibit2.utils.NetworkMonitorUtil

object UpBitOrderBookWebSocket {

    var currentSocketState = SOCKET_IS_CONNECTED
    var currentScreen = IS_ANOTHER_SCREEN

    private var client = OkHttpClient().newBuilder().retryOnConnectionFailure(true)
        .connectTimeout(timeOutDuration)
        .callTimeout(timeOutDuration)
        .readTimeout(readTimeOutDuration)
        .writeTimeout(timeOutDuration)
        .build()
    private val request = Request.Builder()
        .url(upbitWebSocketBaseUrl)
        .build()
    private val socketListener = UpBitOrderBookWebSocketListener()
    var socket = client.newWebSocket(request, socketListener)
    var market = ""

    fun getListener(): UpBitOrderBookWebSocketListener {
        return socketListener
    }

    fun requestOrderBookList(market: String) {
        try {
            socket.send(upbitOrderBookWebSocketMessage(market))
            currentSocketState = SOCKET_IS_CONNECTED
        } catch (e: Exception) {
            onlyRebuildSocket()
        }
    }

    fun onlyRebuildSocket() {
        if (NetworkMonitorUtil.currentNetworkState == INTERNET_CONNECTION) {
            socket.cancel()
            socket = client.newWebSocket(
                request,
                socketListener
            )
            currentSocketState = SOCKET_IS_CONNECTED
        }
    }
    fun rebuildSocket() {
        if (NetworkMonitorUtil.currentNetworkState == INTERNET_CONNECTION && currentSocketState == SOCKET_IS_FAILURE) {
            currentSocketState = SOCKET_IS_CONNECTED
            socket = client.newWebSocket(request, socketListener)
            if (currentScreen != IS_ANOTHER_SCREEN) {
                requestOrderBookList(market = market)
            }
        }
    }

    fun onPause() {
        try {
            socket.send(upbitOrderBookWebSocketMessage("pause"))
            currentSocketState = SOCKET_IS_ON_PAUSE
        } catch (e: Exception) {
            onlyRebuildSocket()
        }
    }
}