package org.jeonfeel.moeuibit2.data.network.websocket.thunder.service

import com.jeremy.thunder.ws.Receive
import com.jeremy.thunder.ws.Send
import kotlinx.coroutines.flow.Flow
import org.jeonfeel.moeuibit2.data.network.retrofit.response.upbit.GetUpbitMarketTickerRes
import org.jeonfeel.moeuibit2.data.network.websocket.model.upbit.UpbitSocketTickerRes
import org.jeonfeel.moeuibit2.data.network.websocket.thunder.request.UpBitSocketTickerRequest

interface UpBitSocketService {
    @Send
    fun requestUpbitRequest(request: List<UpBitSocketTickerRequest>)

    @Receive
    fun collectUpbitTickers(): Flow<UpbitSocketTickerRes>
}