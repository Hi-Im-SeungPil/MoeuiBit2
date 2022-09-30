package org.jeonfeel.moeuibit2.activity.coindetail.viewmodel

import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateListOf
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
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.jeonfeel.moeuibit2.activity.coindetail.viewmodel.usecase.ChartUseCase
import org.jeonfeel.moeuibit2.activity.coindetail.viewmodel.usecase.OrderScreenUseCase
import org.jeonfeel.moeuibit2.constant.*
import org.jeonfeel.moeuibit2.data.local.room.entity.TransactionInfo
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
import org.jeonfeel.moeuibit2.util.EtcUtils
import javax.inject.Inject
import kotlin.collections.set

@HiltViewModel
class CoinDetailViewModel @Inject constructor(
    private val remoteRepository: RemoteRepository,
    private val chartUseCase: ChartUseCase,
    private val orderScreenUseCase: OrderScreenUseCase,
    val localRepository: LocalRepository,
) : ViewModel(), OnCoinDetailMessageReceiveListener,
    OnOrderBookMessageReceiveListener {

    var preClosingPrice = 0.0
    var maxOrderBookSize = 0.0
    var market = ""
    private var marketState = -1
    private var koreanName = ""

    val gson = Gson()
    val transactionInfoList = mutableStateListOf<TransactionInfo>()

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
        this.marketState = EtcUtils.getSelectedMarket(market)
        Log.e("tradPrice1", marketState.toString())
    }

    private fun setCoinDetailWebSocketMessageListener() {
        UpBitCoinDetailWebSocket.getListener().setTickerMessageListener(this)
    }

    fun setOrderBookWebSocketMessageListener() {
        UpBitOrderBookWebSocket.getListener().setOrderBookMessageListener(this)
    }

    fun initOrderScreen() {
        if (orderScreenUseCase.currentTradePriceState.value == 0.0 && orderScreenUseCase.orderBookMutableStateList.isEmpty()) {
            setCoinDetailWebSocketMessageListener()
            setOrderBookWebSocketMessageListener()
            orderScreenUseCase.initOrderScreen(market)
            updateTicker()
        } else {
            setCoinDetailWebSocketMessageListener()
            setOrderBookWebSocketMessageListener()
            UpBitCoinDetailWebSocket.requestCoinDetailData(market)
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

    val orderBookMutableStateList: SnapshotStateList<CoinDetailOrderBookModel>
        get() = orderScreenUseCase.orderBookMutableStateList
    val errorDialogState: MutableState<Boolean> get() = orderScreenUseCase.errorDialogState
    val askBidSelectedTab: MutableState<Int> get() = orderScreenUseCase.askBidSelectedTab
    val askQuantity: MutableState<String> get() = orderScreenUseCase.askQuantity
    val bidQuantity: MutableState<String> get() = orderScreenUseCase.bidQuantity
    val btcQuantity: MutableState<Double> get() = orderScreenUseCase.btcQuantity
    val currentBTCPrice: MutableState<Double> get() = orderScreenUseCase.currentBTCPrice

    private var isTickerSocketRunning: Boolean
        set(value) {
            orderScreenUseCase.isTickerSocketRunning = value
        }
        get() {
            return orderScreenUseCase.isTickerSocketRunning
        }

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
        }
        get() {
            return orderScreenUseCase.totalPriceDesignated.value
        }

    fun bidRequest(currentPrice: Double, quantity: Double, totalPrice: Long): Job {
        return viewModelScope.launch(ioDispatcher) {
            orderScreenUseCase.bidRequest(market, koreanName, currentPrice, quantity, totalPrice)
        }
    }

    fun askRequest(quantity: Double, totalPrice: Long, currentPrice: Double): Job {
        return viewModelScope.launch(ioDispatcher) {
            orderScreenUseCase.askRequest(market, quantity, totalPrice, currentPrice)
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
                    val homepage = snapshot.child(INFO_HOMEPAGE_KEY).getValue(String::class.java) ?: ""
                    val amount = snapshot.child(INFO_AMOUNT_KEY).getValue(String::class.java) ?: ""
                    val twitter = snapshot.child(INFO_TWITTER_KEY).getValue(String::class.java) ?: ""
                    val block = snapshot.child(INFO_BLOCK_KEY).getValue(String::class.java) ?: ""
                    val info = snapshot.child(INFO_INFO_KEY).getValue(String::class.java) ?: ""

                    if (homepage.isEmpty()) {
                        _coinInfoMutableLiveData.postValue(coinInfoHashMap)
                    } else {
                        coinInfoHashMap[INFO_HOMEPAGE_KEY] = homepage
                        coinInfoHashMap[INFO_AMOUNT_KEY] = amount
                        coinInfoHashMap[INFO_TWITTER_KEY] = twitter
                        coinInfoHashMap[INFO_BLOCK_KEY] = block
                        coinInfoHashMap[INFO_INFO_KEY] = info
                        _coinInfoMutableLiveData.postValue(coinInfoHashMap)
                    }
                    coinInfoDialog.value = false
                    coinInfoLoading.value = true
                }

                override fun onCancelled(error: DatabaseError) {
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

    val dialogState: MutableState<Boolean>
        get() {
            return chartUseCase.dialogState
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
     * transactionInfo
     */
    suspend fun getTransactionInfoList() {
        transactionInfoList.clear()
        val job = viewModelScope.async(ioDispatcher) {
            localRepository.getTransactionInfoDao().select(market)
        }
        for (i in job.await()) {
            transactionInfoList.add(i)
        }
    }


    /**
     * Listener
     * */
    override fun onCoinDetailMessageReceiveListener(tickerJsonObject: String) {
        if (isTickerSocketRunning) {
            val model = gson.fromJson(tickerJsonObject, CoinDetailTickerModel::class.java)
            Log.e("tradPrice1", model.code)
            if (marketState == SELECTED_BTC_MARKET && model.code.startsWith("KRW-")) {
                Log.e("tradPrice", model.tradePrice.toString())
                currentBTCPrice.value = model.tradePrice
            } else {
                coinDetailModel = model
                currentTradePriceStateForOrderBook = coinDetailModel.tradePrice
            }
        }
    }

    override fun onOrderBookMessageReceiveListener(orderBookJsonObject: String) {
        if (isTickerSocketRunning) {
            var index = 0
            val modelOBj = gson.fromJson(orderBookJsonObject, JsonObject::class.java)
            val modelJsonArray = modelOBj.getAsJsonArray("obu")
            val indices = modelJsonArray.size()
            if (orderBookMutableStateList.isNotEmpty()) {
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
            } else {
                for (i in indices - 1 downTo 0) {
                    val orderBookAskModel =
                        gson.fromJson(modelJsonArray[i], CoinDetailOrderBookAskModel::class.java)
                    orderBookMutableStateList.add(
                        CoinDetailOrderBookModel(
                            orderBookAskModel.ask_price,
                            orderBookAskModel.ask_size,
                            0
                        )
                    )
                    index++
                }
                for (i in 0 until indices) {
                    val orderBookBidModel =
                        gson.fromJson(modelJsonArray[i], CoinDetailOrderBookBidModel::class.java)
                    orderBookMutableStateList.add(
                        CoinDetailOrderBookModel(
                            orderBookBidModel.bid_price,
                            orderBookBidModel.bid_size,
                            1
                        )
                    )
                    index++
                }
            }
            maxOrderBookSize = orderBookMutableStateList.maxOf { it.size }
        }
    }
}