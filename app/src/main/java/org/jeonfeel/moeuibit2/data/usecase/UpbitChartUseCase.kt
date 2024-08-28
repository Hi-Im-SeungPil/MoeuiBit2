package org.jeonfeel.moeuibit2.data.usecase

import kotlinx.coroutines.flow.Flow
import org.jeonfeel.moeuibit2.data.network.retrofit.request.upbit.GetChartCandleReq
import org.jeonfeel.moeuibit2.data.repository.local.LocalRepository
import org.jeonfeel.moeuibit2.data.repository.network.UpbitRepository
import org.jeonfeel.moeuibit2.ui.base.BaseUseCase
import javax.inject.Inject

class UpbitChartUseCase @Inject constructor(
    private val upbitRepository: UpbitRepository,
    private val localRepository: LocalRepository
) : BaseUseCase() {
    suspend fun getMinuteChartData(
        getChartCandleReq: GetChartCandleReq
    ): Flow<Any> {
        return requestApiResult(
            result = upbitRepository.getMinuteCandle(
                getChartCandleReq.candleType,
                getChartCandleReq.market,
                getChartCandleReq.count,
                getChartCandleReq.to
            ),
            onSuccess = { result ->
                result
            }
        )
    }

    suspend fun getOtherChartData(
        getChartCandleReq: GetChartCandleReq
    ): Flow<Any> {
        return requestApiResult(result = upbitRepository.getOtherCandle(
            getChartCandleReq.candleType,
            getChartCandleReq.market,
            getChartCandleReq.count,
            getChartCandleReq.to
        ), onSuccess = { result ->
            result
        })
    }
}