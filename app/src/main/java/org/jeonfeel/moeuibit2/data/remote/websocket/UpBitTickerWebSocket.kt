package org.jeonfeel.moeuibit2.data.remote.websocket

import android.util.Log
import okhttp3.OkHttpClient
import okhttp3.Request
import org.jeonfeel.moeuibit2.constant.SOCKET_IS_CONNECTED
import org.jeonfeel.moeuibit2.constant.SOCKET_IS_NO_CONNECTION
import org.jeonfeel.moeuibit2.constant.SOCKET_IS_ON_PAUSE
import org.jeonfeel.moeuibit2.data.remote.websocket.listener.UpBitTickerWebSocketListener
import org.jeonfeel.moeuibit2.data.remote.websocket.listener.UpBitTickerWebSocketListener.Companion.NORMAL_CLOSURE_STATUS
import java.util.*

object UpBitTickerWebSocket {
    private var krwMarkets = ""
    var currentSocketState = SOCKET_IS_CONNECTED

    private var client = OkHttpClient()
    private val request = Request.Builder()
        .url("wss://api.upbit.com/websocket/v1")
        .build()
    private val socketListener = UpBitTickerWebSocketListener()
    private var socket = client.newWebSocket(request, socketListener)
    var retryCount = 0

    fun getListener(): UpBitTickerWebSocketListener {
        return socketListener
    }

    fun requestKrwCoinList() {
        socketRebuild()
        val uuid = UUID.randomUUID().toString()
        socket.send("""[{"ticket":"$uuid"},{"type":"ticker","codes":[${krwMarkets}]},{"format":"SIMPLE"}]""")
    }

    private fun socketRebuild() {
        if (currentSocketState != SOCKET_IS_CONNECTED) {
            try{
                client.cache?.let {
                    it.close()
                }
                client.connectionPool.evictAll()
                Log.e("try","catch")
            }catch (e: Exception) {
                Log.e("catch","catch")
                client.dispatcher.executorService.shutdown()
                client = OkHttpClient()
            }
            socket = client.newWebSocket(request, socketListener)
            currentSocketState = SOCKET_IS_CONNECTED
        }
    }

    fun setKrwMarkets(markets: String) {
        krwMarkets = markets
    }

    fun onPause() {
        socket.cancel()
        socket.close(NORMAL_CLOSURE_STATUS, "onPause")
        currentSocketState = SOCKET_IS_ON_PAUSE
    }

    fun onFail() {
        currentSocketState = SOCKET_IS_NO_CONNECTION
        if (retryCount <= 10) {
            requestKrwCoinList()
            retryCount++
        }
    }
}