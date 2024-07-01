package org.jeonfeel.moeuibit2.data.usecase

import kotlinx.coroutines.flow.Flow
import org.jeonfeel.moeuibit2.constants.UPBIT_BTC_SYMBOL_PREFIX
import org.jeonfeel.moeuibit2.constants.UPBIT_KRW_SYMBOL_PREFIX
import org.jeonfeel.moeuibit2.data.network.retrofit.model.upbit.CommonExchangeModel
import org.jeonfeel.moeuibit2.data.network.retrofit.request.upbit.GetUpbitMarketTickerReq
import org.jeonfeel.moeuibit2.data.network.retrofit.response.upbit.UpbitMarketCodeRes
import org.jeonfeel.moeuibit2.data.network.websocket.model.upbit.UpbitSocketTickerRes
import org.jeonfeel.moeuibit2.data.network.websocket.thunder.request.RequestFormatField
import org.jeonfeel.moeuibit2.data.network.websocket.thunder.request.RequestTicketField
import org.jeonfeel.moeuibit2.data.network.websocket.thunder.request.RequestTypeField
import org.jeonfeel.moeuibit2.data.network.websocket.thunder.service.UpBitSocketService
import org.jeonfeel.moeuibit2.data.repository.network.UpbitRepository
import org.jeonfeel.moeuibit2.ui.base.BaseUseCase
import java.util.UUID
import javax.inject.Inject

class UpbitUseCase @Inject constructor(
    private val upbitRepository: UpbitRepository,
    private val upBitSocketService: UpBitSocketService
) : BaseUseCase() {

    /**
     * 업비트 마켓 코드 조회
     */
    suspend fun getMarketCode(): Flow<Any> {
        return requestApiResult(
            result = upbitRepository.getUpbitMarketCodeList(),
            onSuccess = { result ->
                result
            }
        )
    }

    /**
     * 업비트 마켓 Ticker 조회
     */
    suspend fun getMarketTicker(
        getUpbitMarketTickerReq: GetUpbitMarketTickerReq,
        krwUpbitMarketCodeMap: Map<String, UpbitMarketCodeRes>,
        btcUpbitMarketCodeMap: Map<String, UpbitMarketCodeRes>,
        addExchangeModelPosition: (market: String, position: Int, isKrw: Boolean) -> Unit
    ): Flow<Any> {
        return requestApiResult(
            result = upbitRepository.getMarketTicker(getUpbitMarketTickerReq),
            onSuccess = { result ->
                val krwExchangeModelList = arrayListOf<CommonExchangeModel>()
                val btcExchangeModelList = arrayListOf<CommonExchangeModel>()
                var krwIndex = 0
                var btcIndex = 0
                result.forEachIndexed { index, ticker ->
                    when {
                        ticker.market.startsWith(UPBIT_KRW_SYMBOL_PREFIX) -> {
                            val commonExchangeModel = ticker.mapTo(krwUpbitMarketCodeMap[ticker.market]!!)
                            krwExchangeModelList.add(commonExchangeModel)
                            addExchangeModelPosition(
                                commonExchangeModel.market,
                                krwIndex++,
                                true
                            )
                        }

                        ticker.market.startsWith(UPBIT_BTC_SYMBOL_PREFIX) -> {
                            val commonExchangeModel = ticker.mapTo(btcUpbitMarketCodeMap[ticker.market]!!)
                            btcExchangeModelList.add(commonExchangeModel)
                            addExchangeModelPosition(
                                commonExchangeModel.market,
                                btcIndex++,
                                false
                            )
                        }
                    }
                }
                Pair(krwExchangeModelList, btcExchangeModelList)
            }
        )
    }

    suspend fun getMarketTicker(getUpbitMarketTickerReq: GetUpbitMarketTickerReq,): Flow<Any> {
        return requestApiResult(
            result = upbitRepository.getMarketTicker(getUpbitMarketTickerReq),
            onSuccess = { result ->
                result
            }
        )
    }

    /**
     * 업비트 Ticker 구독 요청
     */
    suspend fun requestSubscribeTicker(
        marketCodes: List<String>
    ) {
        upBitSocketService.requestUpbitTickerRequest(
            listOf(
                RequestTicketField(ticket = UUID.randomUUID().toString()),
                RequestTypeField(
                    type = "ticker",
                    codes = marketCodes
                ),
                RequestFormatField()
            )
        )
    }

    fun observeTickerResponse(): Flow<UpbitSocketTickerRes> {
        return upBitSocketService.collectUpbitTickers()
    }
}