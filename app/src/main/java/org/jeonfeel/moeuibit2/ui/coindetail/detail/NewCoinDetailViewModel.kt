package org.jeonfeel.moeuibit2.ui.coindetail.detail

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.viewModelScope
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import org.jeonfeel.moeuibit2.GlobalState
import org.jeonfeel.moeuibit2.constants.EXCHANGE_BITTHUMB
import org.jeonfeel.moeuibit2.constants.EXCHANGE_UPBIT
import org.jeonfeel.moeuibit2.data.network.retrofit.model.upbit.CommonExchangeModel
import org.jeonfeel.moeuibit2.ui.base.BaseViewModel
import org.jeonfeel.moeuibit2.ui.coindetail.chart.utils.upbit.UpbitChart
import org.jeonfeel.moeuibit2.utils.Utils.coinOrderIsKrwMarket
import org.jeonfeel.moeuibit2.data.local.preferences.PreferencesManager
import org.jeonfeel.moeuibit2.ui.coindetail.chart.ChartViewModel
import org.jeonfeel.moeuibit2.ui.coindetail.detail.root_exchange.BiThumbCoinDetail
import org.jeonfeel.moeuibit2.ui.coindetail.detail.root_exchange.UpbitCoinDetail
import java.math.BigDecimal
import javax.inject.Inject

@HiltViewModel
class NewCoinDetailViewModel @Inject constructor(
    private val preferenceManager: PreferencesManager,
    private val upbitCoinDetail: UpbitCoinDetail,
    private val biThumbCoinDetail: BiThumbCoinDetail,
    val chart: ChartViewModel,
) : BaseViewModel(preferenceManager) {

    val koreanCoinName: State<String>
        get() = run {
            when (GlobalState.globalExchangeState.value) {
                EXCHANGE_UPBIT -> {
                    upbitCoinDetail.koreanCoinName
                }

                EXCHANGE_BITTHUMB -> {
                    biThumbCoinDetail.koreanCoinName
                }

                else -> {
                    upbitCoinDetail.koreanCoinName
                }
            }
        }

    val engCoinName: State<String>
        get() = run {
            when (GlobalState.globalExchangeState.value) {
                EXCHANGE_UPBIT -> {
                    upbitCoinDetail.engCoinName
                }

                EXCHANGE_BITTHUMB -> {
                    biThumbCoinDetail.engCoinName
                }

                else -> {
                    upbitCoinDetail.engCoinName
                }
            }
        }

    val coinTicker: State<CommonExchangeModel?>
        get() = run {
            when (GlobalState.globalExchangeState.value) {
                EXCHANGE_UPBIT -> {
                    upbitCoinDetail.coinTicker
                }

                EXCHANGE_BITTHUMB -> {
                    biThumbCoinDetail.coinTicker
                }

                else -> {
                    upbitCoinDetail.coinTicker
                }
            }
        }

    val btcPrice: State<BigDecimal>
        get() = run {
            when (GlobalState.globalExchangeState.value) {
                EXCHANGE_UPBIT -> {
                    upbitCoinDetail.btcPrice
                }

                EXCHANGE_BITTHUMB -> {
                    biThumbCoinDetail.btcPrice
                }

                else -> {
                    upbitCoinDetail.btcPrice
                }
            }
        }

    val lineChartData: State<List<Float>>
        get() = run {
            when (GlobalState.globalExchangeState.value) {
                EXCHANGE_UPBIT -> {
                    upbitCoinDetail.lineChartData
                }

                EXCHANGE_BITTHUMB -> {
                    biThumbCoinDetail.lineChartData
                }

                else -> {
                    upbitCoinDetail.lineChartData
                }
            }
        }

    val isFavorite: State<Boolean>
        get() = run {
            when (GlobalState.globalExchangeState.value) {
                EXCHANGE_UPBIT -> {
                    upbitCoinDetail.isFavorite
                }

                EXCHANGE_BITTHUMB -> {
                    biThumbCoinDetail.isFavorite
                }

                else -> {
                    upbitCoinDetail.isFavorite
                }
            }
        }

    val isShowDeListingSnackBar: State<Boolean>
        get() = run {
            when (GlobalState.globalExchangeState.value) {
                EXCHANGE_UPBIT -> {
                    upbitCoinDetail.isShowDeListingSnackBar
                }

                EXCHANGE_BITTHUMB -> {
                    biThumbCoinDetail.isShowDeListingSnackBar
                }

                else -> {
                    upbitCoinDetail.isShowDeListingSnackBar
                }
            }
        }

    val deListingMessage: String
        get() = run {
            when (GlobalState.globalExchangeState.value) {
                EXCHANGE_UPBIT -> {
                    upbitCoinDetail.deListingMessage
                }

                EXCHANGE_BITTHUMB -> {
                    biThumbCoinDetail.deListingMessage
                }

                else -> {
                    upbitCoinDetail.deListingMessage
                }
            }
        }

    private var realTimeJob: Job? = null

    private var collectTickerJob: Job? = null

    private var chartRealTimeJob: Job? = null

    private var _market = ""

    var isStarted = mutableStateOf(false)

    var isChartStarted = mutableStateOf(false)

    @Volatile
    var isInitSuccess = false
        private set

    fun init(market: String) {
        _market = market

        viewModelScope.launch {
            when (GlobalState.globalExchangeState.value) {
                EXCHANGE_UPBIT -> {
                    upbitCoinDetail.upbitCoinDetailInit(market)
                }

                EXCHANGE_BITTHUMB -> {
                    biThumbCoinDetail.biThumbCoinDetailInit(market)
                }
            }

            isInitSuccess = true
        }.also { it.start() }
    }

    fun onStart(market: String) {
        realTimeJob?.cancel()
        collectTickerJob?.cancel()
        isStarted.value = true

        realTimeJob = viewModelScope.launch {
            when (GlobalState.globalExchangeState.value) {
                EXCHANGE_UPBIT -> {
                    upbitCoinDetail.onStart(market)
                }

                EXCHANGE_BITTHUMB -> {
                    biThumbCoinDetail.onStart(market)
                }

                else -> {
                    upbitCoinDetail.onStart(market)
                }
            }
        }.also { it.start() }

        collectTickerJob = viewModelScope.launch {
            collectTicker(market.coinOrderIsKrwMarket())
        }.also { it.start() }
    }

    fun onStop() {
        isStarted.value = false
        viewModelScope.launch {
            saveFavoriteStatus()
            upbitCoinDetail.onStop()

            realTimeJob?.cancel()
            collectTickerJob?.cancel()

            realTimeJob = null
            collectTickerJob = null
        }
    }

    private suspend fun saveFavoriteStatus() {
        when (GlobalState.globalExchangeState.value) {
            EXCHANGE_UPBIT -> {
                upbitCoinDetail.saveFavoriteStatus(_market)
            }

            EXCHANGE_BITTHUMB -> {
                biThumbCoinDetail.saveFavoriteStatus(_market)
            }

            else -> {
                upbitCoinDetail.saveFavoriteStatus(_market)
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
        chart.newRequestOldData(
            positiveBarDataSet = positiveBarDataSet,
            negativeBarDataSet = negativeBarDataSet,
            candleXMin = candleXMin,
            market = market
        )
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

    fun setLastPeriod(period: String) {
        viewModelScope.launch {
            chart.saveLastPeriod(period)
        }
    }

    fun updateIsFavorite() {
        when (GlobalState.globalExchangeState.value) {
            EXCHANGE_UPBIT -> {
                upbitCoinDetail.updateIsFavorite()
            }

            EXCHANGE_BITTHUMB -> {
                biThumbCoinDetail.updateIsFavorite()
            }
        }
    }

    private suspend fun collectTicker(market: String) {
        when (GlobalState.globalExchangeState.value) {
            EXCHANGE_UPBIT -> {
                upbitCoinDetail.collectTicker(market = market) { price ->
                    chart.updateCandleTicker(price)
                }
            }

            EXCHANGE_BITTHUMB -> {
                biThumbCoinDetail.collectTicker(market = market) { price ->
                    chart.updateCandleTicker(price)
                }
            }
        }
    }
}