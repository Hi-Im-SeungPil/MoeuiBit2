package org.jeonfeel.moeuibit2.data.remote.websocket

import okhttp3.OkHttpClient
import okhttp3.Request
import org.jeonfeel.moeuibit2.constant.SOCKET_IS_CONNECTED
import org.jeonfeel.moeuibit2.constant.SOCKET_IS_ON_PAUSE
import org.jeonfeel.moeuibit2.constant.tickerWebSocketMessage
import org.jeonfeel.moeuibit2.constant.webSocketBaseUrl
import org.jeonfeel.moeuibit2.data.remote.websocket.listener.UpBitPortfolioWebSocketListener
import java.util.*

object UpBitPortfolioWebSocket {

    var currentSocketState = SOCKET_IS_CONNECTED

    private var krwMarkets = ""
    private var client = OkHttpClient().newBuilder().retryOnConnectionFailure(true).build()
    private val request = Request.Builder()
        .url(webSocketBaseUrl)
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
        socket.send(tickerWebSocketMessage(krwMarkets))
    }

    private fun socketRebuild() {
        if (currentSocketState != SOCKET_IS_CONNECTED) {
            socket = client.newWebSocket(request, socketListener)
            currentSocketState = SOCKET_IS_CONNECTED
        }
    }

    fun onPause() {
        try{
            client.dispatcher.cancelAll()
            client.connectionPool.evictAll()
        }catch (e: Exception) {
            e.printStackTrace()
        }
        currentSocketState = SOCKET_IS_ON_PAUSE
    }
}