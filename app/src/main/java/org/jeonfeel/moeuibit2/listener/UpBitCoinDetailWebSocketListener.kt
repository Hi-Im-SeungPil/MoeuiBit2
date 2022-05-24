package org.jeonfeel.moeuibit2.listener

import android.util.Log
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import okio.ByteString
import org.jeonfeel.moeuibit2.data.remote.websocket.UpBitCoinDetailWebSocket

class UpBitCoinDetailWebSocketListener: WebSocketListener() {

    private var messageListener: OnCoinDetailMessageReceiveListener? = null

    fun setTickerMessageListener(onCoinDetailMessageReceiveListener: OnCoinDetailMessageReceiveListener?) {
        if (onCoinDetailMessageReceiveListener != null && this.messageListener !== onCoinDetailMessageReceiveListener) {
            this.messageListener = onCoinDetailMessageReceiveListener
        }
    }

    override fun onOpen(webSocket: WebSocket, response: Response) {
        super.onOpen(webSocket, response)
        UpBitCoinDetailWebSocket.retryCount = 0
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
        webSocket.close(UpBitTickerWebSocketListener.NORMAL_CLOSURE_STATUS, null)
        webSocket.cancel()
    }

    override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
        super.onClosed(webSocket, code, reason)
    }

    override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
        super.onFailure(webSocket, t, response)
        UpBitCoinDetailWebSocket.onFail()
        Log.e("Socket", "Error => ${t.message}")
    }

    companion object{
        const val NORMAL_CLOSURE_STATUS = 1000
    }
}
interface OnCoinDetailMessageReceiveListener {
    fun onCoinDetailMessageReceiveListener(tickerJsonObject: String)
}