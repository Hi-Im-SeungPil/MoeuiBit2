package org.jeonfeel.moeuibit2.ui.main.exchange.root_exchange

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateListOf
import com.orhanobut.logger.Logger
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import org.jeonfeel.moeuibit2.constants.BTC_MARKET
import org.jeonfeel.moeuibit2.constants.SELECTED_FAVORITE
import org.jeonfeel.moeuibit2.constants.UPBIT_KRW_SYMBOL_PREFIX
import org.jeonfeel.moeuibit2.data.network.retrofit.model.upbit.CommonExchangeModel
import org.jeonfeel.moeuibit2.data.network.retrofit.request.upbit.GetUpbitMarketTickerReq
import org.jeonfeel.moeuibit2.data.network.retrofit.response.upbit.UpbitMarketCodeRes
import org.jeonfeel.moeuibit2.data.network.websocket.model.upbit.UpbitSocketTickerRes
import org.jeonfeel.moeuibit2.data.usecase.UpbitExchangeUseCase
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

class UpBitExchange @Inject constructor(
    private val upbitUseCase: UpbitExchangeUseCase,
    private val cacheManager: CacheManager
) : BaseCommunicationModule() {
    private var successInit = false
    private val krwMarketCodeMap = mutableMapOf<String, UpbitMarketCodeRes>()
    private val btcMarketCodeMap = mutableMapOf<String, UpbitMarketCodeRes>()

    private val krwList = arrayListOf<String>()
    private val krwExchangeModelPosition = mutableMapOf<String, Int>()
    private val _krwExchangeModelList = mutableStateListOf<CommonExchangeModel>()

    private val btcList = arrayListOf<String>()
    private val btcExchangeModelPosition = mutableMapOf<String, Int>()
    private val _btcExchangeModelList = mutableStateListOf<CommonExchangeModel>()

    private val favoriteList = arrayListOf<String>()
    private val favoriteModelPosition = mutableMapOf<String, Int>()
    private val _favoriteExchangeModelList = mutableStateListOf<CommonExchangeModel>()

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

    suspend fun onResume() {
        if (!tickerDataIsEmpty() && successInit) {
            updateTickerData()
            requestSubscribeTicker()
        } else if (tickerDataIsEmpty() && successInit) {
            clearTickerData()
            init()
            requestSubscribeTicker()
            collectTicker()
        }
    }

    suspend fun onPause() {
        upbitUseCase.requestSubscribeTicker(marketCodes = listOf(""))
    }

    suspend fun favoriteOnResume() {
        if (favoriteList.size < _favoriteExchangeModelList.size) {
            val count = _favoriteExchangeModelList.size - favoriteList.size
            var removeCount = 0

            val tempFavoriteList = favoriteList.associateWith { true }
            val removeKeyList = arrayListOf<String>()

            favoriteModelPosition.keys.forEach { key ->
                if (tempFavoriteList[key] == null) {
                    removeKeyList.add(key)
                    removeCount++

                    if (count == removeCount) {
                        return@forEach
                    }
                }
            }

            if (removeKeyList.isNotEmpty()) {
                removeKeyList.forEach { key ->
                    favoriteModelPosition.remove(key)
                    _favoriteExchangeModelList.removeIf { it.market == key }
                }
            }
        }
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

    private suspend fun updateTickerData() {
        val marketCodes =
            when (tradeCurrencyState?.value) {
                TRADE_CURRENCY_KRW -> {
                    krwList.mapToMarketCodesRequest()
                }

                TRADE_CURRENCY_BTC -> {
                    btcList.mapToMarketCodesRequest()
                }

                else -> {
                    favoriteList.mapToMarketCodesRequest()
                }
            }
        val req = GetUpbitMarketTickerReq(
            marketCodes = marketCodes
        )
        executeUseCase<List<CommonExchangeModel>>(
            target = upbitUseCase.getMarketTicker(
                getUpbitMarketTickerReq = req,
                krwUpbitMarketCodeMap = krwMarketCodeMap.toMap(),
                btcUpbitMarketCodeMap = btcMarketCodeMap.toMap(),
            ),
            onComplete = { result ->
                when (tradeCurrencyState?.value) {
                    TRADE_CURRENCY_KRW -> {
                        result.forEach { res ->
                            val position = krwExchangeModelPosition[res.market]
                            position?.let {
                                if (_krwExchangeModelList[position].tradePrice > res.tradePrice) {
                                    res.needAnimation.value = TickerAskBidState.BID.name
                                } else if (_krwExchangeModelList[position].tradePrice < res.tradePrice) {
                                    res.needAnimation.value = TickerAskBidState.ASK.name
                                } else {
                                    res.needAnimation.value = TickerAskBidState.NONE.name
                                }
                                _krwExchangeModelList[position] = res
                            }
                        }
                    }

                    TRADE_CURRENCY_BTC -> {
                        result.forEach { res ->
                            val position = btcExchangeModelPosition[res.market]
                            position?.let {
                                if (_btcExchangeModelList[position].tradePrice > res.tradePrice) {
                                    res.needAnimation.value = TickerAskBidState.BID.name
                                } else if (_btcExchangeModelList[position].tradePrice < res.tradePrice) {
                                    res.needAnimation.value = TickerAskBidState.ASK.name
                                } else {
                                    res.needAnimation.value = TickerAskBidState.NONE.name
                                }
                                _btcExchangeModelList[position] = res
                            }
                        }
                    }

                    else -> { // favorite
                        result.forEach { res ->
                            val position = favoriteModelPosition[res.market]
                            position?.let {
                                if (_favoriteExchangeModelList[position].tradePrice > res.tradePrice) {
                                    res.needAnimation.value = TickerAskBidState.BID.name
                                } else if (_favoriteExchangeModelList[position].tradePrice < res.tradePrice) {
                                    res.needAnimation.value = TickerAskBidState.ASK.name
                                } else {
                                    res.needAnimation.value = TickerAskBidState.NONE.name
                                }
                                _favoriteExchangeModelList[position] = res
                            }
                        }
                    }
                }
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
                _favoriteExchangeModelList.toList()
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
                _favoriteExchangeModelList.toList()
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
                    _favoriteExchangeModelList[index] = ticker
                    favoriteModelPosition[ticker.market] = index
                }
            }
        }

        when (tradeCurrencyState?.value) {
            TRADE_CURRENCY_KRW -> {
                _krwExchangeModelList.forEach {
                    it.needAnimation.value = TickerAskBidState.NONE.name
                }
            }

            TRADE_CURRENCY_BTC -> {
                _btcExchangeModelList.forEach {
                    it.needAnimation.value = TickerAskBidState.NONE.name
                }
            }

            else -> {
                _favoriteExchangeModelList.forEach {
                    it.needAnimation.value = TickerAskBidState.NONE.name
                }
            }
        }
    }

    /**
     * 웹소켓 티커 구독 요청
     */
    private suspend fun requestSubscribeTicker() {
        when (tradeCurrencyState?.value) {
            TRADE_CURRENCY_KRW -> {
                upbitUseCase.requestSubscribeTicker(
                    marketCodes = krwList.toList(),
                )
            }

            TRADE_CURRENCY_BTC -> {
                val tempBtcList = ArrayList(btcList)
                tempBtcList.add(BTC_MARKET)
                upbitUseCase.requestSubscribeTicker(marketCodes = tempBtcList.toList())
            }

            TRADE_CURRENCY_FAV -> {
                val tempFavoriteList = ArrayList(favoriteList)
                tempFavoriteList.add(BTC_MARKET)
                upbitUseCase.requestSubscribeTicker(marketCodes = tempFavoriteList.toList())
            }
        }
    }

    suspend fun addFavorite(market: String) {
        upbitUseCase.addFavorite(market)
    }

    suspend fun removeFavorite(market: String) {
        upbitUseCase.removeFavorite(market)
    }

    private suspend fun getFavoriteList() {
        favoriteList.clear()

        val list = upbitUseCase.getFavoriteList()?.let {
            it.map { favorite -> favorite?.market ?: "" }
        } ?: emptyList()

        favoriteList.addAll(list)
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
        favoriteList.clear()
        favoriteModelPosition.clear()
        _favoriteExchangeModelList.clear()
    }

    suspend fun changeTradeCurrencyAction() {
        favoriteMarketChangeAction()

        if (tradeCurrencyState?.value == SELECTED_FAVORITE) return

        updateTickerData()
        requestSubscribeTicker()
    }

    private suspend fun favoriteMarketChangeAction() {
        getFavoriteList()
        _favoriteExchangeModelList.clear()
        favoriteModelPosition.clear()

        if (tradeCurrencyState?.value == TRADE_CURRENCY_FAV
            && favoriteList.isNotEmpty()
        ) {
            favoriteList.forEachIndexed { index, market ->

                val (positionMap, exchangeModelList) =
                    if (market.startsWith(UPBIT_KRW_SYMBOL_PREFIX)) {
                        krwExchangeModelPosition to _krwExchangeModelList
                    } else {
                        btcExchangeModelPosition to _btcExchangeModelList
                    }

                val position = positionMap[market]

                if (position != null) {
                    val favoritePosition = favoriteModelPosition[market]
                    val commonExchangeModel = exchangeModelList[position]

                    if (favoritePosition != null) {
                        _favoriteExchangeModelList[favoritePosition] = commonExchangeModel
                    } else {
                        favoriteModelPosition[market] = index
                        _favoriteExchangeModelList.add(commonExchangeModel)
                    }
                } else {
                    removeFavorite(market)
                    favoriteList.removeIf { it == market }
                }
            }

            updateTickerData()
            requestSubscribeTicker()
        }
    }

    fun getBtcPrice(): BigDecimal {
        val btcPosition = krwExchangeModelPosition[BTC_MARKET]
        return btcPosition?.let {
            _krwExchangeModelList[it].tradePrice
        } ?: BigDecimal.ZERO
    }

    /**
     * 웹소켓 티커 수신
     */
    private suspend fun collectTicker() {
        upbitUseCase.observeTickerResponse().onEach { result ->
            _tickerResponse.update {
                result
            }
        }.collectLatest { upbitSocketTickerRes ->
            try {
                if (isUpdateExchange?.value == false) return@collectLatest

                var positionMap: MutableMap<String, Int>? = null
                var upbitMarketCodeMap: Map<String, UpbitMarketCodeRes>? = null
                var targetModelList: MutableList<CommonExchangeModel>? = null

                when (tradeCurrencyState?.value) {
                    TRADE_CURRENCY_KRW -> {
                        positionMap = krwExchangeModelPosition
                        upbitMarketCodeMap = krwMarketCodeMap
                        targetModelList = _krwExchangeModelList
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
                        }
                    }

                    TRADE_CURRENCY_FAV -> {
                        if (upbitSocketTickerRes.code == BTC_MARKET) {
                            positionMap = krwExchangeModelPosition
                            upbitMarketCodeMap = krwMarketCodeMap
                            targetModelList = _krwExchangeModelList

                            if (favoriteModelPosition[BTC_MARKET] != null) {
                                val position = favoriteModelPosition[BTC_MARKET]
                                val upbitMarketCodeRes =
                                    upbitMarketCodeMap[upbitSocketTickerRes.code]

                                upbitMarketCodeRes?.let {
                                    upbitSocketTickerRes.mapTo(it).apply {
                                        needAnimation.value = upbitSocketTickerRes.askBid
                                    }.also { commonExchangeModel ->
                                        _favoriteExchangeModelList[position!!] = commonExchangeModel
                                    }
                                }
                            }
                        } else {
                            positionMap = favoriteModelPosition
                            targetModelList = _favoriteExchangeModelList
                            upbitMarketCodeMap =
                                if (upbitSocketTickerRes.code.startsWith(UPBIT_KRW_SYMBOL_PREFIX)) {
                                    krwMarketCodeMap
                                } else {
                                    btcMarketCodeMap
                                }
                        }
                    }

                    null -> {}
                }

                val position = positionMap?.get(upbitSocketTickerRes.code)

                position?.let {
                    val upbitMarketCodeRes = upbitMarketCodeMap?.get(upbitSocketTickerRes.code)
                    upbitMarketCodeRes?.let {
                        upbitSocketTickerRes.mapTo(it).apply {
                            needAnimation.value = upbitSocketTickerRes.askBid
                        }.also { commonExchangeModel ->
                            targetModelList?.set(
                                position,
                                commonExchangeModel
                            )
                        }
                    }
                }
            } catch (e: Exception) {
                Logger.e(e.message.toString())
            }
        }
    }
}