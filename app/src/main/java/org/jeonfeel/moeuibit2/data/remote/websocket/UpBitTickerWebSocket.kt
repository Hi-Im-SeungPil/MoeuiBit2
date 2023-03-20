package org.jeonfeel.moeuibit2.data.remote.websocket

import okhttp3.OkHttpClient
import okhttp3.Request
import org.jeonfeel.moeuibit2.constants.*
import org.jeonfeel.moeuibit2.data.remote.websocket.listener.UpBitTickerWebSocketListener

object UpBitTickerWebSocket {

    var currentMarket = 0
    private var krwMarkets = ""
    private var btcMarkets = ""
    private var favoriteMarkets = ""
    var detailMarket = ""
    var currentSocketState = SOCKET_IS_CONNECTED
    private var client = OkHttpClient().newBuilder().retryOnConnectionFailure(true)
        .connectTimeout(timeOutDuration)
        .callTimeout(timeOutDuration)
        .readTimeout(readTimeOutDuration)
        .writeTimeout(timeOutDuration)
        .build()
    private val request = Request.Builder()
        .url(webSocketBaseUrl)
        .build()
    private val socketListener = UpBitTickerWebSocketListener()
    var socket = client.newWebSocket(request, socketListener)

    fun getListener(): UpBitTickerWebSocketListener {
        return socketListener
    }

    fun requestKrwCoinList(marketState: Int) {
        when (marketState) {
            SELECTED_KRW_MARKET -> {
                socket.send(tickerWebSocketMessage(krwMarkets))
                currentMarket = SELECTED_KRW_MARKET
            }
            SELECTED_BTC_MARKET -> {
                socket.send(tickerWebSocketMessage(btcMarkets))
                currentMarket = SELECTED_BTC_MARKET
            }
            SELECTED_FAVORITE -> {
                socket.send(tickerWebSocketMessage(favoriteMarkets))
                currentMarket = SELECTED_FAVORITE
            }
        }
        currentSocketState = SOCKET_IS_CONNECTED
    }

    fun setMarkets(krwMarkets: String, btcMarkets: String) {
        this.krwMarkets = krwMarkets
        this.btcMarkets = btcMarkets
    }

    fun requestCoinDetailTicker(market: String) {
        currentSocketState = SOCKET_IS_CONNECTED
        socket.send(tickerWebSocketMessage(market))
    }

    fun onPause() {
        socket.send(tickerWebSocketMessage(""))
        currentSocketState = SOCKET_IS_ON_PAUSE
    }

    fun setFavoriteMarkets(markets: String) {
        this.favoriteMarkets = markets
    }
}