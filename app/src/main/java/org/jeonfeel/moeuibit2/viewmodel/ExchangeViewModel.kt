package org.jeonfeel.moeuibit2.viewmodel

import android.util.Log
import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.google.gson.JsonArray
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.jeonfeel.moeuibit2.data.remote.retrofit.model.ExchangeModel
import org.jeonfeel.moeuibit2.data.remote.retrofit.model.KrwExchangeModel
import org.jeonfeel.moeuibit2.data.remote.retrofit.model.TickerModel
import org.jeonfeel.moeuibit2.data.remote.retrofit.model.MarketCodeModel
import org.jeonfeel.moeuibit2.data.remote.websocket.UpBitWebSocket
import org.jeonfeel.moeuibit2.listener.OnMessageReceiveListener
import org.jeonfeel.moeuibit2.repository.ExchangeViewModelRepository
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject

@HiltViewModel
class ExchangeViewModel @Inject constructor(private val exchangeViewModelRepository: ExchangeViewModelRepository) :
    ViewModel(), OnMessageReceiveListener {

    private val TAG = ExchangeViewModel::class.java.simpleName
    private val gson = Gson()
    var isSocketRunning = true

    private val krwTickerList: ArrayList<ExchangeModel> = arrayListOf()
    private val krwMarketCodeList: ArrayList<MarketCodeModel> = arrayListOf()
    private val krwCoinKoreanNameAndEngName = HashMap<String, List<String>>()
    private val krwCoinListStringBuffer = StringBuffer()

    val krwExchangeModelList: ArrayList<KrwExchangeModel> = arrayListOf()
    val krwExchangeModelListPosition: HashMap<String, Int> = hashMapOf()
    val preItemArray: ArrayList<KrwExchangeModel> = arrayListOf()

    var krwExchangeModelMutableStateList = mutableStateListOf<KrwExchangeModel>()
    var searchTextFieldValue = mutableStateOf("")

    init {
        UpBitWebSocket.getListener().setMessageListener1(this)
        viewModelScope.launch {
            requestKrwMarketCode()
            requestKrwTicker(krwCoinListStringBuffer.toString())
            createKrwExchangeModelList()
            updateExchange()
        }
    }

    // get market, koreanName, englishName, warning
    private suspend fun requestKrwMarketCode() {
        val resultMarketCode = exchangeViewModelRepository.getMarketCodeService()
        if (!resultMarketCode.isJsonNull){
            for (i in 0 until resultMarketCode.size()) {
                val krwMarketCode =
                    gson.fromJson(resultMarketCode[i], MarketCodeModel::class.java)
                if (krwMarketCode.market.contains("KRW-")) {
                    krwCoinListStringBuffer.append("${krwMarketCode.market},")
                    krwMarketCodeList.add(krwMarketCode)
                }
            }
            krwCoinListStringBuffer.deleteCharAt(krwCoinListStringBuffer.lastIndex)
            for (i in 0 until krwMarketCodeList.size) {
                krwCoinKoreanNameAndEngName[krwMarketCodeList[i].market] =
                    listOf(krwMarketCodeList[i].korean_name, krwMarketCodeList[i].english_name)
            }
        } else {
            Log.e(TAG,"requestKrwMarketCode Error!")
        }
    }

    // get market, tradePrice, signed_change_price, acc_trade_price_24h
    private suspend fun requestKrwTicker(markets: String) {
        val resultKrwTicker =
            exchangeViewModelRepository.getKrwTickerService(markets)
        if (!resultKrwTicker.isJsonNull){
            for (i in 0 until resultKrwTicker.size()) {
                val krwTicker = gson.fromJson(resultKrwTicker[i], ExchangeModel::class.java)
                krwTickerList.add(krwTicker)
            }
        } else {
            Log.e(TAG,"requestKrwTicker Error!")
        }
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
        }
        krwExchangeModelList.sortByDescending { it.accTradePrice24h }
        for (i in krwExchangeModelList.indices) {
            krwExchangeModelListPosition[krwExchangeModelList[i].market] = i
        }
        krwExchangeModelMutableStateList.addAll(krwExchangeModelList)
        preItemArray.addAll(krwExchangeModelList)
        UpBitWebSocket.requestKrwCoinList(krwCoinListStringBuffer.toString())
    }

    private fun updateExchange() {
        viewModelScope.launch(Dispatchers.Main) {
            while (isSocketRunning) {
                krwExchangeModelMutableStateList.clear()
                krwExchangeModelMutableStateList.addAll(krwExchangeModelList)
                delay(300)
            }
        }
    }

    override fun onMessageReceiveListener(tickerJsonObject: String) {
        val model = gson.fromJson(tickerJsonObject, TickerModel::class.java)
        val position = krwExchangeModelListPosition[model.code] ?: -1
        krwExchangeModelList[position] =
            KrwExchangeModel(krwCoinKoreanNameAndEngName[model.code]!![0],
                krwCoinKoreanNameAndEngName[model.code]!![1],
                model.code,
                model.code.substring(4),
                model.tradePrice,
                model.signedChangeRate,
                model.accTradePrice24h)
    }
}