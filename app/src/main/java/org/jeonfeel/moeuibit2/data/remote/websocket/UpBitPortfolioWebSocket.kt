package org.jeonfeel.moeuibit2.data.remote.websocket

import okhttp3.OkHttpClient
import okhttp3.Request
import org.jeonfeel.moeuibit2.constant.SOCKET_IS_CONNECTED
import org.jeonfeel.moeuibit2.constant.SOCKET_IS_NO_CONNECTION
import org.jeonfeel.moeuibit2.constant.SOCKET_IS_ON_PAUSE
import org.jeonfeel.moeuibit2.data.remote.websocket.listener.UpBitPortfolioWebSocketListener
import org.jeonfeel.moeuibit2.data.remote.websocket.listener.UpBitTickerWebSocketListener
import java.util.*

object UpBitPortfolioWebSocket {
    var currentSocketState = SOCKET_IS_CONNECTED

    private var krwMarkets = ""
    private var client = OkHttpClient()
    private val request = Request.Builder()
        .url("wss://api.upbit.com/websocket/v1")
        .build()
    private val socketListener = UpBitPortfolioWebSocketListener()
    private var socket = client.newWebSocket(request, socketListener)
    var retryCount = 0

    fun getListener(): UpBitPortfolioWebSocketListener {
        return socketListener
    }

    fun setMarkets(krwMarkets: String) {
        this.krwMarkets = krwMarkets
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
            }catch (e: Exception) {
                client.dispatcher.executorService.shutdown()
                client = OkHttpClient()
            }
            socket = client.newWebSocket(request, socketListener)
            currentSocketState = SOCKET_IS_CONNECTED
        }
    }

    fun onPause() {
        socket.cancel()
        socket.close(UpBitTickerWebSocketListener.NORMAL_CLOSURE_STATUS, "onPause")
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