package org.jeonfeel.moeuibit2.activity.coindetail.viewmodel

import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.mikephil.charting.charts.CombinedChart
import com.github.mikephil.charting.data.CandleEntry
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.gson.Gson
import com.google.gson.JsonObject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.jeonfeel.moeuibit2.activity.coindetail.viewmodel.usecase.ChartUseCase
import org.jeonfeel.moeuibit2.activity.coindetail.viewmodel.usecase.OrderScreenUseCase
import org.jeonfeel.moeuibit2.data.remote.websocket.UpBitCoinDetailWebSocket
import org.jeonfeel.moeuibit2.data.remote.websocket.UpBitOrderBookWebSocket
import org.jeonfeel.moeuibit2.data.remote.websocket.listener.OnCoinDetailMessageReceiveListener
import org.jeonfeel.moeuibit2.data.remote.websocket.listener.OnOrderBookMessageReceiveListener
import org.jeonfeel.moeuibit2.data.remote.websocket.model.CoinDetailOrderBookAskModel
import org.jeonfeel.moeuibit2.data.remote.websocket.model.CoinDetailOrderBookBidModel
import org.jeonfeel.moeuibit2.data.remote.websocket.model.CoinDetailOrderBookModel
import org.jeonfeel.moeuibit2.data.remote.websocket.model.CoinDetailTickerModel
import org.jeonfeel.moeuibit2.repository.local.LocalRepository
import org.jeonfeel.moeuibit2.repository.remote.RemoteRepository
import javax.inject.Inject
import kotlin.collections.set

@HiltViewModel
class CoinDetailViewModel @Inject constructor(
    private val remoteRepository: RemoteRepository,
    private val chartUseCase: ChartUseCase,
    private val orderScreenUseCase: OrderScreenUseCase,
    val localRepository: LocalRepository
) : ViewModel(), OnCoinDetailMessageReceiveListener,
    OnOrderBookMessageReceiveListener {

    var preClosingPrice = 0.0
    var maxOrderBookSize = 0.0
    var market = ""
    var koreanName = ""

    val gson = Gson()
    /**
     * coin info
     * */
    private val _coinInfoMutableLiveData = MutableLiveData<HashMap<String, String>>()
    val coinInfoLiveData: LiveData<HashMap<String, String>> get() = _coinInfoMutableLiveData
    val coinInfoDialog = mutableStateOf(false)
    val coinInfoLoading = mutableStateOf(false)

    /**
     * favorite
     * */
    val favoriteMutableState = mutableStateOf(false)

    fun initViewModel(market: String, preClosingPrice: Double, isFavorite: Boolean) {
        this.market = market
        this.preClosingPrice = preClosingPrice
        this.favoriteMutableState.value = isFavorite
    }

    private fun setCoinDetailWebSocketMessageListener() {
        UpBitCoinDetailWebSocket.getListener().setTickerMessageListener(this)
    }

    fun setOrderBookWebSocketMessageListener() {
        UpBitOrderBookWebSocket.getListener().setOrderBookMessageListener(this)
    }

    fun initOrderScreen() {
        viewModelScope.launch(Dispatchers.IO) {
            if (orderScreenUseCase.currentTradePriceState.value == 0.0 && orderScreenUseCase.orderBookMutableStateList.isEmpty()) {
                setCoinDetailWebSocketMessageListener()
                setOrderBookWebSocketMessageListener()
                orderScreenUseCase.initOrderScreen(market)
                updateTicker()
            } else {
                setCoinDetailWebSocketMessageListener()
                setOrderBookWebSocketMessageListener()
                UpBitCoinDetailWebSocket.requestCoinDetailData(market)
                UpBitOrderBookWebSocket.requestOrderBookList(market)
            }
        }
    }

    private fun updateTicker() {
        viewModelScope.launch {
            while (orderScreenUseCase.isTickerSocketRunning) {
                val tradPrice = orderScreenUseCase.coinDetailModel.tradePrice
                currentTradePriceState = tradPrice
                chartUseCase.updateCandleTicker(tradPrice)
                delay(100)
            }
        }
    }

    /**
     * orderScreen
     * */

    private var isTickerSocketRunning: Boolean
        set(value) {
            orderScreenUseCase.isTickerSocketRunning = value
        }
        get() {
            return orderScreenUseCase.isTickerSocketRunning
        }

    val orderBookMutableStateList: SnapshotStateList<CoinDetailOrderBookModel>
        get() = orderScreenUseCase.orderBookMutableStateList

    val askBidSelectedTab: MutableState<Int> get() = orderScreenUseCase.askBidSelectedTab

    val askQuantity: MutableState<String> get()  = orderScreenUseCase.askQuantity

    val bidQuantity: MutableState<String> get() = orderScreenUseCase.bidQuantity

    var currentTradePriceState: Double
        set(value) {
            orderScreenUseCase.currentTradePriceState.value = value
        }
        get() {
            return orderScreenUseCase.currentTradePriceState.value
        }

    var coinDetailModel: CoinDetailTickerModel
        set(value) {
            orderScreenUseCase.coinDetailModel = value
        }
        get() {
            return orderScreenUseCase.coinDetailModel
        }

    var currentTradePriceStateForOrderBook: Double
        set(value) {
            orderScreenUseCase.currentTradePriceStateForOrderBook.value = value
        }
        get() {
            return orderScreenUseCase.currentTradePriceStateForOrderBook.value
        }

    var userSeedMoney: Long
        set(value) {
            orderScreenUseCase.userSeedMoney.value = value
        }
        get() {
            return orderScreenUseCase.userSeedMoney.value
        }

    var userCoinQuantity: Double
        set(value) {
            orderScreenUseCase.userCoinQuantity.value = value
        }
        get() {
            return orderScreenUseCase.userCoinQuantity.value
        }

    var askBidDialogState: Boolean
        set(value) {
            orderScreenUseCase.askBidDialogState.value = value
        }
        get() {
            return orderScreenUseCase.askBidDialogState.value
        }

    var totalPriceDesignated: String
        set(value) {
            orderScreenUseCase.totalPriceDesignated.value = value
        }get() {
            return orderScreenUseCase.totalPriceDesignated.value
        }

    fun bidRequest(currentPrice: Double, quantity: Double, totalPrice: Long): Job {
        return viewModelScope.launch(Dispatchers.IO) {
            orderScreenUseCase.bidRequest(market,koreanName,currentPrice,quantity,totalPrice)
        }
    }

    fun askRequest(quantity: Double, totalPrice: Long): Job {
        return viewModelScope.launch(Dispatchers.IO) {
            orderScreenUseCase.askRequest(market,quantity,totalPrice)
        }
    }

    /**
     * coinInfo
     * */
    fun getCoinInfo() {
        coinInfoDialog.value = true
        val mDatabase = FirebaseDatabase.getInstance().reference
        mDatabase.child("coinInfo").child(market)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val coinInfoHashMap = HashMap<String, String>()
                    val homepage = snapshot.child("homepage").getValue(String::class.java) ?: ""
                    val amount = snapshot.child("amount").getValue(String::class.java) ?: ""
                    val twitter = snapshot.child("twitter").getValue(String::class.java) ?: ""
                    val block = snapshot.child("block").getValue(String::class.java) ?: ""
                    val info = snapshot.child("content").getValue(String::class.java) ?: ""

                    if (homepage.isEmpty()) {
                        _coinInfoMutableLiveData.postValue(coinInfoHashMap)
                    } else {
                        coinInfoHashMap["homepage"] = homepage
                        coinInfoHashMap["amount"] = amount
                        coinInfoHashMap["twitter"] = twitter
                        coinInfoHashMap["block"] = block
                        coinInfoHashMap["info"] = info
                        _coinInfoMutableLiveData.postValue(coinInfoHashMap)
                    }
                    coinInfoDialog.value = false
                    coinInfoLoading.value = true
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e("firebase error", error.message)
                    coinInfoDialog.value = false
                }
            })
    }

    /**
     * chartScreen
     * */
    var minuteVisible: Boolean
        set(value) {
            chartUseCase.minuteVisible.value = value
        }
        get() {
            return chartUseCase.minuteVisible.value
        }

    var loadingMoreChartData: Boolean
        set(value) {
            chartUseCase.loadingMoreChartData = value
        }
        get() {
            return chartUseCase.loadingMoreChartData
        }

    var candleType: String
        set(value) {
            chartUseCase.candleType.value = value
        }
        get() {
            return chartUseCase.candleType.value
        }

    var minuteText: String
        set(value) {
            chartUseCase.minuteText.value = value
        }
        get() {
            return chartUseCase.minuteText.value
        }

    var selectedButton: Int
        set(value) {
            chartUseCase.selectedButton.value = value
        }
        get() {
            return chartUseCase.selectedButton.value
        }

    var chartLastData: Boolean
        set(value) {
            chartUseCase.chartLastData = value
        }
        get() {
            return chartUseCase.chartLastData
        }

    var dialogState: Boolean
        set(value) {
            chartUseCase.dialogState.value = value
        }
        get() {
            return chartUseCase.dialogState.value
        }

    var candlePosition: Float
        set(value) {
            chartUseCase.candlePosition = value
        }
        get() {
            return chartUseCase.candlePosition
        }

    var isUpdateChart: Boolean
        set(value) {
            chartUseCase.isUpdateChart = value
        }
        get() {
            return chartUseCase.isUpdateChart
        }

    val accData: HashMap<Int, Double>
        get() {
            return chartUseCase.accData
        }

    val kstDateHashMap: HashMap<Int, String>
        get() {
            return chartUseCase.kstDateHashMap
        }

    val candleEntryLast: CandleEntry
        get() {
            return chartUseCase.getCandleEntryLast()
        }

    val candleEntriesIsEmpty: Boolean
        get() {
            return chartUseCase.candleEntriesIsEmpty()
        }

    val candleUpdateLiveData: LiveData<Int>
        get() {
            return chartUseCase.candleUpdateLiveData
        }

    fun requestMoreData(combinedChart: CombinedChart) {
        viewModelScope.launch {
            chartUseCase.requestMoreData(combinedChart = combinedChart, market = market)
        }
    }

    fun requestChartData(combinedChart: CombinedChart) {
        viewModelScope.launch {
            chartUseCase.requestChartData(combinedChart = combinedChart, market = market)
        }
    }


    /**
     * Listener
     * */

    override fun onCoinDetailMessageReceiveListener(tickerJsonObject: String) {
        if (isTickerSocketRunning) {
            val model = gson.fromJson(tickerJsonObject, CoinDetailTickerModel::class.java)
            coinDetailModel = model
            currentTradePriceStateForOrderBook = coinDetailModel.tradePrice
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