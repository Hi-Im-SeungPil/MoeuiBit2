package org.jeonfeel.moeuibit2.data.network.websocket.thunder.service

import com.jeremy.thunder.ws.Receive
import com.jeremy.thunder.ws.Send
import kotlinx.coroutines.flow.Flow
import org.jeonfeel.moeuibit2.data.network.websocket.model.upbit.UpbitSocketOrderBookRes
import org.jeonfeel.moeuibit2.data.network.websocket.model.upbit.UpbitSocketTickerRes
import org.jeonfeel.moeuibit2.data.network.websocket.thunder.request.UpBitSocketTickerReq
import org.jeonfeel.moeuibit2.data.network.websocket.thunder.request.UpbitSocketOrderBookReq

interface UpBitSocketService {
    @Send
    fun requestUpbitTickerRequest(request: List<UpBitSocketTickerReq>)

    @Receive
    fun collectUpbitTickers(): Flow<UpbitSocketTickerRes>

    @Send
    fun requestUpbitOrderBookRequest(request: List<UpbitSocketOrderBookReq> )

    @Receive
    fun collectUpbitOrderBook(): Flow<UpbitSocketOrderBookRes>
}