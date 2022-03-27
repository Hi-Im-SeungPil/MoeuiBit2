package org.jeonfeel.moeuibit2.data.remote.websocket

import okhttp3.OkHttpClient
import okhttp3.Request
import org.jeonfeel.moeuibit2.listener.UpBitWebSocketListener
import org.jeonfeel.moeuibit2.listener.UpBitWebSocketListener.Companion.NORMAL_CLOSURE_STATUS
import java.util.*

const val SOCKET_IS_CONNECTED = 0
const val SOCKET_IS_NO_CONNECTION = -1
const val SOCKET_IS_ON_PAUSE = -2


object UpBitWebSocket {
    private var krwMarkets = ""
    var currentSocketState = SOCKET_IS_CONNECTED
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

    fun requestKrwCoinList() {
        socketRebuild()
        val uuid = UUID.randomUUID().toString()
        socket.send("""[{"ticket":"$uuid"},{"type":"ticker","codes":[${krwMarkets}],"isOnlyRealtime":true},{"format":"SIMPLE"}]""")
    }

    private fun socketRebuild() {
        if(currentSocketState != SOCKET_IS_CONNECTED) {
            socket = client.newWebSocket(request, socketListener)
            currentSocketState = SOCKET_IS_CONNECTED
        }
    }

    fun setKrwMarkets (markets: String) {
        krwMarkets = markets
    }

    fun onPause() {
        socket.close(NORMAL_CLOSURE_STATUS,"onPause")
        currentSocketState = SOCKET_IS_ON_PAUSE
    }

    fun onResume() {
        if (currentSocketState == SOCKET_IS_ON_PAUSE) {
            requestKrwCoinList()
            currentSocketState = SOCKET_IS_CONNECTED
        }
    }

    fun onFail() {
        if (currentSocketState != SOCKET_IS_NO_CONNECTION) {
            currentSocketState = SOCKET_IS_NO_CONNECTION
        }
    }
}