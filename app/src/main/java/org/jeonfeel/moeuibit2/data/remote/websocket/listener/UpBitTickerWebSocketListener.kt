package org.jeonfeel.moeuibit2.data.remote.websocket.listener

import com.orhanobut.logger.Logger
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import okio.ByteString
import org.jeonfeel.moeuibit2.constants.SOCKET_IS_CONNECTED
import org.jeonfeel.moeuibit2.constants.SOCKET_IS_FAILURE
import org.jeonfeel.moeuibit2.data.remote.websocket.UpBitTickerWebSocket

class UpBitTickerWebSocketListener : WebSocketListener() {

    private var messageListener: OnTickerMessageReceiveListener? = null

    fun setTickerMessageListener(onTickerMessageReceiveListener: OnTickerMessageReceiveListener?) {
//        if (this.messageListener !== onTickerMessageReceiveListener) {
//            this.messageListener = onTickerMessageReceiveListener
//        }
    }

    override fun onOpen(webSocket: WebSocket, response: Response) {
        super.onOpen(webSocket, response)
        UpBitTickerWebSocket.currentSocketState = SOCKET_IS_CONNECTED
    }

    override fun onMessage(webSocket: WebSocket, text: String) {
        super.onMessage(webSocket, text)
    }

    override fun onMessage(webSocket: WebSocket, bytes: ByteString) {
        super.onMessage(webSocket, bytes)
//        Logger.e("exchange  =>  ${bytes.string(Charsets.UTF_8)}")
        UpBitTickerWebSocket.message(bytes.string(Charsets.UTF_8))
    }

    override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
        super.onClosing(webSocket, code, reason)
    }

    override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
        super.onClosed(webSocket, code, reason)
        UpBitTickerWebSocket.currentSocketState = SOCKET_IS_FAILURE
    }

    override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
        super.onFailure(webSocket, t, response)
        Logger.e("ticker on Failure")
        t.printStackTrace()
        UpBitTickerWebSocket.currentSocketState = SOCKET_IS_FAILURE
        UpBitTickerWebSocket.rebuildSocket()
    }
}

interface OnTickerMessageReceiveListener {
    fun onTickerMessageReceiveListener(tickerJsonObject: String)
}