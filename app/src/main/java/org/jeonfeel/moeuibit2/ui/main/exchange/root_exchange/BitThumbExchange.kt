package org.jeonfeel.moeuibit2.ui.main.exchange.root_exchange

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateListOf
import com.orhanobut.logger.Logger
import org.jeonfeel.moeuibit2.data.network.retrofit.model.upbit.CommonExchangeModel
import org.jeonfeel.moeuibit2.data.network.retrofit.response.bitthumb.BitThumbMarketCodeRes
import org.jeonfeel.moeuibit2.data.usecase.BitThumbUseCase
import org.jeonfeel.moeuibit2.ui.common.ResultState
import org.jeonfeel.moeuibit2.ui.main.exchange.ExchangeViewModel.Companion.TRADE_CURRENCY_BTC
import org.jeonfeel.moeuibit2.ui.main.exchange.ExchangeViewModel.Companion.TRADE_CURRENCY_FAV
import org.jeonfeel.moeuibit2.ui.main.exchange.ExchangeViewModel.Companion.TRADE_CURRENCY_KRW
import org.jeonfeel.moeuibit2.utils.ext.mapToMarketCodesRequest
import javax.inject.Inject

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

    suspend fun onStart() {
        if (tickerDataIsEmpty()) {
            fetchBitThumbMarketCodeList()
            fetchBitThumbTicker()
            useCaseOnStart()
        } else {

        }
    }

    suspend fun collectTicker2() {
        collectTicker()
    }

    suspend fun onStop() {
        biThumbUseCase.bithumbSocketOnStop()
    }

    private suspend fun useCaseOnStart() {
        biThumbUseCase.bithumbSocketOnStart(
            marketCodes = krwList.toList(),
        )
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

    private suspend fun collectTicker() {
        biThumbUseCase.observeTickerResponse().collect {

        }
    }
}