package org.jeonfeel.moeuibit2.data.remote.websocket

import android.util.Log
import okhttp3.OkHttpClient
import okhttp3.Request
import org.jeonfeel.moeuibit2.SOCKET_IS_CONNECTED
import org.jeonfeel.moeuibit2.SOCKET_IS_NO_CONNECTION
import org.jeonfeel.moeuibit2.SOCKET_IS_ON_PAUSE
import org.jeonfeel.moeuibit2.listener.UpBitOrderBookWebSocketListener
import java.util.*


object UpBitOrderBookWebSocket {
    var currentSocketState = SOCKET_IS_CONNECTED
    private var retryCount = 0
    private val TAG = UpBitCoinDetailWebSocket::class.java.simpleName

    private val client = OkHttpClient()
    private val request = Request.Builder()
        .url("wss://api.upbit.com/websocket/v1")
        .build()
    private val socketListener = UpBitOrderBookWebSocketListener()
    private var socket = client.newWebSocket(request, socketListener)

    fun getListener(): UpBitOrderBookWebSocketListener {
        return socketListener
    }

    fun requestOrderBookList(market: String) {
        socketRebuild()
        val uuid = UUID.randomUUID().toString()
        socket.send("""[{"ticket":"$uuid"},{"type":"orderbook","codes":[${market}]},{"format":"SIMPLE"}]""")
    }

    fun onPause() {
        socket.close(UpBitOrderBookWebSocketListener.NORMAL_CLOSURE_STATUS, "onPause")
        currentSocketState = SOCKET_IS_ON_PAUSE
        Log.d("onPause", "호출")
    }

    fun onResume(market: String) {
        requestOrderBookList(market)
        currentSocketState = SOCKET_IS_CONNECTED
    }

    private fun socketRebuild() {
        if (currentSocketState != SOCKET_IS_CONNECTED) {
            socket = client.newWebSocket(
                request,
                socketListener)
            currentSocketState = SOCKET_IS_CONNECTED
        }
    }

    fun onFail() {
        currentSocketState = SOCKET_IS_NO_CONNECTION
        if (retryCount != 1) {
            socketRebuild()
            retryCount += 1
        }
    }
}