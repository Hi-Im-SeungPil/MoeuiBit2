package org.jeonfeel.moeuibit2.data.network.websocket.listener.bitthumb

import com.orhanobut.logger.Logger
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import okio.ByteString
import org.jeonfeel.moeuibit2.constants.SOCKET_IS_FAILURE
import org.jeonfeel.moeuibit2.data.network.websocket.bitthumb.BitthumbOrderBookWebSocket
import org.jeonfeel.moeuibit2.data.network.websocket.listener.upbit.OnOrderBookMessageReceiveListener

class BitthumbOrderBookWebSocketListener : WebSocketListener() {

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
        messageListener?.onOrderBookMessageReceiveListener(text)
    }

    override fun onMessage(webSocket: WebSocket, bytes: ByteString) {
        super.onMessage(webSocket, bytes)
        Logger.e("orderbook ${bytes.string(Charsets.UTF_8)}")
    }

    override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
        super.onClosing(webSocket, code, reason)
        Logger.e("orderBook on Closing")
    }

    override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
        super.onClosed(webSocket, code, reason)
        Logger.e("orderBook on closed")
        BitthumbOrderBookWebSocket.currentSocketState = SOCKET_IS_FAILURE
    }

    override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
        super.onFailure(webSocket, t, response)
        Logger.e("orderBook on Failure")
        t.printStackTrace()
        BitthumbOrderBookWebSocket.currentSocketState = SOCKET_IS_FAILURE
        BitthumbOrderBookWebSocket.rebuildSocket()
    }
}