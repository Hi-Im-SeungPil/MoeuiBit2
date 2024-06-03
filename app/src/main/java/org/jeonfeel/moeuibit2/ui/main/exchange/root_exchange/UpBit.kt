package org.jeonfeel.moeuibit2.ui.main.exchange.root_exchange

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import com.orhanobut.logger.Logger
import kotlinx.coroutines.channels.ticker
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import org.jeonfeel.moeuibit2.data.network.retrofit.model.upbit.CommonExchangeModel
import org.jeonfeel.moeuibit2.data.network.retrofit.request.upbit.GetUpbitMarketTickerReq
import org.jeonfeel.moeuibit2.data.network.retrofit.response.upbit.UpbitMarketCodeRes
import org.jeonfeel.moeuibit2.data.network.websocket.model.upbit.UpbitSocketTickerRes
import org.jeonfeel.moeuibit2.data.usecase.UpbitUseCase
import org.jeonfeel.moeuibit2.ui.main.exchange.TradeCurrency
import org.jeonfeel.moeuibit2.ui.main.exchange.component.SortOrder
import org.jeonfeel.moeuibit2.ui.main.exchange.component.SortType
import org.jeonfeel.moeuibit2.utils.Utils
import org.jeonfeel.moeuibit2.utils.mapToMarketCodesRequest
import javax.inject.Inject

sealed class ExchangeInitState {
    data object Wait : ExchangeInitState()

    data object Loading : ExchangeInitState()

    data object Success : ExchangeInitState()

    data class Error(
        val message: String? = null
    ) : ExchangeInitState()
}

class UpBit @Inject constructor(
    private val upbitUseCase: UpbitUseCase
) : BaseRootExchange() {
    private val krwMarketCodeMap = mutableMapOf<String, UpbitMarketCodeRes>()
    private val btcMarketCodeMap = mutableMapOf<String, UpbitMarketCodeRes>()

    private val krwList = arrayListOf<String>()
    private val krwExchangeModelPosition = mutableMapOf<String, Int>()
    private val _krwExchangeModelList = mutableStateListOf<CommonExchangeModel>()

    private val btcList = arrayListOf<String>()
    private val btcExchangeModelPosition = mutableMapOf<String, Int>()
    private val _btcExchangeModelList = mutableStateListOf<CommonExchangeModel>()

    private val _tickerResponse = MutableStateFlow<UpbitSocketTickerRes?>(null)

    /**
     * 업비트 초기화
     */
    fun initUpBit(
        tradeCurrencyState: State<TradeCurrency>,
        isUpdateExchange: State<Boolean>
    ): Flow<ExchangeInitState> {
        return flow {
            emit(ExchangeInitState.Loading)
            runCatching {
                init()
            }.fold(
                onSuccess = {
                    emit(ExchangeInitState.Success)
                },
                onFailure = { emit(ExchangeInitState.Error(it.message)) }
            ).also { requestSubscribeTicker(tradeCurrencyState) }
                .also {
                    collectTicker(
                        tradeCurrencyState = tradeCurrencyState,
                        isUpdateExchange = isUpdateExchange
                    )
                }
        }
    }

    private suspend fun init() {
        requestMarketCode()
        requestTicker()
    }

    /**
     * 업비트 마켓 코드 요청
     */
    private suspend fun requestMarketCode() {
        executeUseCase<List<UpbitMarketCodeRes>>(
            target = upbitUseCase.getMarketCode(),
            onComplete = { result ->
                val codeListPair = Utils.divideKrwResBtcRes(result)
                krwList.addAll(codeListPair.first.map { it.market }.toList())
                btcList.addAll(codeListPair.second.map { it.market }.toList())
                krwMarketCodeMap.putAll(codeListPair.first.associateBy { it.market })
                btcMarketCodeMap.putAll(codeListPair.second.associateBy { it.market })
            }
        )
    }

    /**
     * 업비트 티커 요청
     */
    private suspend fun requestTicker() {
        val marketCodes = (krwList + btcList).mapToMarketCodesRequest()
        val req = GetUpbitMarketTickerReq(
            marketCodes = marketCodes
        )
        executeUseCase<Pair<List<CommonExchangeModel>, List<CommonExchangeModel>>>(
            target = upbitUseCase.getMarketTicker(
                getUpbitMarketTickerReq = req,
                krwUpbitMarketCodeMap = krwMarketCodeMap.toMap(),
                btcUpbitMarketCodeMap = btcMarketCodeMap.toMap(),
                addExchangeModelPosition = ::addExchangeModelPosition
            ),
            onComplete = { result ->
                _krwExchangeModelList.addAll(result.first)
                _btcExchangeModelList.addAll(result.second)
            }
        )
    }

    /**
     * 포지션 추가
     */
    private fun addExchangeModelPosition(
        market: String,
        position: Int,
        isKrw: Boolean
    ) {
        if (isKrw) {
            krwExchangeModelPosition[market] = position
        } else {
            btcExchangeModelPosition[market] = position
        }
    }

    fun getExchangeModelList(tradeCurrencyState: State<TradeCurrency>): List<CommonExchangeModel> {
        return when (tradeCurrencyState.value) {
            TradeCurrency.KRW -> {
                _krwExchangeModelList.toList()
            }

            TradeCurrency.BTC -> {
                _btcExchangeModelList.toList()
            }

            TradeCurrency.FAV -> {
                emptyList()
            }
        }
    }

    /**
     * tickerList 정렬
     */
    fun sortTickerList(
        tradeCurrencyState: State<TradeCurrency>,
        sortType: SortType,
        sortOrder: SortOrder
    ) {
        val tickerList = when (tradeCurrencyState.value) {
            TradeCurrency.KRW -> {
                _krwExchangeModelList.toList()
            }

            TradeCurrency.BTC -> {
                _btcExchangeModelList.toList()
            }

            TradeCurrency.FAV -> {
                _krwExchangeModelList.toList()
            }
        }

        val sortedList = Utils.sortTickerList(
            tickerList = tickerList.toList(), sortType = sortType, sortOrder = sortOrder
        )

        Logger.e("sortedList -> $${sortedList.map { it.signedChangeRate }.toList()}")

        sortedList.forEachIndexed { index, ticker ->
            when (tradeCurrencyState.value) {
                TradeCurrency.KRW -> {
                    _krwExchangeModelList[index] = ticker
                    krwExchangeModelPosition[ticker.market] = index
                }

                TradeCurrency.BTC -> {
                    _btcExchangeModelList[index] = ticker
                    btcExchangeModelPosition[ticker.market] = index
                }

                TradeCurrency.FAV -> {

                }
            }
        }
        Logger.e(krwExchangeModelPosition.toString())
    }

    /**
     * 웹소켓 티커 구독 요청
     */
    private suspend fun requestSubscribeTicker(tradeCurrencyState: State<TradeCurrency>) {
        Logger.e("krwList -> $krwList")
        upbitUseCase.requestSubscribeTicker(marketCodes = krwList)
    }

    /**
     * 웹소켓 티커 수신
     */
    private suspend fun collectTicker(
        tradeCurrencyState: State<TradeCurrency>,
        isUpdateExchange: State<Boolean>
    ) {
        upbitUseCase.observeTickerResponse().onEach { result ->
            _tickerResponse.update {
                result
            }
        }.collect { upbitSocketTickerRes ->
            if (!isUpdateExchange.value) return@collect

            var positionMap: MutableMap<String, Int>? = null
            var upbitMarketCodeMap: Map<String, UpbitMarketCodeRes>? = null
            var targetModelList: MutableList<CommonExchangeModel>? = null
            when (tradeCurrencyState.value) {
                TradeCurrency.KRW -> {
                    positionMap = krwExchangeModelPosition
                    upbitMarketCodeMap = krwMarketCodeMap
                    targetModelList = _krwExchangeModelList
                }

                TradeCurrency.BTC -> {
                    positionMap = btcExchangeModelPosition
                    upbitMarketCodeMap = btcMarketCodeMap
                    targetModelList = _btcExchangeModelList
                }

                TradeCurrency.FAV -> {}
                null -> {}
            }
            val position = positionMap?.get(upbitSocketTickerRes.code) ?: 0
            val upbitMarketCodeRes = upbitMarketCodeMap?.get(upbitSocketTickerRes.code)
            val commonExchangeModel = upbitMarketCodeRes?.let { upbitSocketTickerRes.mapTo(it) }
            commonExchangeModel?.let { targetModelList?.set(position, it) }
        }
    }
}