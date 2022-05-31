package org.jeonfeel.moeuibit2.listener

import android.util.Log
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import okio.ByteString
import org.jeonfeel.moeuibit2.data.remote.websocket.UpBitOrderBookWebSocket

class UpBitOrderBookWebSocketListener : WebSocketListener() {

    private var messageListener: OnOrderBookMessageReceiveListener? = null

    fun setOrderBookMessageListener(onOrderBookMessageReceiveListener: OnOrderBookMessageReceiveListener?) {
        if (onOrderBookMessageReceiveListener != null) {
            this.messageListener = onOrderBookMessageReceiveListener
        }
    }

    override fun onOpen(webSocket: WebSocket, response: Response) {
        super.onOpen(webSocket, response)
        UpBitOrderBookWebSocket.retryCount = 0
    }

    override fun onMessage(webSocket: WebSocket, text: String) {
        super.onMessage(webSocket, text)
    }

    override fun onMessage(webSocket: WebSocket, bytes: ByteString) {
        super.onMessage(webSocket, bytes)
        messageListener?.onOrderBookMessageReceiveListener(bytes.string(Charsets.UTF_8))
    }

    override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
        super.onClosing(webSocket, code, reason)
        webSocket.cancel()
    }

    override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
        super.onClosed(webSocket, code, reason)
    }

    override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
        super.onFailure(webSocket, t, response)
        Log.d("orderbook onFailure",t.message.toString())
        if(UpBitOrderBookWebSocket.retryCount <= 10) {
            UpBitOrderBookWebSocket.onFail()
        } else {
            UpBitOrderBookWebSocket.onPause()
        }
    }

    companion object {
        const val NORMAL_CLOSURE_STATUS = 1000
    }
}

interface OnOrderBookMessageReceiveListener {
    fun onOrderBookMessageReceiveListener(orderBookJsonObject: String)
}