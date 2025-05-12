package org.jeonfeel.moeuibit2.ui.main.exchange.root_exchange

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateListOf
import com.orhanobut.logger.Logger
import kotlinx.coroutines.flow.collectLatest
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
import org.jeonfeel.moeuibit2.ui.main.exchange.component.SortOrder
import org.jeonfeel.moeuibit2.ui.main.exchange.component.SortType
import org.jeonfeel.moeuibit2.utils.ext.mapToMarketCodesRequest
import java.math.BigDecimal
import javax.inject.Inject
import kotlin.reflect.KFunction1

class BitThumbExchange @Inject constructor(
    private val biThumbUseCase: BitThumbUseCase,
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
            fetchBitThumbMarketCodeList()
            fetchBitThumbTicker()
            useCaseOnStart()
        } else {

        }
    }

    suspend fun collectCoinTicker() {
        this.collectTicker()
    }

    suspend fun onStop() {
        biThumbUseCase.bithumbSocketOnStop()
    }

    private suspend fun useCaseOnStart() {
        when (tradeCurrencyState?.value) {
            TRADE_CURRENCY_KRW -> {
                biThumbUseCase.bithumbSocketOnStart(
                    marketCodes = krwList.toList(),
                )
            }

            TRADE_CURRENCY_BTC -> {
                val tempBtcList = ArrayList(btcList)
                tempBtcList.add(BTC_MARKET)
                biThumbUseCase.bithumbSocketOnStart(
                    marketCodes = tempBtcList.toList()
                )
            }

            TRADE_CURRENCY_FAV -> {
                val tempFavoriteList = ArrayList(favoriteList)
                tempFavoriteList.add(BTC_MARKET)
                biThumbUseCase.bithumbSocketOnStart(marketCodes = tempFavoriteList.toList())
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

    private suspend fun fetchBitThumbMarketCodeList() {
        biThumbUseCase.fetchBitThumbMarketCodeList().collect { res ->
            when (res) {
                is ResultState.Success -> {
                    krwMarketCodeMap.putAll(res.data.krwMarketCodeMap)
                    btcMarketCodeMap.putAll(res.data.btcMarketCodeMap)
                    krwList.addAll(res.data.krwList.map { it.market })
                    btcList.addAll(res.data.btcList.map { it.market })
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
        marketCodes.chunked(100).forEach {
            biThumbUseCase.fetchBitThumbTicker(
                marketCodes = it.mapToMarketCodesRequest(),
                krwBitThumbMarketCodeMap = krwMarketCodeMap,
                btcBitThumbMarketCodeMap = btcMarketCodeMap,
            ).collect { res ->
                when (res) {
                    is ResultState.Success -> {
                        val bitThumbTickerGroupedRes = res.data
                        krwExchangeModelPosition.putAll(bitThumbTickerGroupedRes.krwModelPosition)
                        btcExchangeModelPosition.putAll(bitThumbTickerGroupedRes.btcModelPosition)
                        _krwExchangeModelList.addAll(bitThumbTickerGroupedRes.krwCommonExchangeModelList)
                        _btcExchangeModelList.addAll(bitThumbTickerGroupedRes.btcCommonExchangeModelList)
                    }

                    is ResultState.Error -> {
                        Logger.e(res.message)
                    }

                    else -> {

                    }
                }
            }
        }
    }

    suspend fun changeTradeCurrencyAction(
        sortOrder: SortOrder? = null,
        sortType: SortType? = null,
    ) {
//        favoriteMarketChangeAction(sortOrder, sortType)

        if (tradeCurrencyState?.value == TRADE_CURRENCY_FAV) return

//        updateTickerData()
        useCaseOnStart()
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

        val model = bithumbSocketTickerRes.mapToCommonExchangeModel(marketCode).apply {
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

                    if (position != null && marketCode != null) {
                        val model = res.data.mapToCommonExchangeModel(marketCode).apply {
                            needAnimation.value = res.data.askBid
                        }

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