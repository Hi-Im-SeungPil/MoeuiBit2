package org.jeonfeel.moeuibit2.data.remote.websocket

import android.util.Log
import okhttp3.OkHttpClient
import okhttp3.Request
import org.jeonfeel.moeuibit2.SOCKET_IS_CONNECTED
import org.jeonfeel.moeuibit2.SOCKET_IS_NO_CONNECTION
import org.jeonfeel.moeuibit2.SOCKET_IS_ON_PAUSE
import org.jeonfeel.moeuibit2.listener.UpBitCoinDetailWebSocketListener
import java.util.*

object UpBitCoinDetailWebSocket {
    var currentSocketState = SOCKET_IS_CONNECTED

    private val client = OkHttpClient()
    private val request = Request.Builder()
        .url("wss://api.upbit.com/websocket/v1")
        .build()
    private val socketListener = UpBitCoinDetailWebSocketListener()
    private var socket = client.newWebSocket(request, socketListener)
    var market = ""
    var retryCount = 0

    fun getListener(): UpBitCoinDetailWebSocketListener {
        return socketListener
    }

    fun requestCoinDetailData(market: String) {
        socketRebuild()
        val uuid = UUID.randomUUID().toString()
        socket.send("""[{"ticket":"$uuid"},{"type":"ticker","codes":["$market"]},{"format":"SIMPLE"}]""")
    }

    private fun socketRebuild() {
        if (currentSocketState != SOCKET_IS_CONNECTED) {
            socket = client.newWebSocket(
                request, socketListener)
            currentSocketState = SOCKET_IS_CONNECTED
        }
    }

    fun onPause() {
        socket.close(UpBitCoinDetailWebSocketListener.NORMAL_CLOSURE_STATUS, "onPause")
        currentSocketState = SOCKET_IS_ON_PAUSE
        Log.d("onPause", "호출")
    }

    fun onResume(market: String) {
        currentSocketState = SOCKET_IS_CONNECTED
    }

    fun onFail() {
        currentSocketState = SOCKET_IS_NO_CONNECTION
        if (retryCount <= 10) {
            requestCoinDetailData(market)
            retryCount++
        }
    }
}