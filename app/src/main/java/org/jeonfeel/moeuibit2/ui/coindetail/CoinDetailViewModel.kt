package org.jeonfeel.moeuibit2.ui.coindetail

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.viewModelScope
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet
import com.google.gson.JsonObject
import com.orhanobut.logger.Logger
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.jeonfeel.moeuibit2.constants.*
import org.jeonfeel.moeuibit2.data.remote.retrofit.ApiResult
import org.jeonfeel.moeuibit2.data.remote.websocket.bitthumb.BitthumbTickerWebSocket
import org.jeonfeel.moeuibit2.data.remote.websocket.upbit.UpBitTickerWebSocket
import org.jeonfeel.moeuibit2.data.remote.websocket.listener.upbit.OnTickerMessageReceiveListener
import org.jeonfeel.moeuibit2.data.remote.websocket.model.bitthumb.BitthumbCoinDetailTickerModel
import org.jeonfeel.moeuibit2.data.remote.websocket.model.upbit.CoinDetailTickerModel
import org.jeonfeel.moeuibit2.data.repository.remote.RemoteRepository
import org.jeonfeel.moeuibit2.ui.base.BaseViewModel
import org.jeonfeel.moeuibit2.ui.coindetail.chart.utils.upbit.Chart
import org.jeonfeel.moeuibit2.ui.coindetail.coininfo.utils.CoinInfo
import org.jeonfeel.moeuibit2.ui.coindetail.order.utils.CoinOrder
import org.jeonfeel.moeuibit2.ui.main.exchange.ExchangeViewModel.Companion.ROOT_EXCHANGE_BITTHUMB
import org.jeonfeel.moeuibit2.ui.main.exchange.ExchangeViewModel.Companion.ROOT_EXCHANGE_UPBIT
import org.jeonfeel.moeuibit2.utils.Utils
import org.jeonfeel.moeuibit2.utils.calculator.Calculator
import javax.inject.Inject

@HiltViewModel
class CoinDetailViewModel @Inject constructor(
    val coinOrder: CoinOrder,
    val chart: Chart,
    val coinInfo: CoinInfo,
    val remoteRepository: RemoteRepository
) : BaseViewModel(), OnTickerMessageReceiveListener {
    private var name = ""
    private var marketState = -999
    var market = ""
    var preClosingPrice = 0.0
    val favoriteMutableState = mutableStateOf(false)
    var rootExchange = ROOT_EXCHANGE_UPBIT
    private var isBTC = false
    private var updateOrderBlockJob: Job? = null

    fun initViewModel(
        market: String,
        preClosingPrice: Double,
        isFavorite: Boolean,
        rootExchange: String
    ) {
        this.market = market
        this.preClosingPrice = preClosingPrice
        this.favoriteMutableState.value = isFavorite
        this.marketState = Utils.getSelectedMarket(market)
        chart.market = market
        coinOrder.initAdjustCommission()
        if (market.startsWith("BTC")) {
            isBTC = true
        }
        this.rootExchange = rootExchange
        if (rootExchange == ROOT_EXCHANGE_UPBIT) {
            UpBitTickerWebSocket.coinDetailListener = this
        } else {
            BitthumbTickerWebSocket.coinDetailListener = this
        }
        requestCoinTicker()
        Logger.e("isbtc$isBTC rootExchange$rootExchange")
    }

    private fun requestCoinTicker() {
        if (rootExchange == ROOT_EXCHANGE_BITTHUMB) {
            viewModelScope.launch {
                remoteRepository.getBitthumbTickerUnit(market = market).collect { apiResult ->
                    when (apiResult.status) {
                        ApiResult.Status.LOADING -> {

                        }

                        ApiResult.Status.SUCCESS -> {
                            Logger.e("tickerUnit success -> ${apiResult.data}")
                            val bitthumbModel = apiResult.data?.get("data") as JsonObject
                            if (bitthumbModel != null) {
                                val model = CoinDetailTickerModel(
                                    code = market,
                                    tradePrice = bitthumbModel["closing_price"]?.asDouble ?: 0.0,
                                    signedChangeRate = Calculator.orderBookRateCalculator(
                                        bitthumbModel["opening_price"].asDouble,
                                        bitthumbModel["closing_price"].asDouble
                                    ) * 0.01,
                                    signedChangePrice = bitthumbModel["closing_price"].asDouble - bitthumbModel["opening_price"].asDouble
                                )
                                Logger.e("tickerUnit success -> ${bitthumbModel["closing_price"].asDouble}")
                                Logger.e("tickerUnit success -> ${bitthumbModel["opening_price"].asDouble}")
                                Logger.e(
                                    "tickerUnit success -> ${
                                        Calculator.orderBookRateCalculator(
                                            bitthumbModel["opening_price"].asDouble,
                                            bitthumbModel["closing_price"].asDouble
                                        )
                                    }"
                                )
                                if (model.code == market) {
                                    Logger.e("tickerUnit success -> 2")
                                    coinOrder.coinDetailModel = model
                                    coinOrder.state.currentTradePriceStateForOrderBook.value =
                                        coinOrder.coinDetailModel.tradePrice
                                }
                            }
                        }

                        ApiResult.Status.API_ERROR -> {
                            Logger.e("tickerUnit api error -> " + apiResult.message.toString())
                        }

                        ApiResult.Status.NETWORK_ERROR -> {
                            Logger.e("tickerUnit network error -> " + apiResult.message.toString())
                        }
                    }
                }
            }
        }
    }

    private suspend fun updateTicker() {
        while (coinOrder.isTickerSocketRunning) {
            val tradPrice = coinOrder.coinDetailModel.tradePrice
            coinOrder.state.currentTradePriceState.value = tradPrice
            if (rootExchange == ROOT_EXCHANGE_UPBIT) {
                chart.updateCandleTicker(tradPrice)
            } else {
                chart.bitthumbUpdateCandleTicker(tradePrice = tradPrice)
            }
            delay(100L)
        }
    }

    // 주문 화면
    fun initCoinDetailScreen() {
        when (rootExchange) {
            ROOT_EXCHANGE_UPBIT -> {
                if (coinOrder.state.currentTradePriceState.value == 0.0 && coinOrder.state.orderBookMutableStateList.isEmpty()) {
                    viewModelScope.launch(ioDispatcher) {
                        val reqMarket = if (isBTC) "$market,$BTC_MARKET" else market
                        UpBitTickerWebSocket.requestTicker(reqMarket)
                        updateTicker()
                    }
                } else {
                    val reqMarket = if (isBTC) "$market,$BTC_MARKET" else market
                    UpBitTickerWebSocket.requestTicker(reqMarket)
                }
            }

            ROOT_EXCHANGE_BITTHUMB -> {
                if (coinOrder.state.currentTradePriceState.value == 0.0 && coinOrder.state.orderBookMutableStateList.isEmpty()) {
                    viewModelScope.launch(ioDispatcher) {
                        val bitthumbMarket = Utils.upbitMarketToBitthumbMarket(market)
                        val reqMarket =
                            if (isBTC) "\"${bitthumbMarket}\",\"$BITTHUMB_BTC_MARKET\"" else "\"$bitthumbMarket\""
                        BitthumbTickerWebSocket.requestTicker(reqMarket)
                        updateTicker()
                    }
                } else {
                    val bitthumbMarket = Utils.upbitMarketToBitthumbMarket(market)
                    val reqMarket =
                        if (isBTC) "\"${bitthumbMarket}\",\"$BITTHUMB_BTC_MARKET\"" else "\"$bitthumbMarket\""
                    BitthumbTickerWebSocket.requestTicker(reqMarket)
                }
            }
        }
        for (i in 0 until 4) {
            coinOrder.state.commissionStateList.add(mutableStateOf(0f))
        }
    }

    fun initOrderScreen() {
        viewModelScope.launch(ioDispatcher) {
            coinOrder.initOrderScreen(market, rootExchange)
        }
        updateOrderBlockJob = viewModelScope.launch {
            coinOrder.updateOrderBlock()
        }
        updateOrderBlockJob?.start()
    }

    fun cancelUpdateOrderBlockJob() {
        viewModelScope.launch {
            updateOrderBlockJob?.cancelAndJoin()
        }
    }

    /**
     * 매수
     */
    fun bidRequest(
        currentPrice: Double,
        quantity: Double,
        totalPrice: Long = 0L,
        btcTotalPrice: Double = 0.0,
        currentBtcPrice: Double = 0.0
    ): Job {
        return viewModelScope.launch(ioDispatcher) {
            coinOrder.bidRequest(
                market,
                name,
                currentPrice,
                quantity,
                totalPrice,
                btcTotalPrice,
                marketState = marketState,
                currentBtcPrice
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
            if (rootExchange == ROOT_EXCHANGE_UPBIT) {
                chart.requestOldData(
                    positiveBarDataSet = positiveBarDataSet,
                    negativeBarDataSet = negativeBarDataSet,
                    candleXMin = candleXMin
                )
            }
        }
    }

    fun requestChartData() {
        viewModelScope.launch {
            if (rootExchange == ROOT_EXCHANGE_UPBIT) {
                chart.requestUpbitChartData(market = market)
            } else if (rootExchange == ROOT_EXCHANGE_BITTHUMB) {
                Logger.e("requestChartData")
                chart.setBitthumbChart()
                chart.requestBitthumbChartData(market = market)
            }
        }
    }

    fun getOrderBookInitPosition(): Int {
        return if (rootExchange == ROOT_EXCHANGE_UPBIT) {
            8
        } else {
            24
        }
    }

    fun getBlockItemCount(): Int {
        return if (rootExchange == ROOT_EXCHANGE_UPBIT) {
            15
        } else {
            30
        }
    }

    /**
     * 웹소켓 리스너
     */
    override fun onTickerMessageReceiveListener(tickerJsonObject: String) {
        if (rootExchange == ROOT_EXCHANGE_UPBIT) {
            if (coinOrder.isTickerSocketRunning && UpBitTickerWebSocket.currentPage == IS_DETAIL_SCREEN) {
                val model = gson.fromJson(tickerJsonObject, CoinDetailTickerModel::class.java)
                if (marketState == SELECTED_BTC_MARKET && model.code.startsWith(SYMBOL_KRW)) {
                    coinOrder.state.currentBTCPrice.value = model.tradePrice
                }
                if (model.code == market) {
                    coinOrder.coinDetailModel = model
                    coinOrder.state.currentTradePriceStateForOrderBook.value =
                        coinOrder.coinDetailModel.tradePrice
                }
            }
        } else {
            if (coinOrder.isTickerSocketRunning && BitthumbTickerWebSocket.currentScreen == IS_DETAIL_SCREEN) {
                val bitthumbModel =
                    gson.fromJson(tickerJsonObject, BitthumbCoinDetailTickerModel::class.java)
                if (bitthumbModel.content != null) {
                    val model = CoinDetailTickerModel(
                        code = Utils.bitthumbMarketToUpbitMarket(bitthumbModel.content.code),
                        tradePrice = bitthumbModel.content.tradePrice.toDouble(),
                        signedChangeRate = bitthumbModel.content.signedChangeRate.toDouble() * 0.01,
                        signedChangePrice = bitthumbModel.content.signedChangePrice.toDouble()
                    )
                    if (marketState == SELECTED_BTC_MARKET && model.code.startsWith(SYMBOL_KRW)) {
                        coinOrder.state.currentBTCPrice.value = model.tradePrice
                    }
                    if (model.code == market) {
                        coinOrder.coinDetailModel = model
                        coinOrder.state.currentTradePriceStateForOrderBook.value =
                            coinOrder.coinDetailModel.tradePrice
                    }
                }
            }
        }
    }
}