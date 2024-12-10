package org.jeonfeel.moeuibit2.data.network.websocket.thunder.service.upbit

import com.jeremy.thunder.ws.Receive
import com.jeremy.thunder.ws.Send
import kotlinx.coroutines.flow.Flow
import org.jeonfeel.moeuibit2.data.network.websocket.model.upbit.UpbitSocketTickerRes
import org.jeonfeel.moeuibit2.data.network.websocket.model.upbit.UpbitSocketTradeRes
import org.jeonfeel.moeuibit2.data.network.websocket.thunder.request.upbit.UpBitSocketTickerReq

interface UpBitExchangeSocketService {
    @Send
    fun requestUpbitTickerRequest(request: List<UpBitSocketTickerReq>)

    @Send
    fun requestUpbitPortfolioTickerRequest(request: List<UpBitSocketTickerReq>)

    @Send
    fun requestUpbitTradeRequest(request: List<UpBitSocketTickerReq>)

    @Send
    fun requestUpbitCoinDetailTickerRequest(request: List<UpBitSocketTickerReq>)

    @Receive
    fun collectUpbitTickers(): Flow<UpbitSocketTickerRes>

    @Receive
    fun collectUpbitPortfolioTickers(): Flow<UpbitSocketTickerRes>

    @Receive
    fun collectUpbitTrade(): Flow<UpbitSocketTradeRes>

//    @Receive
//    fun collectUpbitTrade(): Flow<UpbitSocketTickerRes>

    @Receive
    fun collectUpbitCoinDetailTickers(): Flow<UpbitSocketTickerRes>
}