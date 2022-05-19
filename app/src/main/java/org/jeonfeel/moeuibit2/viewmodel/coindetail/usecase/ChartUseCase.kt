package org.jeonfeel.moeuibit2.viewmodel.coindetail.usecase

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.github.mikephil.charting.charts.CombinedChart
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.CandleDataSet
import com.github.mikephil.charting.data.CandleEntry
import com.google.gson.Gson
import com.google.gson.JsonArray
import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.delay
import org.jeonfeel.moeuibit2.data.remote.retrofit.model.ChartModel
import org.jeonfeel.moeuibit2.repository.remote.RemoteRepository
import org.jeonfeel.moeuibit2.ui.coindetail.chart.CHART_ADD
import org.jeonfeel.moeuibit2.ui.coindetail.chart.CHART_SET_ALL
import org.jeonfeel.moeuibit2.ui.coindetail.chart.CHART_SET_CANDLE
import org.jeonfeel.moeuibit2.ui.coindetail.chart.MINUTE_SELECT
import org.jeonfeel.moeuibit2.util.XAxisValueFormatter
import org.jeonfeel.moeuibit2.util.chartRefreshLoadMoreData
import org.jeonfeel.moeuibit2.util.chartRefreshSetting
import org.jeonfeel.moeuibit2.util.initCanvas
import retrofit2.Response
import javax.inject.Inject

@ViewModelScoped
class ChartUseCase @Inject constructor(
    private val remoteRepository: RemoteRepository,
    private val xAxisValueFormatter: XAxisValueFormatter,
) : ViewModel() {
    private val gson = Gson()
    private var candleEntriesLastPosition = 0
    private val chartData = ArrayList<ChartModel>()
    private val candleEntries = ArrayList<CandleEntry>()
    private var firstCandleUtcTime = ""
    private var kstTime = ""
    var isUpdateChart = true
    var chartLastData = false
    var loadingMoreChartData = false
    var candlePosition = 0f
    val candleType = mutableStateOf("1")
    val dialogState = mutableStateOf(false)
    val minuteVisible = mutableStateOf(false)
    val minuteText = mutableStateOf("1분")
    val selectedButton = mutableStateOf(MINUTE_SELECT)
    val kstDateHashMap = HashMap<Int, String>()
    val accData = HashMap<Int, Double>()

    private val _candleUpdateMutableLiveData = MutableLiveData<Int>()
    val candleUpdateLiveData: LiveData<Int> get() = _candleUpdateMutableLiveData

    suspend fun requestChartData(
        candleType: String = this.candleType.value,
        combinedChart: CombinedChart,
        market: String,
    ) {
        isUpdateChart = false
        dialogState.value = true
        val response: Response<JsonArray> = if (candleType.toIntOrNull() == null) {
            remoteRepository.getOtherCandleService(candleType, market)
        } else {
            remoteRepository.getMinuteCandleService(candleType, market)
        }
        if (response.isSuccessful && response.body()?.size() ?: JsonArray() != 0) {
            val positiveBarEntries = ArrayList<BarEntry>()
            val negativeBarEntries = ArrayList<BarEntry>()
            candlePosition = 0f
            candleEntriesLastPosition = 0
            chartData.clear()
            candleEntries.clear()
            kstDateHashMap.clear()
            val chartModelList = response.body() ?: JsonArray()
            if (chartModelList.size() != 0) {
                val indices = chartModelList.size()
                firstCandleUtcTime =
                    gson.fromJson(
                        chartModelList[indices - 1],
                        ChartModel::class.java
                    ).candleDateTimeUtc

                kstTime = gson.fromJson(
                    chartModelList[0],
                    ChartModel::class.java
                ).candleDateTimeKst

                for (i in indices - 1 downTo 0) {
                    val model = gson.fromJson(chartModelList[i], ChartModel::class.java)
                    candleEntries.add(
                        CandleEntry(
                            candlePosition,
                            model.highPrice.toFloat(),
                            model.lowPrice.toFloat(),
                            model.openingPrice.toFloat(),
                            model.tradePrice.toFloat()
                        )
                    )
                    if (model.tradePrice - model.openingPrice >= 0.0) {
                        positiveBarEntries.add(
                            BarEntry(candlePosition, model.candleAccTradePrice.toFloat())
                        )
                    } else {
                        negativeBarEntries.add(
                            BarEntry(candlePosition, model.candleAccTradePrice.toFloat())
                        )
                    }

                    kstDateHashMap[candlePosition.toInt()] = model.candleDateTimeKst
                    accData[candlePosition.toInt()] = model.candleAccTradePrice
                    candlePosition += 1f
                    candleEntriesLastPosition = candleEntries.size - 1
                }
            } else {
                //TODO
            }
            this@ChartUseCase.xAxisValueFormatter.setItem(kstDateHashMap)
            candlePosition -= 1f
            combinedChart.chartRefreshSetting(
                candleEntries,
                CandleDataSet(candleEntries, ""),
                BarDataSet(positiveBarEntries, ""),
                BarDataSet(negativeBarEntries, ""),
                this@ChartUseCase.xAxisValueFormatter
            )
            isUpdateChart = true
            dialogState.value = false
            combinedChart.initCanvas()
            updateChart(combinedChart, market)
        }
    }

    suspend fun requestMoreData(
        candleType: String = this.candleType.value,
        combinedChart: CombinedChart,
        market: String,
    ) {
        loadingMoreChartData = true
        val time = firstCandleUtcTime.replace("T", " ")
        if (!chartLastData) {
            dialogState.value = true
        }
        val response: Response<JsonArray> = if (candleType.toIntOrNull() == null) {
            remoteRepository.getOtherCandleService(candleType, market, "200", time)
        } else {
            remoteRepository.getMinuteCandleService(candleType, market, "200", time)
        }
        if (response.isSuccessful && response.body()?.size() ?: JsonArray() != 0) {
            val startPosition = combinedChart.lowestVisibleX
            val currentVisible = combinedChart.visibleXRange
            val tempCandleEntries = ArrayList<CandleEntry>()
            val tempPositiveBarEntries = ArrayList<BarEntry>()
            val tempNegativeBarEntries = ArrayList<BarEntry>()
            val chartModelList = response.body() ?: JsonArray()
            val indices = chartModelList.size()
            val positiveBarDataIndex = combinedChart.barData.dataSets[0].entryCount
            val negativeBarDataIndex = combinedChart.barData.dataSets[1].entryCount
            val tempPositiveBarDataSet = combinedChart.barData.dataSets[0]
            val tempNegativeBarDataSet = combinedChart.barData.dataSets[1]

            var tempCandlePosition = combinedChart.data.candleData.xMin - indices
            firstCandleUtcTime =
                gson.fromJson(
                    chartModelList[indices - 1],
                    ChartModel::class.java
                ).candleDateTimeUtc

            for (i in indices - 1 downTo 0) {
                val model = gson.fromJson(chartModelList[i], ChartModel::class.java)
                tempCandleEntries.add(
                    CandleEntry(
                        tempCandlePosition,
                        model.highPrice.toFloat(),
                        model.lowPrice.toFloat(),
                        model.openingPrice.toFloat(),
                        model.tradePrice.toFloat()
                    )
                )

                if (model.tradePrice - model.openingPrice >= 0.0) {
                    tempPositiveBarEntries.add(
                        BarEntry(tempCandlePosition, model.candleAccTradePrice.toFloat())
                    )
                } else {
                    tempNegativeBarEntries.add(
                        BarEntry(tempCandlePosition, model.candleAccTradePrice.toFloat())
                    )
                }

                kstDateHashMap[tempCandlePosition.toInt()] = model.candleDateTimeKst
                accData[tempCandlePosition.toInt()] = model.candleAccTradePrice
                tempCandlePosition += 1f
            }
            xAxisValueFormatter.setItem(kstDateHashMap)
            tempCandleEntries.addAll(candleEntries)
            candleEntries.clear()
            candleEntries.addAll(tempCandleEntries)
            for (i in 0 until positiveBarDataIndex) {
                tempPositiveBarEntries.add(tempPositiveBarDataSet.getEntryForIndex(i))
            }
            for (i in 0 until negativeBarDataIndex) {
                tempNegativeBarEntries.add(tempNegativeBarDataSet.getEntryForIndex(i))
            }
            val positiveBarDataSet = BarDataSet(tempPositiveBarEntries, "")
            val negativeBarDataSet = BarDataSet(tempNegativeBarEntries, "")
            candleEntriesLastPosition = candleEntries.size - 1

            val candleDataSet = CandleDataSet(candleEntries, "")
            combinedChart.chartRefreshLoadMoreData(
                candleDataSet,
                positiveBarDataSet,
                negativeBarDataSet,
                startPosition,
                currentVisible
            )
            dialogState.value = false
            loadingMoreChartData = false
        } else {
            chartLastData = true
            dialogState.value = false
            loadingMoreChartData = false
        }
    }

    fun updateCandleTicker(tradePrice: Double) {
        if (isUpdateChart && candleEntries.isNotEmpty()) {
            candleEntries[candleEntriesLastPosition].close = tradePrice.toFloat()
            _candleUpdateMutableLiveData.postValue(CHART_SET_CANDLE)
        }
    }

    private suspend fun updateChart(combinedChart: CombinedChart, market: String) {
        while (isUpdateChart) {
            val response: Response<JsonArray> = if (candleType.value.toIntOrNull() == null) {
                remoteRepository.getOtherCandleService(candleType.value, market, "1")
            } else {
                remoteRepository.getMinuteCandleService(candleType.value, market, "1")
            }
            if (response.isSuccessful && response.body()?.size() ?: JsonArray() != 0) {
                val newData = response.body()
                val model = gson.fromJson(newData!!.first(), ChartModel::class.java)
                if (kstTime != model.candleDateTimeKst) {
                    isUpdateChart = false
                    candleEntries.add(
                        CandleEntry(
                            candlePosition,
                            model.highPrice.toFloat(),
                            model.lowPrice.toFloat(),
                            model.openingPrice.toFloat(),
                            model.tradePrice.toFloat()
                        )
                    )
                    kstTime = model.candleDateTimeKst
                    candlePosition += 1f
                    candleEntriesLastPosition += 1
                    kstDateHashMap[candlePosition.toInt()] = kstTime
                    xAxisValueFormatter.addItem(kstTime, candlePosition.toInt())
                    accData[candlePosition.toInt()] = model.candleAccTradePrice

                    if (model.tradePrice - model.openingPrice >= 0.0) {
                        combinedChart.barData.dataSets[0].addEntry(BarEntry(candlePosition,
                            model.candleAccTradePrice.toFloat()))
                    } else {
                        combinedChart.barData.dataSets[1].addEntry(BarEntry(candlePosition,
                            model.candleAccTradePrice.toFloat()))
                    }
                    combinedChart.barData.notifyDataChanged()
                    combinedChart.data.notifyDataChanged()
                    combinedChart.notifyDataSetChanged()
                    _candleUpdateMutableLiveData.postValue(CHART_ADD)
                    isUpdateChart = true
                } else {
                    val last = candleEntries.lastIndex
                    candleEntries[last] =
                        CandleEntry(
                            candlePosition,
                            model.highPrice.toFloat(),
                            model.lowPrice.toFloat(),
                            model.openingPrice.toFloat(),
                            model.tradePrice.toFloat()
                        )
                    accData[candlePosition.toInt()] = model.candleAccTradePrice
                    updateBar(combinedChart)
                    _candleUpdateMutableLiveData.postValue(CHART_SET_ALL)
                }
            }
            delay(600)
        }
    }

    private fun updateBar(combinedChart: CombinedChart) {
        if (isUpdateChart) {
            val positiveBarLast =
                combinedChart.barData.dataSets[0].getEntriesForXValue(candlePosition) ?: emptyList()
            val negativeBarLast =
                combinedChart.barData.dataSets[1].getEntriesForXValue(candlePosition) ?: emptyList()
            val positiveDataSet = combinedChart.barData.dataSets[0]
            val negativeDataSet = combinedChart.barData.dataSets[1]
            val barEntry = BarEntry(candlePosition, accData[candlePosition.toInt()]!!.toFloat())
            if (candleEntries.last().close - candleEntries.last().open >= 0.0) {
                if (positiveBarLast.isNotEmpty()) {
                    positiveDataSet.removeLast()
                    positiveDataSet.addEntry(barEntry)
                } else if (positiveBarLast.isEmpty()) {
                    positiveDataSet.addEntry(barEntry)
                    negativeDataSet.removeLast()
                }
            } else {
                if (negativeBarLast.isNotEmpty()) {
                    negativeDataSet.removeLast()
                    negativeDataSet.addEntry(barEntry)
                } else if (negativeBarLast.isEmpty()) {
                    negativeDataSet.addEntry(barEntry)
                    positiveDataSet.removeLast()
                }
            }
            combinedChart.barData.notifyDataChanged()
            combinedChart.data.notifyDataChanged()
            combinedChart.notifyDataSetChanged()
        }
    }

    fun getCandleEntryLast(): CandleEntry {
        return candleEntries.last()
    }

    fun candleEntriesIsEmpty(): Boolean = candleEntries.isEmpty()
}