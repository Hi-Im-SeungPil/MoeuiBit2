package org.jeonfeel.moeuibit2.data.usecase

import com.orhanobut.logger.Logger
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.jeonfeel.moeuibit2.constants.BTC_SYMBOL_PREFIX
import org.jeonfeel.moeuibit2.constants.EXCHANGE_BITTHUMB
import org.jeonfeel.moeuibit2.constants.EXCHANGE_UPBIT
import org.jeonfeel.moeuibit2.constants.KRW_SYMBOL_PREFIX
import org.jeonfeel.moeuibit2.data.local.room.entity.Favorite
import org.jeonfeel.moeuibit2.data.network.retrofit.ApiResult
import org.jeonfeel.moeuibit2.data.network.retrofit.model.bitthumb.BitThumbMarketCodeGroupedRes
import org.jeonfeel.moeuibit2.data.network.retrofit.model.bitthumb.BitThumbTickerGroupedRes
import org.jeonfeel.moeuibit2.data.network.retrofit.response.bitthumb.BitThumbMarketCodeRes
import org.jeonfeel.moeuibit2.data.network.websocket.manager.bithumb.BiThumbExchangeWebsocketManager
import org.jeonfeel.moeuibit2.data.network.websocket.model.bitthumb.BithumbSocketTickerRes
import org.jeonfeel.moeuibit2.data.repository.local.LocalRepository
import org.jeonfeel.moeuibit2.data.repository.network.BiThumbRepository
import org.jeonfeel.moeuibit2.ui.base.BaseUseCase
import org.jeonfeel.moeuibit2.ui.common.ResultState
import org.jeonfeel.moeuibit2.utils.manager.CacheManager

class BitThumbUseCase(
    private val localRepository: LocalRepository,
    private val bitThumbRepository: BiThumbRepository,
    private val cacheManager: CacheManager
) : BaseUseCase() {

    private val biThumbExchangeWebsocketManager = BiThumbExchangeWebsocketManager()

    suspend fun biThumbSocketOnStart(marketCodes: List<String>) {
        Logger.e(marketCodes.toString())
        biThumbExchangeWebsocketManager.updateIsBackground(false)
        if (biThumbExchangeWebsocketManager.getIsSocketConnected()) {
            biThumbExchangeWebsocketManager.sendMessage(marketCodes.joinToString(separator = ",") { "\"$it\"" })
        } else {
            biThumbExchangeWebsocketManager.connectWebSocketFlow(marketCodes.joinToString(separator = ",") { "\"$it\"" })
        }
    }

    suspend fun biThumbSocketOnStop() {
        biThumbExchangeWebsocketManager.updateIsBackground(true)
        biThumbExchangeWebsocketManager.onStop()
    }

    suspend fun observeTickerResponse(): Flow<ResultState<BithumbSocketTickerRes>> {
        return biThumbExchangeWebsocketManager.tickerFlow.map { res ->
            if (res != null) {
                ResultState.Success(res)
            } else {
                ResultState.Error("Socket Error")
            }
        }
    }

    suspend fun fetchBitThumbMarketCodeList(): Flow<ResultState<BitThumbMarketCodeGroupedRes>> {
        return bitThumbRepository.fetchBitThumbMarketCodeList().map { res ->
            when (res.status) {
                ApiResult.Status.SUCCESS -> {
                    if (res.data != null) {
                        val krwList =
                            res.data.filter { it.market.contains(KRW_SYMBOL_PREFIX) }.toList()
                        val btcList =
                            res.data.filter { it.market.contains(BTC_SYMBOL_PREFIX) }.toList()
                        val krwMarketCodeMap = krwList.associateBy { it.market }
                        val btcMarketCodeMap = btcList.associateBy { it.market }

                        val bitThumbMarketCodeGroupedRes = BitThumbMarketCodeGroupedRes(
                            krwList = krwList,
                            btcList = btcList,
                            krwMarketCodeMap = krwMarketCodeMap,
                            btcMarketCodeMap = btcMarketCodeMap,
                        )

                        cacheManager.saveBiThumbKoreanCoinNameMap(krwMarketCodeMap + btcMarketCodeMap)
                        cacheManager.saveBiThumbEnglishCoinNameMap(krwMarketCodeMap + btcMarketCodeMap)

                        ResultState.Success(bitThumbMarketCodeGroupedRes)
                    } else {
                        ResultState.Error(res.message.toString())
                    }
                }

                ApiResult.Status.API_ERROR,
                ApiResult.Status.NETWORK_ERROR -> {
                    ResultState.Error(res.message.toString())
                }

                else -> {
                    ResultState.Loading
                }
            }
        }
    }

    suspend fun fetchBitThumbTicker(
        marketCodes: String,
        krwBitThumbMarketCodeMap: Map<String, BitThumbMarketCodeRes>,
        btcBitThumbMarketCodeMap: Map<String, BitThumbMarketCodeRes>,
        warningTypeMap: Map<String,List<String>>
    ): Flow<ResultState<BitThumbTickerGroupedRes>> {
        return bitThumbRepository.fetchBitThumbTicker(marketCodes = marketCodes).map { res ->
            when (res.status) {
                ApiResult.Status.SUCCESS -> {
                    val data = res.data
                    if (data != null) {
                        val commonExchangeModelList = data.map {
                            val marketCodeRes = when {
                                it.market.startsWith(KRW_SYMBOL_PREFIX) -> krwBitThumbMarketCodeMap[it.market]
                                else -> btcBitThumbMarketCodeMap[it.market]
                            }

                            val warningType = warningTypeMap[it.market] ?: emptyList()

                            it.mapToCommonExchangeModel(marketCodeRes, warningType)
                        }

                        val krwExchangeModelList = commonExchangeModelList
                            .filter { it.market.startsWith(KRW_SYMBOL_PREFIX) }

                        val btcExchangeModelList = commonExchangeModelList
                            .filter { it.market.startsWith(BTC_SYMBOL_PREFIX) }

                        val krwModelPosition = krwExchangeModelList
                            .mapIndexed { index, model -> model.market to index }
                            .toMap()

                        val btcModelPosition = btcExchangeModelList
                            .mapIndexed { index, model -> model.market to index }
                            .toMap()

                        ResultState.Success(
                            BitThumbTickerGroupedRes(
                                krwCommonExchangeModelList = krwExchangeModelList,
                                btcCommonExchangeModelList = btcExchangeModelList,
                                krwModelPosition = krwModelPosition,
                                btcModelPosition = btcModelPosition,
                            )
                        )
                    } else {
                        ResultState.Error(res.message.toString())
                    }
                }

                ApiResult.Status.API_ERROR,
                ApiResult.Status.NETWORK_ERROR-> {
                    ResultState.Error(res.message.toString())
                }

                else -> {
                    ResultState.Loading
                }
            }
        }
    }

    suspend fun fetchBiThumbWarning(): Flow<ResultState<Map<String, List<String>>>> {
        return bitThumbRepository.fetchBiThumbWarning().map { res ->
            when (res.status) {
                ApiResult.Status.SUCCESS -> {
                    val data = res.data
                    if (data != null) {
                        val result = res.data.groupBy { it.market }
                            .mapValues { entry -> entry.value.map { it.warningType } }

                        ResultState.Success(result)
                    } else {
                        ResultState.Error(res.message.toString())
                    }
                }

                ApiResult.Status.API_ERROR,
                ApiResult.Status.NETWORK_ERROR-> {
                    ResultState.Error(res.message.toString())
                }

                else -> {
                    ResultState.Loading
                }
            }
        }
    }

    suspend fun getFavoriteList(): List<Favorite?>? {
        return localRepository.getFavoriteDao().getAllByExchange(EXCHANGE_BITTHUMB)
    }

    suspend fun removeFavorite(market: String) {
        localRepository.getFavoriteDao().delete(market, EXCHANGE_UPBIT)
    }
}