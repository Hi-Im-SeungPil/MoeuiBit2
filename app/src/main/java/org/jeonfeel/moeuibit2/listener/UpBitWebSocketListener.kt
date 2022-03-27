package org.jeonfeel.moeuibit2.listener

import android.util.Log
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import okio.ByteString
import org.jeonfeel.moeuibit2.data.remote.websocket.UpBitWebSocket

class UpBitWebSocketListener : WebSocketListener() {

    private lateinit var messageListener: OnMessageReceiveListener

    @JvmName("setMessageListener1")
    fun setMessageListener1(onMessageReceiveListener: OnMessageReceiveListener?) {
        if (onMessageReceiveListener != null) {
            this.messageListener = onMessageReceiveListener
        }
    }

    override fun onOpen(webSocket: WebSocket, response: Response) {
        super.onOpen(webSocket, response)
        Log.e("TAG", "open!")
    }

    override fun onMessage(webSocket: WebSocket, text: String) {
        super.onMessage(webSocket, text)
        Log.e("TAG", "Receiving $text")
    }

    override fun onMessage(webSocket: WebSocket, bytes: ByteString) {
        super.onMessage(webSocket, bytes)
        messageListener.onMessageReceiveListener(bytes.string(Charsets.UTF_8))
    }

    override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
        super.onClosing(webSocket, code, reason)
        webSocket.close(NORMAL_CLOSURE_STATUS, null)
        webSocket.cancel()
    }

    override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
        super.onClosed(webSocket, code, reason)
    }

    override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
        super.onFailure(webSocket, t, response)
        Log.e("Socket", "Error => ${t.message}")
        UpBitWebSocket.onFail()
    }

    companion object {
        const val NORMAL_CLOSURE_STATUS = 1000
    }
}

interface OnMessageReceiveListener {
    fun onMessageReceiveListener(tickerJsonObject: String)
}