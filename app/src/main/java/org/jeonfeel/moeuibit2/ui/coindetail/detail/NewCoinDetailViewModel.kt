package org.jeonfeel.moeuibit2.ui.coindetail.detail

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.viewModelScope
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.jeonfeel.moeuibit2.GlobalState
import org.jeonfeel.moeuibit2.constants.BTC_MARKET
import org.jeonfeel.moeuibit2.constants.EXCHANGE_BITTHUMB
import org.jeonfeel.moeuibit2.constants.EXCHANGE_UPBIT
import org.jeonfeel.moeuibit2.constants.ioDispatcher
import org.jeonfeel.moeuibit2.data.local.room.entity.MyCoin
import org.jeonfeel.moeuibit2.data.network.retrofit.model.upbit.CommonExchangeModel
import org.jeonfeel.moeuibit2.data.network.retrofit.model.upbit.OrderBookModel
import org.jeonfeel.moeuibit2.data.network.retrofit.request.upbit.GetUpbitMarketTickerReq
import org.jeonfeel.moeuibit2.data.network.retrofit.response.upbit.GetUpbitMarketTickerRes
import org.jeonfeel.moeuibit2.data.network.websocket.model.upbit.UpbitSocketTickerRes
import org.jeonfeel.moeuibit2.data.usecase.UpbitCoinDetailUseCase
import org.jeonfeel.moeuibit2.ui.base.BaseViewModel
import org.jeonfeel.moeuibit2.ui.coindetail.chart.utils.upbit.Chart
import org.jeonfeel.moeuibit2.ui.coindetail.order.UpbitCoinOrder
import org.jeonfeel.moeuibit2.utils.Utils.coinOrderIsKrwMarket
import org.jeonfeel.moeuibit2.utils.isKrwTradeCurrency
import org.jeonfeel.moeuibit2.utils.manager.CacheManager
import org.jeonfeel.moeuibit2.data.local.preferences.PreferencesManager
import org.jeonfeel.moeuibit2.data.local.room.entity.TransactionInfo
import org.jeonfeel.moeuibit2.data.network.retrofit.response.upbit.GetChartCandleRes
import org.jeonfeel.moeuibit2.utils.getKoreanPostPosition
import java.math.BigDecimal
import javax.inject.Inject

@HiltViewModel
class NewCoinDetailViewModel @Inject constructor(
    private val preferenceManager: PreferencesManager,
    private val upbitCoinDetailUseCase: UpbitCoinDetailUseCase,
    private val cacheManager: CacheManager,
    private val upbitCoinOrder: UpbitCoinOrder,
    val chart: Chart,
) : BaseViewModel(preferenceManager) {

    val rootExchange = GlobalState.globalExchangeState.value

    private val _coinTicker = mutableStateOf<CommonExchangeModel?>(null)
    val coinTicker: State<CommonExchangeModel?> get() = _coinTicker

    private val _btcPrice = mutableStateOf(BigDecimal(0.0))
    val btcPrice: State<BigDecimal> get() = _btcPrice

    private val _koreanCoinName = mutableStateOf("")
    val koreanCoinName: State<String> get() = _koreanCoinName

    private val _engCoinName = mutableStateOf("")
    val engCoinName: State<String> get() = _engCoinName

    private val _orderBookIndication = mutableStateOf("quantity")
    val orderBookIndication: State<String> get() = _orderBookIndication

    private val _transactionInfoList = mutableStateListOf<TransactionInfo>()
    val transactionInfo: List<TransactionInfo> get() = _transactionInfoList

    private var initIsFavorite = false
    private val _isFavorite = mutableStateOf(false)
    val isFavorite: State<Boolean> get() = _isFavorite

    private val _lineChartData = mutableStateOf<List<Float>>(emptyList())
    val lineChartData: State<List<Float>> get() = _lineChartData

    private val _tradeResponse = MutableStateFlow<UpbitSocketTickerRes?>(null)

    private var realTimeJob: Job? = null
    private var collectTickerJob: Job? = null

    private var orderBookRealTimeJob: Job? = null
    private var orderBookCollectJob: Job? = null

    private var chartRealTimeJob: Job? = null

    private var _market = ""

    var isStarted = mutableStateOf(false)

    var isChartStarted = mutableStateOf(false)

    var isCoinOrderStarted = mutableStateOf(false)

    @Volatile
    var isInitSuccess = false
        private set

    private val _isShowDeListingSnackBar = mutableStateOf(false)
    val isShowDeListingSnackBar: State<Boolean> = _isShowDeListingSnackBar
    var deListingMessage: String = ""
        private set

    fun init(market: String) {
        getOrderBookIndication()

        viewModelScope.launch {
            when (rootExchange) {
                EXCHANGE_UPBIT -> {
                    _market = market
                    getKoreanCoinName()
                    getEngCoinName()
                    getIsFavorite()
                    requestCoinTicker(market)
                    isInitSuccess = true
                }

                EXCHANGE_BITTHUMB -> {

                }
            }
        }.also { it.start() }
    }

    fun onStart(market: String) {
        realTimeJob?.cancel()
        collectTickerJob?.cancel()
        isStarted.value = true
        viewModelScope.launch {
            requestLineChartCandleSticks(market)
        }

        realTimeJob = viewModelScope.launch {
            upbitCoinDetailUseCase.onStart(market.coinOrderIsKrwMarket())
        }.also { it.start() }

        collectTickerJob = viewModelScope.launch {
            collectTicker(market.coinOrderIsKrwMarket())
        }.also { it.start() }
    }

    fun onStop() {
        isStarted.value = false
        viewModelScope.launch {
            saveFavoriteStatus()
            upbitCoinDetailUseCase.onStop()
            realTimeJob?.cancel()
            collectTickerJob?.cancel()
            realTimeJob = null
            collectTickerJob = null
        }
    }

    /**
     * coin Order 화면 초기화
     */
    fun initCoinOrder(market: String) {
        viewModelScope.launch(ioDispatcher) {
            when (rootExchange) {
                EXCHANGE_UPBIT -> {
                    upbitCoinOrder.initCoinOrder(market)
                }

                EXCHANGE_BITTHUMB -> {

                }
            }
        }.also { it.start() }
    }

    fun coinOrderScreenOnStart(market: String) {
        orderBookRealTimeJob?.cancel()
        isCoinOrderStarted.value = true

        orderBookRealTimeJob = viewModelScope.launch {
            when (rootExchange) {
                EXCHANGE_UPBIT -> {
                    upbitCoinOrder.onStart(market)
                }

                EXCHANGE_BITTHUMB -> {

                }
            }
        }.also { it.start() }

        orderBookCollectJob = viewModelScope.launch {
            when (rootExchange) {
                EXCHANGE_UPBIT -> {
                    upbitCoinOrder.collectOrderBook()
                }

                EXCHANGE_BITTHUMB -> {

                }
            }
        }.also { it.start() }
    }

    /**
     * 코인 주문 화면 pause
     */
    fun coinOrderScreenOnStop() {
        isCoinOrderStarted.value = false
        viewModelScope.launch {
            upbitCoinOrder.onStop()
            orderBookRealTimeJob?.cancel()
            orderBookRealTimeJob = null
        }
    }

    private suspend fun saveFavoriteStatus() {
        if (initIsFavorite != isFavorite.value) {
            if (isFavorite.value) {
                upbitCoinDetailUseCase.addFavorite(market = _market)
            } else {
                upbitCoinDetailUseCase.deleteFavorite(market = _market)
            }
        }
    }

    private suspend fun getKoreanCoinName() {
        _koreanCoinName.value =
            cacheManager.readKoreanCoinNameMap()[_market.substring(4)] ?: ""
    }

    private suspend fun getEngCoinName() {
        _engCoinName.value =
            cacheManager.readEnglishCoinNameMap()[_market.substring(4)]?.replace(" ", "-") ?: ""
    }

    private suspend fun getIsFavorite() {
        initIsFavorite = upbitCoinDetailUseCase.getIsFavorite(_market) != null
        _isFavorite.value = initIsFavorite
    }

    private suspend fun requestCoinTicker(market: String) {
        val getUpbitTickerReq = GetUpbitMarketTickerReq(
            market.coinOrderIsKrwMarket()
        )
        executeUseCase<List<GetUpbitMarketTickerRes>>(
            target = upbitCoinDetailUseCase.getMarketTicker(getUpbitTickerReq, isList = true),
            onComplete = { ticker ->
                ticker.forEach {
                    if (!market.isKrwTradeCurrency() && it.market == BTC_MARKET) {
                        _btcPrice.value = it.mapTo().tradePrice
                    }
                    if (it.market == market) {
                        _coinTicker.value = it.mapTo()
                    }
                }
            }
        )
    }

    private suspend fun requestLineChartCandleSticks(market: String) {
        executeUseCase<List<GetChartCandleRes>>(
            target = upbitCoinDetailUseCase.getLineChartCandleSticks(market),
            onComplete = {
                _lineChartData.value = it.map { res -> res.tradePrice.toFloat() }.reversed()
            }
        )
    }

    /**
     * orderBookList 받아옴
     */
    fun getOrderBookList(): List<OrderBookModel> {
        return when (rootExchange) {
            EXCHANGE_UPBIT -> {
                upbitCoinOrder.orderBookList
            }

            EXCHANGE_BITTHUMB -> {
                upbitCoinOrder.orderBookList
            }

            else -> {
                upbitCoinOrder.orderBookList
            }
        }
    }

    fun getMaxOrderBookSize(): State<Double> {
        return when (rootExchange) {
            EXCHANGE_UPBIT -> {
                upbitCoinOrder.maxOrderBookSize
            }

            EXCHANGE_BITTHUMB -> {
                upbitCoinOrder.maxOrderBookSize
            }

            else -> {
                upbitCoinOrder.maxOrderBookSize
            }
        }
    }

    private fun getOrderBookIndication() {
        viewModelScope.launch {
            preferenceManager.getString("orderBookIndication").collect {
                _orderBookIndication.value = it
            }
        }
    }

    fun changeOrderBookIndication() {
        if (_orderBookIndication.value == "quantity") {
            _orderBookIndication.value = "totalPrice"
        } else {
            _orderBookIndication.value = "quantity"
        }
    }

    fun saveOrderBookIndication() {
        viewModelScope.launch {
            preferenceManager.setValue(
                "orderBookIndication",
                _orderBookIndication.value
            )
        }
    }

    /**
     * 사용자 시드머니 받아옴
     */
    fun getUserSeedMoney(): State<Double> {
        return when (rootExchange) {
            EXCHANGE_UPBIT -> {
                upbitCoinOrder.userSeedMoney
            }

            EXCHANGE_BITTHUMB -> {
                upbitCoinOrder.userSeedMoney
            }

            else -> {
                upbitCoinOrder.userSeedMoney
            }
        }
    }

    fun requestBid(
        market: String,
        quantity: Double,
        price: BigDecimal,
        totalPrice: Double,
    ) {
        rootExchangeCoroutineBranch(
            upbitAction = {
                upbitCoinOrder.requestBid(
                    market = market,
                    totalPrice = totalPrice,
                    quantity = quantity,
                    coinPrice = price,
                    koreanName = koreanCoinName.value,
                    btcPrice = btcPrice.value.toDouble()
                )
            },
            bitthumbAction = {

            },
            dispatcher = Dispatchers.IO
        )
    }

    fun requestAsk(
        market: String,
        quantity: Double,
        totalPrice: Double = 0.0,
        price: BigDecimal,
        totalPriceBTC: Double = 0.0
    ) {
        rootExchangeCoroutineBranch(
            upbitAction = {
                upbitCoinOrder.requestAsk(
                    market = market,
                    totalPrice = totalPrice,
                    quantity = quantity,
                    coinPrice = price,
                    totalPriceBTC = totalPriceBTC,
                    btcPrice = btcPrice.value.toDouble()
                )
            },
            bitthumbAction = {

            },
            dispatcher = Dispatchers.IO
        )
    }

    /**
     * 사용자 코인 받아옴
     */
    fun getUserCoin(): State<MyCoin> {
        return when (rootExchange) {
            EXCHANGE_UPBIT -> {
                upbitCoinOrder.userCoin
            }

            EXCHANGE_BITTHUMB -> {
                upbitCoinOrder.userCoin
            }

            else -> {
                upbitCoinOrder.userCoin
            }
        }
    }

    /**
     * BTC마켓 일 때 필요한데, 사용자 BTC 코인 받아옴
     */
    fun getUserBtcCoin(): State<MyCoin> {
        return when (rootExchange) {
            EXCHANGE_UPBIT -> {
                upbitCoinOrder.userBtcCoin
            }

            EXCHANGE_BITTHUMB -> {
                upbitCoinOrder.userBtcCoin
            }

            else -> {
                upbitCoinOrder.userBtcCoin
            }
        }
    }

    // 차트 화면
    fun requestOldData(
        positiveBarDataSet: IBarDataSet,
        negativeBarDataSet: IBarDataSet,
        candleXMin: Float,
        market: String
    ) {
        viewModelScope.launch {
            if (rootExchange == EXCHANGE_UPBIT) {
                chart.newRequestOldData(
                    positiveBarDataSet = positiveBarDataSet,
                    negativeBarDataSet = negativeBarDataSet,
                    candleXMin = candleXMin,
                    market = market
                )
            }
        }
    }

    fun requestChartData(market: String) {
        isChartStarted.value = true
        chartRealTimeJob?.cancel()
        chartRealTimeJob = viewModelScope.launch {
            chart.refresh(market = market)
        }.also { it.start() }
    }

    fun stopRequestChartData() {
        isChartStarted.value = false
        chartRealTimeJob?.cancel()
    }

    fun updateIsFavorite() {
        _isFavorite.value = !isFavorite.value
    }

    fun setLastPeriod(period: String) {
        viewModelScope.launch {
            chart.saveLastPeriod(period)
        }
    }

    fun getTransactionInfoList(market: String) {
        viewModelScope.launch {
            _transactionInfoList.clear()
            _transactionInfoList.addAll(upbitCoinOrder.getTransactionInfoList(market = market))
        }
    }

    private fun createTradeEndMessage(deListingDate: UpbitSocketTickerRes.DeListingDate): String {
        return "${koreanCoinName.value}${koreanCoinName.value.getKoreanPostPosition()} ${deListingDate.year}년 ${deListingDate.month}월 ${deListingDate.day}일 거래지원 종료 예정입니다."
    }


    private suspend fun collectTicker(market: String) {
        upbitCoinDetailUseCase.observeTickerResponse()?.onEach { result ->
            _tradeResponse.update {
                result
            }
        }?.collect { upbitSocketTickerRes ->
            runCatching {
//                Logger.e(upbitSocketTickerRes.toString())
//                Logger.e("coinDetail message!!")
                if (upbitSocketTickerRes?.delistingDate != null && !isShowDeListingSnackBar.value) {
                    deListingMessage = createTradeEndMessage(upbitSocketTickerRes.delistingDate)
                    _isShowDeListingSnackBar.value = true
                }
                val commonExchangeModel = upbitSocketTickerRes?.mapTo()
                if (!market.isKrwTradeCurrency() && upbitSocketTickerRes?.code == BTC_MARKET) {
                    _btcPrice.value = commonExchangeModel?.tradePrice ?: BigDecimal.ZERO
                }

                if (market != upbitSocketTickerRes?.code) return@runCatching

                _coinTicker.value = commonExchangeModel
            }.fold(
                onSuccess = {
                    chart.updateCandleTicker(_coinTicker.value?.tradePrice?.toDouble() ?: 0.0)
                },
                onFailure = {
                }
            )
        }
    }
}