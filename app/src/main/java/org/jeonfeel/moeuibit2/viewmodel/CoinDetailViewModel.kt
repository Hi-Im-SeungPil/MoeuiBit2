package org.jeonfeel.moeuibit2.viewmodel

import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.mikephil.charting.charts.CombinedChart
import com.github.mikephil.charting.data.CandleDataSet
import com.github.mikephil.charting.data.CandleEntry
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
import org.jeonfeel.moeuibit2.util.chartRefreshLoadMoreData
import org.jeonfeel.moeuibit2.util.chartRefreshSetting
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

    private val firstCandleDataSetMutableLiveData = MutableLiveData<CandleDataSet>()
    val firstCandleDataSetLiveData: LiveData<CandleDataSet> get() = firstCandleDataSetMutableLiveData

    var candlePosition = 0f
    private val chartData = ArrayList<ChartModel>()
    val candleEntries = ArrayList<CandleEntry>()

    var loadingMoreChartData = false
    val kstDateHashMap = HashMap<Int, String>()
    val utcDateArray = ArrayList<String>()
    private val valueFormatter = MyValueFormatter()
//    val highestVisibleXPrice = mutableStateOf(0f)

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
        viewModelScope.launch {
            val response = coinDetailRepository.getCandleService(minute, market)
            if (response.isSuccessful) {
                chartData.clear()
                candleEntries.clear()
                kstDateHashMap.clear()
                val chartModelList = response.body() ?: JsonArray()
                if (chartModelList.size() != 0) {
                    val indices = chartModelList.size()
                    utcDateArray.add(gson.fromJson(chartModelList[indices - 1], ChartModel::class.java).candleDateTimeUtc)
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
                        kstDateHashMap[candlePosition.toInt()] = model.candleDateTimeKst
                        candlePosition += 1f
                    }
                } else {
                    //TODO
                }
                valueFormatter.setItem(kstDateHashMap)
            }
            combinedChart.chartRefreshSetting(candleEntries, CandleDataSet(candleEntries,""),valueFormatter)
        }
    }

    fun requestMoreData(minute: String, combinedChart: CombinedChart) {
        loadingMoreChartData = true
        val startPosition = combinedChart.lowestVisibleX
        val currentVisible = combinedChart.visibleXRange
        val tempCandleEntries = ArrayList<CandleEntry>()
        val time = utcDateArray.last().replace("T", " ")
        Log.d("aaaa",time)
//        Log.d("aaaa",kstDateHashMap[combinedChart.candleData.xMin.toInt()]!!)
        viewModelScope.launch {
            val response = coinDetailRepository.getCandleService("1", market, "200", time)
            if (response.isSuccessful) {
                val chartModelList = response.body() ?: JsonArray()
                val indices = chartModelList.size()
                var tempCandlePosition = combinedChart.data.candleData.xMin - indices
                utcDateArray.add(gson.fromJson(chartModelList[indices - 1], ChartModel::class.java).candleDateTimeUtc)
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
                    kstDateHashMap[tempCandlePosition.toInt()] = model.candleDateTimeKst
                    tempCandlePosition += 1f
                }
                valueFormatter.setItem(kstDateHashMap)
                tempCandleEntries.addAll(candleEntries)
                candleEntries.clear()
                candleEntries.addAll(tempCandleEntries)
                val candleDataSet = CandleDataSet(candleEntries, "")
                combinedChart.chartRefreshLoadMoreData(
                    candleDataSet,
                    startPosition,
                    currentVisible
                )
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