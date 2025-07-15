package org.jeonfeel.moeuibit2.data.usecase

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.jeonfeel.moeuibit2.constants.KeyConst.PREF_KEY_CHART_LAST_PERIOD
import org.jeonfeel.moeuibit2.data.local.preferences.PreferencesManager
import org.jeonfeel.moeuibit2.data.local.room.entity.MyCoin
import org.jeonfeel.moeuibit2.data.network.retrofit.ApiResult
import org.jeonfeel.moeuibit2.data.network.retrofit.request.bithumb.BiThumbMinuteCandleReq
import org.jeonfeel.moeuibit2.data.network.retrofit.request.upbit.GetChartCandleReq
import org.jeonfeel.moeuibit2.data.network.retrofit.response.bitthumb.toChartCandle
import org.jeonfeel.moeuibit2.data.repository.local.LocalRepository
import org.jeonfeel.moeuibit2.data.repository.network.UpbitRepository
import org.jeonfeel.moeuibit2.ui.base.BaseUseCase
import org.jeonfeel.moeuibit2.ui.coindetail.chart.ui.DAY_SELECT
import org.jeonfeel.moeuibit2.ui.coindetail.chart.ui.MINUTE_SELECT
import org.jeonfeel.moeuibit2.ui.coindetail.chart.ui.MONTH_SELECT
import org.jeonfeel.moeuibit2.ui.coindetail.chart.ui.WEEK_SELECT
import org.jeonfeel.moeuibit2.ui.coindetail.chart.utils.bithumb.CommonChartModel
import org.jeonfeel.moeuibit2.ui.coindetail.new_chart.model.ChartCombineModel
import org.jeonfeel.moeuibit2.ui.coindetail.new_chart.util.NewChartUtils
import org.jeonfeel.moeuibit2.ui.common.ResultState
import javax.inject.Inject

class UpbitChartUseCase @Inject constructor(
    private val upbitRepository: UpbitRepository,
    private val localRepository: LocalRepository,
    private val preferenceManager: PreferencesManager
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

    suspend fun fetchMinuteChartData(getChartCandleReq: GetChartCandleReq): Flow<ResultState<ChartCombineModel>> {
        return upbitRepository.fetchMinuteCandle(
            minute = getChartCandleReq.candleType,
            market = getChartCandleReq.market,
            count = getChartCandleReq.count,
            time = getChartCandleReq.to
        ).map { res ->
            when (res.status) {
                ApiResult.Status.SUCCESS -> {
                    if (res.data != null) {
                        val commonChartModelList = res.data.map { it.mapToCommonChartModel() }
                        val (candleEntries, volumeEntries) =
                            NewChartUtils.createEntryLists(commonChartModelList)

                        ResultState.Success(
                            ChartCombineModel(
                                candleEntries = candleEntries,
                                volumeEntries = volumeEntries,
                                commonChartDataList = commonChartModelList
                            )
                        )
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

    suspend fun fetchOtherChartData(getChartCandleReq: GetChartCandleReq): Flow<ResultState<ChartCombineModel>> {
        return upbitRepository.fetchOtherCandle(
            minute = getChartCandleReq.candleType,
            market = getChartCandleReq.market,
            count = getChartCandleReq.count,
            time = getChartCandleReq.to
        ).map { res ->
            when (res.status) {
                ApiResult.Status.SUCCESS -> {
                    if (res.data != null) {
                        val commonChartModelList = res.data.map { it.mapToCommonChartModel() }
                        val (candleEntries, volumeEntries) =
                            NewChartUtils.createEntryLists(commonChartModelList)

                        ResultState.Success(
                            ChartCombineModel(
                                candleEntries = candleEntries,
                                volumeEntries = volumeEntries,
                                commonChartDataList = commonChartModelList
                            )
                        )
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

    fun fetchUserLastSettingCandleType(): Flow<Triple<String, Int, String>> {
        return preferenceManager.getString(PREF_KEY_CHART_LAST_PERIOD).map {
            when {
                it.isEmpty() -> {
                    Triple("1", MINUTE_SELECT, "1분")
                }

                it.toIntOrNull() is Int -> {
                    Triple(it, MINUTE_SELECT, "${it}분")
                }

                else -> {
                    val selectedType = when (it) {
                        "days" -> DAY_SELECT
                        "weeks" -> WEEK_SELECT
                        "months" -> MONTH_SELECT
                        else -> MINUTE_SELECT
                    }
                    Triple(it, selectedType, "분")
                }
            }
        }
    }

    suspend fun fetchUserHoldCoin(market: String, exchange: String): MyCoin? {
        return localRepository.getMyCoinDao().getCoin(market, exchange)
    }
}