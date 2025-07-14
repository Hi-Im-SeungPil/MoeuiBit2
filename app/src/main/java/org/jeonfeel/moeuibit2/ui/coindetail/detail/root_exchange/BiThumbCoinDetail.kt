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
        val date = parseDateInfo(deListingDate)
        if (date != null) {
            val (year, month, day) = date
            return "${koreanCoinName.value}${koreanCoinName.value.getKoreanPostPosition()} ${year}년 ${month}월 ${day}일 거래지원 종료 예정입니다."
        } else {
            return "${koreanCoinName.value}는 거래지원 종료가 예정되어 있으나, 종료일 정보가 확인되지 않았습니다."
        }
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

    private fun parseDateInfo(input: String): Triple<Int, Int, Int>? {
        val regex = """year\s*:\s*(\d+),\s*month\s*:\s*(\d+),\s*day\s*:\s*(\d+)""".toRegex()
        val match = regex.find(input)
        return if (match != null) {
            val (year, month, day) = match.destructured
            Triple(year.toInt(), month.toInt(), day.toInt())
        } else {
            null // 포맷이 잘못된 경우
        }
    }

    suspend fun collectTicker(
        market: String,
        successCallback: (tradePrice: Double) -> Unit
    ) {
        biThumbCoinDetailUseCase.observeTickerResponse().collect { biThumbSocketTickerRes ->
            runCatching {
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