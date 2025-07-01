package org.jeonfeel.moeuibit2.ui.coindetail.detail.root_exchange

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import com.tradingview.lightweightcharts.Logger
import org.jeonfeel.moeuibit2.constants.BTC_MARKET
import org.jeonfeel.moeuibit2.data.network.retrofit.model.upbit.CommonExchangeModel
import org.jeonfeel.moeuibit2.data.usecase.BiThumbCoinDetailUseCase
import org.jeonfeel.moeuibit2.ui.common.ResultState
import org.jeonfeel.moeuibit2.utils.Utils.coinOrderIsKrwMarket
import org.jeonfeel.moeuibit2.utils.Utils.coinOrderIsKrwMarketForBiThumb
import org.jeonfeel.moeuibit2.utils.getKoreanPostPosition
import org.jeonfeel.moeuibit2.utils.isKrwTradeCurrency
import org.jeonfeel.moeuibit2.utils.manager.CacheManager
import java.math.BigDecimal
import javax.inject.Inject

class BiThumbCoinDetail @Inject constructor(
    private val biThumbCoinDetailUseCase: BiThumbCoinDetailUseCase,
    private val cacheManager: CacheManager,
) {
    private val _koreanCoinName = mutableStateOf("")
    val koreanCoinName: State<String> get() = _koreanCoinName

    private val _engCoinName = mutableStateOf("")
    val engCoinName: State<String> get() = _engCoinName

    private val _isFavorite = mutableStateOf(false)
    val isFavorite: State<Boolean> get() = _isFavorite

    private val _coinTicker = mutableStateOf<CommonExchangeModel?>(null)
    val coinTicker: State<CommonExchangeModel?> get() = _coinTicker

    private val _btcPrice = mutableStateOf(BigDecimal(0.0))
    val btcPrice: State<BigDecimal> get() = _btcPrice

    private val _lineChartData = mutableStateOf<List<Float>>(emptyList())
    val lineChartData: State<List<Float>> get() = _lineChartData

    private var initIsFavorite = false

    private val _isShowDeListingSnackBar = mutableStateOf(false)
    val isShowDeListingSnackBar: State<Boolean> = _isShowDeListingSnackBar

    var deListingMessage: String = ""
        private set

    suspend fun biThumbCoinDetailInit(market: String) {
        getKoreanCoinName(market)
        getEngCoinName(market)
        getIsFavorite(market)
        requestCoinTicker(market)
    }

    suspend fun onStart(market: String) {
        fetchLineChartCandleSticks(market)
        biThumbCoinDetailUseCase.onStart(market.coinOrderIsKrwMarketForBiThumb())
    }

    suspend fun onStop() {
        biThumbCoinDetailUseCase.onStop()
    }

    private suspend fun requestCoinTicker(market: String) {
        biThumbCoinDetailUseCase.fetchMarketTicker(market.coinOrderIsKrwMarket()).collect { res ->
            when (res) {
                is ResultState.Success -> {
                    res.data.forEach {
                        if (!market.isKrwTradeCurrency() && it.market == BTC_MARKET) {
                            _btcPrice.value = it.mapToCommonExchangeModel().tradePrice
                        }

                        if (it.market == market) {
                            _coinTicker.value = it.mapToCommonExchangeModel()
                        }
                    }
                }

                is ResultState.Error -> {

                }

                is ResultState.Loading -> {

                }
            }
        }
    }

    suspend fun saveFavoriteStatus(market: String) {
        if (initIsFavorite != isFavorite.value) {
            if (isFavorite.value) {
                biThumbCoinDetailUseCase.addFavorite(market = market)
            } else {
                biThumbCoinDetailUseCase.deleteFavorite(market = market)
            }
        }
    }

    private suspend fun getIsFavorite(market: String) {
        initIsFavorite = biThumbCoinDetailUseCase.getIsFavorite(market) != null
        _isFavorite.value = initIsFavorite
    }

    private suspend fun getKoreanCoinName(market: String) {
        _koreanCoinName.value =
            cacheManager.readBiThumbKoreanCoinNameMap()[market.substring(4)] ?: ""
    }

    private suspend fun getEngCoinName(market: String) {
        _engCoinName.value =
            cacheManager.readBiThumbEnglishCoinNameMap()[market.substring(4)]?.replace(" ", "-") ?: ""
    }

    private fun createTradeEndMessage(deListingDate: String): String {
        return "${koreanCoinName.value}${koreanCoinName.value.getKoreanPostPosition()} $deListingDate 거래지원 종료 예정입니다."
    }

    private suspend fun fetchLineChartCandleSticks(market: String) {
        biThumbCoinDetailUseCase.fetchLineChartCandleSticks(market).collect { res ->
            when (res) {
                is ResultState.Success -> {
                    _lineChartData.value = res.data
                }

                is ResultState.Error -> {

                }

                is ResultState.Loading -> {

                }
            }
        }
    }

    fun updateIsFavorite() {
        _isFavorite.value = !isFavorite.value
    }

    suspend fun collectTicker(
        market: String,
        successCallback: (tradePrice: Double) -> Unit
    ) {
        biThumbCoinDetailUseCase.observeTickerResponse().collect { biThumbSocketTickerRes ->
            runCatching {
                Logger.e(biThumbSocketTickerRes.toString())
                if (biThumbSocketTickerRes?.delistingDate != null && !isShowDeListingSnackBar.value) {
                    deListingMessage = createTradeEndMessage(biThumbSocketTickerRes.delistingDate)
                    _isShowDeListingSnackBar.value = true
                }

                val commonExchangeModel = biThumbSocketTickerRes?.mapToCommonExchangeModel()
                if (!market.isKrwTradeCurrency() && biThumbSocketTickerRes?.code == BTC_MARKET) {
                    _btcPrice.value = commonExchangeModel?.tradePrice ?: BigDecimal.ZERO
                }

                if (market != biThumbSocketTickerRes?.code) return@runCatching

                _coinTicker.value = commonExchangeModel
            }.fold(
                onSuccess = {
                    val tradePrice = _coinTicker.value?.tradePrice?.toDouble() ?: 0.0
                    successCallback(tradePrice)
                },
                onFailure = {
                }
            )
        }
    }
}