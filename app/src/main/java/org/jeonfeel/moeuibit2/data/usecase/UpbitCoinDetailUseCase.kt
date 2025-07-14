package org.jeonfeel.moeuibit2.data.usecase

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.jeonfeel.moeuibit2.constants.EXCHANGE_BITTHUMB
import org.jeonfeel.moeuibit2.constants.EXCHANGE_UPBIT
import org.jeonfeel.moeuibit2.data.local.room.entity.Favorite
import org.jeonfeel.moeuibit2.data.local.room.entity.MyCoin
import org.jeonfeel.moeuibit2.data.network.retrofit.ApiResult
import org.jeonfeel.moeuibit2.data.network.retrofit.request.upbit.GetUpbitMarketTickerReq
import org.jeonfeel.moeuibit2.data.network.retrofit.response.upbit.GetChartCandleRes
import org.jeonfeel.moeuibit2.data.network.retrofit.response.upbit.GetUpbitMarketTickerRes
import org.jeonfeel.moeuibit2.data.network.websocket.manager.upbit.CoinDetailWebsocketManager
import org.jeonfeel.moeuibit2.data.network.websocket.model.upbit.UpbitSocketTickerRes
import org.jeonfeel.moeuibit2.data.repository.local.LocalRepository
import org.jeonfeel.moeuibit2.data.repository.network.UpbitRepository
import org.jeonfeel.moeuibit2.ui.base.BaseUseCase
import org.jeonfeel.moeuibit2.ui.common.ResultState
import javax.inject.Inject

class UpbitCoinDetailUseCase @Inject constructor(
    private val localRepository: LocalRepository,
    private val upbitRepository: UpbitRepository
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
        return localRepository.getFavoriteDao().select(market, EXCHANGE_UPBIT)
    }

    suspend fun addFavorite(market: String) {
        localRepository.getFavoriteDao().insert(market, EXCHANGE_UPBIT)
    }

    suspend fun deleteFavorite(market: String) {
        localRepository.getFavoriteDao().delete(market, EXCHANGE_UPBIT)
    }

    suspend fun getUserHoldCoin(market: String): MyCoin? {
        val myCoin =
            localRepository.getMyCoinDao().getCoin(market = market, exchange = EXCHANGE_BITTHUMB)
        return myCoin
    }

    suspend fun fetchMarketTicker(
        getUpbitMarketTickerReq: GetUpbitMarketTickerReq,
    ): Flow<ResultState<List<GetUpbitMarketTickerRes>>> {
        return upbitRepository.fetchMarketTicker(getUpbitMarketTickerReq).map { res ->
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

    suspend fun getLineChartCandleSticks(market: String): Flow<Any> {
        return requestApiResult(
            result = upbitRepository.getMinuteCandle(minute = "10", market = market, count = "144"),
            onSuccess = { result: List<GetChartCandleRes> ->
                result
            }
        )
    }

    suspend fun fetchLineChart(market: String): Flow<ResultState<List<Float>>> {
        return upbitRepository.fetchMinuteCandle(market = market, minute = "10", count = "144")
            .map { res ->
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