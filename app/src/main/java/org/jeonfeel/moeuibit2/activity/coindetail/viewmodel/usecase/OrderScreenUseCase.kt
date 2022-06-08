package org.jeonfeel.moeuibit2.activity.coindetail.viewmodel.usecase

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.google.gson.JsonObject
import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.jeonfeel.moeuibit2.data.remote.retrofit.model.CoinDetailOrderBookAskRetrofitModel
import org.jeonfeel.moeuibit2.data.remote.retrofit.model.CoinDetailOrderBookBidRetrofitModel
import org.jeonfeel.moeuibit2.data.remote.websocket.UpBitCoinDetailWebSocket
import org.jeonfeel.moeuibit2.data.remote.websocket.UpBitOrderBookWebSocket
import org.jeonfeel.moeuibit2.data.remote.websocket.model.CoinDetailOrderBookModel
import org.jeonfeel.moeuibit2.data.remote.websocket.model.CoinDetailTickerModel
import org.jeonfeel.moeuibit2.repository.local.LocalRepository
import org.jeonfeel.moeuibit2.repository.remote.RemoteRepository
import javax.inject.Inject

@ViewModelScoped
class OrderScreenUseCase @Inject constructor(
    private val remoteRepository: RemoteRepository,
    private val localRepository: LocalRepository
) {
    val gson = Gson()
    private val isTickerSocketRunning = true

    private val currentTradePriceState = mutableStateOf(0.0)
    val currentTradePriceStateForOrderBook = mutableStateOf(0.0)
    private val orderBookMutableStateList = mutableStateListOf<CoinDetailOrderBookModel>()

    val askBidSelectedTab = mutableStateOf(1)
    private val userSeedMoney = mutableStateOf(0L)
    private val userCoinQuantity = mutableStateOf(0.0)
    val bidQuantity = mutableStateOf("")
    val askQuantity = mutableStateOf("")
    val askBidDialogState = mutableStateOf(false)
    var coinDetailModel = CoinDetailTickerModel("", 0.0, 0.0, 0.0)

    suspend fun initOrder(market: String) {
        if (currentTradePriceState.value == 0.0 && orderBookMutableStateList.isEmpty()) {
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
        } else {
            UpBitCoinDetailWebSocket.requestCoinDetailData(market)
            UpBitOrderBookWebSocket.requestOrderBookList(market)
        }
    }

    suspend fun initOrderBook(market: String) {
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
}