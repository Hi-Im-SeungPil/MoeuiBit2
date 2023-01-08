package org.jeonfeel.moeuibit2.data.remote.websocket.listener

import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import okio.ByteString

class UpBitCoinDetailWebSocketListener : WebSocketListener() {

    private var messageListener: OnCoinDetailMessageReceiveListener? = null

    fun setTickerMessageListener(onCoinDetailMessageReceiveListener: OnCoinDetailMessageReceiveListener?) {
        if (onCoinDetailMessageReceiveListener != null && this.messageListener !== onCoinDetailMessageReceiveListener) {
            this.messageListener = onCoinDetailMessageReceiveListener
        }
    }

    override fun onOpen(webSocket: WebSocket, response: Response) {
        super.onOpen(webSocket, response)
    }

    override fun onMessage(webSocket: WebSocket, text: String) {
        super.onMessage(webSocket, text)
    }

    override fun onMessage(webSocket: WebSocket, bytes: ByteString) {
        super.onMessage(webSocket, bytes)
        messageListener?.onCoinDetailMessageReceiveListener(bytes.string(Charsets.UTF_8))
    }

    override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
        super.onClosing(webSocket, code, reason)

    }

    override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
        super.onClosed(webSocket, code, reason)
    }

    override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
        super.onFailure(webSocket, t, response)
    }

    companion object {
        const val NORMAL_CLOSURE_STATUS = 1000
    }
}

interface OnCoinDetailMessageReceiveListener {
    fun onCoinDetailMessageReceiveListener(tickerJsonObject: String)
}