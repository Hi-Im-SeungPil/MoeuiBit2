package org.jeonfeel.moeuibit2.ui.viewmodels

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.github.mikephil.charting.charts.CombinedChart
import com.github.mikephil.charting.data.CandleEntry
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.gson.JsonObject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.jeonfeel.moeuibit2.constants.*
import org.jeonfeel.moeuibit2.data.local.room.entity.TransactionInfo
import org.jeonfeel.moeuibit2.data.remote.websocket.UpBitCoinDetailWebSocket
import org.jeonfeel.moeuibit2.data.remote.websocket.UpBitOrderBookWebSocket
import org.jeonfeel.moeuibit2.data.remote.websocket.listener.OnCoinDetailMessageReceiveListener
import org.jeonfeel.moeuibit2.data.remote.websocket.listener.OnOrderBookMessageReceiveListener
import org.jeonfeel.moeuibit2.data.remote.websocket.model.CoinDetailOrderBookAskModel
import org.jeonfeel.moeuibit2.data.remote.websocket.model.CoinDetailOrderBookBidModel
import org.jeonfeel.moeuibit2.data.remote.websocket.model.CoinDetailOrderBookModel
import org.jeonfeel.moeuibit2.data.remote.websocket.model.CoinDetailTickerModel
import org.jeonfeel.moeuibit2.data.repository.local.LocalRepository
import org.jeonfeel.moeuibit2.data.repository.remote.RemoteRepository
import org.jeonfeel.moeuibit2.ui.base.BaseViewModel
import org.jeonfeel.moeuibit2.ui.coindetail.chart.Chart
import org.jeonfeel.moeuibit2.utils.Utils
import javax.inject.Inject
import kotlin.collections.set

class CoinDetailState {
    val favoriteMutableState = mutableStateOf(false)

    // 코인 정보
    val coinInfoDialog = mutableStateOf(false)
    val coinInfoLoading = mutableStateOf(false)
    val webViewLoading = mutableStateOf(false)

    // 차트

}

@HiltViewModel
class CoinDetailViewModel @Inject constructor(
    private val remoteRepository: RemoteRepository,
    private val chartUseCase: ChartUseCase,
    private val orderScreenUseCase: OrderScreenUseCase,
    val chart: Chart,
    val localRepository: LocalRepository,
) : BaseViewModel(),
    OnCoinDetailMessageReceiveListener,
    OnOrderBookMessageReceiveListener {
    var market = ""
    var preClosingPrice = 0.0
    var maxOrderBookSize = 0.0
    private var marketState = -1
    private var name = ""

    val transactionInfoList = mutableStateListOf<TransactionInfo>()

    /**
     * coin info
     * */
    private val _coinInfoMutableLiveData = MutableLiveData<HashMap<String, String>>()
    val coinInfoLiveData: LiveData<HashMap<String, String>> get() = _coinInfoMutableLiveData
    val coinInfoDialog = mutableStateOf(false)
    val coinInfoLoading = mutableStateOf(false)
    val webViewLoading = mutableStateOf(false)

    /**
     * favorite
     * */
    val favoriteMutableState = mutableStateOf(false)

    fun initViewModel(market: String, preClosingPrice: Double, isFavorite: Boolean) {
        this.market = market
        this.preClosingPrice = preClosingPrice
        this.favoriteMutableState.value = isFavorite
        this.marketState = Utils.getSelectedMarket(market)
        chart.market = market
        orderScreenUseCase.initAdjustFee()
    }

    private fun setCoinDetailWebSocketMessageListener() {
        UpBitCoinDetailWebSocket.getListener().setTickerMessageListener(this)
    }

    fun setOrderBookWebSocketMessageListener() {
        UpBitOrderBookWebSocket.getListener().setOrderBookMessageListener(this)
    }

    fun initOrderScreen() {
        if (orderScreenUseCase.currentTradePriceState.value == 0.0 && orderScreenUseCase.orderBookMutableStateList.isEmpty()) {
            viewModelScope.launch(ioDispatcher) {
                setCoinDetailWebSocketMessageListener()
                setOrderBookWebSocketMessageListener()
                orderScreenUseCase.initOrderScreen(market)
                updateTicker()
            }
        } else {
            setCoinDetailWebSocketMessageListener()
            setOrderBookWebSocketMessageListener()
            UpBitCoinDetailWebSocket.requestCoinDetailData(market)
        }
        for (i in 0 until 4) {
            feeStateList.add(mutableStateOf(0f))
        }
    }

    private fun updateTicker() {
        viewModelScope.launch {
            while (orderScreenUseCase.isTickerSocketRunning) {
                val tradPrice = orderScreenUseCase.coinDetailModel.tradePrice
                currentTradePriceState = tradPrice
                chart.updateCandleTicker(tradPrice)
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
    val orderScreenLoadingState get() = orderScreenUseCase.orderScreenLoadingState
    val isShowAdjustFeeDialog get() = orderScreenUseCase.isShowAdjustFeeDialog
    val feeStateList get() = orderScreenUseCase.feeStateList

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

    fun bidRequest(
        currentPrice: Double,
        quantity: Double,
        totalPrice: Long = 0L,
        btcTotalPrice: Double = 0.0,
        currentBtcPrice: Double = 0.0
    ): Job {
        return viewModelScope.launch(ioDispatcher) {
            orderScreenUseCase.bidRequest(
                market,
                name,
                currentPrice,
                quantity,
                totalPrice,
                btcTotalPrice,
                marketState = marketState,
                currentBtcPrice
            )
        }
    }

    fun askRequest(
        quantity: Double,
        totalPrice: Long,
        currentPrice: Double,
        btcTotalPrice: Double = 0.0,
    ): Job {
        return viewModelScope.launch(ioDispatcher) {
            orderScreenUseCase.askRequest(
                market,
                quantity,
                totalPrice,
                btcTotalPrice,
                currentPrice,
                marketState
            )
        }
    }

    fun adjustFee() {
        viewModelScope.launch(ioDispatcher) {
            orderScreenUseCase.adjustFee()
        }
    }

    fun initAdjustFee() {
        orderScreenUseCase.initAdjustFee()
    }

    fun getFee(key: String): Float {
        return orderScreenUseCase.getFee(key)
    }

    /**
     * coinInfo
     * */
    fun getCoinInfo() {
        coinInfoDialog.value = true
        val mDatabase = FirebaseDatabase.getInstance().reference
        mDatabase.child("secondCoinInfo").child(market.substring(4))
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val coinInfoHashMap = HashMap<String, String>()
                    val homepage =
                        snapshot.child(INFO_HOMEPAGE_KEY).getValue(String::class.java) ?: ""
                    val amount = snapshot.child(INFO_AMOUNT_KEY).getValue(String::class.java) ?: ""
                    val twitter =
                        snapshot.child(INFO_TWITTER_KEY).getValue(String::class.java) ?: ""
                    val block = snapshot.child(INFO_BLOCK_KEY).getValue(String::class.java) ?: ""
                    val info = snapshot.child(INFO_INFO_KEY).getValue(String::class.java) ?: ""

//                    Log.e("infos","$homepage, $amount, $twitter, $block, $info")
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

    fun requestOldData(
        positiveBarDataSet: IBarDataSet,
        negativeBarDataSet: IBarDataSet,
        candleXMin: Float
    ) {
        viewModelScope.launch {
            chart.requestOldData(
                positiveBarDataSet = positiveBarDataSet,
                negativeBarDataSet = negativeBarDataSet,
                candleXMin = candleXMin
            )
        }
    }
//    var minuteVisible: Boolean
//        set(value) {
//            chartUseCase.minuteVisible.value = value
//        }
//        get() {
//            return chartUseCase.minuteVisible.value
//        }
//
//    var loadingMoreChartData: Boolean
//        set(value) {
//            chartUseCase.loadingMoreChartData = value
//        }
//        get() {
//            return chartUseCase.loadingMoreChartData
//        }
//
//    var candleType: String
//        set(value) {
//            chartUseCase.candleType.value = value
//        }
//        get() {
//            return chartUseCase.candleType.value
//        }
//
//    var minuteText: String
//        set(value) {
//            chartUseCase.minuteText.value = value
//        }
//        get() {
//            return chartUseCase.minuteText.value
//        }
//
//    var selectedButton: Int
//        set(value) {
//            chartUseCase.selectedButton.value = value
//        }
//        get() {
//            return chartUseCase.selectedButton.value
//        }
//
//    var chartLastData: Boolean
//        set(value) {
//            chartUseCase.chartLastData = value
//        }
//        get() {
//            return chartUseCase.chartLastData
//        }
//
//    val dialogState: MutableState<Boolean>
//        get() {
//            return chartUseCase.dialogState
//        }
//
//    var candlePosition: Float
//        set(value) {
//            chartUseCase.candlePosition = value
//        }
//        get() {
//            return chartUseCase.candlePosition
//        }
//
//    var isUpdateChart: Boolean
//        set(value) {
//            chartUseCase.isUpdateChart = value
//        }
//        get() {
//            return chartUseCase.isUpdateChart
//        }
//
//    val accData: HashMap<Int, Double>
//        get() {
//            return chartUseCase.accData
//        }
//
//    val kstDateHashMap: HashMap<Int, String>
//        get() {
//            return chartUseCase.kstDateHashMap
//        }

    val candleEntryLast: CandleEntry
        get() {
            return chartUseCase.getCandleEntryLast()
        }

    val candleEntriesIsEmpty: Boolean
        get() {
            return chartUseCase.candleEntriesIsEmpty()
        }

//    val candleUpdateLiveData: LiveData<Int>
//        get() {
//            return chartUseCase.candleUpdateLiveData
//        }

    fun requestMoreData(combinedChart: CombinedChart) {
        viewModelScope.launch {
            chartUseCase.requestMoreData(combinedChart = combinedChart, market = market)
        }
    }

    fun requestChartData() {
        viewModelScope.launch {
            chart.requestChartData(market = market)
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
//            Log.e("tradPrice1", model.code)
            if (marketState == SELECTED_BTC_MARKET && model.code.startsWith(SYMBOL_KRW)) {
//                Log.e("tradPrice", model.tradePrice.toString())
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