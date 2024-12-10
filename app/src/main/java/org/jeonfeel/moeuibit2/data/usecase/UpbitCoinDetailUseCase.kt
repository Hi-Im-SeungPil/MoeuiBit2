package org.jeonfeel.moeuibit2.data.usecase

import kotlinx.coroutines.flow.Flow
import org.jeonfeel.moeuibit2.data.local.room.entity.Favorite
import org.jeonfeel.moeuibit2.data.network.retrofit.request.upbit.GetUpbitMarketTickerReq
import org.jeonfeel.moeuibit2.data.network.websocket.model.upbit.UpbitSocketTradeRes
import org.jeonfeel.moeuibit2.data.network.websocket.thunder.request.upbit.RequestFormatField
import org.jeonfeel.moeuibit2.data.network.websocket.thunder.request.upbit.RequestTicketField
import org.jeonfeel.moeuibit2.data.network.websocket.thunder.request.upbit.RequestTypeField
import org.jeonfeel.moeuibit2.data.network.websocket.thunder.service.upbit.UpbitCoinDetailSocketService
import org.jeonfeel.moeuibit2.data.repository.local.LocalRepository
import org.jeonfeel.moeuibit2.data.repository.network.UpbitRepository
import org.jeonfeel.moeuibit2.ui.base.BaseUseCase
import java.util.UUID
import javax.inject.Inject

class UpbitCoinDetailUseCase @Inject constructor(
    private val localRepository: LocalRepository,
    private val upbitRepository: UpbitRepository,
    private val upbitCoinDetailSocketService: UpbitCoinDetailSocketService
) : BaseUseCase() {

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

    suspend fun requestSubscribeTrade(
        marketCodes: List<String>,
    ) {
        upbitCoinDetailSocketService.requestUpbitTradeRequest(
            listOf(
                RequestTicketField(ticket = UUID.randomUUID().toString()),
                RequestTypeField(
                    type = "trade",
                    codes = marketCodes,
                ),
                RequestFormatField()
            )
        )
    }

    fun observeTradeResponse(): Flow<UpbitSocketTradeRes> {
        return upbitCoinDetailSocketService.collectUpbitTrade()
    }
}