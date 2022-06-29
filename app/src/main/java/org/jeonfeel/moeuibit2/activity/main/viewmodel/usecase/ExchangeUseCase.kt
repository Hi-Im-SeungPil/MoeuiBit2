package org.jeonfeel.moeuibit2.activity.main.viewmodel.usecase

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import com.google.gson.Gson
import com.google.gson.JsonArray
import kotlinx.coroutines.delay
import org.jeonfeel.moeuibit2.constant.INTERNET_CONNECTION
import org.jeonfeel.moeuibit2.constant.NETWORK_ERROR
import org.jeonfeel.moeuibit2.constant.NO_INTERNET_CONNECTION
import org.jeonfeel.moeuibit2.constant.SELECTED_KRW_MARKET
import org.jeonfeel.moeuibit2.data.local.room.entity.Favorite
import org.jeonfeel.moeuibit2.data.remote.retrofit.ApiResult
import org.jeonfeel.moeuibit2.data.remote.retrofit.model.ExchangeModel
import org.jeonfeel.moeuibit2.data.remote.retrofit.model.KrwExchangeModel
import org.jeonfeel.moeuibit2.data.remote.retrofit.model.MarketCodeModel
import org.jeonfeel.moeuibit2.data.remote.retrofit.model.TickerModel
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
    val selectedButtonState = mutableStateOf(-1)
    val searchTextFieldValueState = mutableStateOf("")

    private val krwMarketCodeList: ArrayList<MarketCodeModel> = arrayListOf()
    private val krwCoinListStringBuffer = StringBuffer()
    val krwCoinKoreanNameAndEngName = HashMap<String, List<String>>()
    val krwExchangeModelList: ArrayList<KrwExchangeModel> = arrayListOf()
    val preItemArray: ArrayList<KrwExchangeModel> = arrayListOf()
    val favoriteHashMap = HashMap<String, Int>()
    val krwExchangeModelListPosition: HashMap<String, Int> = hashMapOf()
    var krwExchangeModelMutableStateList = mutableStateListOf<KrwExchangeModel>()

    suspend fun requestData() {
        loadingState.value = true
        when (NetworkMonitorUtil.currentNetworkState) {
            INTERNET_CONNECTION -> {
                    requestKrwMarketCode()
                    requestKrwTicker(krwCoinListStringBuffer.toString())
                    requestFavoriteData()
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

    // get market, koreanName, englishName, warning
    private suspend fun requestKrwMarketCode() {
        remoteRepository.getMarketCodeService().collect {
            when (it.status) {
                ApiResult.Status.LOADING -> {}
                ApiResult.Status.SUCCESS -> {
                    try {
                        val data = it.data ?: JsonArray()
                        val indices = data.size()
                        for (i in 0 until indices) {
                            val krwMarketCode = gson.fromJson(data[i], MarketCodeModel::class.java)
                            if (krwMarketCode.market.contains("KRW-")) {
                                krwCoinListStringBuffer.append("${krwMarketCode.market},")
                                krwMarketCodeList.add(krwMarketCode)
                            }
                        }
                        krwCoinListStringBuffer.deleteCharAt(krwCoinListStringBuffer.lastIndex)
                        UpBitTickerWebSocket.setKrwMarkets(krwCoinListStringBuffer.toString())
                        for (i in krwMarketCodeList.indices) {
                            krwCoinKoreanNameAndEngName[krwMarketCodeList[i].market] =
                                listOf(krwMarketCodeList[i].korean_name,
                                    krwMarketCodeList[i].english_name)
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

    // get market, tradePrice, signed_change_price, acc_trade_price_24h
    private suspend fun requestKrwTicker(markets: String) {
        remoteRepository.getKrwTickerService(markets).collect {
            when (it.status) {
                ApiResult.Status.LOADING -> {}
                ApiResult.Status.SUCCESS -> {
                    try {
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
                                KrwExchangeModel(
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
                        preItemArray.addAll(krwExchangeModelList)
                    } catch (e: Exception) {
                        e.printStackTrace()
                        NetworkMonitorUtil.currentNetworkState = NETWORK_ERROR
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

    suspend fun requestKrwCoinListToWebSocket() {
        UpBitTickerWebSocket.getListener().setTickerMessageListener(this)
        UpBitTickerWebSocket.requestKrwCoinList()
        if (!updateExchange) updateExchange = true
        while (updateExchange) {
            for (i in krwExchangeModelMutableStateList.indices) {
                krwExchangeModelMutableStateList[i] = krwExchangeModelList[i]
            }
            delay(300)
        }
    }

    private suspend fun requestFavoriteData() {
        val favoriteList = localRepository.getFavoriteDao().all ?: emptyList<Favorite>()
        if (favoriteList.isNotEmpty()) {
            for (i in favoriteList.indices) {
                favoriteHashMap[favoriteList[i]?.market ?: ""] = 0
            }
        }
    }

    suspend fun updateFavorite(market: String, isFavorite: Boolean) {
        if (favoriteHashMap[market] == null && isFavorite) {
            favoriteHashMap[market] = 0
            localRepository.getFavoriteDao().insert(market)
        } else if (favoriteHashMap[market] != null && !isFavorite) {
            favoriteHashMap.remove(market)
            localRepository.getFavoriteDao().delete(market)
        }
    }

    override fun onTickerMessageReceiveListener(tickerJsonObject: String) {
        if (updateExchange) {
            val model = gson.fromJson(tickerJsonObject, TickerModel::class.java)
            val position = krwExchangeModelListPosition[model.code] ?: -1
            krwExchangeModelList[position] =
                KrwExchangeModel(
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
    }
}