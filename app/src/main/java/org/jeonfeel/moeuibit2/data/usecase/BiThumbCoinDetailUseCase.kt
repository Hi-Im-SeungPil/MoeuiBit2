package org.jeonfeel.moeuibit2.data.usecase

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.jeonfeel.moeuibit2.constants.EXCHANGE_BITTHUMB
import org.jeonfeel.moeuibit2.data.local.room.entity.Favorite
import org.jeonfeel.moeuibit2.data.local.room.entity.MyCoin
import org.jeonfeel.moeuibit2.data.network.retrofit.ApiResult
import org.jeonfeel.moeuibit2.data.network.retrofit.request.bithumb.BiThumbMinuteCandleReq
import org.jeonfeel.moeuibit2.data.network.retrofit.response.bitthumb.BitThumbTickerRes
import org.jeonfeel.moeuibit2.data.network.websocket.manager.upbit.CoinDetailWebsocketManager
import org.jeonfeel.moeuibit2.data.network.websocket.model.upbit.UpbitSocketTickerRes
import org.jeonfeel.moeuibit2.data.repository.local.LocalRepository
import org.jeonfeel.moeuibit2.data.repository.network.BiThumbRepository
import org.jeonfeel.moeuibit2.ui.base.BaseUseCase
import org.jeonfeel.moeuibit2.ui.common.ResultState
import javax.inject.Inject

class BiThumbCoinDetailUseCase @Inject constructor(
    private val localRepository: LocalRepository,
    private val biThumbRepository: BiThumbRepository
) : BaseUseCase() {
    private val coinDetailWebsocketManager = CoinDetailWebsocketManager()

    suspend fun onStart(marketCodes: String) {
        coinDetailWebsocketManager.updateIsBackground(false)
        coinDetailWebsocketManager.connectWebSocketFlow(marketCodes)
    }

    suspend fun onStop() {
        coinDetailWebsocketManager.updateIsBackground(true)
        coinDetailWebsocketManager.onStop()
    }

    suspend fun getIsFavorite(market: String): Favorite? {
        return localRepository.getFavoriteDao().select(market = market, exchange = EXCHANGE_BITTHUMB)
    }

    suspend fun addFavorite(market: String) {
        localRepository.getFavoriteDao().insert(market = market, exchange = EXCHANGE_BITTHUMB)
    }

    suspend fun deleteFavorite(market: String) {
        localRepository.getFavoriteDao().delete(market = market, exchange = EXCHANGE_BITTHUMB)
    }

    suspend fun getUserHoldCoin(market: String): MyCoin? {
        val myCoin = localRepository.getMyCoinDao().getCoin(market = market, exchange = EXCHANGE_BITTHUMB)
        return myCoin
    }

    suspend fun fetchMarketTicker(
        getUpbitMarketTickerReq: String,
    ): Flow<ResultState<List<BitThumbTickerRes>>> {
        return biThumbRepository.fetchBitThumbTicker(getUpbitMarketTickerReq).map { res ->
            when (res.status) {
                ApiResult.Status.SUCCESS -> {
                    if (res.data != null) {
                        ResultState.Success(res.data)
                    } else {
                        ResultState.Error("Unknown error")
                    }
                }

                ApiResult.Status.API_ERROR, ApiResult.Status.NETWORK_ERROR -> {
                    ResultState.Error(res.message.toString())
                }

                else -> {
                    ResultState.Loading
                }
            }
        }
    }

    suspend fun fetchLineChartCandleSticks(market: String): Flow<ResultState<List<Float>>> {
        val biThumbMinuteCandleReq = BiThumbMinuteCandleReq(
            market = market,
            to = "",
            count = "144"
        )

        return biThumbRepository.fetchMinuteCandle(
            biThumbMinuteCandleReq = biThumbMinuteCandleReq,
            unit = "10"
        ).map { res ->
            when (res.status) {
                ApiResult.Status.SUCCESS -> {
                    if (res.data != null) {
                        val lineChartData = res.data.map { it.tradePrice.toFloat() }.reversed()
                        ResultState.Success(lineChartData)
                    } else {
                        ResultState.Error("Unknown error")
                    }
                }

                ApiResult.Status.API_ERROR, ApiResult.Status.NETWORK_ERROR -> {
                    ResultState.Error(res.message.toString())
                }

                else -> {
                    ResultState.Loading
                }
            }
        }
    }

    fun observeTickerResponse(): Flow<UpbitSocketTickerRes?> {
        return coinDetailWebsocketManager.tickerFlow
    }
}