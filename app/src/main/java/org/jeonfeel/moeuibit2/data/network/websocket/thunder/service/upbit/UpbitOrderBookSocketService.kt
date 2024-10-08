package org.jeonfeel.moeuibit2.data.network.websocket.thunder.service.upbit

import com.jeremy.thunder.ws.Receive
import com.jeremy.thunder.ws.Send
import kotlinx.coroutines.flow.Flow
import org.jeonfeel.moeuibit2.data.network.websocket.model.upbit.UpbitSocketOrderBookRes
import org.jeonfeel.moeuibit2.data.network.websocket.thunder.request.upbit.UpbitSocketOrderBookReq

interface UpbitOrderBookSocketService {
    @Send
    fun requestUpbitOrderBookRequest(request: List<UpbitSocketOrderBookReq>)

    @Receive
    fun collectUpbitOrderBook(): Flow<UpbitSocketOrderBookRes>
}