package org.jeonfeel.moeuibit2.data.remote.websocket

import okhttp3.OkHttpClient
import okhttp3.Request
import org.jeonfeel.moeuibit2.listener.UpBitWebSocketListener
import java.util.*

object UpBitWebSocket {
    var currentSocketState = 0
    private val TAG = UpBitWebSocket::class.java.simpleName

    private val client = OkHttpClient()
    private val request = Request.Builder()
        .url("wss://api.upbit.com/websocket/v1")
        .build()
    private val socketListener = UpBitWebSocketListener()
    private var socket = client.newWebSocket(request, socketListener)

    fun getListener(): UpBitWebSocketListener {
        return socketListener
    }

    fun requestKrwCoinList(markets: String) {
        socketRebuild()
        val uuid = UUID.randomUUID().toString()
        socket.send("""[{"ticket":"$uuid"},{"type":"ticker","codes":[${markets}],"isOnlyRealtime":true},{"format":"SIMPLE"}]""")
    }

    private fun socketRebuild() {
        if(currentSocketState != 0) {
            socket = client.newWebSocket(request, socketListener)
            currentSocketState = 0
        }
    }

    fun close() {
//        socket.close()
    }
}