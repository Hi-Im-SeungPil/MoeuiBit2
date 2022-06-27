package org.jeonfeel.moeuibit2.data.remote.websocket

import okhttp3.OkHttpClient
import okhttp3.Request
import org.jeonfeel.moeuibit2.constant.SOCKET_IS_CONNECTED
import org.jeonfeel.moeuibit2.constant.SOCKET_IS_ON_PAUSE
import org.jeonfeel.moeuibit2.data.remote.websocket.listener.UpBitOrderBookWebSocketListener
import java.util.*


object UpBitOrderBookWebSocket {
    var currentSocketState = SOCKET_IS_CONNECTED

    private var client = OkHttpClient().newBuilder().retryOnConnectionFailure(true).build()
    private val request = Request.Builder()
        .url("wss://api.upbit.com/websocket/v1")
        .build()
    private val socketListener = UpBitOrderBookWebSocketListener()
    private var socket = client.newWebSocket(request, socketListener)
    var market = ""
    var retryCount = 0

    fun getListener(): UpBitOrderBookWebSocketListener {
        return socketListener
    }

    fun requestOrderBookList(market: String) {
        socketRebuild()
        val uuid = UUID.randomUUID().toString()
        socket.send("""[{"ticket":"$uuid"},{"type":"orderbook","codes":[${market}]},{"format":"SIMPLE"}]""")
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

    private fun socketRebuild() {
        if (currentSocketState != SOCKET_IS_CONNECTED) {
            socket = client.newWebSocket(
                request,
                socketListener
            )
            currentSocketState = SOCKET_IS_CONNECTED
        }
    }

    fun onFail() {
//        currentSocketState = SOCKET_IS_NO_CONNECTION
//        if (retryCount <= 10) {
//            requestOrderBookList(market)
//            retryCount++
//        }
    }
}