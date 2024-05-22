package org.jeonfeel.moeuibit2.data.remote.websocket.bitthumb

import com.orhanobut.logger.Logger
import okhttp3.OkHttpClient
import okhttp3.Request
import org.jeonfeel.moeuibit2.constants.*
import org.jeonfeel.moeuibit2.data.remote.websocket.listener.bitthumb.BitthumbOrderBookWebSocketListener
import org.jeonfeel.moeuibit2.utils.NetworkMonitorUtil

object BitthumbOrderBookWebSocket {

    var currentSocketState = SOCKET_IS_CONNECTED
    var currentScreen = IS_ANOTHER_SCREEN

    private var client = OkHttpClient().newBuilder().retryOnConnectionFailure(true)
        .connectTimeout(timeOutDuration)
        .callTimeout(timeOutDuration)
        .readTimeout(readTimeOutDuration)
        .writeTimeout(timeOutDuration)
        .build()
    private val request = Request.Builder()
        .url(bitthumbWebSocketBaseUrl)
        .build()
    private val socketListener = BitthumbOrderBookWebSocketListener()
    var socket = client.newWebSocket(request, socketListener)
    var market = ""

    fun getListener(): BitthumbOrderBookWebSocketListener {
        return socketListener
    }

    fun requestOrderBookList(market: String) {
        try {
            Logger.e("reqOrderbook ${bitthumbOrderBookWebSocketMessage(market)}")
            socket.send(bitthumbOrderBookWebSocketMessage(market))
            currentSocketState = SOCKET_IS_CONNECTED
        } catch (e: Exception) {
            Logger.e(e.message.toString())
            onlyRebuildSocket()
        }
    }

    private fun onlyRebuildSocket() {
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
                requestOrderBookList(market = "\"${market}\"")
            }
        }
    }

    fun onPause() {
        try {
            socket.send(bitthumbOrderBookWebSocketMessage(""))
            currentSocketState = SOCKET_IS_ON_PAUSE
        } catch (e: Exception) {
            onlyRebuildSocket()
        }
    }
}