package org.jeonfeel.moeuibit2.ui.coindetail.chart.utils.upbit

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet
import com.orhanobut.logger.Logger
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.jeonfeel.moeuibit2.MoeuiBitDataStore
import org.jeonfeel.moeuibit2.constants.KeyConst
import org.jeonfeel.moeuibit2.constants.darkMovingAverageLineColorArray
import org.jeonfeel.moeuibit2.constants.ioDispatcher
import org.jeonfeel.moeuibit2.constants.movingAverageLineArray
import org.jeonfeel.moeuibit2.data.local.room.entity.MyCoin
import org.jeonfeel.moeuibit2.data.network.retrofit.model.upbit.ChartModel
import org.jeonfeel.moeuibit2.data.network.retrofit.request.upbit.GetChartCandleReq
import org.jeonfeel.moeuibit2.data.network.retrofit.response.upbit.GetChartCandleRes
import org.jeonfeel.moeuibit2.data.repository.local.LocalRepository
import org.jeonfeel.moeuibit2.data.repository.network.UpbitRepository
import org.jeonfeel.moeuibit2.data.usecase.UpbitChartUseCase
import org.jeonfeel.moeuibit2.ui.coindetail.chart.*
import org.jeonfeel.moeuibit2.ui.coindetail.chart.utils.GetMovingAverage
import org.jeonfeel.moeuibit2.ui.coindetail.chart.utils.defaultSet
import org.jeonfeel.moeuibit2.ui.main.exchange.root_exchange.BaseCommunicationModule
import org.jeonfeel.moeuibit2.utils.manager.PreferencesManager
import org.json.JSONException
import org.json.JSONObject
import javax.inject.Inject

class ChartState {
    val isUpdateChart = mutableStateOf(false)
    val candleType = mutableStateOf("1")
    val loadingDialogState = mutableStateOf(false)
    val minuteVisible = mutableStateOf(false)
    val selectedButton = mutableStateOf(MINUTE_SELECT)
    val minuteText = mutableStateOf("1분")
    val loadingOldData = mutableStateOf(false)
    val isLastData = mutableStateOf(false) // 더이상 불러올 과거 데이터가 없는지 FLAG값
}

class Chart @Inject constructor(
    private val upbitRepository: UpbitRepository,
    private val localRepository: LocalRepository,
    private val upbitChartUseCase: UpbitChartUseCase,
    private val preferenceManager: PreferencesManager
) : BaseCommunicationModule() {
    val state = ChartState()
    private var firstInit = true
    var market = ""
    var chartUpdateJob: Job? = null
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

    init {
        for (i in movingAverageLineArray) {
            movingAverage.add(GetMovingAverage(i))
        }
    }

    suspend fun refresh(candleType: String = state.candleType.value, market: String) {
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
                    newUpdateChart(market)
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

//    suspend fun requestBitthumbChartData(
//        candleType: String = state.candleType.value, //분봉인지, 일봉인지
//        market: String, // 어느 코인인지
//    ) {
//        state.isUpdateChart.value = false
//        state.loadingDialogState.value = true
//
//        val response: Response<BitthumbChartModel> = remoteRepository.getBitthumbChart(
//            market = Utils.upbitMarketToBitthumbMarket(market),
//            candleType = candleType
//        )
//        Logger.e("requestBitthumbChartData, ${response.message()}")
//        if (response.isSuccessful && response.body()?.status == "0000") {
//            Logger.e("requestBitthumbChartData, ${response.body()?.status}")
//            resetChartData()
//            val positiveBarEntries = ArrayList<BarEntry>()
//            val negativeBarEntries = ArrayList<BarEntry>()
//            val chartModelList = response.body()?.data
//            if ((chartModelList?.size ?: 0) != 0) {
//                val indices = chartModelList?.size ?: 0
//                Logger.e(response.body().toString())
//                kstTime = Utils.millisToUpbitFormat(
//                    (chartModelList?.last()?.get(0) ?: 0).toString().toDouble().toLong()
//                )
//                for (i in 0 until indices) {
//                    val openingPrice = (chartModelList?.get(i)?.get(1) ?: "").toString().toFloat()
//                    val closePrice = (chartModelList?.get(i)?.get(2) ?: "").toString().toFloat()
//                    val highPrice = (chartModelList?.get(i)?.get(3) ?: "").toString().toFloat()
//                    val lowPrice = (chartModelList?.get(i)?.get(4) ?: "").toString().toFloat()
//                    val accAmount = (chartModelList?.get(i)?.get(5) ?: "").toString().toFloat()
//                    val time = Utils.millisToUpbitFormat(
//                        (chartModelList?.get(i)?.get(0) ?: 0).toString().toDouble().toLong()
//                    )
//                    candleEntries.add(
//                        CandleEntry(
//                            candlePosition,
//                            highPrice,
//                            lowPrice,
//                            openingPrice,
//                            closePrice
//                        )
//                    )
//                    if (closePrice - openingPrice >= 0.0) {
//                        positiveBarEntries.add(
//                            BarEntry(candlePosition, accAmount)
//                        )
//                    } else {
//                        negativeBarEntries.add(
//                            BarEntry(candlePosition, accAmount)
//                        )
//                    }
//                    kstDateHashMap[candlePosition.toInt()] = time
//                    accData[candlePosition.toInt()] = accAmount.toDouble()
//                    candlePosition += 1f
//                    candleEntriesLastPosition = candleEntries.size - 1
//                }
//                candlePosition += 1f
//            } else {
//                //TODO
//            }
//            /**
//             * 현재 보유 코인인지 있으면 불러옴
//             */
//            val myCoin = getChartCoinPurchaseAverage(market)
//            myCoin?.let {
//                purchaseAveragePrice = it.purchasePrice.toFloat()
//            }
//            candlePosition -= 1f
//            positiveBarDataSet = BarDataSet(positiveBarEntries, "")
//            negativeBarDataSet = BarDataSet(negativeBarEntries, "")
//            candleDataSet = CandleDataSet(candleEntries, "")
//            state.isUpdateChart.value = true
//            state.loadingDialogState.value = false
//            _chartUpdateMutableLiveData.value = CHART_INIT
//            updateChart(market)
//        }
//    }

    suspend fun newRequestOldData(
        candleType: String = state.candleType.value,
        market: String,
        positiveBarDataSet: IBarDataSet,
        negativeBarDataSet: IBarDataSet,
        candleXMin: Float
    ) {
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

//    fun bitthumbUpdateCandleTicker(tradePrice: Double) {
//        if (kstTime.isNotEmpty()) {
//            val kstTimeMillis = Utils.upbitFormatToMillis(kstTime)
//            val standardMillis = Utils.getStandardMillis(state.candleType.value)
//            val currentMillis = System.currentTimeMillis()
//            Logger.e("standard -> ${standardMillis + kstTimeMillis} current -> $currentMillis")
//            if (currentMillis < kstTimeMillis + standardMillis) {
//                Logger.e("CHART_SET_CANDLE".toString())
//                if (state.isUpdateChart.value && candleEntries.isNotEmpty()) {
//                    val candleEntry = candleEntries[candleEntriesLastPosition]
//                    if (candleEntry.close < candleEntry.low) {
//                        candleEntries[candleEntriesLastPosition].low = tradePrice.toFloat()
//                        candleEntries[candleEntriesLastPosition].close = tradePrice.toFloat()
//                    } else if (candleEntry.close > candleEntry.high) {
//                        candleEntries[candleEntriesLastPosition].high = tradePrice.toFloat()
//                        candleEntries[candleEntriesLastPosition].close = tradePrice.toFloat()
//                    } else {
//                        candleEntries[candleEntriesLastPosition].close = tradePrice.toFloat()
//                    }
//                    try {
////                        modifyLineData()
//                    } catch (e: Exception) {
//
//                    }
//                    _chartUpdateMutableLiveData.postValue(CHART_SET_CANDLE)
//                }
//            } else {
//                Logger.e("CHART_ADD_CANDLE")
//                kstTime = Utils.millisToUpbitFormat(kstTimeMillis + standardMillis)
//                addModel = ChartModel(
//                    candleDateTimeKst = kstTime,
//                    candleDateTimeUtc = "",
//                    openingPrice = tradePrice,
//                    highPrice = tradePrice,
//                    lowPrice = tradePrice,
//                    tradePrice = tradePrice,
//                    candleAccTradePrice = 0.0,
//                    timestamp = currentMillis
//                )
//                state.isUpdateChart.value = false
//                _candleEntries.add(
//                    CandleEntry(
//                        candlePosition,
//                        tradePrice.toFloat(),
//                        tradePrice.toFloat(),
//                        tradePrice.toFloat(),
//                        tradePrice.toFloat()
//                    )
//                )
//                candlePosition += 1f
//                candleEntriesLastPosition += 1
//                kstDateHashMap[candlePosition.toInt()] = kstTime
//                _chartUpdateMutableLiveData.postValue(CHART_ADD)
//                state.isUpdateChart.value = true
//            }
//        }
//    }

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
        return localRepository.getMyCoinDao().isInsert(market)
    }

    fun setBitthumbChart() {
        state.isLastData.value = true
        if (state.candleType.value == "1") {
            state.candleType.value = "1m"
        }
    }

    fun getLastCandleEntry() = candleEntries.last()
    fun isCandleEntryEmpty() = candleEntries.isEmpty()
}
