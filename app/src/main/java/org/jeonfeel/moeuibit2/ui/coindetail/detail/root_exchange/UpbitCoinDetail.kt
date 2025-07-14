package org.jeonfeel.moeuibit2.ui.coindetail.detail.root_exchange

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import org.jeonfeel.moeuibit2.constants.BTC_MARKET
import org.jeonfeel.moeuibit2.data.network.retrofit.model.upbit.CommonExchangeModel
import org.jeonfeel.moeuibit2.data.network.retrofit.request.upbit.GetUpbitMarketTickerReq
import org.jeonfeel.moeuibit2.data.network.retrofit.response.upbit.GetChartCandleRes
import org.jeonfeel.moeuibit2.data.network.websocket.model.upbit.UpbitSocketTickerRes
import org.jeonfeel.moeuibit2.data.repository.local.LocalRepository
import org.jeonfeel.moeuibit2.data.usecase.UpbitCoinDetailUseCase
import org.jeonfeel.moeuibit2.ui.common.ResultState
import org.jeonfeel.moeuibit2.utils.Utils.coinOrderIsKrwMarket
import org.jeonfeel.moeuibit2.utils.getKoreanPostPosition
import org.jeonfeel.moeuibit2.utils.isKrwTradeCurrency
import org.jeonfeel.moeuibit2.utils.manager.CacheManager
import java.math.BigDecimal
import javax.inject.Inject

class UpbitCoinDetail @Inject constructor(
    private val upbitCoinDetailUseCase: UpbitCoinDetailUseCase,
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

    suspend fun upbitCoinDetailInit(market: String) {
        getKoreanCoinName(market)
        getEngCoinName(market)
        getIsFavorite(market)
        requestCoinTicker(market)
    }

    suspend fun onStart(market: String) {
        fetchLineChartCandleSticks(market)
        upbitCoinDetailUseCase.onStart(market.coinOrderIsKrwMarket())
    }

    suspend fun onStop() {
        upbitCoinDetailUseCase.onStop()
    }

    private suspend fun requestCoinTicker(market: String) {
        val getUpbitTickerReq = GetUpbitMarketTickerReq(
            market.coinOrderIsKrwMarket()
        )

        upbitCoinDetailUseCase.fetchMarketTicker(getUpbitTickerReq).collect { res ->
            when (res) {
                is ResultState.Success -> {
                    res.data.forEach {
                        if (!market.isKrwTradeCurrency() && it.market == BTC_MARKET) {
                            _btcPrice.value = it.mapTo().tradePrice
                        }

                        if (it.market == market) {
                            _coinTicker.value = it.mapTo()
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

    private suspend fun fetchLineChartCandleSticks(market: String) {
        upbitCoinDetailUseCase.fetchLineChart(market).collect { res ->
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

    suspend fun saveFavoriteStatus(market: String) {
        if (initIsFavorite != isFavorite.value) {
            if (isFavorite.value) {
                upbitCoinDetailUseCase.addFavorite(market = market)
            } else {
                upbitCoinDetailUseCase.deleteFavorite(market = market)
            }
        }
    }

    private suspend fun getIsFavorite(market: String) {
        initIsFavorite = upbitCoinDetailUseCase.getIsFavorite(market) != null
        _isFavorite.value = initIsFavorite
    }

    private suspend fun getKoreanCoinName(market: String) {
        _koreanCoinName.value =
            cacheManager.readKoreanCoinNameMap()[market.substring(4)] ?: ""
    }

    private suspend fun getEngCoinName(market: String) {
        _engCoinName.value =
            cacheManager.readEnglishCoinNameMap()[market.substring(4)]?.replace(" ", "-") ?: ""
    }

    private fun createTradeEndMessage(deListingDate: UpbitSocketTickerRes.DeListingDate): String {
        return "${koreanCoinName.value}${koreanCoinName.value.getKoreanPostPosition()} ${deListingDate.year}년 ${deListingDate.month}월 ${deListingDate.day}일 거래지원 종료 예정입니다."
    }

    fun updateIsFavorite() {
        _isFavorite.value = !isFavorite.value
    }

    suspend fun collectTicker(
        market: String,
        successCallback: (tradePrice: Double) -> Unit
    ) {
        upbitCoinDetailUseCase.observeTickerResponse().collect { upbitSocketTickerRes ->
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
                    val tradePrice = _coinTicker.value?.tradePrice?.toDouble() ?: 0.0
                    successCallback(tradePrice)
//                    chart.updateCandleTicker(_coinTicker.value?.tradePrice?.toDouble() ?: 0.0)
                },
                onFailure = {
                }
            )
        }
    }
}