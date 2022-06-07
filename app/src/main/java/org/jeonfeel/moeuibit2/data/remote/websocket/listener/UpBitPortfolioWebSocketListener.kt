package org.jeonfeel.moeuibit2.data.remote.websocket.listener

import android.util.Log
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import okio.ByteString
import org.jeonfeel.moeuibit2.data.remote.websocket.UpBitPortfolioWebSocket

class UpBitPortfolioWebSocketListener: WebSocketListener() {

    private var messageListener: PortfolioOnTickerMessageReceiveListener? = null

    fun setPortfolioMessageListener(portfolioOnTickerMessageReceiveListener: PortfolioOnTickerMessageReceiveListener?) {
        if (this.messageListener !== portfolioOnTickerMessageReceiveListener) {
            this.messageListener = portfolioOnTickerMessageReceiveListener
            Log.e("portfolio Socket", "setListener")
        }
    }

    override fun onOpen(webSocket: WebSocket, response: Response) {
        super.onOpen(webSocket, response)
        UpBitPortfolioWebSocket.retryCount = 0
        Log.e("portfolio Socket", "open")
    }

    override fun onMessage(webSocket: WebSocket, text: String) {
        super.onMessage(webSocket, text)
        Log.e("portfolio Socket", "Receiving $text")
    }

    override fun onMessage(webSocket: WebSocket, bytes: ByteString) {
        super.onMessage(webSocket, bytes)
        Log.e("portfolio Socket", "onMessage")
        messageListener?.portfolioOnTickerMessageReceiveListener(bytes.string(Charsets.UTF_8))
    }

    override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
        super.onClosing(webSocket, code, reason)
        Log.e("portfolio Socket", "onClosing")
        webSocket.close(NORMAL_CLOSURE_STATUS, null)
        webSocket.cancel()
    }

    override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
        super.onClosed(webSocket, code, reason)
        Log.e("portfolio Socket", "Closed")
    }

    override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
        super.onFailure(webSocket, t, response)
        Log.e("portfolio Socket", "Error1 => ${t.message}")
        if(UpBitPortfolioWebSocket.retryCount <= 10) {
            UpBitPortfolioWebSocket.onFail()
        } else {
            UpBitPortfolioWebSocket.onPause()
        }
    }

    companion object {
        const val NORMAL_CLOSURE_STATUS = 1000
    }
}

interface PortfolioOnTickerMessageReceiveListener {
    fun portfolioOnTickerMessageReceiveListener(tickerJsonObject: String)
}
