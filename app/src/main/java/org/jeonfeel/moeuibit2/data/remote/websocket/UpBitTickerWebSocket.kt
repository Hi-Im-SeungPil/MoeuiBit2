package org.jeonfeel.moeuibit2.data.remote.websocket

import com.orhanobut.logger.Logger
import okhttp3.OkHttpClient
import okhttp3.Request
import org.jeonfeel.moeuibit2.constants.*
import org.jeonfeel.moeuibit2.data.remote.websocket.listener.OnCoinDetailMessageReceiveListener
import org.jeonfeel.moeuibit2.data.remote.websocket.listener.OnTickerMessageReceiveListener
import org.jeonfeel.moeuibit2.data.remote.websocket.listener.PortfolioOnTickerMessageReceiveListener
import org.jeonfeel.moeuibit2.data.remote.websocket.listener.UpBitTickerWebSocketListener
import org.jeonfeel.moeuibit2.utils.NetworkMonitorUtil

object UpBitTickerWebSocket {

    var currentSocketState = SOCKET_IS_CONNECTED

    var currentMarket = 0
    var currentPage = 0
    private var krwMarkets = ""
    private var btcMarkets = ""
    private var favoriteMarkets = ""
    var detailMarket = ""
    var portfolioMarket = ""

    var tickerListener: OnTickerMessageReceiveListener? = null
    var portfolioListener: OnTickerMessageReceiveListener? = null
    var coinDetailListener: OnTickerMessageReceiveListener? = null

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

    fun requestKrwCoinList(
        marketState: Int
    ) {
        if (currentSocketState != SOCKET_IS_FAILURE) {
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
        } else {
            rebuildSocket()
        }
    }

    fun setMarkets(krwMarkets: String, btcMarkets: String) {
        this.krwMarkets = krwMarkets
        this.btcMarkets = btcMarkets
    }

    fun requestTicker(market: String) {
        currentSocketState = SOCKET_IS_CONNECTED
        socket.send(tickerWebSocketMessage(market))
    }

    fun onPause() {
        socket.send(tickerWebSocketMessage("pause"))
        currentSocketState = SOCKET_IS_ON_PAUSE
    }

    fun rebuildSocket() {
        if (NetworkMonitorUtil.currentNetworkState == INTERNET_CONNECTION) {
            socket.cancel()
            socket = client.newWebSocket(request, socketListener)
            currentSocketState = SOCKET_IS_CONNECTED
            when (currentPage) {
                IS_EXCHANGE_SCREEN -> {
                    requestKrwCoinList(currentMarket)
                }
                IS_DETAIL_SCREEN -> {
                    requestTicker(detailMarket)
                }
                IS_PORTFOLIO_SCREEN -> {
                    requestTicker(portfolioMarket)
                }
            }
        }
    }

    fun onlyRebuildSocket() {
        if (NetworkMonitorUtil.currentNetworkState == INTERNET_CONNECTION) {
            socket.cancel()
            socket = client.newWebSocket(request, socketListener)
            currentSocketState = SOCKET_IS_CONNECTED
        }
    }

    fun setFavoriteMarkets(markets: String) {
        this.favoriteMarkets = markets
    }

    fun message(message: String) {
        when (currentPage) {
            IS_EXCHANGE_SCREEN -> {
                tickerListener?.onTickerMessageReceiveListener(message)
            }
            IS_DETAIL_SCREEN -> {
                coinDetailListener?.onTickerMessageReceiveListener(message)
            }
            IS_PORTFOLIO_SCREEN -> {
                portfolioListener?.onTickerMessageReceiveListener(message)
            }
        }
    }
}