package org.jeonfeel.moeuibit2.viewmodel

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.google.gson.JsonObject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
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
import org.jeonfeel.moeuibit2.repository.OrderBookRepository
import javax.inject.Inject

@HiltViewModel
class CoinDetailViewModel @Inject constructor(
    private val orderBookRepository: OrderBookRepository,
) : ViewModel(), OnTickerMessageReceiveListener,
    OnOrderBookMessageReceiveListener {

    var preClosingPrice = 0.0
    val gson = Gson()
    private val isTickerSocketRunning = true
    val isOrderBookSocketRunning = true
    val priceState = mutableStateOf(0.0)
    val orderBook = mutableStateListOf<CoinDetailOrderBookModel>()
    var coinDetailModel = CoinDetailTickerModel("", 0.0, 0.0, 0.0)

    fun initOrder(market: String, preClosingPrice: Double) {
        setWebSocketMessageListener()
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
        UpBitOrderBookWebSocket.getListener().setOrderBookMessageListener(this)
    }

    private suspend fun initOrderBook(market: String) {
        val response = orderBookRepository.getOrderBookService(market)
        if (response.isSuccessful) {
            val body = response.body()
            val a = body?.first() ?: JsonObject()
            val modelOBj = gson.fromJson(a, JsonObject::class.java)
            val modelJsonArray = modelOBj.getAsJsonArray("orderbook_units")
            for (i in modelJsonArray.size() - 1 downTo 0) {
                val orderBookAskModel =
                    gson.fromJson(modelJsonArray[i],
                        CoinDetailOrderBookAskRetrofitModel::class.java)
                orderBook.add(CoinDetailOrderBookModel(orderBookAskModel.ask_price,
                    orderBookAskModel.ask_size,
                    0))
            }
            for (i in 0 until modelJsonArray.size()) {
                val orderBookBidModel =
                    gson.fromJson(modelJsonArray[i],
                        CoinDetailOrderBookBidRetrofitModel::class.java)
                orderBook.add(CoinDetailOrderBookModel(orderBookBidModel.bid_price,
                    orderBookBidModel.bid_size,
                    1))
            }
        }
    }

    private fun updateTicker() {
        viewModelScope.launch {
            while (isTickerSocketRunning) {
                priceState.value = coinDetailModel.tradePrice
                delay(100)
            }
        }
    }

    override fun onTickerMessageReceiveListener(tickerJsonObject: String) {
        if (isTickerSocketRunning) {
            val model = gson.fromJson(tickerJsonObject, CoinDetailTickerModel::class.java)
            coinDetailModel = model
            priceState.value = model.tradePrice
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
                orderBook[index] = CoinDetailOrderBookModel(orderBookAskModel.ask_price,
                    orderBookAskModel.ask_size,
                    0)
                index++
            }
            for (i in 0 until modelJsonArray.size()) {
                val orderBookBidModel =
                    gson.fromJson(modelJsonArray[i], CoinDetailOrderBookBidModel::class.java)
                orderBook[index] = CoinDetailOrderBookModel(orderBookBidModel.bid_price,
                    orderBookBidModel.bid_size,
                    1)
                index++
            }
        }
    }
}