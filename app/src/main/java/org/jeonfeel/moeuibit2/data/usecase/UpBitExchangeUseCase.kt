package org.jeonfeel.moeuibit2.data.usecase

import android.content.Context
import com.jeremy.thunder.Thunder
import com.jeremy.thunder.event.converter.ConverterType
import com.jeremy.thunder.makeWebSocketCore
import kotlinx.coroutines.flow.Flow
import okhttp3.OkHttpClient
import org.jeonfeel.moeuibit2.constants.UPBIT_BTC_SYMBOL_PREFIX
import org.jeonfeel.moeuibit2.constants.UPBIT_KRW_SYMBOL_PREFIX
import org.jeonfeel.moeuibit2.data.local.room.entity.Favorite
import org.jeonfeel.moeuibit2.data.network.retrofit.model.upbit.CommonExchangeModel
import org.jeonfeel.moeuibit2.data.network.retrofit.request.upbit.GetUpbitMarketTickerReq
import org.jeonfeel.moeuibit2.data.network.retrofit.response.upbit.UpbitMarketCodeRes
import org.jeonfeel.moeuibit2.data.network.websocket.manager.ExchangeWebsocketManager
import org.jeonfeel.moeuibit2.data.network.websocket.model.upbit.UpbitSocketTickerRes
import org.jeonfeel.moeuibit2.data.network.websocket.thunder.request.upbit.RequestFormatField
import org.jeonfeel.moeuibit2.data.network.websocket.thunder.request.upbit.RequestTicketField
import org.jeonfeel.moeuibit2.data.network.websocket.thunder.request.upbit.RequestTypeField
import org.jeonfeel.moeuibit2.data.network.websocket.thunder.service.upbit.UpBitExchangeSocketService
import org.jeonfeel.moeuibit2.data.repository.local.LocalRepository
import org.jeonfeel.moeuibit2.data.repository.network.UpbitRepository
import org.jeonfeel.moeuibit2.ui.base.BaseUseCase
import java.util.UUID
import javax.inject.Inject

class UpBitExchangeUseCase @Inject constructor(
    private val upbitRepository: UpbitRepository,
    private val localRepository: LocalRepository,
) : BaseUseCase() {

    private val exchangeWebsocketManager = ExchangeWebsocketManager()

    suspend fun onStart(marketCodes: List<String>) {
        if (exchangeWebsocketManager.getIsSocketConnected()) {
            exchangeWebsocketManager.onStart(marketCodes.joinToString(separator = ","))
        } else {
            exchangeWebsocketManager.connectWebSocketFlow(marketCodes.joinToString(separator = ","))
        }
    }

    suspend fun onStop() {
        exchangeWebsocketManager.onStop()
    }

    /**
     * 업비트 마켓 코드 조회
     */
    suspend fun getMarketCode(): Flow<Any> {
        return requestApiResult(
            result = upbitRepository.getUpbitMarketCodeList(),
            onSuccess = { result ->
                result
            }
        )
    }

    /**
     * 업비트 마켓 Ticker 조회
     */
    suspend fun getMarketTicker(
        getUpbitMarketTickerReq: GetUpbitMarketTickerReq,
        krwUpbitMarketCodeMap: Map<String, UpbitMarketCodeRes>,
        btcUpbitMarketCodeMap: Map<String, UpbitMarketCodeRes>,
        addExchangeModelPosition: (market: String, position: Int, isKrw: Boolean) -> Unit
    ): Flow<Any> {
        return requestApiResult(
            result = upbitRepository.getMarketTicker(getUpbitMarketTickerReq),
            onSuccess = { result ->
                val krwExchangeModelList = arrayListOf<CommonExchangeModel>()
                val btcExchangeModelList = arrayListOf<CommonExchangeModel>()
                var krwIndex = 0
                var btcIndex = 0
                result.forEachIndexed loop@{ index, ticker ->

                    when {
                        ticker.market.startsWith(UPBIT_KRW_SYMBOL_PREFIX) -> {
                            val commonExchangeModel =
                                ticker.mapTo(krwUpbitMarketCodeMap[ticker.market]!!)
                            krwExchangeModelList.add(commonExchangeModel)
                            addExchangeModelPosition(
                                commonExchangeModel.market,
                                krwIndex++,
                                true
                            )
                        }

                        ticker.market.startsWith(UPBIT_BTC_SYMBOL_PREFIX) -> {
                            val commonExchangeModel =
                                ticker.mapTo(btcUpbitMarketCodeMap[ticker.market]!!)
                            btcExchangeModelList.add(commonExchangeModel)
                            addExchangeModelPosition(
                                commonExchangeModel.market,
                                btcIndex++,
                                false
                            )
                        }
                    }
                }
                Pair(krwExchangeModelList, btcExchangeModelList)
            }
        )
    }

    suspend fun getMarketTicker(
        getUpbitMarketTickerReq: GetUpbitMarketTickerReq,
        krwUpbitMarketCodeMap: Map<String, UpbitMarketCodeRes>,
        btcUpbitMarketCodeMap: Map<String, UpbitMarketCodeRes>,
    ): Flow<Any> {
        return requestApiResult(
            result = upbitRepository.getMarketTicker(getUpbitMarketTickerReq),
            onSuccess = { result ->
                val exchangeModelList = arrayListOf<CommonExchangeModel>()
                result.forEachIndexed loop@{ index, ticker ->
                    if (ticker.tradePrice == 0.0) return@loop

                    when {
                        ticker.market.startsWith(UPBIT_KRW_SYMBOL_PREFIX) -> {
                            val commonExchangeModel =
                                ticker.mapTo(krwUpbitMarketCodeMap[ticker.market]!!)
                            exchangeModelList.add(commonExchangeModel)
                        }

                        ticker.market.startsWith(UPBIT_BTC_SYMBOL_PREFIX) -> {
                            val commonExchangeModel =
                                ticker.mapTo(btcUpbitMarketCodeMap[ticker.market]!!)
                            exchangeModelList.add(commonExchangeModel)
                        }
                    }
                }
                exchangeModelList
            }
        )
    }

    fun observeTickerResponse(): Flow<UpbitSocketTickerRes?> {
        return exchangeWebsocketManager.tickerFlow
    }

    suspend fun addFavorite(market: String) {
        localRepository.getFavoriteDao().insert(market)
    }

    suspend fun removeFavorite(market: String) {
        localRepository.getFavoriteDao().delete(market)
    }

    suspend fun getFavoriteList(): List<Favorite?>? {
        return localRepository.getFavoriteDao().all
    }
}