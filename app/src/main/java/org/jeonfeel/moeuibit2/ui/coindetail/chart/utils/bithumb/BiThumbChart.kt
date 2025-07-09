package org.jeonfeel.moeuibit2.ui.coindetail.chart.utils.bithumb

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.CandleDataSet
import com.github.mikephil.charting.data.CandleEntry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet
import com.orhanobut.logger.Logger
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.jeonfeel.moeuibit2.constants.KeyConst
import org.jeonfeel.moeuibit2.constants.darkMovingAverageLineColorArray
import org.jeonfeel.moeuibit2.constants.ioDispatcher
import org.jeonfeel.moeuibit2.constants.movingAverageLineArray
import org.jeonfeel.moeuibit2.data.local.preferences.PreferencesManager
import org.jeonfeel.moeuibit2.data.local.room.entity.MyCoin
import org.jeonfeel.moeuibit2.data.network.retrofit.model.upbit.ChartModel
import org.jeonfeel.moeuibit2.data.network.retrofit.request.upbit.GetChartCandleReq
import org.jeonfeel.moeuibit2.data.usecase.BiThumbChartUsecase
import org.jeonfeel.moeuibit2.ui.coindetail.chart.ui.CHART_ADD
import org.jeonfeel.moeuibit2.ui.coindetail.chart.ui.CHART_INIT
import org.jeonfeel.moeuibit2.ui.coindetail.chart.ui.CHART_OLD_DATA
import org.jeonfeel.moeuibit2.ui.coindetail.chart.ui.CHART_SET_ALL
import org.jeonfeel.moeuibit2.ui.coindetail.chart.ui.CHART_SET_CANDLE
import org.jeonfeel.moeuibit2.ui.coindetail.chart.ui.DAY_SELECT
import org.jeonfeel.moeuibit2.ui.coindetail.chart.ui.MINUTE_SELECT
import org.jeonfeel.moeuibit2.ui.coindetail.chart.ui.MONTH_SELECT
import org.jeonfeel.moeuibit2.ui.coindetail.chart.ui.WEEK_SELECT
import org.jeonfeel.moeuibit2.ui.coindetail.chart.utils.GetMovingAverage
import org.jeonfeel.moeuibit2.ui.coindetail.chart.utils.defaultSet
import org.jeonfeel.moeuibit2.ui.coindetail.chart.utils.upbit.BaseChartState
import org.jeonfeel.moeuibit2.ui.common.ResultState
import org.jeonfeel.moeuibit2.utils.NetworkConnectivityObserver
import javax.inject.Inject

class BithumbChartState : BaseChartState() {
}

class BiThumbChart @Inject constructor(
    private val biThumbChartUsecase: BiThumbChartUsecase,
    private val preferenceManager: PreferencesManager
) {
    val state = BithumbChartState()
    var market = ""

    private var firstInit = true
    private var chartLastData = false

    private val _candleEntries = ArrayList<CandleEntry>()
    val candleEntries: List<CandleEntry> get() = _candleEntries

    private val movingAverage = ArrayList<GetMovingAverage>()
    private val chartData = ArrayList<ChartModel>()

    lateinit var positiveBarDataSet: BarDataSet
    lateinit var negativeBarDataSet: BarDataSet
    lateinit var candleDataSet: CandleDataSet
    lateinit var addModel: ChartModel

    private var candleEntriesLastPosition = 0
    private var firstCandleUtcTime = ""
    private var kstTime = ""

    var purchaseAveragePrice: Float? = null
    var candlePosition = 0f
    val accData = HashMap<Int, Double>()
    val kstDateHashMap = HashMap<Int, String>()

    private val _chartUpdateMutableLiveData = MutableLiveData<Int>()
    val chartUpdateLiveData: LiveData<Int> get() = _chartUpdateMutableLiveData

    private var chartUpdateJob: Job? = null

    init {
        for (i in movingAverageLineArray) {
            movingAverage.add(GetMovingAverage(i))
        }
    }

    suspend fun refresh(candleType: String = state.candleType.value, market: String) {
        if (!NetworkConnectivityObserver.isNetworkAvailable.value) return

        if (firstInit) {
            firstInit = false
            preferenceManager.getString(KeyConst.PREF_KEY_CHART_LAST_PERIOD).collect {
                state.candleType.value = it
                when {
                    it.isEmpty() -> {
                        state.candleType.value = "1"
                        state.selectedButton.value = MINUTE_SELECT
                        state.minuteText.value = "1분"
                    }

                    it.toIntOrNull() is Int -> {
                        state.minuteText.value = it + "분"
                    }

                    else -> {
                        state.minuteText.value = "분"
                        state.selectedButton.value = when (it) {
                            "days" -> DAY_SELECT
                            "weeks" -> WEEK_SELECT
                            "months" -> MONTH_SELECT
                            else -> MINUTE_SELECT
                        }
                    }
                }
                chartUpdateJob?.cancelAndJoin()
                getUserCoinPurchaseAverage(market)
                newRequestBithumbChartData(state.candleType.value, market)
            }
        } else {
            chartUpdateJob?.cancelAndJoin()
            getUserCoinPurchaseAverage(market)
            newRequestBithumbChartData(candleType, market)
        }
    }

    private suspend fun newRequestBithumbChartData(
        candleType: String = state.candleType.value,
        market: String
    ) {
        if (!NetworkConnectivityObserver.isNetworkAvailable.value) return

        state.isUpdateChart.value = false
        state.loadingDialogState.value = true

        val getChartCandleReq = GetChartCandleReq(
            candleType = candleType,
            market = market
        )

        val flow = when (getChartCandleReq.candleType) {
            "days" -> {
                biThumbChartUsecase.fetchDayCandle(getChartCandleReq)
            }

            "weeks" -> {
                biThumbChartUsecase.fetchWeekCandle(getChartCandleReq)
            }

            "months" -> {
                biThumbChartUsecase.fetchMonthCandle(getChartCandleReq)
            }

            else -> {
                biThumbChartUsecase.fetchMinuteChartData(getChartCandleReq = getChartCandleReq)
            }
        }

        flow.collect { res ->
            when (res) {
                is ResultState.Success -> {
                    resetChartData()
                    val positiveBarEntries = ArrayList<BarEntry>()
                    val negativeBarEntries = ArrayList<BarEntry>()
                    val lastIndex = res.data.lastIndex
                    val data = res.data
                    firstCandleUtcTime = data[lastIndex].candleDateTimeUtc
                    kstTime = data.first().candleDateTimeKst

                    data.reversed().forEachIndexed { index, chartInfo ->
                        val candleEntry = CandleEntry(
                            candlePosition,
                            chartInfo.highPrice.toFloat(),
                            chartInfo.lowPrice.toFloat(),
                            chartInfo.openingPrice.toFloat(),
                            chartInfo.tradePrice.toFloat()
                        )
                        _candleEntries.add(candleEntry)

                        val colorFlag = chartInfo.tradePrice - chartInfo.openingPrice
                        if (colorFlag >= 0.0) {
                            positiveBarEntries.add(
                                BarEntry(candlePosition, chartInfo.candleAccTradePrice.toFloat())
                            )
                        } else {
                            negativeBarEntries.add(
                                BarEntry(candlePosition, chartInfo.candleAccTradePrice.toFloat())
                            )
                        }

                        kstDateHashMap[candlePosition.toInt()] = chartInfo.candleDateTimeKst
                        accData[candlePosition.toInt()] = chartInfo.candleAccTradePrice
                        candlePosition += 1f
                        candleEntriesLastPosition = candleEntries.size - 1
                    }

                    candlePosition -= 1f
                    positiveBarDataSet = BarDataSet(positiveBarEntries, "")
                    negativeBarDataSet = BarDataSet(negativeBarEntries, "")
                    candleDataSet = CandleDataSet(candleEntries, "")
                    state.isUpdateChart.value = true
                    state.loadingDialogState.value = false
                    _chartUpdateMutableLiveData.value = CHART_INIT

                    chartUpdateJob = CoroutineScope(ioDispatcher).launch {
                        try {
                            newUpdateChart(market = market)
                        } catch (e: Exception) {
                            Logger.e(e.message.toString())
                        }
                    }.also { it.start() }
                }

                is ResultState.Error -> {
                    Logger.e(res.message)
                }

                is ResultState.Loading -> {

                }
            }
        }
    }

    private suspend fun getUserCoinPurchaseAverage(market: String) {
        val myCoin = getChartCoinPurchaseAverage(market)
        myCoin?.let {
            purchaseAveragePrice = it.purchasePrice.toFloat()
        }
    }

    suspend fun newRequestOldData(
        candleType: String = state.candleType.value,
        market: String,
        positiveBarDataSet: IBarDataSet,
        negativeBarDataSet: IBarDataSet,
        candleXMin: Float
    ) {
        if (!NetworkConnectivityObserver.isNetworkAvailable.value) return

        val time = firstCandleUtcTime.replace("T", " ")
        if (!chartLastData) state.loadingDialogState.value = true
        val getChartCandleReq = GetChartCandleReq(
            candleType = candleType,
            market = market,
            to = time
        )

        val flow = if (candleType.toIntOrNull() == null) {
            when (getChartCandleReq.candleType) {
                "days" -> {
                    biThumbChartUsecase.fetchDayCandle(getChartCandleReq)
                }

                "weeks" -> {
                    biThumbChartUsecase.fetchWeekCandle(getChartCandleReq)
                }

                "months" -> {
                    biThumbChartUsecase.fetchMonthCandle(getChartCandleReq)
                }

                else -> {
                    biThumbChartUsecase.fetchDayCandle(getChartCandleReq)
                }
            }
        } else {
            biThumbChartUsecase.fetchMinuteChartData(getChartCandleReq = getChartCandleReq)
        }

        flow.collect { res ->
            when (res) {
                is ResultState.Success -> {
                    val lastIndex = res.data.lastIndex
                    val data = res.data

                    if (data.isEmpty()) {
                        state.isLastData.value = true
                        state.loadingDialogState.value = false
                        state.loadingOldData.value = false
                        return@collect
                    }

                    firstCandleUtcTime = data[lastIndex].candleDateTimeUtc
                    val tempCandleEntries = ArrayList<CandleEntry>()
                    val tempPositiveBarEntries = ArrayList<BarEntry>()
                    val tempNegativeBarEntries = ArrayList<BarEntry>()
                    var tempCandlePosition = candleXMin - data.size
                    val positiveBarDataCount = positiveBarDataSet.entryCount
                    val negativeBarDataCount = negativeBarDataSet.entryCount

                    data.reversed().forEach {
                        val candleEntry = CandleEntry(
                            tempCandlePosition,
                            it.highPrice.toFloat(),
                            it.lowPrice.toFloat(),
                            it.openingPrice.toFloat(),
                            it.tradePrice.toFloat()
                        )
                        tempCandleEntries.add(candleEntry)

                        val colorFlag = it.tradePrice - it.openingPrice
                        if (colorFlag >= 0.0) {
                            tempPositiveBarEntries.add(
                                BarEntry(tempCandlePosition, it.candleAccTradePrice.toFloat())
                            )
                        } else {
                            tempNegativeBarEntries.add(
                                BarEntry(tempCandlePosition, it.candleAccTradePrice.toFloat())
                            )
                        }
                        kstDateHashMap[tempCandlePosition.toInt()] = it.candleDateTimeKst
                        accData[tempCandlePosition.toInt()] = it.candleAccTradePrice
                        tempCandlePosition += 1f
                    }

                    tempCandleEntries.addAll(_candleEntries)
                    _candleEntries.clear()
                    _candleEntries.addAll(tempCandleEntries)

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
                }

                is ResultState.Error -> {
                    Logger.e(res.message)
                }

                is ResultState.Loading -> {

                }
            }
        }
    }

    private suspend fun newUpdateChart(
        candleType: String = state.candleType.value,
        market: String
    ) {
        while (true) {
            if (!NetworkConnectivityObserver.isNetworkAvailable.value) break

            if (state.isUpdateChart.value) {
                val getChartCandleReq = GetChartCandleReq(
                    candleType = state.candleType.value,
                    market = market,
                    count = "1"
                )

                val flow = if (candleType.toIntOrNull() == null) {
                    when (getChartCandleReq.candleType) {
                        "days" -> {
                            biThumbChartUsecase.fetchDayCandle(getChartCandleReq)
                        }

                        "weeks" -> {
                            biThumbChartUsecase.fetchWeekCandle(getChartCandleReq)
                        }

                        "months" -> {
                            biThumbChartUsecase.fetchMonthCandle(getChartCandleReq)
                        }

                        else -> {
                            biThumbChartUsecase.fetchDayCandle(getChartCandleReq)
                        }
                    }
                } else {
                    biThumbChartUsecase.fetchMinuteChartData(getChartCandleReq = getChartCandleReq)
                }

                flow.collect { res ->
                    when (res) {
                        is ResultState.Success -> {
                            val chartData = res.data.first()
                            when {
                                kstTime != chartData.candleDateTimeKst -> {
                                    state.isUpdateChart.value = false
                                    addModel = ChartModel(
                                        candleDateTimeKst = kstTime,
                                        candleDateTimeUtc = "",
                                        openingPrice = chartData.openingPrice,
                                        highPrice = chartData.highPrice,
                                        lowPrice = chartData.lowPrice,
                                        tradePrice = chartData.tradePrice,
                                        candleAccTradePrice = 0.0,
                                        timestamp = System.currentTimeMillis()
                                    )
                                    val candleEntry = CandleEntry(
                                        candlePosition,
                                        chartData.highPrice.toFloat(),
                                        chartData.lowPrice.toFloat(),
                                        chartData.openingPrice.toFloat(),
                                        chartData.tradePrice.toFloat()
                                    )
                                    _candleEntries.add(candleEntry)
                                    kstTime = chartData.candleDateTimeKst
                                    candlePosition += 1f
                                    candleEntriesLastPosition += 1
                                    kstDateHashMap[candlePosition.toInt()] = kstTime
                                    accData[candlePosition.toInt()] = chartData.candleAccTradePrice
                                    _chartUpdateMutableLiveData.postValue(CHART_ADD)
                                    state.isUpdateChart.value = true
                                }

                                kstTime == chartData.candleDateTimeKst -> {
                                    _candleEntries[candleEntries.lastIndex] =
                                        CandleEntry(
                                            candlePosition,
                                            chartData.highPrice.toFloat(),
                                            chartData.lowPrice.toFloat(),
                                            chartData.openingPrice.toFloat(),
                                            chartData.tradePrice.toFloat()
                                        )
                                    accData[candlePosition.toInt()] = chartData.candleAccTradePrice
                                    _chartUpdateMutableLiveData.postValue(CHART_SET_ALL)
                                }
                            }
                        }

                        is ResultState.Error -> {
                        }

                        is ResultState.Loading -> {
                        }
                    }
                }
            }
            delay(700)
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
                e.printStackTrace()
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
            i.createLineData(_candleEntries)
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
            val yValue = candleEntries[candleEntries.lastIndex - i.getNumber()].y
            i.addLineData(lastCandle, yValue)
        }
    }

    suspend fun saveLastPeriod(period: String) {
        preferenceManager.setValue(KeyConst.PREF_KEY_CHART_LAST_PERIOD, period)
    }

    fun updateCandlePosition(position: Float) {
        candlePosition = position
    }

    /**
     * 차트 초기화
     */
    private fun resetChartData() {
        candlePosition = 0f
        candleEntriesLastPosition = 0
        chartData.clear()
        _candleEntries.clear()
        kstDateHashMap.clear()
    }

    private suspend fun getChartCoinPurchaseAverage(market: String): MyCoin? {
        return biThumbChartUsecase.getChartCoinPurchaseAverage(market)
    }

    fun getLastCandleEntry() = candleEntries.last()
    fun isCandleEntryEmpty() = candleEntries.isEmpty()
}