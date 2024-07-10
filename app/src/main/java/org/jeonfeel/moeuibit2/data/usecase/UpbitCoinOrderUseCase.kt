package org.jeonfeel.moeuibit2.data.usecase

import com.orhanobut.logger.Logger
import kotlinx.coroutines.flow.Flow
import org.jeonfeel.moeuibit2.constants.BTC_MARKET
import org.jeonfeel.moeuibit2.data.local.room.entity.MyCoin
import org.jeonfeel.moeuibit2.data.local.room.entity.User
import org.jeonfeel.moeuibit2.data.network.retrofit.request.upbit.GetUpbitOrderBookReq
import org.jeonfeel.moeuibit2.data.network.websocket.model.upbit.UpbitSocketOrderBookRes
import org.jeonfeel.moeuibit2.data.network.websocket.thunder.request.OrderBookIsOnlyRealTimeField
import org.jeonfeel.moeuibit2.data.network.websocket.thunder.request.OrderBookRequestTypeField
import org.jeonfeel.moeuibit2.data.network.websocket.thunder.request.OrderBookTicketField
import org.jeonfeel.moeuibit2.data.network.websocket.thunder.request.RequestTicketField
import org.jeonfeel.moeuibit2.data.network.websocket.thunder.service.UpBitSocketService
import org.jeonfeel.moeuibit2.data.repository.local.LocalRepository
import org.jeonfeel.moeuibit2.data.repository.network.UpbitRepository
import org.jeonfeel.moeuibit2.ui.base.BaseUseCase
import java.util.UUID
import javax.inject.Inject

enum class OrderBookKind {
    ASK, BID
}

class UpbitCoinOrderUseCase @Inject constructor(
    private val upbitRepository: UpbitRepository,
    private val localRepository: LocalRepository,
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

    suspend fun requestSubscribeOrderBook(marketCodes: List<String>) {
        upBitSocketService.requestUpbitOrderBookRequest(
            listOf(
                OrderBookTicketField(ticket = UUID.randomUUID().toString()),
                OrderBookRequestTypeField(codes = marketCodes),
//                OrderBookIsOnlyRealTimeField()
            )
        )
    }

    suspend fun requestObserveOrderBook(): Flow<UpbitSocketOrderBookRes> {
        return upBitSocketService.collectUpbitOrderBook()
    }

    suspend fun getUserSeedMoney(): User? {
        return localRepository.getUserDao().all
    }

    suspend fun getUserCoin(market: String): MyCoin? {
        return localRepository.getMyCoinDao().isInsert(market)
    }

    suspend fun getUserBtcCoin(): MyCoin? {
        return localRepository.getMyCoinDao().isInsert(BTC_MARKET)
    }
}