package org.jeonfeel.moeuibit2.data.remote.websocket

import okhttp3.OkHttpClient
import okhttp3.Request
import org.jeonfeel.moeuibit2.SOCKET_IS_CONNECTED
import org.jeonfeel.moeuibit2.SOCKET_IS_NO_CONNECTION
import org.jeonfeel.moeuibit2.SOCKET_IS_ON_PAUSE
import org.jeonfeel.moeuibit2.listener.UpBitTickerWebSocketListener
import org.jeonfeel.moeuibit2.listener.UpBitTickerWebSocketListener.Companion.NORMAL_CLOSURE_STATUS
import java.util.*

object UpBitTickerWebSocket {
    private var krwMarkets = ""
    var currentSocketState = SOCKET_IS_CONNECTED
    private val TAG = UpBitCoinDetailWebSocket::class.java.simpleName

    private val client = OkHttpClient()
    private val request = Request.Builder()
        .url("wss://api.upbit.com/websocket/v1")
        .build()
    private val socketListener = UpBitTickerWebSocketListener()
    private var socket = client.newWebSocket(request, socketListener)

    fun getListener(): UpBitTickerWebSocketListener {
        return socketListener
    }

    fun requestKrwCoinList() {
        socketRebuild()
        val uuid = UUID.randomUUID().toString()
        socket.send("""[{"ticket":"$uuid"},{"type":"ticker","codes":[${krwMarkets}]},{"format":"SIMPLE"}]""")
    }

//    fun requestKrwCoin(market: String) {
//        socketRebuild()
//        val uuid = UUID.randomUUID().toString()
//        socket.send("""[{"ticket":"$uuid"},{"type":"ticker","codes":["$market"]},{"format":"SIMPLE"}]""")
//    }

    private fun socketRebuild() {
        if (currentSocketState != SOCKET_IS_CONNECTED) {
            socket = client.newWebSocket(request, socketListener)
            currentSocketState = SOCKET_IS_CONNECTED
        }
    }

    fun setKrwMarkets(markets: String) {
        krwMarkets = markets
    }

    fun onPause() {
        socket.close(NORMAL_CLOSURE_STATUS, "onPause")
        currentSocketState = SOCKET_IS_ON_PAUSE
    }

//    fun coinDetailScreenOnResume(market: String) {
//        if (currentSocketState == SOCKET_IS_ON_PAUSE) {
//            requestKrwCoin(market)
//            currentSocketState = SOCKET_IS_CONNECTED
//        }
//    }

    fun exchangeScreenOnResume() {
        requestKrwCoinList()
        currentSocketState = SOCKET_IS_CONNECTED
    }

    fun onFail() {
        currentSocketState = SOCKET_IS_NO_CONNECTION
    }
}