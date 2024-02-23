package org.jeonfeel.moeuibit2.ui.coindetail.chart.utils

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet
import com.google.gson.Gson
import com.google.gson.JsonArray
import com.orhanobut.logger.Logger
import kotlinx.coroutines.delay
import org.jeonfeel.moeuibit2.MoeuiBitDataStore
import org.jeonfeel.moeuibit2.constants.darkMovingAverageLineColorArray
import org.jeonfeel.moeuibit2.constants.movingAverageLineArray
import org.jeonfeel.moeuibit2.constants.movingAverageLineColorArray
import org.jeonfeel.moeuibit2.data.local.room.entity.MyCoin
import org.jeonfeel.moeuibit2.data.remote.retrofit.model.ChartModel
import org.jeonfeel.moeuibit2.data.repository.local.LocalRepository
import org.jeonfeel.moeuibit2.data.repository.remote.RemoteRepository
import org.jeonfeel.moeuibit2.ui.coindetail.chart.*
import retrofit2.Response
import javax.inject.Inject

class ChartState {
    val isUpdateChart = mutableStateOf(false)
    val candleType = mutableStateOf("1")
    val loadingDialogState = mutableStateOf(false)
    val minuteVisible = mutableStateOf(false)
    val selectedButton = mutableStateOf(MINUTE_SELECT)
    val minuteText = if (MoeuiBitDataStore.isKor) mutableStateOf("1분") else mutableStateOf("1m")
    val loadingOldData = mutableStateOf(false)
    val isLastData = mutableStateOf(false) // 더이상 불러올 과거 데이터가 없는지 FLAG값
}

class Chart @Inject constructor(
    private val remoteRepository: RemoteRepository,
    private val localRepository: LocalRepository
) {
    val state = ChartState()
    var market = ""
    private val gson = Gson()
    private var chartLastData = false
    val candleEntries = ArrayList<CandleEntry>()
    private val movingAverage = ArrayList<GetMovingAverage>()
    private val chartData = ArrayList<ChartModel>()

    lateinit var positiveBarDataSet: BarDataSet
    lateinit var negativeBarDataSet: BarDataSet
    lateinit var candleDataSet: CandleDataSet
    lateinit var addModel: ChartModel

    private var candleEntriesLastPosition = 0
    private var firstCandleUtcTime = ""
    private var kstTime = "" // 캔들 / 바 / 라인 추가를 위한 kstTime

    var purchaseAveragePrice: Float? = null // 매수평균가
    var candlePosition = 0f // 현재 캔들 포지션
    val accData = HashMap<Int, Double>() // 거래량
    val kstDateHashMap = HashMap<Int, String>() // XValueFommater

    private val _chartUpdateMutableLiveData = MutableLiveData<Int>() //차트 업데이트인지 추가인지 판별
    val chartUpdateLiveData: LiveData<Int> get() = _chartUpdateMutableLiveData

    init {
        for (i in movingAverageLineArray) {
            movingAverage.add(GetMovingAverage(i))
        }
    }

    suspend fun requestChartData(
        candleType: String = state.candleType.value, //분봉인지, 일봉인지
        market: String, // 어느 코인인지
    ) {
        state.isUpdateChart.value = false
        state.loadingDialogState.value = true

        val response: Response<JsonArray> = if (candleType.toIntOrNull() == null) {
            delay(100L)
            remoteRepository.getOtherCandleService(candleType, market)
        } else {
            delay(100L)
            remoteRepository.getMinuteCandleService(candleType, market)
        }

        if (response.isSuccessful && (response.body()?.size() ?: JsonArray()) != 0) {
            resetChartData()
            val positiveBarEntries = ArrayList<BarEntry>()
            val negativeBarEntries = ArrayList<BarEntry>()
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
                    Logger.e(model.toString())
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
            candlePosition -= 1f
            positiveBarDataSet = BarDataSet(positiveBarEntries, "")
            negativeBarDataSet = BarDataSet(negativeBarEntries, "")
            candleDataSet = CandleDataSet(candleEntries, "")
            state.isUpdateChart.value = true
            state.loadingDialogState.value = false
            _chartUpdateMutableLiveData.value = CHART_INIT
            updateChart(market)
        }
    }

    /**
     * 과거 데이터 불러옴
     */
    suspend fun requestOldData(
        candleType: String = state.candleType.value,
        positiveBarDataSet: IBarDataSet,
        negativeBarDataSet: IBarDataSet,
        candleXMin: Float
    ) {
        val time = firstCandleUtcTime.replace("T", " ")
        if (!chartLastData) {
            state.loadingDialogState.value = true
        }
        val response: Response<JsonArray> = if (candleType.toIntOrNull() == null) {
            remoteRepository.getOtherCandleService(candleType, market, "200", time)
        } else {
            remoteRepository.getMinuteCandleService(candleType, market, "200", time)
        }
        if (response.isSuccessful && (response.body()?.size() ?: JsonArray()) != 0) {
            val chartModelList = response.body() ?: JsonArray()
            val chartModelListSize = chartModelList.size()
            val tempCandleEntries = ArrayList<CandleEntry>()
            val tempPositiveBarEntries = ArrayList<BarEntry>()
            val tempNegativeBarEntries = ArrayList<BarEntry>()
            val positiveBarDataCount = positiveBarDataSet.entryCount
            val negativeBarDataCount = negativeBarDataSet.entryCount
            var tempCandlePosition = candleXMin - chartModelListSize
            firstCandleUtcTime =
                gson.fromJson(
                    chartModelList[chartModelListSize - 1],
                    ChartModel::class.java
                ).candleDateTimeUtc

            for (i in chartModelListSize - 1 downTo 0) {
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
            tempCandleEntries.addAll(candleEntries)
            candleEntries.clear()
            candleEntries.addAll(tempCandleEntries)
            for (i in 0 until positiveBarDataCount) {
                tempPositiveBarEntries.add(positiveBarDataSet.getEntryForIndex(i))
            }
            for (i in 0 until negativeBarDataCount) {
                tempNegativeBarEntries.add(negativeBarDataSet.getEntryForIndex(i))
            }
            this.positiveBarDataSet = BarDataSet(tempPositiveBarEntries, "")
            this.negativeBarDataSet = BarDataSet(tempNegativeBarEntries, "")
            this.candleDataSet = CandleDataSet(candleEntries, "")
            candleEntriesLastPosition = candleEntries.size - 1
            state.loadingDialogState.value = false
            _chartUpdateMutableLiveData.value = CHART_OLD_DATA
        } else {
            state.isLastData.value = true
            state.loadingDialogState.value = false
            state.loadingOldData.value = false
        }
    }

    /**
     * 차트 업데이터 BUT 레트로핏을 사용해서 업데이트한다. 웹소켓 문제 때문에..
     */
    private suspend fun updateChart(market: String) {
        while (state.isUpdateChart.value) {
            val response: Response<JsonArray> = if (state.candleType.value.toIntOrNull() == null) {
                remoteRepository.getOtherCandleService(state.candleType.value, market, "1")
            } else {
                remoteRepository.getMinuteCandleService(state.candleType.value, market, "1")
            }

            if (response.isSuccessful && (response.body()?.size() ?: JsonArray()) != 0) {
                val newData = response.body()
                val model = gson.fromJson(newData!!.first(), ChartModel::class.java)
                addModel = model
                if (kstTime != model.candleDateTimeKst) {
                    state.isUpdateChart.value = false
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
                    accData[candlePosition.toInt()] = model.candleAccTradePrice
                    _chartUpdateMutableLiveData.postValue(CHART_ADD)
                    state.isUpdateChart.value = true
                } else {
                    candleEntries[candleEntries.lastIndex] =
                        CandleEntry(
                            candlePosition,
                            model.highPrice.toFloat(),
                            model.lowPrice.toFloat(),
                            model.openingPrice.toFloat(),
                            model.tradePrice.toFloat()
                        )
                    accData[candlePosition.toInt()] = model.candleAccTradePrice
                    _chartUpdateMutableLiveData.postValue(CHART_SET_ALL)
                }
            }
            delay(600)
        }
    }

    /**
     * 실시간 차트 업데이트
     */
    fun updateCandleTicker(tradePrice: Double) {
        if (state.isUpdateChart.value && candleEntries.isNotEmpty()) {
            candleEntries[candleEntriesLastPosition].close = tradePrice.toFloat()
            try {
                modifyLineData()
            } catch (e: Exception) {

            }
            _chartUpdateMutableLiveData.postValue(CHART_SET_CANDLE)
        }
    }

    /**
     * 이동평균선 만든다
     */
    fun createLineData(): LineData {
        val lineData = LineData()

        for (i in movingAverage) {
            i.createLineData(candleEntries)
        }

        for (i in movingAverage.indices) {
            lineData.addDataSet(
                LineDataSet(movingAverage[i].lineEntry, "").apply {
                    defaultSet(
                        darkMovingAverageLineColorArray[i]
                    )
                }
            )
        }

        return lineData
    }

    /**
     * 이평선 수정
     */
    private fun modifyLineData() {
        val lastCandle = candleEntries.last()
        for (i in movingAverage) {
            i.modifyLineData(lastCandle)
        }
    }

    /**
     * 이평선 추가
     */
    fun addLineData() {
        val lastCandle = candleEntries.last()
        for (i in movingAverage) {
            i.addLineData(lastCandle)
        }
    }

    /**
     * 차트 초기화
     */
    private fun resetChartData() {
        candlePosition = 0f
        candleEntriesLastPosition = 0
        chartData.clear()
        candleEntries.clear()
        kstDateHashMap.clear()
    }

    private suspend fun getChartCoinPurchaseAverage(market: String): MyCoin? {
        return localRepository.getMyCoinDao().isInsert(market)
    }

    fun getLastCandleEntry() = candleEntries.last()
    fun isCandleEntryEmpty() = candleEntries.isEmpty()
}