package org.jeonfeel.moeuibit2.data.usecase

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.jeonfeel.moeuibit2.constants.EXCHANGE_BITTHUMB
import org.jeonfeel.moeuibit2.data.local.room.entity.MyCoin
import org.jeonfeel.moeuibit2.data.network.retrofit.ApiResult
import org.jeonfeel.moeuibit2.data.network.retrofit.request.bithumb.BiThumbDayCandleReq
import org.jeonfeel.moeuibit2.data.network.retrofit.request.bithumb.BiThumbMinuteCandleReq
import org.jeonfeel.moeuibit2.data.network.retrofit.request.bithumb.BiThumbMonthCandleReq
import org.jeonfeel.moeuibit2.data.network.retrofit.request.bithumb.BiThumbWeekCandleReq
import org.jeonfeel.moeuibit2.data.network.retrofit.request.upbit.GetChartCandleReq
import org.jeonfeel.moeuibit2.data.network.retrofit.response.bitthumb.toChartCandle
import org.jeonfeel.moeuibit2.data.repository.local.LocalRepository
import org.jeonfeel.moeuibit2.data.repository.network.BiThumbRepository
import org.jeonfeel.moeuibit2.ui.coindetail.chart.utils.bithumb.CommonChartModel
import org.jeonfeel.moeuibit2.ui.common.ResultState

class BiThumbChartUsecase(
    private val biThumbRepository: BiThumbRepository,
    private val localRepository: LocalRepository
) {
    fun fetchMinuteChartData(getChartCandleReq: GetChartCandleReq): Flow<ResultState<List<CommonChartModel>>> {
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
                        ResultState.Success(res.data.map { it.toChartCandle() })
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

    fun fetchDayCandle(getChartCandleReq: GetChartCandleReq): Flow<ResultState<List<CommonChartModel>>> {
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
                        ResultState.Success(res.data.map { it.toChartCandle() })
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

    fun fetchWeekCandle(getChartCandleReq: GetChartCandleReq): Flow<ResultState<List<CommonChartModel>>> {
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
                        ResultState.Success(res.data.map { it.toChartCandle() })
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

    fun fetchMonthCandle(getChartCandleReq: GetChartCandleReq): Flow<ResultState<List<CommonChartModel>>> {
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
                        ResultState.Success(res.data.map { it.toChartCandle() })
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

    suspend fun getChartCoinPurchaseAverage(market: String): MyCoin? {
        return localRepository.getMyCoinDao().getCoin(market, exchange = EXCHANGE_BITTHUMB)
    }
}