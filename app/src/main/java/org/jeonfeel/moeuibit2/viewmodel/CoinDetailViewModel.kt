package org.jeonfeel.moeuibit2.viewmodel

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.mikephil.charting.charts.CombinedChart
import com.github.mikephil.charting.data.CandleData
import com.github.mikephil.charting.data.CandleDataSet
import com.github.mikephil.charting.data.CandleEntry
import com.github.mikephil.charting.data.CombinedData
import com.google.gson.Gson
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.jeonfeel.moeuibit2.data.remote.retrofit.model.ChartModel
import org.jeonfeel.moeuibit2.data.remote.retrofit.model.CoinDetailOrderBookAskRetrofitModel
import org.jeonfeel.moeuibit2.data.remote.retrofit.model.CoinDetailOrderBookBidRetrofitModel
import org.jeonfeel.moeuibit2.data.remote.websocket.UpBitOrderBookWebSocket
import org.jeonfeel.moeuibit2.data.remote.websocket.UpBitTickerWebSocket
import org.jeonfeel.moeuibit2.data.remote.websocket.model.CoinDetailOrderBookAskModel
import org.jeonfeel.moeuibit2.data.remote.websocket.model.CoinDetailOrderBookBidModel
import org.jeonfeel.moeuibit2.data.remote.websocket.model.CoinDetailOrderBookModel
import org.jeonfeel.moeuibit2.data.remote.websocket.model.CoinDetailTickerModel
import org.jeonfeel.moeuibit2.listener.OnOrderBookMessageReceiveListener
import org.jeonfeel.moeuibit2.listener.OnTickerMessageReceiveListener
import org.jeonfeel.moeuibit2.repository.CoinDetailRepository
import org.jeonfeel.moeuibit2.util.MyValueFormatter
import org.jeonfeel.moeuibit2.util.chartRefreshSetting
import org.jeonfeel.moeuibit2.util.initCandleDataSet
import javax.inject.Inject

@HiltViewModel
class CoinDetailViewModel @Inject constructor(
    private val coinDetailRepository: CoinDetailRepository,
) : ViewModel(), OnTickerMessageReceiveListener,
    OnOrderBookMessageReceiveListener {

    var preClosingPrice = 0.0
    var maxOrderBookSize = 0.0
    var market = ""
    var coinDetailModel = CoinDetailTickerModel("", 0.0, 0.0, 0.0)

    val gson = Gson()
    private val isTickerSocketRunning = true
    private val isOrderBookSocketRunning = true

    val currentTradePriceState = mutableStateOf(0.0)
    val currentTradePriceStateForOrderBook = mutableStateOf(0.0)
    val orderBookMutableStateList = mutableStateListOf<CoinDetailOrderBookModel>()

    var candlePosition = 0f
    private val chartData = ArrayList<ChartModel>()
    private val candleEntries = ArrayList<CandleEntry>()
    var loadingMoreChartData = false
    private val dateHashMap = HashMap<Int, String>()
    private val valueFormatter = MyValueFormatter()
    val highestVisibleXPrice = mutableStateOf(0f)

    /**
     * orderScreen
     * */
    fun initOrder(market: String, preClosingPrice: Double) {
        setWebSocketMessageListener()
        setOrderBookWebSocketMessageListener()
        this.market = market
        this.preClosingPrice = preClosingPrice
        viewModelScope.launch {
            UpBitTickerWebSocket.requestKrwCoin(market)
            updateTicker()
            initOrderBook(market)
            UpBitOrderBookWebSocket.requestOrderBookList(market)
        }
    }

    fun setWebSocketMessageListener() {
        UpBitTickerWebSocket.getListener().setTickerMessageListener(this)
    }

    fun setOrderBookWebSocketMessageListener() {
        UpBitOrderBookWebSocket.getListener().setOrderBookMessageListener(this)
    }

    private suspend fun initOrderBook(market: String) {
        val response = coinDetailRepository.getOrderBookService(market)
        if (response.isSuccessful) {
            val body = response.body()
            val a = body?.first() ?: JsonObject()
            val modelOBj = gson.fromJson(a, JsonObject::class.java)
            val modelJsonArray = modelOBj.getAsJsonArray("orderbook_units")
            val indices = modelJsonArray.size()
            for (i in indices - 1 downTo 0) {
                val orderBookAskModel =
                    gson.fromJson(
                        modelJsonArray[i],
                        CoinDetailOrderBookAskRetrofitModel::class.java
                    )
                orderBookMutableStateList.add(
                    CoinDetailOrderBookModel(
                        orderBookAskModel.ask_price,
                        orderBookAskModel.ask_size,
                        0
                    )
                )
            }
            for (i in 0 until indices) {
                val orderBookBidModel =
                    gson.fromJson(
                        modelJsonArray[i],
                        CoinDetailOrderBookBidRetrofitModel::class.java
                    )
                orderBookMutableStateList.add(
                    CoinDetailOrderBookModel(
                        orderBookBidModel.bid_price,
                        orderBookBidModel.bid_size,
                        1
                    )
                )
            }
        }
    }

    private fun updateTicker() {
        viewModelScope.launch {
            while (isTickerSocketRunning) {
                currentTradePriceState.value = coinDetailModel.tradePrice
                delay(100)
            }
        }
    }

    /**
     * chartScreen
     * */
    fun requestChartData(minute: String, combinedChart: CombinedChart) {
        val candleDataSet = CandleDataSet(candleEntries, "")
        viewModelScope.launch {
            val response = coinDetailRepository.getCandleService(minute, market)
            if (response.isSuccessful) {
                chartData.clear()
                candleEntries.clear()
                dateHashMap.clear()
                val chartModelList = response.body() ?: JsonArray()
                if (chartModelList.size() != 0) {
                    val indices = chartModelList.size()
                    for (i in indices - 1 downTo 0) {
                        val model = gson.fromJson(chartModelList[i], ChartModel::class.java)
                        if (candlePosition == 0f) {
                            chartData.add(model)
                        }
                        candleEntries.add(
                            CandleEntry(
                                candlePosition,
                                model.highPrice.toFloat(),
                                model.lowPrice.toFloat(),
                                model.openingPrice.toFloat(),
                                model.tradePrice.toFloat()
                            )
                        )
                        dateHashMap[candlePosition.toInt()] = model.candleDateTimeKst
                        candlePosition += 1f
                    }
                } else {
                    //TODO
                }
                valueFormatter.setItem(dateHashMap)
            }
            candleDataSet.notifyDataSetChanged()
            candleDataSet.initCandleDataSet()
            val candleData = CandleData(candleDataSet)

            if (combinedChart.combinedData != null) {
                combinedChart.combinedData.candleData.removeDataSet(0)
                combinedChart.combinedData.candleData.addDataSet(candleDataSet)
                combinedChart.combinedData.candleData.notifyDataChanged()
            } else {
                val combinedData = CombinedData()
                combinedData.setData(candleData)
                combinedChart.data = combinedData
            }
            combinedChart.invalidate()
            combinedChart.chartRefreshSetting(candleEntries)
            combinedChart.xAxis.valueFormatter = valueFormatter
        }
    }

    fun requestMoreData(minute: String, combinedChart: CombinedChart) {
        loadingMoreChartData = true
        val startPosition = combinedChart.lowestVisibleX
        val currentVisible = combinedChart.visibleXRange
        val tempCandleEntries = ArrayList<CandleEntry>()
        val time = chartData.last().candleDateTimeUtc.replace("T", " ")

        viewModelScope.launch {
            val response = coinDetailRepository.getCandleService("1", market, "200", time)
            if (response.isSuccessful) {
                val chartModelList = response.body() ?: JsonArray()
                val indices = chartModelList.size()
                var tempCandlePosition = combinedChart.data.candleData.xMin - indices - 1
                for (i in indices - 1 downTo 0) {
                    val model = gson.fromJson(chartModelList[i], ChartModel::class.java)
                    if (i == indices) {
                        chartData.add(model)
                    }
                    tempCandleEntries.add(
                        CandleEntry(
                            tempCandlePosition,
                            model.highPrice.toFloat(),
                            model.lowPrice.toFloat(),
                            model.openingPrice.toFloat(),
                            model.tradePrice.toFloat()
                        )
                    )
                    dateHashMap[tempCandlePosition.toInt()] = model.candleDateTimeKst
                    tempCandlePosition += 1f
                }
                valueFormatter.setItem(dateHashMap)
                tempCandleEntries.addAll(candleEntries)
                candleEntries.clear()
                candleEntries.addAll(tempCandleEntries)
                val candleDataSet = CandleDataSet(candleEntries, "")
                candleDataSet.initCandleDataSet()
                combinedChart.data.candleData.removeDataSet(0)
                combinedChart.data.candleData.addDataSet(candleDataSet)
                combinedChart.data.candleData.notifyDataChanged()
                combinedChart.xAxis.axisMinimum = (combinedChart.data.candleData.xMin - 3f)
                combinedChart.fitScreen()
                combinedChart.setVisibleXRangeMaximum(currentVisible)
                combinedChart.data.notifyDataChanged()
                combinedChart.moveViewToX(startPosition)

                combinedChart.setVisibleXRangeMinimum(20f)
                combinedChart.setVisibleXRangeMaximum(190f)
            }
        }
    }

    override fun onTickerMessageReceiveListener(tickerJsonObject: String) {
        if (isTickerSocketRunning) {
            val model = gson.fromJson(tickerJsonObject, CoinDetailTickerModel::class.java)
            coinDetailModel = model
            currentTradePriceStateForOrderBook.value = coinDetailModel.tradePrice
        }
    }

    override fun onOrderBookMessageReceiveListener(orderBookJsonObject: String) {
        if (isTickerSocketRunning) {
            var index = 0
            val modelOBj = gson.fromJson(orderBookJsonObject, JsonObject::class.java)
            val modelJsonArray = modelOBj.getAsJsonArray("obu")
            val indices = modelJsonArray.size()
            for (i in indices - 1 downTo 0) {
                val orderBookAskModel =
                    gson.fromJson(modelJsonArray[i], CoinDetailOrderBookAskModel::class.java)
                orderBookMutableStateList[index] =
                    CoinDetailOrderBookModel(
                        orderBookAskModel.ask_price,
                        orderBookAskModel.ask_size,
                        0
                    )
                index++
            }
            for (i in 0 until indices) {
                val orderBookBidModel =
                    gson.fromJson(modelJsonArray[i], CoinDetailOrderBookBidModel::class.java)
                orderBookMutableStateList[index] =
                    CoinDetailOrderBookModel(
                        orderBookBidModel.bid_price,
                        orderBookBidModel.bid_size,
                        1
                    )
                index++
            }
            maxOrderBookSize = orderBookMutableStateList.maxOf { it.size }
        }
    }
}