package org.jeonfeel.moeuibit2.ui.viewmodels

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.github.mikephil.charting.charts.CombinedChart
import com.github.mikephil.charting.data.*
import com.google.gson.Gson
import com.google.gson.JsonArray
import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.delay
import org.jeonfeel.moeuibit2.MoeuiBitDataStore.isKor
import org.jeonfeel.moeuibit2.data.local.room.entity.MyCoin
import org.jeonfeel.moeuibit2.data.remote.retrofit.model.ChartModel
import org.jeonfeel.moeuibit2.data.repository.local.LocalRepository
import org.jeonfeel.moeuibit2.data.repository.remote.RemoteRepository
import org.jeonfeel.moeuibit2.ui.coindetail.chart.*
import org.jeonfeel.moeuibit2.utils.*
import retrofit2.Response
import javax.inject.Inject

@ViewModelScoped
class ChartUseCase @Inject constructor(
    private val remoteRepository: RemoteRepository,
    private val xAxisValueFormatter: XAxisValueFormatter,
    private val localRepository: LocalRepository,
) : ViewModel() {
    private val gson = Gson()
    private val candleEntries = ArrayList<CandleEntry>()
    private val movingAverage5 = GetMovingAverage(5)
    private val movingAverage10 = GetMovingAverage(10)
    private val movingAverage20 = GetMovingAverage(20)
    private val movingAverage60 = GetMovingAverage(60)
    private val movingAverage120 = GetMovingAverage(120)
    private var candleEntriesLastPosition = 0
    private val chartData = ArrayList<ChartModel>()
    private var firstCandleUtcTime = ""
    private var kstTime = "" // 캔들 / 바 / 라인 추가를 위한 kstTime
    private var purchaseAveragePrice: Float? = null // 매수평균가
    var isUpdateChart = true // 차트 작업있을 때 멈춤
    var chartLastData = false // 더이상 불러올 과거 데이터가 없는지 FLAG값
    var loadingMoreChartData = false
    var candlePosition = 0f // 현재 캔들 포지션

    val candleType = mutableStateOf("1")
    val dialogState = mutableStateOf(false)
    val minuteVisible = mutableStateOf(false) // 분봉 메뉴
    val selectedButton = mutableStateOf(MINUTE_SELECT)
    val kstDateHashMap = HashMap<Int, String>() // XValueFommater
    val accData = HashMap<Int, Double>() // 거래량
    val minuteText = if (isKor) mutableStateOf("1분") else mutableStateOf("1m") // 분봉 텍스트

    private val _candleUpdateMutableLiveData = MutableLiveData<Int>() //차트 업데이트인지 추가인지 판별
    val candleUpdateLiveData: LiveData<Int> get() = _candleUpdateMutableLiveData

    /**
     * 초기 차트 데이터 불러옴
     */
    suspend fun requestChartData(
        candleType: String = this.candleType.value, //분봉인지, 일봉인지
        combinedChart: CombinedChart,
        market: String, // 어느 코인인지
    ) {
        isUpdateChart = false
        dialogState.value = true

        val response: Response<JsonArray> = if (candleType.toIntOrNull() == null) {
            delay(100L)
            remoteRepository.getOtherCandleService(candleType, market)
        } else {
            delay(100L)
            remoteRepository.getMinuteCandleService(candleType, market)
        }

        if (response.isSuccessful && (response.body()?.size() ?: JsonArray()) != 0) {
            /**
             * 차트 초기화
             */
            combinedChart.axisRight.removeAllLimitLines()
            combinedChart.xAxis.removeAllLimitLines()
            candlePosition = 0f
            candleEntriesLastPosition = 0
            chartData.clear()
            candleEntries.clear()
            kstDateHashMap.clear()
            val positiveBarEntries = ArrayList<BarEntry>()
            val negativeBarEntries = ArrayList<BarEntry>()

            /**
             * 차트 데이터 불러옴
             */
            val chartModelList = response.body() ?: JsonArray()
            if (chartModelList.size() != 0) {
                val indices = chartModelList.size()
                // 과거 데이터 불러오기 위해
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
            /**
             * 현재 보유 코인인지 있으면 불러옴
             */
            val myCoin = getChartCoinPurchaseAverage(market)
            myCoin?.let {
                purchaseAveragePrice = it.purchasePrice.toFloat()
            }
            this@ChartUseCase.xAxisValueFormatter.setItem(kstDateHashMap)
            candlePosition -= 1f

            combinedChart.chartRefreshSettings(
                candleEntries,
                CandleDataSet(candleEntries, ""),
                BarDataSet(positiveBarEntries, ""),
                BarDataSet(negativeBarEntries, ""),
                LineData(),
                this@ChartUseCase.xAxisValueFormatter,
                purchaseAveragePrice,
                Utils.getSelectedMarket(market)
            )
            isUpdateChart = true
            dialogState.value = false
            combinedChart.initCanvas()
//            updateChart(combinedChart, market)
        }
    }

    /**
     * 과거 데이터 불러옴
     */
    suspend fun requestMoreData(
        candleType: String = this.candleType.value,
        combinedChart: CombinedChart,
        market: String,
    ) {
        loadingMoreChartData = true // 차트 터치 중단
        val time = firstCandleUtcTime.replace("T", " ")
        if (!chartLastData) {
            dialogState.value = true
        }
        val response: Response<JsonArray> = if (candleType.toIntOrNull() == null) {
            remoteRepository.getOtherCandleService(candleType, market, "200", time)
        } else {
            remoteRepository.getMinuteCandleService(candleType, market, "200", time)
        }
        if (response.isSuccessful && (response.body()?.size() ?: JsonArray()) != 0) {
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
//            combinedChart.chartRefreshLoadMoreData(
//                candleDataSet,
//                positiveBarDataSet,
//                negativeBarDataSet,
//                LineData(),
//                startPosition,
//                currentVisible,
//            )
            dialogState.value = false
            loadingMoreChartData = false
        } else {
            chartLastData = true
            dialogState.value = false
            loadingMoreChartData = false
        }
    }

    /**
     * 차트 업데이트
     */
    fun updateCandleTicker(tradePrice: Double) {
        if (isUpdateChart && candleEntries.isNotEmpty()) {
            candleEntries[candleEntriesLastPosition].close = tradePrice.toFloat()
            try {
                modifyLineData()
            } catch (e: Exception) {
                e.printStackTrace()
            }
            _candleUpdateMutableLiveData.postValue(CHART_SET_CANDLE)
        }
    }

    /**
     * 차트 업데이터 BUT 레트로핏을 사용해서 업데이트한다. 웹소켓 문제 때문에..
     */
    private suspend fun updateChart(market: String) {
        while (isUpdateChart) {
            val response: Response<JsonArray> = if (candleType.value.toIntOrNull() == null) {
                remoteRepository.getOtherCandleService(candleType.value, market, "1")
            } else {
                remoteRepository.getMinuteCandleService(candleType.value, market, "1")
            }
            if (response.isSuccessful && (response.body()?.size() ?: JsonArray()) != 0) {
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
                    _candleUpdateMutableLiveData.postValue(CHART_SET_ALL)
                }
            }
            delay(600)
        }
    }

    /**
     * 이동평균선 만든다
     */
//    private fun createLineData(): LineData {
//        movingAverage5.createLineData(candleEntries)
//        movingAverage10.createLineData(candleEntries)
//        movingAverage20.createLineData(candleEntries)
//        movingAverage60.createLineData(candleEntries)
//        movingAverage120.createLineData(candleEntries)

//        val lineDataSet5 =
//            LineDataSet(movingAverage5.lineEntry, "").apply { defaultSet() }
//        val lineDataSet10 =
//            LineDataSet(movingAverage10.lineEntry, "").apply { defaultSet() }
//        val lineDataSet20 =
//            LineDataSet(movingAverage20.lineEntry, "").apply { defaultSet() }
//        val lineDataSet60 =
//            LineDataSet(movingAverage60.lineEntry, "").apply { defaultSet() }
//        val lineDataSet120 =
//            LineDataSet(movingAverage120.lineEntry, "").apply { defaultSet() }
//        val lineData = LineData()
//        lineData.apply {
//            addDataSet(lineDataSet5)
//            addDataSet(lineDataSet10)
//            addDataSet(lineDataSet20)
//            addDataSet(lineDataSet60)
//            addDataSet(lineDataSet120)
//        }

//        return lineData
//    }

    private fun modifyLineData() {
        val lastCandle = candleEntries.last()
        movingAverage5.modifyLineData(lastCandle)
        movingAverage10.modifyLineData(lastCandle)
        movingAverage20.modifyLineData(lastCandle)
        movingAverage60.modifyLineData(lastCandle)
        movingAverage120.modifyLineData(lastCandle)
    }

    private fun addLineData() {
        val lastCandle = candleEntries.last()
        movingAverage5.addLineData(lastCandle)
        movingAverage10.addLineData(lastCandle)
        movingAverage20.addLineData(lastCandle)
        movingAverage60.addLineData(lastCandle)
        movingAverage120.addLineData(lastCandle)
    }

    fun getCandleEntryLast(): CandleEntry {
        return candleEntries.last()
    }

    fun candleEntriesIsEmpty(): Boolean = candleEntries.isEmpty()

    private suspend fun getChartCoinPurchaseAverage(market: String): MyCoin? {
        return localRepository.getMyCoinDao().isInsert(market)
    }
}