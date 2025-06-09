package org.jeonfeel.moeuibit2.ui.coindetail.detail

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.viewModelScope
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.jeonfeel.moeuibit2.GlobalState
import org.jeonfeel.moeuibit2.constants.BTC_MARKET
import org.jeonfeel.moeuibit2.constants.EXCHANGE_BITTHUMB
import org.jeonfeel.moeuibit2.constants.EXCHANGE_UPBIT
import org.jeonfeel.moeuibit2.data.network.retrofit.model.upbit.CommonExchangeModel
import org.jeonfeel.moeuibit2.data.network.retrofit.request.upbit.GetUpbitMarketTickerReq
import org.jeonfeel.moeuibit2.data.network.retrofit.response.upbit.GetUpbitMarketTickerRes
import org.jeonfeel.moeuibit2.data.network.websocket.model.upbit.UpbitSocketTickerRes
import org.jeonfeel.moeuibit2.data.usecase.UpbitCoinDetailUseCase
import org.jeonfeel.moeuibit2.ui.base.BaseViewModel
import org.jeonfeel.moeuibit2.ui.coindetail.chart.utils.upbit.Chart
import org.jeonfeel.moeuibit2.utils.Utils.coinOrderIsKrwMarket
import org.jeonfeel.moeuibit2.utils.isKrwTradeCurrency
import org.jeonfeel.moeuibit2.utils.manager.CacheManager
import org.jeonfeel.moeuibit2.data.local.preferences.PreferencesManager
import org.jeonfeel.moeuibit2.data.network.retrofit.response.upbit.GetChartCandleRes
import org.jeonfeel.moeuibit2.utils.getKoreanPostPosition
import java.math.BigDecimal
import javax.inject.Inject

@HiltViewModel
class NewCoinDetailViewModel @Inject constructor(
    private val preferenceManager: PreferencesManager,
    private val upbitCoinDetailUseCase: UpbitCoinDetailUseCase,
    private val cacheManager: CacheManager,
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

    private var initIsFavorite = false

    private val _isFavorite = mutableStateOf(false)
    val isFavorite: State<Boolean> get() = _isFavorite

    private val _lineChartData = mutableStateOf<List<Float>>(emptyList())
    val lineChartData: State<List<Float>> get() = _lineChartData

    private val _tradeResponse = MutableStateFlow<UpbitSocketTickerRes?>(null)

    private var realTimeJob: Job? = null
    private var collectTickerJob: Job? = null

    private var chartRealTimeJob: Job? = null

    private var _market = ""

    var isStarted = mutableStateOf(false)

    var isChartStarted = mutableStateOf(false)

    @Volatile
    var isInitSuccess = false
        private set

    private val _isShowDeListingSnackBar = mutableStateOf(false)
    val isShowDeListingSnackBar: State<Boolean> = _isShowDeListingSnackBar
    var deListingMessage: String = ""
        private set

    fun init(market: String) {
        _market = market

        viewModelScope.launch {
            when (rootExchange) {
                EXCHANGE_UPBIT -> {
                    getKoreanCoinName()
                    getEngCoinName()
                    getIsFavorite()
                    requestCoinTicker(market)
                    isInitSuccess = true
                }

                EXCHANGE_BITTHUMB -> {
                    getBiThumbKoreanCoinName()
                    getBiThumbEngCoinName()
                    getIsFavorite()
                    requestCoinTicker(market)
                    isInitSuccess = true
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

    private suspend fun getBiThumbKoreanCoinName() {
        _koreanCoinName.value =
            cacheManager.readBiThumbKoreanCoinNameMap()[_market.substring(4)] ?: ""
    }

    private suspend fun getBiThumbEngCoinName() {
        _engCoinName.value =
            cacheManager.readBiThumbEnglishCoinNameMap()[_market.substring(4)]?.replace(" ", "-") ?: ""
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

    private fun createTradeEndMessage(deListingDate: UpbitSocketTickerRes.DeListingDate): String {
        return "${koreanCoinName.value}${koreanCoinName.value.getKoreanPostPosition()} ${deListingDate.year}년 ${deListingDate.month}월 ${deListingDate.day}일 거래지원 종료 예정입니다."
    }


    private suspend fun collectTicker(market: String) {
        upbitCoinDetailUseCase.observeTickerResponse().onEach { result ->
            _tradeResponse.update {
                result
            }
        }.collect { upbitSocketTickerRes ->
            runCatching {
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