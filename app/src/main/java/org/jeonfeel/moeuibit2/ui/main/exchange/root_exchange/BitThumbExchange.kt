package org.jeonfeel.moeuibit2.ui.main.exchange.root_exchange

import android.os.Handler
import android.os.Looper
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateListOf
import com.orhanobut.logger.Logger
import org.jeonfeel.moeuibit2.constants.BTC_MARKET
import org.jeonfeel.moeuibit2.constants.KRW_SYMBOL_PREFIX
import org.jeonfeel.moeuibit2.data.network.retrofit.model.upbit.CommonExchangeModel
import org.jeonfeel.moeuibit2.data.network.retrofit.response.bitthumb.BitThumbMarketCodeRes
import org.jeonfeel.moeuibit2.data.network.websocket.model.bitthumb.BithumbSocketTickerRes
import org.jeonfeel.moeuibit2.data.usecase.BitThumbUseCase
import org.jeonfeel.moeuibit2.ui.common.ResultState
import org.jeonfeel.moeuibit2.ui.main.exchange.ExchangeViewModel.Companion.TRADE_CURRENCY_BTC
import org.jeonfeel.moeuibit2.ui.main.exchange.ExchangeViewModel.Companion.TRADE_CURRENCY_FAV
import org.jeonfeel.moeuibit2.ui.main.exchange.ExchangeViewModel.Companion.TRADE_CURRENCY_KRW
import org.jeonfeel.moeuibit2.ui.main.exchange.TickerAskBidState
import org.jeonfeel.moeuibit2.ui.main.exchange.component.SortOrder
import org.jeonfeel.moeuibit2.ui.main.exchange.component.SortType
import org.jeonfeel.moeuibit2.utils.Utils
import org.jeonfeel.moeuibit2.utils.ext.mapToMarketCodesRequest
import org.jeonfeel.moeuibit2.utils.manager.CacheManager
import java.math.BigDecimal
import javax.inject.Inject
import kotlin.reflect.KFunction1

class BitThumbExchange @Inject constructor(
    private val biThumbUseCase: BitThumbUseCase,
    private val cacheManager: CacheManager
) {
    private val krwMarketCodeMap = mutableMapOf<String, BitThumbMarketCodeRes>()
    private val btcMarketCodeMap = mutableMapOf<String, BitThumbMarketCodeRes>()

    private val krwList = arrayListOf<String>()
    private val krwExchangeModelPosition = mutableMapOf<String, Int>()
    private val _krwExchangeModelList = mutableStateListOf<CommonExchangeModel>()

    private val btcList = arrayListOf<String>()
    private val btcExchangeModelPosition = mutableMapOf<String, Int>()
    private val _btcExchangeModelList = mutableStateListOf<CommonExchangeModel>()

    private val favoriteList = arrayListOf<String>()
    private val favoriteModelPosition = mutableMapOf<String, Int>()
    private val _favoriteExchangeModelList = mutableStateListOf<CommonExchangeModel>()

    private val _biThumbWarningMap = mutableMapOf<String, List<String>>()

    private var tradeCurrencyState: State<Int>? = null
    private var isUpdateExchange: State<Boolean>? = null

    fun initBitThumb(
        tradeCurrencyState: State<Int>,
        isUpdateExchange: State<Boolean>,
    ) {
        this.tradeCurrencyState = tradeCurrencyState
        this.isUpdateExchange = isUpdateExchange
    }

    suspend fun onStart(updateLoadingState: KFunction1<Boolean, Unit>) {
        if (tickerDataIsEmpty()) {
            clearTickerData()
            updateLoadingState(true)

            fetchBiThumbWarning()
            fetchBitThumbMarketCodeList()
            fetchBitThumbTicker()

            loadingDelay(updateLoadingState)

            useCaseOnStart()
        } else {
            if (tradeCurrencyState?.value == TRADE_CURRENCY_FAV) {
                favoriteOnResume()
            }

            useCaseOnStart()
        }
    }

    private fun loadingDelay(updateLoadingState: KFunction1<Boolean, Unit>) {
        Handler(Looper.getMainLooper()).postDelayed({
            updateLoadingState(false)
        }, 500)
    }

    suspend fun collectCoinTicker() {
        this.collectTicker()
    }

    suspend fun onStop() {
        biThumbUseCase.biThumbSocketOnStop()
    }

    private suspend fun useCaseOnStart() {
        when (tradeCurrencyState?.value) {
            TRADE_CURRENCY_KRW -> {
                biThumbUseCase.biThumbSocketOnStart(
                    marketCodes = krwList.toList(),
                )
            }

            TRADE_CURRENCY_BTC -> {
                val tempBtcList = ArrayList(btcList)
                tempBtcList.add(BTC_MARKET)
                biThumbUseCase.biThumbSocketOnStart(
                    marketCodes = tempBtcList.toList()
                )
            }

            TRADE_CURRENCY_FAV -> {
                val tempFavoriteList = ArrayList(favoriteList)
                tempFavoriteList.add(BTC_MARKET)
                biThumbUseCase.biThumbSocketOnStart(marketCodes = tempFavoriteList.toList())
            }
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
        favoriteList.clear()
        favoriteModelPosition.clear()
        _favoriteExchangeModelList.clear()
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

    private suspend fun fetchBiThumbWarning() {
        biThumbUseCase.fetchBiThumbWarning().collect { res ->
            when (res) {
                is ResultState.Success -> {
                    _biThumbWarningMap.putAll(res.data)
                }

                is ResultState.Error -> {
                    Logger.e(res.message)
                }

                else -> {

                }
            }
        }
    }

    private suspend fun fetchBitThumbMarketCodeList() {
        biThumbUseCase.fetchBitThumbMarketCodeList().collect { res ->
            when (res) {
                is ResultState.Success -> {
                    krwMarketCodeMap.putAll(res.data.krwMarketCodeMap)
                    btcMarketCodeMap.putAll(res.data.btcMarketCodeMap)
                    krwList.addAll(res.data.krwList.map { it.market })
                    btcList.addAll(res.data.btcList.map { it.market })
                    cacheManager.saveBiThumbKoreanCoinNameMap(krwMarketCodeMap + btcMarketCodeMap)
                    cacheManager.saveBiThumbEnglishCoinNameMap(krwMarketCodeMap + btcMarketCodeMap)
                }

                is ResultState.Error -> {
                    Logger.e(res.message)
                }

                else -> {

                }
            }
        }
    }

    private suspend fun fetchBitThumbTicker() {
        val marketCodes = (krwList + btcList)
        marketCodes.chunked(100).forEach { chunk ->
            biThumbUseCase.fetchBitThumbTicker(
                marketCodes = chunk.mapToMarketCodesRequest(),
                krwBitThumbMarketCodeMap = krwMarketCodeMap,
                btcBitThumbMarketCodeMap = btcMarketCodeMap,
                warningTypeMap = _biThumbWarningMap
            ).collect { res ->
                when (res) {
                    is ResultState.Success -> {
                        val bitThumbTickerGroupedRes = res.data

                        val krwOffset = _krwExchangeModelList.size
                        val btcOffset = _btcExchangeModelList.size

                        _krwExchangeModelList.addAll(bitThumbTickerGroupedRes.krwCommonExchangeModelList)
                        _btcExchangeModelList.addAll(bitThumbTickerGroupedRes.btcCommonExchangeModelList)

                        bitThumbTickerGroupedRes.krwModelPosition.forEach { (market, index) ->
                            krwExchangeModelPosition[market] = index + krwOffset
                        }

                        bitThumbTickerGroupedRes.btcModelPosition.forEach { (market, index) ->
                            btcExchangeModelPosition[market] = index + btcOffset
                        }
                    }

                    is ResultState.Error -> {
                        Logger.e(res.message)
                    }

                    else -> {
                        // Do nothing
                    }
                }
            }
        }
        sortTickerList(
            tradeCurrency = TRADE_CURRENCY_KRW,
            sortType = SortType.VOLUME,
            sortOrder = SortOrder.DESCENDING
        )
    }

    private suspend fun updateTickerData(sortOrder: SortOrder? = null, sortType: SortType? = null) {
        val marketCodes = when (tradeCurrencyState?.value) {
            TRADE_CURRENCY_KRW -> krwList
            TRADE_CURRENCY_BTC -> btcList
            else -> favoriteList
        }

        marketCodes.chunked(100).forEach { chunk ->
            biThumbUseCase.fetchBitThumbTicker(
                marketCodes = chunk.mapToMarketCodesRequest(),
                krwBitThumbMarketCodeMap = krwMarketCodeMap,
                btcBitThumbMarketCodeMap = btcMarketCodeMap,
                warningTypeMap = _biThumbWarningMap
            ).collect { res ->
                when (res) {
                    is ResultState.Success -> {
                        val bitThumbTickerGroupedRes = res.data

                        bitThumbTickerGroupedRes.krwCommonExchangeModelList.forEach {
                            val position = krwExchangeModelPosition[it.market]
                            if (position != null) {
                                _krwExchangeModelList[position] = it
                            }
                        }

                        bitThumbTickerGroupedRes.btcCommonExchangeModelList.forEach {
                            val position = btcExchangeModelPosition[it.market]
                            if (position != null) {
                                _btcExchangeModelList[position] = it
                            }
                        }
                    }

                    is ResultState.Error -> {
                        Logger.e(res.message)
                    }

                    else -> {
                        // Do nothing
                    }
                }
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

    suspend fun changeTradeCurrencyAction(
        sortOrder: SortOrder? = null,
        sortType: SortType? = null,
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
                    if (market.startsWith(KRW_SYMBOL_PREFIX)) {
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
                val model = if (market.startsWith(KRW_SYMBOL_PREFIX)) {
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

    private suspend fun getFavoriteList() {
        favoriteList.clear()

        val list = biThumbUseCase.getFavoriteList()?.let {
            it.map { favorite -> favorite?.market ?: "" }
        } ?: emptyList()

        favoriteList.addAll(list)
    }

    private suspend fun removeFavorite(market: String) {
        biThumbUseCase.removeFavorite(market)
    }

    fun sortTickerList(
        tradeCurrency: Int,
        sortType: SortType,
        sortOrder: SortOrder,
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

    fun getBtcPrice(): BigDecimal {
        val btcPosition = krwExchangeModelPosition[BTC_MARKET]
        return btcPosition?.let {
            _krwExchangeModelList[it].tradePrice
        } ?: BigDecimal.ZERO
    }

    private fun getTargetMaps(
        bithumbSocketTickerRes: BithumbSocketTickerRes,
    ): Triple<MutableMap<String, Int>?, Map<String, BitThumbMarketCodeRes>?, MutableList<CommonExchangeModel>?> {
        return when (tradeCurrencyState?.value) {
            TRADE_CURRENCY_KRW -> Triple(
                krwExchangeModelPosition,
                krwMarketCodeMap,
                _krwExchangeModelList
            )

            TRADE_CURRENCY_BTC -> {
                if (bithumbSocketTickerRes.code == BTC_MARKET) {
                    Triple(
                        krwExchangeModelPosition,
                        krwMarketCodeMap,
                        _krwExchangeModelList
                    )
                } else {
                    Triple(
                        btcExchangeModelPosition,
                        btcMarketCodeMap,
                        _btcExchangeModelList
                    )
                }
            }

            TRADE_CURRENCY_FAV -> {
                if (bithumbSocketTickerRes.code == BTC_MARKET) {
                    updateFavoriteModel(bithumbSocketTickerRes, krwMarketCodeMap)
                    Triple(krwExchangeModelPosition, krwMarketCodeMap, _krwExchangeModelList)
                } else {
                    val marketCodeMap =
                        if (bithumbSocketTickerRes.code.startsWith(KRW_SYMBOL_PREFIX)) {
                            krwMarketCodeMap
                        } else {
                            btcMarketCodeMap
                        }
                    Triple(
                        favoriteModelPosition,
                        marketCodeMap,
                        _favoriteExchangeModelList
                    )
                }
            }

            else -> Triple(null, null, null)
        }
    }

    private fun updateFavoriteModel(
        bithumbSocketTickerRes: BithumbSocketTickerRes,
        marketCodeMap: Map<String, BitThumbMarketCodeRes>,
    ) {
        val position = favoriteModelPosition[BTC_MARKET] ?: return
        val marketCode = marketCodeMap[bithumbSocketTickerRes.code] ?: return
        val waringList = _biThumbWarningMap[marketCode.market] ?: emptyList()

        val model = bithumbSocketTickerRes.mapToCommonExchangeModel(marketCode, waringList).apply {
            needAnimation.value = bithumbSocketTickerRes.askBid
        }

        _favoriteExchangeModelList[position] = model
    }

    private suspend fun collectTicker() {
        biThumbUseCase.observeTickerResponse().collect { res ->
            try {
                if (res is ResultState.Success) {
                    if (isUpdateExchange?.value == false) {

                        return@collect
                    }

                    if (res.data.tradePrice == 0.0) {

                        return@collect
                    }

                    val (positionMap, marketCodeMap, modelList) = getTargetMaps(
                        bithumbSocketTickerRes = res.data
                    )

                    val position = positionMap?.get(res.data.code)
                    val marketCode = marketCodeMap?.get(res.data.code)
                    val waringList = _biThumbWarningMap[marketCode?.market] ?: emptyList()

                    Logger.e(marketCode?.market + " / / " + position.toString())

                    if (position != null && marketCode != null) {
                        val model =
                            res.data.mapToCommonExchangeModel(marketCode, waringList).apply {
                                needAnimation.value = res.data.askBid
                            }
                        Logger.e(model.toString())
                        modelList?.set(position, model)
                    }
                } else {
                    Logger.e((res as ResultState.Error).message)
                }
            } catch (e: Exception) {
                Logger.e(e.message.toString())
            }
        }
    }
}