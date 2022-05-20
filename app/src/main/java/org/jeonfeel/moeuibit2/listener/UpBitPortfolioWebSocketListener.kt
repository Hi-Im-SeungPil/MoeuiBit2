package org.jeonfeel.moeuibit2.listener

import android.util.Log
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import okio.ByteString

class UpBitPortfolioWebSocketListener: WebSocketListener() {

    private var messageListener: PortfolioOnTickerMessageReceiveListener? = null

    fun setPortfolioMessageListener(portfolioOnTickerMessageReceiveListener: PortfolioOnTickerMessageReceiveListener?) {
        if (portfolioOnTickerMessageReceiveListener != null && this.messageListener !== portfolioOnTickerMessageReceiveListener) {
            this.messageListener = portfolioOnTickerMessageReceiveListener
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
        messageListener?.portfolioOnTickerMessageReceiveListener(bytes.string(Charsets.UTF_8))
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
    }

    companion object {
        const val NORMAL_CLOSURE_STATUS = 1000
    }
}

interface PortfolioOnTickerMessageReceiveListener {
    fun portfolioOnTickerMessageReceiveListener(tickerJsonObject: String)
}
