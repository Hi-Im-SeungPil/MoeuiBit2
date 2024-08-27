package org.jeonfeel.moeuibit2.ui.main.exchange.root_exchange

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import com.orhanobut.logger.Logger
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import org.jeonfeel.moeuibit2.constants.BTC_MARKET
import org.jeonfeel.moeuibit2.data.network.retrofit.model.upbit.CommonExchangeModel
import org.jeonfeel.moeuibit2.data.network.retrofit.request.upbit.GetUpbitMarketTickerReq
import org.jeonfeel.moeuibit2.data.network.retrofit.response.upbit.UpbitMarketCodeRes
import org.jeonfeel.moeuibit2.data.network.websocket.model.upbit.UpbitSocketTickerRes
import org.jeonfeel.moeuibit2.data.usecase.UpbitUseCase
import org.jeonfeel.moeuibit2.ui.main.exchange.ExchangeViewModel.Companion.TRADE_CURRENCY_BTC
import org.jeonfeel.moeuibit2.ui.main.exchange.ExchangeViewModel.Companion.TRADE_CURRENCY_FAV
import org.jeonfeel.moeuibit2.ui.main.exchange.ExchangeViewModel.Companion.TRADE_CURRENCY_KRW
import org.jeonfeel.moeuibit2.ui.main.exchange.TickerAskBidState
import org.jeonfeel.moeuibit2.ui.main.exchange.component.SortOrder
import org.jeonfeel.moeuibit2.ui.main.exchange.component.SortType
import org.jeonfeel.moeuibit2.utils.Utils
import org.jeonfeel.moeuibit2.utils.manager.CacheManager
import org.jeonfeel.moeuibit2.utils.mapToMarketCodesRequest
import java.math.BigDecimal
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
    private val upbitUseCase: UpbitUseCase,
    private val cacheManager: CacheManager
) : BaseCommunicationModule() {
    private var successInit = false
    private val krwMarketCodeMap = mutableMapOf<String, UpbitMarketCodeRes>()
    private val btcMarketCodeMap = mutableMapOf<String, UpbitMarketCodeRes>()

    private val krwList = arrayListOf<String>()
    private val krwExchangeModelPosition = mutableMapOf<String, Int>()
    private val _krwExchangeModelList = mutableStateListOf<CommonExchangeModel>()
    private val krwNeedAnimationList: ArrayList<MutableState<String>> = arrayListOf()

    private val btcList = arrayListOf<String>()
    private val btcExchangeModelPosition = mutableMapOf<String, Int>()
    private val _btcExchangeModelList = mutableStateListOf<CommonExchangeModel>()
    private val btcNeedAnimationList: ArrayList<MutableState<String>> = arrayListOf()

    private var tradeCurrencyState: State<Int>? = null
    private var isUpdateExchange: State<Boolean>? = null

    private val _tickerResponse = MutableStateFlow<UpbitSocketTickerRes?>(null)

    /**
     * 업비트 초기화
     */
    fun initUpBit(
        tradeCurrencyState: State<Int>,
        isUpdateExchange: State<Boolean>,
    ): Flow<ExchangeInitState> {
        this.tradeCurrencyState = tradeCurrencyState
        this.isUpdateExchange = isUpdateExchange
        return flow {
            emit(ExchangeInitState.Loading)
            runCatching {
                init()
            }.fold(
                onSuccess = {
                    emit(ExchangeInitState.Success)
                    successInit = true
                },
                onFailure = { emit(ExchangeInitState.Error(it.message)) }
            ).also { requestSubscribeTicker() }
                .also {
                    collectTicker()
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
                krwList.addAll(codeListPair.first.map { it.market })
                btcList.addAll(codeListPair.second.map { it.market })
                krwMarketCodeMap.putAll(codeListPair.first.associateBy { it.market })
                btcMarketCodeMap.putAll(codeListPair.second.associateBy { it.market })
                krwNeedAnimationList.addAll(krwList.map { mutableStateOf(TickerAskBidState.NONE.name) })
                btcNeedAnimationList.addAll(btcList.map { mutableStateOf(TickerAskBidState.NONE.name) })
                cacheManager.saveKoreanCoinNameMap(krwMarketCodeMap + btcMarketCodeMap)
                cacheManager.saveEnglishCoinNameMap(krwMarketCodeMap + btcMarketCodeMap)
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
                sortTickerList(
                    tradeCurrency = TRADE_CURRENCY_KRW,
                    sortType = SortType.VOLUME,
                    sortOrder = SortOrder.DESCENDING
                )
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

    fun getExchangeModelList(tradeCurrencyState: State<Int>): List<CommonExchangeModel> {
        return when (tradeCurrencyState.value) {
            TRADE_CURRENCY_KRW -> {
                _krwExchangeModelList.toList()
            }

            TRADE_CURRENCY_BTC -> {
                _btcExchangeModelList.toList()
            }

            TRADE_CURRENCY_FAV -> {
                emptyList()
            }

            else -> {
                emptyList()
            }
        }
    }

    /**
     * tickerList 정렬
     */
    fun sortTickerList(
        tradeCurrency: Int,
        sortType: SortType,
        sortOrder: SortOrder
    ) {
        val tickerList = when (tradeCurrency) {
            TRADE_CURRENCY_KRW -> {
                _krwExchangeModelList.toList()
            }

            TRADE_CURRENCY_BTC -> {
                _btcExchangeModelList.toList()
            }

            TRADE_CURRENCY_FAV -> {
                _krwExchangeModelList.toList()
            }

            else -> {
                emptyList()
            }
        }

        val sortedList = Utils.sortTickerList(
            tickerList = tickerList.toList(), sortType = sortType, sortOrder = sortOrder
        )

        sortedList.forEachIndexed { index, ticker ->
            when (tradeCurrency) {
                TRADE_CURRENCY_KRW -> {
                    _krwExchangeModelList[index] = ticker
                    krwExchangeModelPosition[ticker.market] = index
                }

                TRADE_CURRENCY_BTC -> {
                    _btcExchangeModelList[index] = ticker
                    btcExchangeModelPosition[ticker.market] = index
                }

                TRADE_CURRENCY_FAV -> {

                }
            }
        }
    }

    /**
     * 웹소켓 티커 구독 요청
     */
    private suspend fun requestSubscribeTicker() {
        when (tradeCurrencyState?.value) {
            TRADE_CURRENCY_KRW -> upbitUseCase.requestSubscribeTicker(marketCodes = krwList.toList())
            TRADE_CURRENCY_BTC -> {
                val tempBtcList = ArrayList(btcList)
                tempBtcList.add(BTC_MARKET)
                upbitUseCase.requestSubscribeTicker(marketCodes = tempBtcList.toList())
            }

            TRADE_CURRENCY_FAV -> {}
        }
    }

    suspend fun onPause() {
        upbitUseCase.requestSubscribeTicker(marketCodes = listOf(""))
    }

    suspend fun onResume() {
        if (!tickerDataIsEmpty() && successInit) {
            requestSubscribeTicker()
        } else if (tickerDataIsEmpty() && successInit) {
            clearTickerData()
            init()
            requestSubscribeTicker()
            collectTicker()
        }
    }

    private fun tickerDataIsEmpty(): Boolean {
        return krwMarketCodeMap.isEmpty()
                || btcMarketCodeMap.isEmpty()
                || krwList.isEmpty()
                || krwExchangeModelPosition.isEmpty()
                || _krwExchangeModelList.isEmpty()
                || btcList.isEmpty()
                || btcExchangeModelPosition.isEmpty()
                || _btcExchangeModelList.isEmpty()
    }

    private fun clearTickerData() {
        krwMarketCodeMap.clear()
        btcMarketCodeMap.clear()
        krwList.clear()
        krwExchangeModelPosition.clear()
        _krwExchangeModelList.clear()
        btcList.clear()
        btcExchangeModelPosition.clear()
        _btcExchangeModelList.clear()
    }

    suspend fun changeTradeCurrencyAction() {
        requestSubscribeTicker()
    }

    fun getBtcPrice(): BigDecimal {
        val btcPosition = krwExchangeModelPosition[BTC_MARKET]
        return btcPosition?.let {
            _krwExchangeModelList[it].tradePrice
        } ?: BigDecimal.ZERO
    }

    fun getNeedAnimationList(): List<State<String>> {
        return when (tradeCurrencyState?.value) {
            TRADE_CURRENCY_KRW -> {
                krwNeedAnimationList
            }

            TRADE_CURRENCY_BTC -> {
                btcNeedAnimationList
            }

            else -> {
                listOf()
            }
        }
    }

    fun stopAnimation(market: String) {
        when (tradeCurrencyState?.value) {
            TRADE_CURRENCY_KRW -> {
                val position = krwExchangeModelPosition[market] ?: 0
                krwNeedAnimationList[position].value = TickerAskBidState.NONE.name
            }

            TRADE_CURRENCY_BTC -> {
                val position = btcExchangeModelPosition[market] ?: 0
                btcNeedAnimationList[position].value = TickerAskBidState.NONE.name
            }
        }
    }

    /**
     * 웹소켓 티커 수신
     */
    private suspend fun collectTicker() {
        upbitUseCase.observeTickerResponse().onEach { result ->
            _tickerResponse.update {
                result
            }
        }.collect { upbitSocketTickerRes ->
            try {
                if (isUpdateExchange?.value == false) return@collect

                var positionMap: MutableMap<String, Int>? = null
                var upbitMarketCodeMap: Map<String, UpbitMarketCodeRes>? = null
                var targetModelList: MutableList<CommonExchangeModel>? = null
                when (tradeCurrencyState?.value) {
                    TRADE_CURRENCY_KRW -> {
                        positionMap = krwExchangeModelPosition
                        upbitMarketCodeMap = krwMarketCodeMap
                        targetModelList = _krwExchangeModelList
                        val position = positionMap[upbitSocketTickerRes.code] ?: 0
                        krwNeedAnimationList[position].value = upbitSocketTickerRes.askBid
                    }

                    TRADE_CURRENCY_BTC -> {
                        if (upbitSocketTickerRes.code == BTC_MARKET) {
                            positionMap = krwExchangeModelPosition
                            upbitMarketCodeMap = krwMarketCodeMap
                            targetModelList = _krwExchangeModelList
                        } else {
                            positionMap = btcExchangeModelPosition
                            upbitMarketCodeMap = btcMarketCodeMap
                            targetModelList = _btcExchangeModelList
                            val position = positionMap[upbitSocketTickerRes.code] ?: 0
                            btcNeedAnimationList[position].value = upbitSocketTickerRes.askBid
                        }
                    }

                    TRADE_CURRENCY_FAV -> {}
                    null -> {}
                }
                val position = positionMap?.get(upbitSocketTickerRes.code) ?: 0
                val upbitMarketCodeRes = upbitMarketCodeMap?.get(upbitSocketTickerRes.code)
                val commonExchangeModel = upbitMarketCodeRes?.let { upbitSocketTickerRes.mapTo(it) }
                commonExchangeModel?.let { targetModelList?.set(position, it) }
            } catch (e: Exception) {
                Logger.e(e.message.toString())
            }
        }
    }
}