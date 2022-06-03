package org.jeonfeel.moeuibit2.viewmodel.coindetail

import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
import org.jeonfeel.moeuibit2.data.local.room.entity.MyCoin
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
import org.jeonfeel.moeuibit2.util.Calculator
import org.jeonfeel.moeuibit2.viewmodel.coindetail.usecase.ChartUseCase
import javax.inject.Inject
import kotlin.collections.set
import kotlin.math.round
import kotlin.math.roundToLong

@HiltViewModel
class CoinDetailViewModel @Inject constructor(
    private val remoteRepository: RemoteRepository,
    private val chartUseCase: ChartUseCase,
    val localRepository: LocalRepository,
) : ViewModel(), OnCoinDetailMessageReceiveListener,
    OnOrderBookMessageReceiveListener {

    var preClosingPrice = 0.0
    var maxOrderBookSize = 0.0
    var market = ""
    var koreanName = ""
    var coinDetailModel = CoinDetailTickerModel("", 0.0, 0.0, 0.0)

    val gson = Gson()
    private val isTickerSocketRunning = true

    val currentTradePriceState = mutableStateOf(0.0)
    val currentTradePriceStateForOrderBook = mutableStateOf(0.0)
    val orderBookMutableStateList = mutableStateListOf<CoinDetailOrderBookModel>()

    val askBidSelectedTab = mutableStateOf(1)
    val userSeedMoney = mutableStateOf(0L)
    val userCoinQuantity = mutableStateOf(0.0)
    val bidDropdownText = mutableStateOf("")
    val askDropdownText = mutableStateOf("")
    val bidQuantity = mutableStateOf("")
    val askQuantity = mutableStateOf("")
    val askBidDialogState = mutableStateOf(false)

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
        this.koreanName = koreanName
    }

    /**
     * orderScreen
     * */
    fun initOrder() {
        if (currentTradePriceState.value == 0.0 && orderBookMutableStateList.isEmpty()) {
            setCoinDetailWebSocketMessageListener()
            setOrderBookWebSocketMessageListener()
            viewModelScope.launch {
                UpBitCoinDetailWebSocket.market = market
                UpBitOrderBookWebSocket.market = market
                UpBitCoinDetailWebSocket.requestCoinDetailData(market)
                updateTicker()
                initOrderBook(market)
                UpBitOrderBookWebSocket.requestOrderBookList(market)
                localRepository.getUserDao().all.let {
                    userSeedMoney.value = it?.krw ?: 0L
                }
                localRepository.getMyCoinDao().isInsert(market).let {
                    userCoinQuantity.value = it?.quantity ?: 0.0
                }
            }
        } else {
            setCoinDetailWebSocketMessageListener()
            setOrderBookWebSocketMessageListener()
            UpBitCoinDetailWebSocket.requestCoinDetailData(market)
            UpBitOrderBookWebSocket.requestOrderBookList(market)
        }
    }

    private fun setCoinDetailWebSocketMessageListener() {
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
                chartUseCase.updateCandleTicker(tradPrice)
                delay(100)
            }
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
                    val coinInfos = HashMap<String, String>()
                    val homepage = snapshot.child("homepage").getValue(String::class.java) ?: ""
                    val amount = snapshot.child("amount").getValue(String::class.java) ?: ""
                    val twitter = snapshot.child("twitter").getValue(String::class.java) ?: ""
                    val block = snapshot.child("block").getValue(String::class.java) ?: ""
                    val info = snapshot.child("content").getValue(String::class.java) ?: ""

                    if (homepage.isEmpty()) {
                        _coinInfoMutableLiveData.postValue(coinInfos)
                    } else {
                        coinInfos["homepage"] = homepage
                        coinInfos["amount"] = amount
                        coinInfos["twitter"] = twitter
                        coinInfos["block"] = block
                        coinInfos["info"] = info
                        _coinInfoMutableLiveData.postValue(coinInfos)
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

    fun bidRequest(currentPrice: Double, quantity: Double, totalPrice: Long): Job {
        val job = viewModelScope.launch(Dispatchers.IO) {
            val coinDao = localRepository.getMyCoinDao()
            val userDao = localRepository.getUserDao()
            val symbol = market.substring(4)
            val myCoin: MyCoin? = coinDao.isInsert(market)

            if (myCoin == null) {
                coinDao.insert(
                    MyCoin(
                        market,
                        currentPrice,
                        koreanName,
                        symbol,
                        quantity
                    )
                )
                userDao.updateMinusMoney((totalPrice + (totalPrice * 0.0005)).roundToLong())
                userSeedMoney.value = userDao.all?.krw ?: 0L
                bidQuantity.value = ""
                userCoinQuantity.value = coinDao.isInsert(market)?.quantity ?: 0.0
            } else {
                val preAveragePurchasePrice = myCoin.purchasePrice
                val preCoinQuantity = myCoin.quantity
                val purchaseAverage = Calculator.averagePurchasePriceCalculator(
                    currentPrice,
                    quantity,
                    preAveragePurchasePrice,
                    preCoinQuantity
                )

                if (purchaseAverage >= 100) {
                    coinDao.updatePurchasePriceInt(market, purchaseAverage.toInt())
                } else {
                    coinDao.updatePurchasePrice(market, purchaseAverage)
                }

                coinDao.updatePlusQuantity(market, quantity)
                userDao.updateMinusMoney((totalPrice + (totalPrice * 0.0005)).roundToLong())
                userSeedMoney.value = userDao.all?.krw ?: 0L
                bidQuantity.value = ""
                userCoinQuantity.value = coinDao.isInsert(market)?.quantity ?: 0.0
            }
        }
        return job
    }

    fun askRequest(quantity: Double, totalPrice: Long): Job {
        val job = viewModelScope.launch(Dispatchers.IO) {
            val coinDao = localRepository.getMyCoinDao()
            val userDao = localRepository.getUserDao()

            coinDao.updateMinusQuantity(market, quantity)
            userDao.updatePlusMoney((totalPrice - (totalPrice * 0.0005)).roundToLong())
            userSeedMoney.value = userDao.all?.krw ?: 0L
            askQuantity.value = ""
            val currentCoin = coinDao.isInsert(market)
            if (currentCoin != null && currentCoin.quantity == 0.0) {
                coinDao.delete(market)
                userCoinQuantity.value = 0.0
            } else {
                userCoinQuantity.value = currentCoin?.quantity ?: 0.0
            }
        }
        return job
    }

    override fun onCoinDetailMessageReceiveListener(tickerJsonObject: String) {
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