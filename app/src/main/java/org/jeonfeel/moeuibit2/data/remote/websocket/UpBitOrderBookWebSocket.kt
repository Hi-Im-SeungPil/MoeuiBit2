package org.jeonfeel.moeuibit2.data.remote.websocket

import okhttp3.OkHttpClient
import okhttp3.Request
import org.jeonfeel.moeuibit2.listener.UpBitOrderBookWebSocketListener
import java.util.*

object UpBitOrderBookWebSocket {
    var currentSocketState = SOCKET_IS_CONNECTED
    private val TAG = UpBitTickerWebSocket::class.java.simpleName

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
        val uuid = UUID.randomUUID().toString()
        socket.send("""[{"ticket":"$uuid"},{"type":"orderbook","codes":[${market}]},{"format":"SIMPLE"}]""")
    }
    fun initOrderBook(market: String) {
        val uuid = UUID.randomUUID().toString()
        socket.send("""[{"ticket":"$uuid"},{"type":"orderbook","codes":[${market}],"isOnlySnapshot":true},{"format":"SIMPLE"}]""")
    }
}