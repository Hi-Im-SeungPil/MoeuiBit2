package org.jeonfeel.moeuibit2.data.usecase

import kotlinx.coroutines.flow.Flow
import org.jeonfeel.moeuibit2.constants.UPBIT_BTC_SYMBOL_PREFIX
import org.jeonfeel.moeuibit2.constants.UPBIT_KRW_SYMBOL_PREFIX
import org.jeonfeel.moeuibit2.data.network.retrofit.model.upbit.CommonExchangeModel
import org.jeonfeel.moeuibit2.data.network.retrofit.request.upbit.GetUpbitMarketTickerReq
import org.jeonfeel.moeuibit2.data.network.retrofit.response.upbit.GetUpbitMarketTickerRes
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
import kotlin.reflect.KFunction3

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
        upbitMarketCodeList: List<UpbitMarketCodeRes>,
        addExchangeModelPosition: (market: String, position: Int, isKor: Boolean) -> Unit
    ): Flow<Any> {
        return requestApiResult(
            result = upbitRepository.getMarketTicker(getUpbitMarketTickerReq),
            onSuccess = { result ->
                val krwExchangeModelList = arrayListOf<CommonExchangeModel>()
                val btcExchangeModelList = arrayListOf<CommonExchangeModel>()
                result.forEachIndexed { index, ticker ->
                    val commonExchangeModel = ticker.mapTo(upbitMarketCodeList[index])
                    when {
                        ticker.market.startsWith(UPBIT_KRW_SYMBOL_PREFIX) -> {
                            krwExchangeModelList.add(commonExchangeModel)
                            addExchangeModelPosition(
                                commonExchangeModel.market,
                                (krwExchangeModelList.size - 1),
                                true
                            )
                        }

                        ticker.market.startsWith(UPBIT_BTC_SYMBOL_PREFIX) -> {
                            btcExchangeModelList.add(commonExchangeModel)
                            addExchangeModelPosition(
                                commonExchangeModel.market,
                                (btcExchangeModelList.size - 1),
                                false
                            )
                        }
                    }
                }
                Pair(krwExchangeModelList, btcExchangeModelList)
            }
        )
    }

    /**
     * 업비트 Ticker 구독 요청
     */
    suspend fun requestSubscribeTicker(
        marketCodes: List<String>
    ) {
        upBitSocketService.requestUpbitRequest(
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

    fun observeTickerResponse(): () -> Flow<UpbitSocketTickerRes> {
        return upBitSocketService::collectUpbitTickers
    }
}