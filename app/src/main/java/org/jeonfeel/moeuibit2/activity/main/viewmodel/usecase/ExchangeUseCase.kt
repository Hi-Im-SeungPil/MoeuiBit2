package org.jeonfeel.moeuibit2.activity.main.viewmodel.usecase

import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import com.google.gson.Gson
import com.google.gson.JsonArray
import kotlinx.coroutines.delay
import org.jeonfeel.moeuibit2.constant.*
import org.jeonfeel.moeuibit2.data.local.room.entity.Favorite
import org.jeonfeel.moeuibit2.data.remote.retrofit.ApiResult
import org.jeonfeel.moeuibit2.data.remote.retrofit.model.exchange.CommonExchangeModel
import org.jeonfeel.moeuibit2.data.remote.retrofit.model.exchange.ExchangeModel
import org.jeonfeel.moeuibit2.data.remote.retrofit.model.exchange.MarketCodeModel
import org.jeonfeel.moeuibit2.data.remote.retrofit.model.exchange.TickerModel
import org.jeonfeel.moeuibit2.data.remote.websocket.UpBitTickerWebSocket
import org.jeonfeel.moeuibit2.data.remote.websocket.listener.OnTickerMessageReceiveListener
import org.jeonfeel.moeuibit2.repository.local.LocalRepository
import org.jeonfeel.moeuibit2.repository.remote.RemoteRepository
import org.jeonfeel.moeuibit2.util.NetworkMonitorUtil
import javax.inject.Inject

class ExchangeUseCase @Inject constructor(
    private val localRepository: LocalRepository,
    private val remoteRepository: RemoteRepository,
) : OnTickerMessageReceiveListener {

    private val gson = Gson()
    var updateExchange = false

    val loadingState = mutableStateOf(true)
    val showFavoriteState = mutableStateOf(false)
    val selectedMarketState = mutableStateOf(SELECTED_KRW_MARKET)
    val errorState = mutableStateOf(INTERNET_CONNECTION)
    val sortButtonState = mutableStateOf(-1)
    val searchTextFieldValueState = mutableStateOf("")
    val btcTradePrice = mutableStateOf(0.0)

    private val krwMarketCodeList: ArrayList<MarketCodeModel> = arrayListOf()
    private val btcMarketCodeList: ArrayList<MarketCodeModel> = arrayListOf()
    private val krwMarketListStringBuffer = StringBuffer()
    private val btcMarketListStringBuffer = StringBuffer()

    val btcCoinKoreanNameAndEngName = HashMap<String, List<String>>()
    val btcExchangeModelList: ArrayList<CommonExchangeModel> = arrayListOf()
    val btcPreItemArray: ArrayList<CommonExchangeModel> = arrayListOf()
    val btcExchangeModelListPosition: HashMap<String, Int> = hashMapOf()
    var btcExchangeModelMutableStateList = mutableStateListOf<CommonExchangeModel>()

    val krwCoinKoreanNameAndEngName = HashMap<String, List<String>>()
    val krwExchangeModelList: ArrayList<CommonExchangeModel> = arrayListOf()
    val krwPreItemArray: ArrayList<CommonExchangeModel> = arrayListOf()
    val krwExchangeModelListPosition: HashMap<String, Int> = hashMapOf()
    var krwExchangeModelMutableStateList = mutableStateListOf<CommonExchangeModel>()

    val favoritePreItemArray: ArrayList<CommonExchangeModel> = arrayListOf()
    val favoriteExchangeModelList: ArrayList<CommonExchangeModel> = arrayListOf()
    val favoriteExchangeModelListPosition: HashMap<String, Int> = hashMapOf()
    var favoriteExchangeModelMutableStateList = mutableStateListOf<CommonExchangeModel>()
    val favoriteHashMap = HashMap<String, Int>()

    suspend fun requestExchangeData() {
        loadingState.value = true
        when (NetworkMonitorUtil.currentNetworkState) {
            INTERNET_CONNECTION -> {
                requestMarketCode()
                requestKrwTicker(krwMarketListStringBuffer.toString())
                requestBtcTicker(btcMarketListStringBuffer.toString())
                if (errorState.value != INTERNET_CONNECTION) {
                    errorState.value = INTERNET_CONNECTION
                }
                loadingState.value = false
            }
            else -> {
                loadingState.value = false
                updateExchange = false
                errorState.value = NetworkMonitorUtil.currentNetworkState
            }
        }
    }

    /**
     * get market, koreanName, englishName, warning
     */
    private suspend fun requestMarketCode() {
        remoteRepository.getMarketCodeService().collect {
            when (it.status) {
                ApiResult.Status.LOADING -> {}
                ApiResult.Status.SUCCESS -> {
                    try {
                        val data = it.data ?: JsonArray()
                        val indices = data.size()
                        for (i in 0 until indices) {
                            val marketCode = gson.fromJson(data[i], MarketCodeModel::class.java)
                            if (marketCode.market.startsWith(SYMBOL_KRW)) {
                                krwMarketListStringBuffer.append("${marketCode.market},")
                                krwMarketCodeList.add(marketCode)
                            } else if (marketCode.market.startsWith(SYMBOL_BTC)) {
                                btcMarketListStringBuffer.append("${marketCode.market},")
                                btcMarketCodeList.add(marketCode)
                            }
                        }
                        krwMarketListStringBuffer.deleteCharAt(krwMarketListStringBuffer.lastIndex)
                        btcMarketListStringBuffer.deleteCharAt(btcMarketListStringBuffer.lastIndex)
                        UpBitTickerWebSocket.setMarkets(krwMarketListStringBuffer.toString(),
                            "$btcMarketListStringBuffer,$BTC_MARKET")
                        for (i in krwMarketCodeList.indices) {
                            krwCoinKoreanNameAndEngName[krwMarketCodeList[i].market] =
                                listOf(krwMarketCodeList[i].korean_name,
                                    krwMarketCodeList[i].english_name)
                        }
                        for (i in btcMarketCodeList.indices) {
                            btcCoinKoreanNameAndEngName[btcMarketCodeList[i].market] =
                                listOf(btcMarketCodeList[i].korean_name,
                                    btcMarketCodeList[i].english_name)
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                        errorState.value = NETWORK_ERROR
                    }
                }
                ApiResult.Status.API_ERROR -> {
                    errorState.value = NETWORK_ERROR
                }
                ApiResult.Status.NETWORK_ERROR -> {
                    errorState.value = NO_INTERNET_CONNECTION
                    NetworkMonitorUtil.currentNetworkState = NO_INTERNET_CONNECTION
                }
            }
        }
    }

    /**
     * get market, tradePrice, signed_change_price, acc_trade_price_24h
     */
    private suspend fun requestKrwTicker(markets: String) {
        remoteRepository.getKrwTickerService(markets).collect {
            when (it.status) {
                ApiResult.Status.LOADING -> {}
                ApiResult.Status.SUCCESS -> {
                    try {
                        if (krwMarketCodeList.isNotEmpty()) {
                            val data = it.data ?: JsonArray()
                            val indices = data.size()
                            for (i in 0 until indices) {
                                val krwTicker = gson.fromJson(data[i], ExchangeModel::class.java)
                                val krwMarketCodeModel = krwMarketCodeList[i]
                                val koreanName = krwMarketCodeModel.korean_name
                                val englishName = krwMarketCodeModel.english_name
                                val market = krwMarketCodeModel.market
                                val warning = krwMarketCodeModel.market_warning
                                val tradePrice = krwTicker.tradePrice
                                val signedChangeRate = krwTicker.signedChangePrice
                                val accTradePrice24h = krwTicker.accTradePrice24h
                                val openingPrice = krwTicker.preClosingPrice
                                val symbol = market.substring(4)
                                krwExchangeModelList.add(
                                    CommonExchangeModel(
                                        koreanName,
                                        englishName,
                                        market,
                                        symbol,
                                        openingPrice,
                                        tradePrice,
                                        signedChangeRate,
                                        accTradePrice24h,
                                        warning
                                    )
                                )
                            }
                            krwExchangeModelList.sortByDescending { model ->
                                model.accTradePrice24h
                            }
                            for (i in krwExchangeModelList.indices) {
                                krwExchangeModelListPosition[krwExchangeModelList[i].market] = i
                            }
                            krwExchangeModelMutableStateList.addAll(krwExchangeModelList)
                            krwPreItemArray.addAll(krwExchangeModelList)
                        } else {
                            errorState.value = NETWORK_ERROR
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                        NetworkMonitorUtil.currentNetworkState = NETWORK_ERROR
                        errorState.value = NETWORK_ERROR
                    }
                }
                ApiResult.Status.API_ERROR -> {
                    errorState.value = NETWORK_ERROR
                }
                ApiResult.Status.NETWORK_ERROR -> {
                    errorState.value = NO_INTERNET_CONNECTION
                    NetworkMonitorUtil.currentNetworkState = NO_INTERNET_CONNECTION
                }
            }
        }
    }

    private suspend fun requestBtcTicker(markets: String) {
        remoteRepository.getKrwTickerService(markets).collect {
            when (it.status) {
                ApiResult.Status.LOADING -> {}
                ApiResult.Status.SUCCESS -> {
                    try {
                        if (btcMarketCodeList.isNotEmpty()) {
                            val data = it.data ?: JsonArray()
                            val indices = data.size()
                            for (i in 0 until indices) {
                                val btcTicker = gson.fromJson(data[i], ExchangeModel::class.java)
                                val btcMarketCodeModel = btcMarketCodeList[i]
                                val koreanName = btcMarketCodeModel.korean_name
                                val englishName = btcMarketCodeModel.english_name
                                val market = if (btcMarketCodeModel.market == "KRW-BTC") {
                                    continue
                                } else {
                                    btcMarketCodeModel.market
                                }
                                val warning = btcMarketCodeModel.market_warning
                                val tradePrice = btcTicker.tradePrice
                                val signedChangeRate = btcTicker.signedChangePrice
                                val accTradePrice24h = btcTicker.accTradePrice24h
                                val openingPrice = btcTicker.preClosingPrice
                                val symbol = market.substring(4)
                                btcExchangeModelList.add(
                                    CommonExchangeModel(
                                        koreanName,
                                        englishName,
                                        market,
                                        symbol,
                                        openingPrice,
                                        tradePrice,
                                        signedChangeRate,
                                        accTradePrice24h,
                                        warning
                                    )
                                )
                            }
                            btcExchangeModelList.sortByDescending { model ->
                                model.accTradePrice24h
                            }
                            for (i in btcExchangeModelList.indices) {
                                btcExchangeModelListPosition[btcExchangeModelList[i].market] = i
                            }
                            btcExchangeModelMutableStateList.addAll(btcExchangeModelList)
                            btcPreItemArray.addAll(btcExchangeModelList)
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                        NetworkMonitorUtil.currentNetworkState = NETWORK_ERROR
                        errorState.value = NETWORK_ERROR
                    }
                }
                ApiResult.Status.API_ERROR -> {
                    errorState.value = NETWORK_ERROR
                }
                ApiResult.Status.NETWORK_ERROR -> {
                    errorState.value = NO_INTERNET_CONNECTION
                    NetworkMonitorUtil.currentNetworkState = NO_INTERNET_CONNECTION
                }
            }
        }
    }

    /**
     * 거래소 화면 업데이트
     */
    suspend fun updateExchange() {
        if (!updateExchange) updateExchange = true
        while (updateExchange) {
            if (selectedMarketState.value == SELECTED_KRW_MARKET) {
                for (i in krwExchangeModelMutableStateList.indices) {
                    krwExchangeModelMutableStateList[i] = krwExchangeModelList[i]
                }
            } else if (selectedMarketState.value == SELECTED_BTC_MARKET) {
                for (i in btcExchangeModelMutableStateList.indices) {
                    btcExchangeModelMutableStateList[i] = btcExchangeModelList[i]
                }
            }
            delay(300)
        }
    }

    /**
     * 웹소켓에 실시간 정보 요청
     */
    fun requestCoinListToWebSocket() {
        UpBitTickerWebSocket.getListener().setTickerMessageListener(this)
        UpBitTickerWebSocket.requestKrwCoinList(selectedMarketState.value)
    }

    /**
     * 관심코인 목록 가져오기
     */
    suspend fun requestFavoriteData() {
        val count = 0
        val favoriteMarketListStringBuffer = StringBuffer()
        favoritePreItemArray.clear()
        favoriteExchangeModelMutableStateList.clear()
        favoriteExchangeModelList.clear()
        favoriteExchangeModelListPosition.clear()
        updateExchange = false
        val favoriteList = localRepository.getFavoriteDao().all ?: emptyList<Favorite>()
        if (favoriteList.isNotEmpty()) {
            for (i in favoriteList.indices) {
                val market = favoriteList[i]?.market ?: ""
                Log.e("favorite",market)
                favoriteHashMap[market] = 0
                if(market.startsWith(SYMBOL_KRW)) {
                    val model = krwExchangeModelList[krwExchangeModelListPosition[market]!!]
                    favoritePreItemArray.add(model)
                    favoriteExchangeModelMutableStateList.add(model)
                    favoriteExchangeModelList.add(model)
                    favoriteExchangeModelListPosition[market] = i
                    favoriteMarketListStringBuffer.append("$market,")
                } else {
                    if(count == 0) {
                        favoriteMarketListStringBuffer.append("$BTC_MARKET,")
                    }
                    val model = btcExchangeModelList[btcExchangeModelListPosition[market]!!]
                    favoritePreItemArray.add(model)
                    favoriteExchangeModelMutableStateList.add(model)
                    favoriteExchangeModelList.add(model)
                    favoriteExchangeModelListPosition[market] = i
                    favoriteMarketListStringBuffer.append("$market,")
                }
            }
            favoriteMarketListStringBuffer.deleteCharAt(favoriteMarketListStringBuffer.lastIndex)
            UpBitTickerWebSocket.setFavoriteMarkets(favoriteMarketListStringBuffer.toString())
        }
        updateExchange = true
    }

    /**
     * 사용자 관심코인 변경사항 업데이트
     */
    suspend fun updateFavorite(market: String, isFavorite: Boolean) {
        Log.e("fafafafa",market)
        if (favoriteHashMap[market] == null && isFavorite) {
            favoriteHashMap[market] = 0
            try {
                localRepository.getFavoriteDao().insert(market)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        } else if (favoriteHashMap[market] != null && !isFavorite) {
            favoriteHashMap.remove(market)
            localRepository.getFavoriteDao().delete(market)
        }
    }

    /**
     * 웹소켓 메세지
     */
    override fun onTickerMessageReceiveListener(tickerJsonObject: String) {
        val model = gson.fromJson(tickerJsonObject, TickerModel::class.java)
        if (updateExchange && model.code.startsWith(SYMBOL_KRW)) {
            if (selectedMarketState.value != SELECTED_KRW_MARKET && model.code == BTC_MARKET) {
                btcTradePrice.value = model.tradePrice
            } else {
                val position = krwExchangeModelListPosition[model.code] ?: -1
                krwExchangeModelList[position] =
                    CommonExchangeModel(
                        krwCoinKoreanNameAndEngName[model.code]!![0],
                        krwCoinKoreanNameAndEngName[model.code]!![1],
                        model.code,
                        model.code.substring(4),
                        model.preClosingPrice,
                        model.tradePrice,
                        model.signedChangeRate,
                        model.accTradePrice24h,
                        model.marketWarning
                    )
            }
        } else if (updateExchange && model.code.startsWith(BTC_MARKET)) {
            val position = btcExchangeModelListPosition[model.code] ?: -1
            btcExchangeModelList[position] =
                CommonExchangeModel(
                    btcCoinKoreanNameAndEngName[model.code]!![0],
                    btcCoinKoreanNameAndEngName[model.code]!![1],
                    model.code,
                    model.code.substring(4),
                    model.preClosingPrice,
                    model.tradePrice,
                    model.signedChangeRate,
                    model.accTradePrice24h,
                    model.marketWarning
                )
        } else {
            val position = favoriteExchangeModelListPosition[model.code] ?: -1
            var koreanName = ""
            var engName = ""
            if(model.code.startsWith(SYMBOL_KRW)) {
                koreanName = krwCoinKoreanNameAndEngName[model.code]!![0]
                engName = krwCoinKoreanNameAndEngName[model.code]!![1]
            } else {
                koreanName = btcCoinKoreanNameAndEngName[model.code]!![0]
                engName = btcCoinKoreanNameAndEngName[model.code]!![1]
            }
            favoriteExchangeModelList[position] =
                CommonExchangeModel(
                    koreanName,
                    engName,
                    model.code,
                    model.code.substring(4),
                    model.preClosingPrice,
                    model.tradePrice,
                    model.signedChangeRate,
                    model.accTradePrice24h,
                    model.marketWarning
                )
        }
        Log.e(model.code,model.code)
    }
}