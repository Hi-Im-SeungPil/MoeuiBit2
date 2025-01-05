package org.jeonfeel.moeuibit2.data.network.websocket.thunder.service.upbit

import com.jeremy.thunder.ws.Receive
import com.jeremy.thunder.ws.Send
import kotlinx.coroutines.flow.Flow
import org.jeonfeel.moeuibit2.data.network.websocket.model.upbit.UpbitSocketTickerRes
import org.jeonfeel.moeuibit2.data.network.websocket.thunder.request.upbit.UpBitSocketTickerReq

interface UpbitPortfolioSocketService {
    @Send
    fun requestUpbitTickerRequest(request: List<UpBitSocketTickerReq>)

    @Receive
    fun collectUpbitTicker(): Flow<UpbitSocketTickerRes>
}