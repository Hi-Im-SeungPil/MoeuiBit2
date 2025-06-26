package org.jeonfeel.moeuibit2.ui.coindetail.chart.utils.upbit

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet
import com.orhanobut.logger.Logger
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.jeonfeel.moeuibit2.constants.EXCHANGE_UPBIT
import org.jeonfeel.moeuibit2.constants.KeyConst
import org.jeonfeel.moeuibit2.constants.darkMovingAverageLineColorArray
import org.jeonfeel.moeuibit2.constants.ioDispatcher
import org.jeonfeel.moeuibit2.constants.movingAverageLineArray
import org.jeonfeel.moeuibit2.data.local.room.entity.MyCoin
import org.jeonfeel.moeuibit2.data.network.retrofit.model.upbit.ChartModel
import org.jeonfeel.moeuibit2.data.network.retrofit.request.upbit.GetChartCandleReq
import org.jeonfeel.moeuibit2.data.network.retrofit.response.upbit.GetChartCandleRes
import org.jeonfeel.moeuibit2.data.repository.local.LocalRepository
import org.jeonfeel.moeuibit2.data.usecase.UpbitChartUseCase
import org.jeonfeel.moeuibit2.ui.coindetail.chart.utils.GetMovingAverage
import org.jeonfeel.moeuibit2.ui.coindetail.chart.utils.defaultSet
import org.jeonfeel.moeuibit2.ui.base.BaseCommunicationModule
import org.jeonfeel.moeuibit2.data.local.preferences.PreferencesManager
import org.jeonfeel.moeuibit2.ui.coindetail.chart.ui.CHART_ADD
import org.jeonfeel.moeuibit2.ui.coindetail.chart.ui.CHART_INIT
import org.jeonfeel.moeuibit2.ui.coindetail.chart.ui.CHART_OLD_DATA
import org.jeonfeel.moeuibit2.ui.coindetail.chart.ui.CHART_SET_ALL
import org.jeonfeel.moeuibit2.ui.coindetail.chart.ui.CHART_SET_CANDLE
import org.jeonfeel.moeuibit2.ui.coindetail.chart.ui.DAY_SELECT
import org.jeonfeel.moeuibit2.ui.coindetail.chart.ui.MINUTE_SELECT
import org.jeonfeel.moeuibit2.ui.coindetail.chart.ui.MONTH_SELECT
import org.jeonfeel.moeuibit2.ui.coindetail.chart.ui.WEEK_SELECT
import org.jeonfeel.moeuibit2.utils.NetworkConnectivityObserver
import javax.inject.Inject

class UpbitChartState {
    val isUpdateChart = mutableStateOf(false)
    val candleType = mutableStateOf("1")
    val loadingDialogState = mutableStateOf(false)
    val minuteVisible = mutableStateOf(false)
    val selectedButton = mutableStateOf(MINUTE_SELECT)
    val minuteText = mutableStateOf("1분")
    val loadingOldData = mutableStateOf(false)
    val isLastData = mutableStateOf(false) // 더이상 불러올 과거 데이터가 없는지 FLAG값
}

class UpbitChart @Inject constructor(
    private val localRepository: LocalRepository,
    private val upbitChartUseCase: UpbitChartUseCase,
    private val preferenceManager: PreferencesManager
) : BaseCommunicationModule() {
    val state = UpbitChartState()
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
    private var kstTime = "" // 캔들 / 바 / 라인 추가를 위한 kstTime

    var purchaseAveragePrice: Float? = null // 매수평균가
    var candlePosition = 0f // 현재 캔들 포지션
    val accData = HashMap<Int, Double>() // 거래량
    val kstDateHashMap = HashMap<Int, String>() // XValueFommater

    private val _chartUpdateMutableLiveData = MutableLiveData<Int>() //차트 업데이트인지 추가인지 판별
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
                newRequestUpbitChartData(state.candleType.value, market)
            }
        } else {
            chartUpdateJob?.cancelAndJoin()
            getUserCoinPurchaseAverage(market)
            newRequestUpbitChartData(candleType, market)
        }
    }

    private suspend fun newRequestUpbitChartData(
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

        executeUseCase<List<GetChartCandleRes>>(
            target = if (candleType.toIntOrNull() == null) upbitChartUseCase.getOtherChartData(
                getChartCandleReq = getChartCandleReq
            ) else upbitChartUseCase.getMinuteChartData(getChartCandleReq = getChartCandleReq),
            onLoading = {
                state.loadingDialogState.value = true
            },
            onComplete = { apiResult ->
                resetChartData()
                val positiveBarEntries = ArrayList<BarEntry>()
                val negativeBarEntries = ArrayList<BarEntry>()
                firstCandleUtcTime = apiResult[apiResult.lastIndex].candleDateTimeUtc
                kstTime = apiResult.first().candleDateTimeKst
                apiResult.reversed().forEachIndexed { index, chartInfo ->
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
                        newUpdateChart(market)
                    } catch (e: Exception) {
                        Logger.e(e.message.toString())
                    }
                }.also { it.start() }
            },
            onApiError = { apiResult ->
                Logger.e(apiResult.message.toString())
            }
        )
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
        executeUseCase<List<GetChartCandleRes>>(
            target = if (candleType.toIntOrNull() == null) upbitChartUseCase.getOtherChartData(
                getChartCandleReq = getChartCandleReq
            ) else upbitChartUseCase.getMinuteChartData(getChartCandleReq = getChartCandleReq),
            onLoading = {
                state.loadingDialogState.value = true
            },
            onComplete = { apiResult ->
                if (apiResult.isEmpty()) {
                    state.isLastData.value = true
                    state.loadingDialogState.value = false
                    state.loadingOldData.value = false
                    return@executeUseCase
                }

                firstCandleUtcTime = apiResult[apiResult.lastIndex].candleDateTimeUtc
                val tempCandleEntries = ArrayList<CandleEntry>()
                val tempPositiveBarEntries = ArrayList<BarEntry>()
                val tempNegativeBarEntries = ArrayList<BarEntry>()
                var tempCandlePosition = candleXMin - apiResult.size
                val positiveBarDataCount = positiveBarDataSet.entryCount
                val negativeBarDataCount = negativeBarDataSet.entryCount

                apiResult.reversed().forEach {
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
        )
    }

    private suspend fun newUpdateChart(market: String) {
        while (true) {
            if (!NetworkConnectivityObserver.isNetworkAvailable.value) break

            if (state.isUpdateChart.value) {
                val getChartCandleReq = GetChartCandleReq(
                    candleType = state.candleType.value,
                    market = market,
                    count = "1"
                )
                executeUseCase<List<GetChartCandleRes>>(
                    target = if (state.candleType.value.toIntOrNull() == null) upbitChartUseCase.getOtherChartData(
                        getChartCandleReq = getChartCandleReq
                    ) else upbitChartUseCase.getMinuteChartData(getChartCandleReq = getChartCandleReq),
                    onComplete = {
                        val chartData = it.first()
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
                )
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
        return localRepository.getMyCoinDao().getCoin(market, exchange = EXCHANGE_UPBIT)
    }

    fun getLastCandleEntry() = candleEntries.last()
    fun isCandleEntryEmpty() = candleEntries.isEmpty()
}
