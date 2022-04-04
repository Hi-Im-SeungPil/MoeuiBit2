package org.jeonfeel.moeuibit2.viewmodel

import android.graphics.Color
import android.graphics.Paint
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.mikephil.charting.charts.CombinedChart
import com.github.mikephil.charting.components.YAxis
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

    //    private val combinedChartDataSetMutableLiveData = MutableLiveData<ArrayList<ChartModel>>()
//    val combinedChartDataSetLiveData: LiveData<ArrayList<ChartModel>>
//        get() = combinedChartDataSetMutableLiveData
    val chartData = ArrayList<ChartModel>()
    val candleEntries = ArrayList<CandleEntry>()
    val combinedData = CombinedData()

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
            for (i in modelJsonArray.size() - 1 downTo 0) {
                val orderBookAskModel =
                    gson.fromJson(modelJsonArray[i],
                        CoinDetailOrderBookAskRetrofitModel::class.java)
                orderBookMutableStateList.add(CoinDetailOrderBookModel(orderBookAskModel.ask_price,
                    orderBookAskModel.ask_size,
                    0))
            }
            for (i in 0 until modelJsonArray.size()) {
                val orderBookBidModel =
                    gson.fromJson(modelJsonArray[i],
                        CoinDetailOrderBookBidRetrofitModel::class.java)
                orderBookMutableStateList.add(CoinDetailOrderBookModel(orderBookBidModel.bid_price,
                    orderBookBidModel.bid_size,
                    1))
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
        var position = 1f
        viewModelScope.launch {
            val response = coinDetailRepository.getCandleService(minute, market)
            if (response.isSuccessful) {
                val chartModelList = response.body() ?: JsonArray()
                if (chartModelList.size() != 0) {
                    for (i in chartModelList.size() - 1 downTo 0) {
                        val model = gson.fromJson(chartModelList[i], ChartModel::class.java)
                        chartData.add(model)
                        candleEntries.add(CandleEntry(position,
                            model.highPrice.toFloat(),
                            model.lowPrice.toFloat(),
                            model.openingPrice.toFloat(),
                            model.tradePrice.toFloat()))
                        position += 1f
                    }
                } else {
                    //TODO
                }
            }
            val candleDataSet = CandleDataSet(candleEntries, "")
            candleDataSet.axisDependency = YAxis.AxisDependency.RIGHT
            candleDataSet.shadowColorSameAsCandle = true
            candleDataSet.shadowWidth = 1f
            candleDataSet.decreasingColor = Color.BLUE
            candleDataSet.decreasingPaintStyle = Paint.Style.FILL
            candleDataSet.increasingColor = Color.RED
            candleDataSet.increasingPaintStyle = Paint.Style.FILL
            candleDataSet.neutralColor = Color.GRAY
            candleDataSet.setDrawValues(false)

            val candleData = CandleData(candleDataSet)
            combinedData.setData(candleData)
            combinedChart.data = combinedData
            combinedChart.invalidate()
        }
    }

//    val candleDateTimeKst = jsonObject.getString("candle_date_time_kst")
//                    val openingPrice = jsonObject.getDouble("opening_price")
//                    val highPrice = jsonObject.getDouble("high_price")
//                    val lowPrice = jsonObject.getDouble("low_price")
//                    val tradePrice = jsonObject.getDouble("trade_price")
//                    val candleTransactionAmount = jsonObject.getDouble("candle_acc_trade_price")

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
            for (i in modelJsonArray.size() - 1 downTo 0) {
                val orderBookAskModel =
                    gson.fromJson(modelJsonArray[i], CoinDetailOrderBookAskModel::class.java)
                orderBookMutableStateList[index] =
                    CoinDetailOrderBookModel(orderBookAskModel.ask_price,
                        orderBookAskModel.ask_size,
                        0)
                index++
            }
            for (i in 0 until modelJsonArray.size()) {
                val orderBookBidModel =
                    gson.fromJson(modelJsonArray[i], CoinDetailOrderBookBidModel::class.java)
                orderBookMutableStateList[index] =
                    CoinDetailOrderBookModel(orderBookBidModel.bid_price,
                        orderBookBidModel.bid_size,
                        1)
                index++
            }
            maxOrderBookSize = orderBookMutableStateList.maxOf { it.size }
        }
    }
}