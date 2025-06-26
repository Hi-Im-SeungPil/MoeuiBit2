package org.jeonfeel.moeuibit2.data.usecase

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.jeonfeel.moeuibit2.data.network.retrofit.ApiResult
import org.jeonfeel.moeuibit2.data.network.retrofit.request.bithumb.BiThumbDayCandleReq
import org.jeonfeel.moeuibit2.data.network.retrofit.request.bithumb.BiThumbMinuteCandleReq
import org.jeonfeel.moeuibit2.data.network.retrofit.request.bithumb.BiThumbMonthCandleReq
import org.jeonfeel.moeuibit2.data.network.retrofit.request.bithumb.BiThumbWeekCandleReq
import org.jeonfeel.moeuibit2.data.network.retrofit.request.upbit.GetChartCandleReq
import org.jeonfeel.moeuibit2.data.network.retrofit.response.bitthumb.BiThumbDayCandleRes
import org.jeonfeel.moeuibit2.data.network.retrofit.response.bitthumb.BiThumbMinuteCandleRes
import org.jeonfeel.moeuibit2.data.network.retrofit.response.bitthumb.BiThumbMonthCandleRes
import org.jeonfeel.moeuibit2.data.network.retrofit.response.bitthumb.BiThumbWeekCandleRes
import org.jeonfeel.moeuibit2.data.repository.local.LocalRepository
import org.jeonfeel.moeuibit2.data.repository.network.BiThumbRepository
import org.jeonfeel.moeuibit2.ui.common.ResultState

class BiThumbChartUsecase(
    private val biThumbRepository: BiThumbRepository,
    private val localRepository: LocalRepository
) {
    fun fetchMinuteChartData(getChartCandleReq: GetChartCandleReq): Flow<ResultState<Pair<List<BiThumbMinuteCandleRes>, String>>> {
        val biThumbMinuteCandleReq = BiThumbMinuteCandleReq(
            market = getChartCandleReq.market,
            count = getChartCandleReq.count,
            to = getChartCandleReq.to
        )

        return biThumbRepository.fetchMinuteCandle(
            biThumbMinuteCandleReq = biThumbMinuteCandleReq,
            unit = getChartCandleReq.candleType
        ).map { res ->
            when (res.status) {
                ApiResult.Status.SUCCESS -> {
                    if (res.data != null) {
                        ResultState.Success(Pair(res.data, "minute"))
                    } else {
                        ResultState.Error(res.message.toString())
                    }
                }

                ApiResult.Status.API_ERROR, ApiResult.Status.NETWORK_ERROR -> {
                    ResultState.Error(res.message.toString())
                }

                ApiResult.Status.LOADING -> {
                    ResultState.Loading
                }
            }
        }
    }

    fun fetchDayCandle(getChartCandleReq: GetChartCandleReq): Flow<ResultState<Pair<List<BiThumbDayCandleRes>, String>>> {
        val biThumbDayCandleReq = BiThumbDayCandleReq(
            market = getChartCandleReq.market,
            count = getChartCandleReq.count,
            to = getChartCandleReq.to
        )

        return biThumbRepository.fetchDayCandle(
            biThumbDayCandleReq = biThumbDayCandleReq
        ).map { res ->
            when (res.status) {
                ApiResult.Status.SUCCESS -> {
                    if (res.data != null) {
                        ResultState.Success(Pair(res.data, "days"))
                    } else {
                        ResultState.Error(res.message.toString())
                    }
                }

                ApiResult.Status.API_ERROR, ApiResult.Status.NETWORK_ERROR -> {
                    ResultState.Error(res.message.toString())
                }

                ApiResult.Status.LOADING -> {
                    ResultState.Loading
                }
            }
        }
    }

    fun fetchWeekCandle(getChartCandleReq: GetChartCandleReq): Flow<ResultState<Pair<List<BiThumbWeekCandleRes>, String>>> {
        val biThumbWeekCandleReq = BiThumbWeekCandleReq(
            market = getChartCandleReq.market,
            count = getChartCandleReq.count,
            to = getChartCandleReq.to
        )

        return biThumbRepository.fetchWeekCandle(
            biThumbWeekCandleReq = biThumbWeekCandleReq
        ).map { res ->
            when (res.status) {
                ApiResult.Status.SUCCESS -> {
                    if (res.data != null) {
                        ResultState.Success(Pair(res.data, "weeks"))
                    } else {
                        ResultState.Error(res.message.toString())
                    }
                }

                ApiResult.Status.API_ERROR, ApiResult.Status.NETWORK_ERROR -> {
                    ResultState.Error(res.message.toString())
                }

                ApiResult.Status.LOADING -> {
                    ResultState.Loading
                }
            }
        }
    }

    fun fetchMonthCandle(getChartCandleReq: GetChartCandleReq): Flow<ResultState<Pair<List<BiThumbMonthCandleRes>, String>>> {
        val biThumbMonthCandleReq = BiThumbMonthCandleReq(
            market = getChartCandleReq.market,
            count = getChartCandleReq.count,
            to = getChartCandleReq.to
        )

        return biThumbRepository.fetchMonthCandle(
            biThumbMonthCandleReq = biThumbMonthCandleReq
        ).map { res ->
            when (res.status) {
                ApiResult.Status.SUCCESS -> {
                    if (res.data != null) {
                        ResultState.Success(Pair(res.data, "month"))
                    } else {
                        ResultState.Error(res.message.toString())
                    }
                }

                ApiResult.Status.API_ERROR, ApiResult.Status.NETWORK_ERROR -> {
                    ResultState.Error(res.message.toString())
                }

                ApiResult.Status.LOADING -> {
                    ResultState.Loading
                }
            }
        }
    }
}