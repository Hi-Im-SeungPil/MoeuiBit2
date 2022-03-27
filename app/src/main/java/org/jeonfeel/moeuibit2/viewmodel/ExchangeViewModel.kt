package org.jeonfeel.moeuibit2.viewmodel

import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.jeonfeel.moeuibit2.data.remote.retrofit.model.ExchangeModel
import org.jeonfeel.moeuibit2.data.remote.retrofit.model.KrwExchangeModel
import org.jeonfeel.moeuibit2.data.remote.retrofit.model.MarketCodeModel
import org.jeonfeel.moeuibit2.data.remote.retrofit.model.TickerModel
import org.jeonfeel.moeuibit2.data.remote.websocket.UpBitWebSocket
import org.jeonfeel.moeuibit2.listener.OnMessageReceiveListener
import org.jeonfeel.moeuibit2.repository.ExchangeViewModelRepository
import org.jeonfeel.moeuibit2.util.INTERNET_CONNECTION
import org.jeonfeel.moeuibit2.util.NETWORK_ERROR
import org.jeonfeel.moeuibit2.util.NetworkMonitorUtil.Companion.currentNetworkState
import javax.inject.Inject

@HiltViewModel
class ExchangeViewModel @Inject constructor(
    private val exchangeViewModelRepository: ExchangeViewModelRepository,
) : ViewModel(), OnMessageReceiveListener {

    private val TAG = ExchangeViewModel::class.java.simpleName
    private val gson = Gson()
    private var isSocketRunning = true

    private val krwTickerList: ArrayList<ExchangeModel> = arrayListOf()
    private val krwMarketCodeList: ArrayList<MarketCodeModel> = arrayListOf()
    private val krwCoinKoreanNameAndEngName = HashMap<String, List<String>>()
    private val krwCoinListStringBuffer = StringBuffer()

    private val krwExchangeModelList: ArrayList<KrwExchangeModel> = arrayListOf()
    val krwExchangeModelListPosition: HashMap<String, Int> = hashMapOf()
    val preItemArray: ArrayList<KrwExchangeModel> = arrayListOf()

    private var krwExchangeModelMutableStateList = mutableStateListOf<KrwExchangeModel>()
    val searchTextFieldValue = mutableStateOf("")
    val errorState = mutableStateOf(INTERNET_CONNECTION)
    val selectedButtonState =  mutableStateOf(-1)
    val loading = mutableStateOf(true)

    init {
        UpBitWebSocket.getListener().setMessageListener1(this)
        requestData()
    }

    /**
     * request data
     * */
    fun requestData() {
        if(!loading.value) loading.value = true
        when (currentNetworkState) {
            INTERNET_CONNECTION -> {
                viewModelScope.launch {
                    requestKrwMarketCode()
                    requestKrwTicker(krwCoinListStringBuffer.toString())
                    createKrwExchangeModelList()
                    updateExchange()
                    if (errorState.value != INTERNET_CONNECTION) {
                        errorState.value = INTERNET_CONNECTION
                    }
                    loading.value = false
                }
            }
            else -> {
                loading.value = false
                errorState.value = currentNetworkState
            }
        }
    }

    // get market, koreanName, englishName, warning
    private suspend fun requestKrwMarketCode() {
        val resultMarketCode = exchangeViewModelRepository.getMarketCodeService()
        if (resultMarketCode.isSuccessful) {
            try {
                for (i in 0 until resultMarketCode.body()!!.size()) {
                    val krwMarketCode =
                        gson.fromJson(resultMarketCode.body()!![i], MarketCodeModel::class.java)
                    if (krwMarketCode.market.contains("KRW-")) {
                        krwCoinListStringBuffer.append("${krwMarketCode.market},")
                        krwMarketCodeList.add(krwMarketCode)
                    }
                }
                krwCoinListStringBuffer.deleteCharAt(krwCoinListStringBuffer.lastIndex)
                UpBitWebSocket.setKrwMarkets(krwCoinListStringBuffer.toString())
                for (i in 0 until krwMarketCodeList.size) {
                    krwCoinKoreanNameAndEngName[krwMarketCodeList[i].market] =
                        listOf(krwMarketCodeList[i].korean_name, krwMarketCodeList[i].english_name)
                }
            } catch (e: Exception) {
                e.printStackTrace()
                currentNetworkState = NETWORK_ERROR
            }
        }
    }

    // get market, tradePrice, signed_change_price, acc_trade_price_24h
    private suspend fun requestKrwTicker(markets: String) {
        val resultKrwTicker =
            exchangeViewModelRepository.getKrwTickerService(markets)
        if (resultKrwTicker.isSuccessful) {
            try {
                for (i in 0 until resultKrwTicker.body()!!.size()) {
                    val krwTicker =
                        gson.fromJson(resultKrwTicker.body()!![i], ExchangeModel::class.java)
                    krwTickerList.add(krwTicker)
                }
            } catch (e: Exception) {
                e.printStackTrace()
                currentNetworkState = NETWORK_ERROR
            }
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
        requestKrwCoinList()
    }

    private fun updateExchange() {
        viewModelScope.launch(Dispatchers.Main) {
            while (isSocketRunning) {
                for (i in 0 until krwExchangeModelMutableStateList.size) {
                    krwExchangeModelMutableStateList[i] = krwExchangeModelList[i]
                }
                delay(300)
            }
        }
    }

    fun requestKrwCoinList() {
        UpBitWebSocket.requestKrwCoinList()
    }

    /**
     * data sorting, filter
     * */
    fun filterKrwCoinList(): SnapshotStateList<KrwExchangeModel> {
        return if (searchTextFieldValue.value.isEmpty()) {
            krwExchangeModelMutableStateList
        } else {
            val resultList = SnapshotStateList<KrwExchangeModel>()
            for (element in krwExchangeModelMutableStateList) {
                if (element.koreanName.contains(searchTextFieldValue.value) || element.EnglishName.uppercase()
                        .contains(searchTextFieldValue.value.uppercase()) || element.symbol.uppercase()
                        .contains(
                            searchTextFieldValue.value.uppercase())
                ) {
                    resultList.add(element)
                }
            }
            resultList
        }
    }

    fun sortList(sortStandard: Int) {
        isSocketRunning = false

        when (sortStandard) {
            0 -> {
                krwExchangeModelList.sortByDescending { element ->
                    element.tradePrice
                }
            }
            1 -> {
                krwExchangeModelList.sortBy { element ->
                    element.tradePrice
                }
            }
            2 -> {
                krwExchangeModelList.sortByDescending { element ->
                    element.signedChangeRate
                }
            }
            3 -> {
                krwExchangeModelList.sortBy { element ->
                    element.signedChangeRate
                }
            }
            4 -> {
                krwExchangeModelList.sortByDescending { element ->
                    element.accTradePrice24h
                }
            }
            5 -> {
                krwExchangeModelList.sortBy { element ->
                    element.accTradePrice24h
                }
            }
            else -> {
                krwExchangeModelList.sortByDescending { element ->
                    element.accTradePrice24h
                }
            }
        }

        for (i in 0 until preItemArray.size) {
            preItemArray[i] =
                krwExchangeModelList[i]
        }

        for (i in 0 until krwExchangeModelList.size) {
            krwExchangeModelListPosition[krwExchangeModelList[i].market] =
                i
        }

        for (i in 0 until krwExchangeModelList.size) {
            krwExchangeModelMutableStateList[i] =
                krwExchangeModelList[i]
        }

        isSocketRunning = true
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
        Log.e(TAG,model.code)
    }
}