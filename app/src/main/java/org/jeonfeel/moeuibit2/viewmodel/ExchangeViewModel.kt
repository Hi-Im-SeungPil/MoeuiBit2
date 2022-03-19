package org.jeonfeel.moeuibit2.viewmodel

import android.util.Log
import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.google.gson.JsonArray
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.jeonfeel.moeuibit2.ExchangeModel
import org.jeonfeel.moeuibit2.KrwExchangeModel
import org.jeonfeel.moeuibit2.TickerModel
import org.jeonfeel.moeuibit2.data.remote.retrofit.model.MarketCodeModel
import org.jeonfeel.moeuibit2.data.remote.websocket.UpBitWebSocket
import org.jeonfeel.moeuibit2.listener.OnMessageReceiveListener
import org.jeonfeel.moeuibit2.repository.ExchangeViewModelRepository
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ExchangeViewModel : ViewModel(), OnMessageReceiveListener {

    private val TAG = ExchangeViewModel::class.java.simpleName
    private val repository = ExchangeViewModelRepository()
    private val gson = Gson()

    private val krwMarketCodeList: ArrayList<MarketCodeModel> = arrayListOf()
    private val krwTickerList: ArrayList<ExchangeModel> = arrayListOf()
    private val krwExchangeModelList: ArrayList<KrwExchangeModel> = arrayListOf()

    private val krwCoinListStringBuffer = StringBuffer()

    val krwExchangeModelListPosition: HashMap<String, Int> = hashMapOf()

    var krwExchangeModelMutableStateList = mutableStateListOf<KrwExchangeModel>()
    var searchTextFieldValue = mutableStateOf("")

    init {
        UpBitWebSocket.getListener().setMessageListener1(this)
        requestKrwMarketCode()
    }

    // get market, koreanName, englishName, warning
    private fun requestKrwMarketCode() {
        repository.getMarketCodeCall().enqueue(object : Callback<JsonArray> {
            override fun onResponse(call: Call<JsonArray>, response: Response<JsonArray>) {
                val jsonArray = response.body()
                for (i in 0 until jsonArray!!.size()) {
                    val krwMarketCode = gson.fromJson(jsonArray[i], MarketCodeModel::class.java)
                    if (krwMarketCode.market.contains("KRW-")) {
                        krwCoinListStringBuffer.append("${krwMarketCode.market},")
                        krwMarketCodeList.add(krwMarketCode)
                    }
                }
                krwCoinListStringBuffer.deleteCharAt(krwCoinListStringBuffer.lastIndex)
                requestKrwTicker(krwCoinListStringBuffer.toString())
            }

            override fun onFailure(call: Call<JsonArray>, t: Throwable) {
                Log.e(TAG, "ERROR => ${t.message}")
            }
        })
    }

    // get market, tradePrice, signed_change_price, acc_trade_price_24h
    private fun requestKrwTicker(markets: String) {
        Log.e(TAG, markets)
        repository.getKrwTickerCall(markets).enqueue(object : Callback<JsonArray> {
            override fun onResponse(call: Call<JsonArray>, response: Response<JsonArray>) {
                val jsonArray = response.body()
                Log.e(TAG, response.body().toString())
                for (i in 0 until jsonArray!!.size()) {
                    val krwTicker = gson.fromJson(jsonArray[i], ExchangeModel::class.java)
                    krwTickerList.add(krwTicker)
                }
                createKrwExchangeModelList()
            }

            override fun onFailure(call: Call<JsonArray>, t: Throwable) {
                Log.e(TAG, "ERROR => ${t.message}")
            }
        })
    }

    private fun createKrwExchangeModelList() {
        for (i in 0 until krwMarketCodeList.size) {
            val koreanName = krwMarketCodeList[i].korean_name
            val englishName = krwMarketCodeList[i].english_name
            val market = krwMarketCodeList[i].market
            val tradePrice = krwTickerList[i].tradePrice
            val signedChangeRate = krwTickerList[i].signedChangePrice
            val accTradePrice24h = krwTickerList[i].accTradePrice24h
            val symbol = market.substring(4)
            krwExchangeModelList.add(KrwExchangeModel(koreanName,
                englishName,
                market,
                symbol,
                tradePrice,
                signedChangeRate,
                accTradePrice24h))
            krwExchangeModelListPosition[market] = i
        }
        krwExchangeModelMutableStateList.addAll(krwExchangeModelList)
        UpBitWebSocket.requestKrwCoinList(krwCoinListStringBuffer.toString())
        updateExchange()
    }

    private fun updateExchange() {
        viewModelScope.launch(Dispatchers.Main) {
            while (true){
                UpBitWebSocket.requestKrwCoinList(krwCoinListStringBuffer.toString())
                krwExchangeModelMutableStateList.clear()
                krwExchangeModelMutableStateList.addAll(krwExchangeModelList)
                delay(300)
            }
        }
    }

    override fun onMessageReceiveListener(tickerJsonObject: String) {
        val model = gson.fromJson(tickerJsonObject, TickerModel::class.java)
        val position = krwExchangeModelListPosition[model.cd] ?: -1
        krwExchangeModelList[position] =
            KrwExchangeModel(krwMarketCodeList[position].korean_name,
                krwMarketCodeList[position].english_name,
                model.cd,
                model.cd.substring(4),
                model.tp,
                model.scr,
                model.atp24h)
    }
}