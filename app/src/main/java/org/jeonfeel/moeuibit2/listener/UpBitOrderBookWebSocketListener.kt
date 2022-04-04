package org.jeonfeel.moeuibit2.listener

import android.util.Log
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import okio.ByteString

class UpBitOrderBookWebSocketListener : WebSocketListener() {

    private var messageListener: OnOrderBookMessageReceiveListener? = null
//            && this.messageListener !== onOrderBookMessageReceiveListener
    fun setOrderBookMessageListener(onOrderBookMessageReceiveListener: OnOrderBookMessageReceiveListener?) {
        if (onOrderBookMessageReceiveListener != null) {
            this.messageListener = onOrderBookMessageReceiveListener
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
        Log.d("onFailure",t.message.toString())
    }

    companion object {
        const val NORMAL_CLOSURE_STATUS = 1000
    }
}

interface OnOrderBookMessageReceiveListener {
    fun onOrderBookMessageReceiveListener(orderBookJsonObject: String)
}