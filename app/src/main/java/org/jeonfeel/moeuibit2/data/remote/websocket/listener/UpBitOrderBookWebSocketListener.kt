package org.jeonfeel.moeuibit2.data.remote.websocket.listener

import com.orhanobut.logger.Logger
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import okio.ByteString

class UpBitOrderBookWebSocketListener : WebSocketListener() {

    private var messageListener: OnOrderBookMessageReceiveListener? = null

    fun setOrderBookMessageListener(onOrderBookMessageReceiveListener: OnOrderBookMessageReceiveListener?) {
        if (onOrderBookMessageReceiveListener != null) {
            this.messageListener = onOrderBookMessageReceiveListener
        }
    }

    override fun onOpen(webSocket: WebSocket, response: Response) {
        super.onOpen(webSocket, response)
        Logger.d("UpBitOrderBookWebSocketListener on Open")
    }

    override fun onMessage(webSocket: WebSocket, text: String) {
        super.onMessage(webSocket, text)
    }

    override fun onMessage(webSocket: WebSocket, bytes: ByteString) {
        super.onMessage(webSocket, bytes)
//        Logger.e(bytes.string(Charsets.UTF_8))
        messageListener?.onOrderBookMessageReceiveListener(bytes.string(Charsets.UTF_8))
    }

    override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
        super.onClosing(webSocket, code, reason)
    }

    override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
        super.onClosed(webSocket, code, reason)
    }

    override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
        super.onFailure(webSocket, t, response)
        Logger.e("UpBitTickerWebSocketListener on Failure}")
        t.printStackTrace()
    }

    companion object {
        const val NORMAL_CLOSURE_STATUS = 1000
    }
}

interface OnOrderBookMessageReceiveListener {
    fun onOrderBookMessageReceiveListener(orderBookJsonObject: String)
}