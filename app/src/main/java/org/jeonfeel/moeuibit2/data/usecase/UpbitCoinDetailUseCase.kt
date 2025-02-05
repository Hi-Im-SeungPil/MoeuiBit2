package org.jeonfeel.moeuibit2.data.usecase

import kotlinx.coroutines.flow.Flow
import org.jeonfeel.moeuibit2.data.local.room.entity.Favorite
import org.jeonfeel.moeuibit2.data.network.retrofit.request.upbit.GetUpbitMarketTickerReq
import org.jeonfeel.moeuibit2.data.network.websocket.manager.CoinDetailWebsocketManager
import org.jeonfeel.moeuibit2.data.network.websocket.model.upbit.UpbitSocketTickerRes
import org.jeonfeel.moeuibit2.data.repository.local.LocalRepository
import org.jeonfeel.moeuibit2.data.repository.network.UpbitRepository
import org.jeonfeel.moeuibit2.ui.base.BaseUseCase
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
        return localRepository.getFavoriteDao().select(market)
    }

    suspend fun addFavorite(market: String) {
        localRepository.getFavoriteDao().insert(market)
    }

    suspend fun deleteFavorite(market: String) {
        localRepository.getFavoriteDao().delete(market)
    }

    suspend fun getMarketTicker(
        getUpbitMarketTickerReq: GetUpbitMarketTickerReq,
        isList: Boolean = false
    ): Flow<Any> {
        return requestApiResult(
            result = upbitRepository.getMarketTicker(getUpbitMarketTickerReq),
            onSuccess = { ticker ->
                if (isList) {
                    ticker
                } else {
                    ticker[0]
                }
            }
        )
    }

    fun observeTickerResponse(): Flow<UpbitSocketTickerRes?> {
        return coinDetailWebsocketManager.tickerFlow
    }
}