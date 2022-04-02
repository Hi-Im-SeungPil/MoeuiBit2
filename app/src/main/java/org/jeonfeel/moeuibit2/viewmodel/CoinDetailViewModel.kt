package org.jeonfeel.moeuibit2.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.jeonfeel.moeuibit2.data.remote.websocket.UpBitWebSocket
import org.jeonfeel.moeuibit2.data.remote.websocket.model.CoinDetailTickerModel
import org.jeonfeel.moeuibit2.listener.OnMessageReceiveListener

class CoinDetailViewModel : ViewModel(), OnMessageReceiveListener {

    val gson = Gson()
    val isSocketRunning = true
    val priceState = mutableStateOf(0.0)
    var coinDetailModel = CoinDetailTickerModel("", 0.0, 0.0, 0.0)

    init {
        setWebSocketMessageListener()
    }

    fun setWebSocketMessageListener() {
        UpBitWebSocket.getListener().setMessageListener1(this)
    }

    fun requestCoinTicker(market: String) {
        UpBitWebSocket.requestKrwCoin(market)
        update()
    }

    fun update() {
        viewModelScope.launch{
            while (isSocketRunning) {
                priceState.value = coinDetailModel.tradePrice
                delay(100)
            }
        }
    }

    override fun onMessageReceiveListener(tickerJsonObject: String) {
        if (isSocketRunning) {
            val model = gson.fromJson(tickerJsonObject, CoinDetailTickerModel::class.java)
            coinDetailModel = model
        }
    }
}