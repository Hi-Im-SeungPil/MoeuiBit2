package org.jeonfeel.moeuibit2.ui.coindetail.new_chart

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.CandleEntry
import kotlinx.coroutines.flow.Flow
import org.jeonfeel.moeuibit2.constants.EXCHANGE_UPBIT
import org.jeonfeel.moeuibit2.data.network.retrofit.request.upbit.GetChartCandleReq
import org.jeonfeel.moeuibit2.data.usecase.UpbitChartUseCase
import org.jeonfeel.moeuibit2.ui.coindetail.chart.ui.MINUTE_SELECT
import org.jeonfeel.moeuibit2.ui.coindetail.chart.utils.bithumb.CommonChartModel
import org.jeonfeel.moeuibit2.ui.coindetail.new_chart.model.ChartCombineModel
import org.jeonfeel.moeuibit2.ui.common.ResultState

enum class ChartReqType {
    CHART_UPDATE, CHART_GET, CHART_OLD_DATA
}

class NewUpbitChart(
    private val upbitChartUseCase: UpbitChartUseCase,
) {
    private val _commonEntries = mutableListOf<CommonChartModel>()
    val commonEntries: List<CommonChartModel> get() = _commonEntries

    private val _candleEntries = mutableListOf<CandleEntry>()
    val candleEntries: List<CandleEntry> get() = _candleEntries

    private val _volumeEntries = mutableListOf<BarEntry>()
    val volumeEntries: List<BarEntry> get() = _volumeEntries

    private val _chartTypeText: MutableState<String> = mutableStateOf("")
    val chartTypeText: State<String> get() = _chartTypeText

    private val _chartType: MutableState<String> = mutableStateOf("")
    val chartType: State<String> get() = _chartType

    private val _selectedCandleType: MutableState<Int> = mutableIntStateOf(MINUTE_SELECT)
    val selectedCandleType: State<Int> get() = _selectedCandleType

    suspend fun init(market: String) {
        fetchUserLastSettingCandleType()
        upbitChartUseCase.fetchUserHoldCoin(market = market, exchange = EXCHANGE_UPBIT)
    }

    private suspend fun fetchUserLastSettingCandleType() {
        upbitChartUseCase.fetchUserLastSettingCandleType().collect {
            val (candleType, selectedType, text) = it

            _chartType.value = candleType
            _selectedCandleType.value = selectedType
            _chartTypeText.value = text
        }
    }

    suspend fun fetchChartData(market: String, chartReqType: ChartReqType) {
        val getChartCandleReq = createGetChartCandleReq(market = market, reqType = chartReqType)

        getFetchChartDataType(getChartCandleReq).collect {
            when (it) {
                is ResultState.Success -> {
                    processingChartData(chartReqType = chartReqType, data = it.data)
                }

                is ResultState.Error -> {

                }

                is ResultState.Loading -> {

                }
            }
        }
    }

    private fun processingChartData(chartReqType: ChartReqType, data: ChartCombineModel) {
        when (chartReqType) {
            ChartReqType.CHART_GET -> {
                clearChartData()
                _commonEntries.addAll(data.commonChartDataList)
                _candleEntries.addAll(data.candleEntries)
                _volumeEntries.addAll(data.volumeEntries)
            }

            ChartReqType.CHART_UPDATE -> {

            }

            ChartReqType.CHART_OLD_DATA -> {

            }
        }
    }

    private suspend fun getFetchChartDataType(getChartCandleReq: GetChartCandleReq): Flow<ResultState<ChartCombineModel>> {
        return if (chartType.value.toIntOrNull() != null) {
            upbitChartUseCase.fetchMinuteChartData(getChartCandleReq)
        } else {
            upbitChartUseCase.fetchOtherChartData(getChartCandleReq)
        }
    }

    private fun createGetChartCandleReq(market: String, reqType: ChartReqType): GetChartCandleReq {
        return GetChartCandleReq(
            market = market,
            candleType = chartType.value,
            to = getTimeStringForReq(reqType), // 현재 시간 기준 or 과거 시간 기준
            count = when (reqType) {
                ChartReqType.CHART_UPDATE -> "1"
                else -> "200"
            }
        )
    }

    private fun getTimeStringForReq(reqType: ChartReqType): String {
        val firstCandleUtcTime = commonEntries.first().candleDateTimeUtc

        return when (reqType) {
            ChartReqType.CHART_OLD_DATA -> firstCandleUtcTime.replace("T", " ")
            else -> ""
        }
    }

    private fun clearChartData() {
        _commonEntries.clear()
        _candleEntries.clear()
        _volumeEntries.clear()
    }
}