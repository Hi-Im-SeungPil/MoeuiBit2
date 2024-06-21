package org.jeonfeel.moeuibit2.data.usecase

import kotlinx.coroutines.flow.Flow
import org.jeonfeel.moeuibit2.data.network.retrofit.request.upbit.GetUpbitOrderBookReq
import org.jeonfeel.moeuibit2.data.network.websocket.model.upbit.UpbitSocketOrderBookRes
import org.jeonfeel.moeuibit2.data.network.websocket.thunder.request.OrderBookIsOnlyRealTimeField
import org.jeonfeel.moeuibit2.data.network.websocket.thunder.request.OrderBookRequestTypeField
import org.jeonfeel.moeuibit2.data.network.websocket.thunder.request.RequestFormatField
import org.jeonfeel.moeuibit2.data.network.websocket.thunder.request.RequestTicketField
import org.jeonfeel.moeuibit2.data.network.websocket.thunder.request.RequestTypeField
import org.jeonfeel.moeuibit2.data.network.websocket.thunder.request.UpbitSocketOrderBookReq
import org.jeonfeel.moeuibit2.data.network.websocket.thunder.service.UpBitSocketService
import org.jeonfeel.moeuibit2.data.repository.network.UpbitRepository
import org.jeonfeel.moeuibit2.ui.base.BaseUseCase
import java.util.UUID
import javax.inject.Inject

enum class OrderBookKind {
    ASK, BID
}

class UpbitCoinOrderUseCase @Inject constructor(
    private val upbitRepository: UpbitRepository,
    private val upBitSocketService: UpBitSocketService
) : BaseUseCase() {
    suspend fun getOrderBook(market: String): Flow<Any> {
        val getUpbitOrderBookReq = GetUpbitOrderBookReq(market = market)
        return requestApiResult(
            result = upbitRepository.getOrderBook(getUpbitOrderBookReq),
            onSuccess = {
                it[0].mapTo()
            }
        )
    }

    suspend fun getSocketOrderBook(marketCodes: List<String>) {
        upBitSocketService.requestUpbitOrderBookRequest(
            listOf(
                OrderBookRequestTypeField(codes = marketCodes),
                OrderBookIsOnlyRealTimeField()
            )
        )
    }

    suspend fun observeOrderBook(): Flow<UpbitSocketOrderBookRes> {
        return upBitSocketService.collectUpbitOrderBook()
    }
}