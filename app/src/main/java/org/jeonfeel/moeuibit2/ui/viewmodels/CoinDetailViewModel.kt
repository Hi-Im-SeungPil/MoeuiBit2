package org.jeonfeel.moeuibit2.ui.viewmodels

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.viewModelScope
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet
import com.orhanobut.logger.Logger
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.jeonfeel.moeuibit2.constants.*
import org.jeonfeel.moeuibit2.data.remote.websocket.UpBitTickerWebSocket
import org.jeonfeel.moeuibit2.data.remote.websocket.listener.OnTickerMessageReceiveListener
import org.jeonfeel.moeuibit2.data.remote.websocket.model.CoinDetailTickerModel
import org.jeonfeel.moeuibit2.ui.base.BaseViewModel
import org.jeonfeel.moeuibit2.ui.coindetail.chart.utils.Chart
import org.jeonfeel.moeuibit2.ui.coindetail.coininfo.utils.CoinInfo
import org.jeonfeel.moeuibit2.ui.coindetail.order.utils.CoinOrder
import org.jeonfeel.moeuibit2.utils.Utils
import javax.inject.Inject

@HiltViewModel
class CoinDetailViewModel @Inject constructor(
    val coinOrder: CoinOrder, val chart: Chart, val coinInfo: CoinInfo
) : BaseViewModel(), OnTickerMessageReceiveListener {
    private var name = ""
    private var marketState = -999
    var market = ""
    var preClosingPrice = 0.0
    val favoriteMutableState = mutableStateOf(false)
    private var isBTC = false

    fun initViewModel(market: String, preClosingPrice: Double, isFavorite: Boolean) {
        UpBitTickerWebSocket.coinDetailListener = this
        this.market = market
        this.preClosingPrice = preClosingPrice
        this.favoriteMutableState.value = isFavorite
        this.marketState = Utils.getSelectedMarket(market)
        chart.market = market
        coinOrder.initAdjustCommission()
        if (market.startsWith("BTC")) {
            isBTC = true
        }
        Logger.e("isbtc$isBTC")
    }

    private fun updateTicker() {
        viewModelScope.launch {
            while (coinOrder.isTickerSocketRunning) {
                val tradPrice = coinOrder.coinDetailModel.tradePrice
                coinOrder.state.currentTradePriceState.value = tradPrice
                chart.updateCandleTicker(tradPrice)
                delay(100L)
            }
        }
    }

    // 주문 화면
    fun initCoinDetailScreen() {
        if (coinOrder.state.currentTradePriceState.value == 0.0 && coinOrder.state.orderBookMutableStateList.value.isEmpty()) {
            viewModelScope.launch(ioDispatcher) {
                UpBitTickerWebSocket.getListener().setTickerMessageListener(this@CoinDetailViewModel)
                val reqMarket = if (isBTC) "$market,$BTC_MARKET" else market
                UpBitTickerWebSocket.requestTicker(reqMarket)
                updateTicker()
            }
        } else {
            UpBitTickerWebSocket.getListener().setTickerMessageListener(this)
            val reqMarket = if (isBTC) "$market,$BTC_MARKET" else market
            UpBitTickerWebSocket.requestTicker(reqMarket)
        }
        for (i in 0 until 4) {
            coinOrder.state.commissionStateList.add(mutableStateOf(0f))
        }
    }

    fun initOrderScreen() {
        viewModelScope.launch(ioDispatcher) {
            coinOrder.initOrderScreen(market)
        }
    }

    /**
     * 매수
     */
    fun bidRequest(
        currentPrice: Double, quantity: Double, totalPrice: Long = 0L, btcTotalPrice: Double = 0.0, currentBtcPrice: Double = 0.0
    ): Job {
        return viewModelScope.launch(ioDispatcher) {
            coinOrder.bidRequest(
                market, name, currentPrice, quantity, totalPrice, btcTotalPrice, marketState = marketState, currentBtcPrice
            )
        }
    }

    /**
     * 매도
     */
    fun askRequest(
        quantity: Double,
        totalPrice: Long,
        currentPrice: Double,
        btcTotalPrice: Double = 0.0,
    ): Job {
        return viewModelScope.launch(ioDispatcher) {
            coinOrder.askRequest(
                market, quantity, totalPrice, btcTotalPrice, currentPrice, marketState
            )
        }
    }

    /**
     * 수수료 조정
     */
    fun adjustCommission() {
        viewModelScope.launch(ioDispatcher) {
            coinOrder.adjustCommission()
        }
    }

    /**
     * 수수료 조정 초기화
     */
    fun initAdjustCommission() {
        coinOrder.initAdjustCommission()
    }

    /**
     * 수수료 얻어오기
     */
    fun getCommission(key: String): Float {
        return coinOrder.getCommission(key)
    }

    /**
     * 거래내역 조회
     */
    fun getTransactionInfoList() {
        viewModelScope.launch(ioDispatcher) {
            coinOrder.getTransactionInfoList(market)
        }
    }

    // 코인 정보 화면
    fun getCoinInfo() {
        coinInfo.getCoinInfo(market)
    }

    // 차트 화면
    fun requestOldData(
        positiveBarDataSet: IBarDataSet, negativeBarDataSet: IBarDataSet, candleXMin: Float
    ) {
        viewModelScope.launch {
            chart.requestOldData(
                positiveBarDataSet = positiveBarDataSet, negativeBarDataSet = negativeBarDataSet, candleXMin = candleXMin
            )
        }
    }

    fun requestChartData() {
        viewModelScope.launch {
            chart.requestChartData(market = market)
        }
    }

    /**
     * 웹소켓 리스너
     */
    override fun onTickerMessageReceiveListener(tickerJsonObject: String) {
        if (coinOrder.isTickerSocketRunning && UpBitTickerWebSocket.currentPage == IS_DETAIL_SCREEN) {
            val model = gson.fromJson(tickerJsonObject, CoinDetailTickerModel::class.java)
            if (marketState == SELECTED_BTC_MARKET && model.code.startsWith(SYMBOL_KRW)) {
                coinOrder.state.currentBTCPrice.value = model.tradePrice
            }
            if (model.code == market) {
                coinOrder.coinDetailModel = model
                coinOrder.state.currentTradePriceStateForOrderBook.value = coinOrder.coinDetailModel.tradePrice
            }
        }
    }
}