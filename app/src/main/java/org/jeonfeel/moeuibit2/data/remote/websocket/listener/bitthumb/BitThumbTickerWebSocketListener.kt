package org.jeonfeel.moeuibit2.data.remote.websocket.listener.bitthumb

import com.orhanobut.logger.Logger
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import okio.ByteString
import org.jeonfeel.moeuibit2.constants.SOCKET_IS_FAILURE
import org.jeonfeel.moeuibit2.data.remote.websocket.bitthumb.BitthumbTickerWebSocket

class BitThumbTickerWebSocketListener : WebSocketListener() {

    override fun onOpen(webSocket: WebSocket, response: Response) {
        super.onOpen(webSocket, response)

    }

    override fun onMessage(webSocket: WebSocket, text: String) {
        super.onMessage(webSocket, text)
//        Logger.e("text text -> $text")
        BitthumbTickerWebSocket.message(text)
    }

    override fun onMessage(webSocket: WebSocket, bytes: ByteString) {
        super.onMessage(webSocket, bytes)
        Logger.e("text text2 -> ${bytes.string(Charsets.UTF_8)}")
    }

    override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
        super.onClosing(webSocket, code, reason)
        Logger.e("ticker on closing")
        Logger.e("failure response ${reason}")
        Logger.e("failure message ${code}")
    }

    override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
        super.onClosed(webSocket, code, reason)
        BitthumbTickerWebSocket.currentSocketState = SOCKET_IS_FAILURE
        Logger.e("ticker on closed")
        Logger.e("failure response ${reason}")
        Logger.e("failure message ${code}")
    }

    override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
        super.onFailure(webSocket, t, response)
        Logger.e("ticker on Failure")
        Logger.e("failure response ${response?.message}")
        Logger.e("failure message ${t.message}")
        BitthumbTickerWebSocket.currentSocketState = SOCKET_IS_FAILURE
        BitthumbTickerWebSocket.rebuildSocket()
    }
}

//interface OnTickerMessageReceiveListener {
//    fun onTickerMessageReceiveListener(tickerJsonObject: String)
//}