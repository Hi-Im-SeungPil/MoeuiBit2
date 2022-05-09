package org.jeonfeel.moeuibit2.viewmodel

import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.mikephil.charting.charts.CombinedChart
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.CandleDataSet
import com.github.mikephil.charting.data.CandleEntry
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.gson.Gson
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.jeonfeel.moeuibit2.data.remote.retrofit.model.ChartModel
import org.jeonfeel.moeuibit2.data.remote.retrofit.model.CoinDetailOrderBookAskRetrofitModel
import org.jeonfeel.moeuibit2.data.remote.retrofit.model.CoinDetailOrderBookBidRetrofitModel
import org.jeonfeel.moeuibit2.data.remote.websocket.UpBitCoinDetailWebSocket
import org.jeonfeel.moeuibit2.data.remote.websocket.UpBitOrderBookWebSocket
import org.jeonfeel.moeuibit2.data.remote.websocket.model.CoinDetailOrderBookAskModel
import org.jeonfeel.moeuibit2.data.remote.websocket.model.CoinDetailOrderBookBidModel
import org.jeonfeel.moeuibit2.data.remote.websocket.model.CoinDetailOrderBookModel
import org.jeonfeel.moeuibit2.data.remote.websocket.model.CoinDetailTickerModel
import org.jeonfeel.moeuibit2.listener.OnCoinDetailMessageReceiveListener
import org.jeonfeel.moeuibit2.listener.OnOrderBookMessageReceiveListener
import org.jeonfeel.moeuibit2.repository.local.LocalRepository
import org.jeonfeel.moeuibit2.repository.remote.RemoteRepository
import org.jeonfeel.moeuibit2.util.MyValueFormatter
import org.jeonfeel.moeuibit2.util.chartRefreshLoadMoreData
import org.jeonfeel.moeuibit2.util.chartRefreshSetting
import retrofit2.Response
import javax.inject.Inject

@HiltViewModel
class CoinDetailViewModel @Inject constructor(
    private val remoteRepository: RemoteRepository,
    private val localRepository: LocalRepository,
) : ViewModel(), OnCoinDetailMessageReceiveListener,
    OnOrderBookMessageReceiveListener {

    var preClosingPrice = 0.0
    var maxOrderBookSize = 0.0
    var market = ""
    var coinDetailModel = CoinDetailTickerModel("", 0.0, 0.0, 0.0)

    val gson = Gson()
    private val isTickerSocketRunning = true

    val currentTradePriceState = mutableStateOf(0.0)
    val currentTradePriceStateForOrderBook = mutableStateOf(0.0)
    val orderBookMutableStateList = mutableStateListOf<CoinDetailOrderBookModel>()

    /**
     * chart
     * */
    var isUpdateChart = true
    var candlePosition = 0f
    private var candleEntriesLastPosition = 0
    private val chartData = ArrayList<ChartModel>()
    private val candleEntries = ArrayList<CandleEntry>()
    private val positiveBarEntries = ArrayList<BarEntry>()
    private val negativeBarEntries = ArrayList<BarEntry>()
    val candleType = mutableStateOf("1")
    var loadingMoreChartData = false
    val kstDateHashMap = HashMap<Int, String>()
    private var firstCandleUtcTime = ""
    private var kstTime = ""
    private val valueFormatter = MyValueFormatter()
    private val _firstCandleDataSetMutableLiveData = MutableLiveData<String>()
    val firstCandleDataSetLiveData: LiveData<String> get() = _firstCandleDataSetMutableLiveData
    val dialogState = mutableStateOf(false)
    val accData = HashMap<Int,Double>()
    val minuteVisible = mutableStateOf(false)
    var chartLastData = false
    /**
     * coin info
     * */
    private val _coinInfoMutableLiveData = MutableLiveData<HashMap<String, String>>()
    val coinInfoLiveData: LiveData<HashMap<String, String>> get() = _coinInfoMutableLiveData

    /**
     * favorite
     * */
    val favoriteMutableState = mutableStateOf(false)

    /**
     * orderScreen
     * */

    fun initViewModel(market: String, preClosingPrice: Double, isFavorite: Boolean) {
        this.market = market
        this.preClosingPrice = preClosingPrice
        this.favoriteMutableState.value = isFavorite
    }

    fun initOrder() {
        if (currentTradePriceState.value == 0.0 && orderBookMutableStateList.isEmpty()) {
            setCoinDetailWebSocketMessageListener()
            setOrderBookWebSocketMessageListener()
            viewModelScope.launch {
                UpBitCoinDetailWebSocket.requestCoinDetailData(market)
                updateTicker()
                initOrderBook(market)
                UpBitOrderBookWebSocket.requestOrderBookList(market)
            }
        } else {
            setCoinDetailWebSocketMessageListener()
            setOrderBookWebSocketMessageListener()
            UpBitCoinDetailWebSocket.requestCoinDetailData(market)
            UpBitOrderBookWebSocket.requestOrderBookList(market)
        }
    }

    fun setCoinDetailWebSocketMessageListener() {
        UpBitCoinDetailWebSocket.getListener().setTickerMessageListener(this)
    }

    fun setOrderBookWebSocketMessageListener() {
        UpBitOrderBookWebSocket.getListener().setOrderBookMessageListener(this)
    }

    private suspend fun initOrderBook(market: String) {
        val response = remoteRepository.getOrderBookService(market)
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
                val tradPrice = coinDetailModel.tradePrice
                currentTradePriceState.value = tradPrice
                if (isUpdateChart && candleEntries.isNotEmpty()) {
                    candleEntries[candleEntriesLastPosition].close = tradPrice.toFloat()
                    _firstCandleDataSetMutableLiveData.postValue("set")
                }
                delay(100)
            }
        }
    }

    /**
     * chartScreen
     * */
    fun requestChartData(candleType: String, combinedChart: CombinedChart) {
        isUpdateChart = false
        dialogState.value = true
        viewModelScope.launch {
            val response: Response<JsonArray> = if (candleType.toIntOrNull() == null) {
                remoteRepository.getOtherCandleService(candleType, market)
            } else {
                remoteRepository.getMinuteCandleService(candleType, market)
            }
            if (response.isSuccessful && response.body()?.size() ?: JsonArray() != 0) {
                candlePosition = 0f
                candleEntriesLastPosition = 0
                chartData.clear()
                candleEntries.clear()
                positiveBarEntries.clear()
                negativeBarEntries.clear()
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
                valueFormatter.setItem(kstDateHashMap)
                candlePosition -= 1f
            }
            combinedChart.chartRefreshSetting(
                candleEntries,
                CandleDataSet(candleEntries, ""),
                BarDataSet(positiveBarEntries, ""),
                BarDataSet(negativeBarEntries, ""),
                valueFormatter
            )
            isUpdateChart = true
            dialogState.value = false
            updateChart()
        }
    }

    fun requestMoreData(candleType: String, combinedChart: CombinedChart) {
        loadingMoreChartData = true
        val time = firstCandleUtcTime.replace("T", " ")
        if(!chartLastData) {
            dialogState.value = true
        }
        viewModelScope.launch {
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
                valueFormatter.setItem(kstDateHashMap)
                tempCandleEntries.addAll(candleEntries)
                candleEntries.clear()
                candleEntries.addAll(tempCandleEntries)
                tempPositiveBarEntries.addAll(positiveBarEntries)
                tempNegativeBarEntries.addAll(negativeBarEntries)
                positiveBarEntries.clear()
                negativeBarEntries.clear()
                positiveBarEntries.addAll(tempPositiveBarEntries)
                negativeBarEntries.addAll(tempNegativeBarEntries)

                candleEntriesLastPosition = candleEntries.size - 1

                val candleDataSet = CandleDataSet(candleEntries, "")
                val positiveBarDataSet = BarDataSet(positiveBarEntries, "")
                val negativeBarDataSet = BarDataSet(negativeBarEntries, "")
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
    }

    private fun updateChart() {
        viewModelScope.launch {
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
                        valueFormatter.addItem(kstTime, candlePosition.toInt())
                        accData[candlePosition.toInt()] = model.candleAccTradePrice

                        if (model.tradePrice - model.openingPrice >= 0.0) {
                            positiveBarEntries.add(
                                BarEntry(candlePosition, model.candleAccTradePrice.toFloat())
                            )
                        } else {
                            BarEntry(candlePosition, model.candleAccTradePrice.toFloat())
                        }
                        _firstCandleDataSetMutableLiveData.postValue("add")
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
                        _firstCandleDataSetMutableLiveData.postValue("set")
                    }
                }
                delay(600)
            }
        }
    }

    fun setBar() {
        if (candleEntries.last().close - candleEntries.last().open >= 0.0) {
            if(positiveBarEntries[positiveBarEntries.lastIndex].x == candlePosition) {
                positiveBarEntries[positiveBarEntries.lastIndex] = BarEntry(candlePosition,accData[candlePosition.toInt()]!!.toFloat())
            }else if(positiveBarEntries[positiveBarEntries.lastIndex].x != candlePosition && negativeBarEntries[negativeBarEntries.lastIndex].x == candlePosition) {
                positiveBarEntries.add(BarEntry(candlePosition,accData[candlePosition.toInt()]!!.toFloat()))
                negativeBarEntries.removeLast()
            }
        } else {
            if(negativeBarEntries[negativeBarEntries.lastIndex].x == candlePosition) {
                negativeBarEntries[negativeBarEntries.lastIndex] = BarEntry(candlePosition,accData[candlePosition.toInt()]!!.toFloat())
            } else if(negativeBarEntries[negativeBarEntries.lastIndex].x != candlePosition && positiveBarEntries[positiveBarEntries.lastIndex].x == candlePosition){
                negativeBarEntries.add(BarEntry(candlePosition,accData[candlePosition.toInt()]!!.toFloat()))
                positiveBarEntries.removeLast()
            }
        }
    }

    /**
     * coinInfo
     * */
    fun getCoinInfo() {
        val mDatabase = FirebaseDatabase.getInstance().reference
        mDatabase.child("coinInfo").child(market)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val coinInfos = HashMap<String, String>()

                    coinInfos["homepage"] =
                        snapshot.child("homepage").getValue(String::class.java)!!
                    coinInfos["amount"] = snapshot.child("amount").getValue(String::class.java)!!
                    coinInfos["twitter"] = snapshot.child("twitter").getValue(String::class.java)!!
                    coinInfos["block"] = snapshot.child("block").getValue(String::class.java)!!
                    coinInfos["info"] = snapshot.child("content").getValue(String::class.java)!!

                    _coinInfoMutableLiveData.postValue(coinInfos)
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e("firebase error", error.message)
                }
            })
    }

    override fun onCoinDetailMessageReceiveListener(tickerJsonObject: String) {
        if (isTickerSocketRunning) {
            val model = gson.fromJson(tickerJsonObject, CoinDetailTickerModel::class.java)
            coinDetailModel = model
            currentTradePriceStateForOrderBook.value = coinDetailModel.tradePrice
            Log.d("model", model.toString())
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
            Log.d("model", "a")
        }
    }
}