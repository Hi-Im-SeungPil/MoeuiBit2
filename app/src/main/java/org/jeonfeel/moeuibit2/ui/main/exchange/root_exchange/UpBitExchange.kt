package org.jeonfeel.moeuibit2.ui.main.exchange.root_exchange

import android.os.Looper
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
import org.jeonfeel.moeuibit2.constants.UPBIT_KRW_SYMBOL_PREFIX
import org.jeonfeel.moeuibit2.data.network.retrofit.model.upbit.CommonExchangeModel
import org.jeonfeel.moeuibit2.data.network.retrofit.request.upbit.GetUpbitMarketTickerReq
import org.jeonfeel.moeuibit2.data.network.retrofit.response.upbit.UpbitMarketCodeRes
import org.jeonfeel.moeuibit2.data.network.websocket.model.upbit.UpbitSocketTickerRes
import org.jeonfeel.moeuibit2.data.usecase.UpBitExchangeUseCase
import org.jeonfeel.moeuibit2.ui.base.BaseCommunicationModule
import org.jeonfeel.moeuibit2.ui.main.exchange.ExchangeViewModel.Companion.TRADE_CURRENCY_BTC
import org.jeonfeel.moeuibit2.ui.main.exchange.ExchangeViewModel.Companion.TRADE_CURRENCY_FAV
import org.jeonfeel.moeuibit2.ui.main.exchange.ExchangeViewModel.Companion.TRADE_CURRENCY_KRW
import org.jeonfeel.moeuibit2.ui.main.exchange.TickerAskBidState
import org.jeonfeel.moeuibit2.ui.main.exchange.component.SortOrder
import org.jeonfeel.moeuibit2.ui.main.exchange.component.SortType
import org.jeonfeel.moeuibit2.utils.NetworkConnectivityObserver.Companion.needRefresh
import org.jeonfeel.moeuibit2.utils.Utils
import org.jeonfeel.moeuibit2.utils.manager.CacheManager
import org.jeonfeel.moeuibit2.utils.ext.mapToMarketCodesRequest
import java.math.BigDecimal
import java.util.logging.Handler
import javax.inject.Inject
import kotlin.reflect.KFunction1

sealed class ExchangeInitState {
    data object Wait : ExchangeInitState()

    data object Loading : ExchangeInitState()

    data object Success : ExchangeInitState()

    data class Error(
        val message: String? = null
    ) : ExchangeInitState()
}

class UpBitExchange @Inject constructor(
    private val upBitExchangeUseCase: UpBitExchangeUseCase,
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
            ).also { useCaseOnStart() }
        }
    }

    private suspend fun init() {
        requestMarketCode()
        requestTicker()
    }

    suspend fun onStart(
        updateLoadingState: KFunction1<Boolean, Unit>
    ) {
        Logger.e("${tickerDataIsEmpty()} $successInit $needRefresh")

        if (!tickerDataIsEmpty() && successInit) {
            if (tradeCurrencyState?.value == TRADE_CURRENCY_FAV) {
                favoriteOnResume()
            }
            useCaseOnStart()
        } else if (tickerDataIsEmpty() && successInit) {
            updateLoadingState(true)
            clearTickerData()
            init()
            android.os.Handler(Looper.getMainLooper()).postDelayed({
                updateLoadingState(false)
            }, 500)
            useCaseOnStart()
        }
    }

    suspend fun onStop() {
        upBitExchangeUseCase.onStop()
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

    suspend fun exchangeCollectTicker() {
        collectTicker()
    }

    private suspend fun favoriteOnResume() {
        getFavoriteList()

        val favoriteSet = favoriteList.toSet()
        val removeKeyList = _favoriteExchangeModelList
            .map { it.market }
            .filterNot { it in favoriteSet }

        if (removeKeyList.isNotEmpty()) {
            _favoriteExchangeModelList.removeAll { it.market in removeKeyList }
            favoriteModelPosition.clear()

            _favoriteExchangeModelList.forEachIndexed { index, model ->
                favoriteModelPosition[model.market] = index
            }
        }

        val newFavorites =
            favoriteList.filterNot { it in _favoriteExchangeModelList.map { model -> model.market } }

        if (newFavorites.isNotEmpty()) {
            val newModels = newFavorites.map { market ->
                CommonExchangeModel(market = market)
            }

            newModels.forEach { commonExchangeModel ->
                val market = commonExchangeModel.market
                val model = if (market.startsWith(UPBIT_KRW_SYMBOL_PREFIX)) {
                    val position = krwExchangeModelPosition[market]
                    position?.let {
                        _krwExchangeModelList[position]
                    } ?: CommonExchangeModel(market = market)
                } else {
                    val position = btcExchangeModelPosition[market]
                    position?.let {
                        _btcExchangeModelList[position]
                    } ?: CommonExchangeModel(market = market)
                }
                _favoriteExchangeModelList.add(model)
            }

            _favoriteExchangeModelList.forEachIndexed { index, model ->
                favoriteModelPosition[model.market] = index
            }
        }
    }

    /**
     * 업비트 마켓 코드 요청
     */
    private suspend fun requestMarketCode() {
        executeUseCase<List<UpbitMarketCodeRes>>(
            target = upBitExchangeUseCase.getMarketCode(),
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
            target = upBitExchangeUseCase.getMarketTicker(
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

    private suspend fun updateTickerData(sortOrder: SortOrder? = null, sortType: SortType? = null) {
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
            target = upBitExchangeUseCase.getMarketTicker(
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
                        if (sortOrder != null && sortType != null) {
                            sortTickerList(
                                tradeCurrency = TRADE_CURRENCY_FAV,
                                sortOrder = sortOrder,
                                sortType = sortType
                            )
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
                _krwExchangeModelList
            }

            TRADE_CURRENCY_BTC -> {
                _btcExchangeModelList
            }

            TRADE_CURRENCY_FAV -> {
                _favoriteExchangeModelList
            }

            else -> {
                emptyList()
            }
        }

        val sortedList = if (tradeCurrency == TRADE_CURRENCY_FAV) {
            Utils.sortTickerList(
                tickerList = tickerList,
                sortType = sortType,
                sortOrder = sortOrder,
                btcPrice = getBtcPrice()
            )
        } else {
            Utils.sortTickerList(
                tickerList = tickerList, sortType = sortType, sortOrder = sortOrder
            )
        }

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
     * onStart
     */
    private suspend fun useCaseOnStart() {
        when (tradeCurrencyState?.value) {
            TRADE_CURRENCY_KRW -> {
                upBitExchangeUseCase.onStart(
                    marketCodes = krwList.toList(),
                )
            }

            TRADE_CURRENCY_BTC -> {
                val tempBtcList = ArrayList(btcList)
                tempBtcList.add(BTC_MARKET)
                upBitExchangeUseCase.onStart(marketCodes = tempBtcList.toList())
            }

            TRADE_CURRENCY_FAV -> {
                val tempFavoriteList = ArrayList(favoriteList)
                tempFavoriteList.add(BTC_MARKET)
                upBitExchangeUseCase.onStart(marketCodes = tempFavoriteList.toList())
            }
        }
    }

    suspend fun addFavorite(market: String) {
        upBitExchangeUseCase.addFavorite(market)
    }

    private suspend fun removeFavorite(market: String) {
        upBitExchangeUseCase.removeFavorite(market)
    }

    private suspend fun getFavoriteList() {
        favoriteList.clear()

        val list = upBitExchangeUseCase.getFavoriteList()?.let {
            it.map { favorite -> favorite?.market ?: "" }
        } ?: emptyList()

        favoriteList.addAll(list)
    }

    suspend fun changeTradeCurrencyAction(
        sortOrder: SortOrder? = null,
        sortType: SortType? = null
    ) {
        favoriteMarketChangeAction(sortOrder, sortType)

        if (tradeCurrencyState?.value == TRADE_CURRENCY_FAV) return

        updateTickerData()
        useCaseOnStart()
    }

    private suspend fun favoriteMarketChangeAction(sortOrder: SortOrder?, sortType: SortType?) {
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

            updateTickerData(sortOrder, sortType)
            useCaseOnStart()
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
        upBitExchangeUseCase.observeTickerResponse()?.onEach { result ->
            _tickerResponse.update {
                result
            }
        }?.collectLatest { upbitSocketTickerRes ->
//            Logger.e(upbitSocketTickerRes.toString())
            try {
                if (isUpdateExchange?.value == false) return@collectLatest

                if (upbitSocketTickerRes == null || upbitSocketTickerRes.tradePrice == 0.0) return@collectLatest

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