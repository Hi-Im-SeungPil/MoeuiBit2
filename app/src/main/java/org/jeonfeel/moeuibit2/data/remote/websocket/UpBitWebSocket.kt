package org.jeonfeel.moeuibit2.data.remote.websocket

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.Dispatcher
import okhttp3.OkHttpClient
import okhttp3.Request
import org.jeonfeel.moeuibit2.listener.UpBitWebSocketListener
import java.util.*

object UpBitWebSocket {
    private val TAG = UpBitWebSocket::class.java.simpleName

    private val client = OkHttpClient()
    private val request = Request.Builder()
        .url("wss://api.upbit.com/websocket/v1")
        .build()
    private val socketListener = UpBitWebSocketListener()
    private val socket = client.newWebSocket(request, socketListener)

    fun getListener(): UpBitWebSocketListener {
        return socketListener
    }

    fun requestKrwCoinList(markets: String) {
        val uuid = UUID.randomUUID().toString()
        socket.send("""[{"ticket":"$uuid"},{"type":"ticker","codes":[${markets}],"isOnlyRealtime":true},{"format":"SIMPLE"}]""")
    }

    fun close() {
//        socket.close()
    }

//    fun getRealTimeKrwCoinList(markets: String) {
//        val uuid = UUID.randomUUID().toString()
//        socket.send("""[{"ticket":"$uuid"},{"type":"ticker","codes":[${markets}],"isOnlyRealtime":true},{"format":"SIMPLE"}]""")
//    }
}