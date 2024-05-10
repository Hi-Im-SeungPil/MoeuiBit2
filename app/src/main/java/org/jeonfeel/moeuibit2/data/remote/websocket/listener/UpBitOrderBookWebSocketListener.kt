package org.jeonfeel.moeuibit2.data.remote.websocket.listener

import com.orhanobut.logger.Logger
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import okio.ByteString
import org.jeonfeel.moeuibit2.constants.SOCKET_IS_FAILURE
import org.jeonfeel.moeuibit2.data.remote.websocket.upbit.UpBitOrderBookWebSocket

class UpBitOrderBookWebSocketListener : WebSocketListener() {

    private var messageListener: OnOrderBookMessageReceiveListener? = null

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
    }

    override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
        super.onClosed(webSocket, code, reason)
        UpBitOrderBookWebSocket.currentSocketState = SOCKET_IS_FAILURE
    }

    override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
        super.onFailure(webSocket, t, response)
        Logger.e("orderBook on Failure")
        t.printStackTrace()
        UpBitOrderBookWebSocket.currentSocketState = SOCKET_IS_FAILURE
        UpBitOrderBookWebSocket.rebuildSocket()
    }
}

interface OnOrderBookMessageReceiveListener {
    fun onOrderBookMessageReceiveListener(orderBookJsonObject: String)
}