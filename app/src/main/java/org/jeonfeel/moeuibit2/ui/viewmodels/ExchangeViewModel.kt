package org.jeonfeel.moeuibit2.ui.viewmodels

import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.google.gson.JsonArray
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.jeonfeel.moeuibit2.MoeuiBitDataStore
import org.jeonfeel.moeuibit2.constants.*
import org.jeonfeel.moeuibit2.data.local.room.entity.Favorite
import org.jeonfeel.moeuibit2.data.remote.retrofit.ApiResult
import org.jeonfeel.moeuibit2.data.remote.retrofit.model.*
import org.jeonfeel.moeuibit2.data.remote.websocket.UpBitTickerWebSocket
import org.jeonfeel.moeuibit2.data.remote.websocket.listener.OnTickerMessageReceiveListener
import org.jeonfeel.moeuibit2.data.repository.local.LocalRepository
import org.jeonfeel.moeuibit2.data.repository.remote.RemoteRepository
import org.jeonfeel.moeuibit2.ui.base.BaseViewModel
import org.jeonfeel.moeuibit2.utils.NetworkMonitorUtil

class ExchangeViewModelState {
    val loadingFavorite = mutableStateOf(true)
    val loadingExchange = mutableStateOf(true)
    val selectedMarket = mutableStateOf(SELECTED_KRW_MARKET)
    val error = mutableStateOf(INTERNET_CONNECTION)
    val searchTextFieldValue = mutableStateOf("")
    val sortButton = mutableStateOf(-1)
    val btcPrice = mutableStateOf(0.0)
}

class ExchangeViewModel(
    private val remoteRepository: RemoteRepository,
    private val localRepository: LocalRepository
) : BaseViewModel(), OnTickerMessageReceiveListener {
    var updateExchange = false
    val exchangeState = ExchangeViewModelState()

    private val krwMarketCodeList: ArrayList<MarketCodeModel> = arrayListOf()
    private val btcMarketCodeList: ArrayList<MarketCodeModel> = arrayListOf()
    private val krwMarketListStringBuffer = StringBuffer()
    private val btcMarketListStringBuffer = StringBuffer()
    val coinKrNameEngName = HashMap<String, Pair<String, String>>()

    val krwExchangeModelList: ArrayList<CommonExchangeModel> = arrayListOf()
    val krwPreItemArray: ArrayList<CommonExchangeModel> = arrayListOf()
    val krwExchangeModelListPosition: HashMap<String, Int> = hashMapOf()
    var krwExchangeModelMutableStateList = mutableStateListOf<CommonExchangeModel>()

    val btcExchangeModelList: ArrayList<CommonExchangeModel> = arrayListOf()
    val btcPreItemArray: ArrayList<CommonExchangeModel> = arrayListOf()
    val btcExchangeModelListPosition: HashMap<String, Int> = hashMapOf()
    var btcExchangeModelMutableStateList = mutableStateListOf<CommonExchangeModel>()

    val favoritePreItemArray: ArrayList<CommonExchangeModel> = arrayListOf()
    val favoriteExchangeModelList: ArrayList<CommonExchangeModel> = arrayListOf()
    val favoriteExchangeModelListPosition: HashMap<String, Int> = hashMapOf()
    var favoriteExchangeModelMutableStateList = mutableStateListOf<CommonExchangeModel>()
    val favoriteHashMap = HashMap<String, Int>()

    fun initExchangeData() {
        viewModelScope.launch {
            delay(650L)
            if (krwExchangeModelMutableStateList.isEmpty()) {
                viewModelScope.launch {
                    requestExchangeData()
                }.join()
            }
            requestCoinListToWebSocket()
            updateExchange()
        }
    }

    private suspend fun requestExchangeData() {
        exchangeState.loadingExchange.value = true
        when (NetworkMonitorUtil.currentNetworkState) {
            INTERNET_CONNECTION -> {
                requestMarketCode()
                requestKrwTicker(krwMarketListStringBuffer.toString())
                requestBtcTicker(btcMarketListStringBuffer.toString())
                exchangeState.error.value = INTERNET_CONNECTION
                exchangeState.loadingExchange.value = false
            }
            else -> {
                exchangeState.loadingExchange.value = false
                updateExchange = false
                exchangeState.error.value = NetworkMonitorUtil.currentNetworkState
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

                        UpBitTickerWebSocket.setMarkets(
                            krwMarketListStringBuffer.toString(),
                            "$btcMarketListStringBuffer,$BTC_MARKET"
                        )
                        for (i in krwMarketCodeList.indices) {
                            coinKrNameEngName[krwMarketCodeList[i].market] =
                                Pair(
                                    krwMarketCodeList[i].korean_name,
                                    krwMarketCodeList[i].english_name
                                )
                        }
                        for (i in btcMarketCodeList.indices) {
                            coinKrNameEngName[btcMarketCodeList[i].market] = Pair(
                                btcMarketCodeList[i].korean_name,
                                btcMarketCodeList[i].english_name
                            )
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                        exchangeState.error.value = NETWORK_ERROR
                    }
                }
                ApiResult.Status.API_ERROR -> {
                    exchangeState.error.value = NETWORK_ERROR
                }
                ApiResult.Status.NETWORK_ERROR -> {
                    exchangeState.error.value = NO_INTERNET_CONNECTION
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
                            exchangeState.error.value = NETWORK_ERROR
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                        NetworkMonitorUtil.currentNetworkState = NETWORK_ERROR
                        exchangeState.error.value = NETWORK_ERROR
                    }
                }
                ApiResult.Status.API_ERROR -> {
                    exchangeState.error.value = NETWORK_ERROR
                }
                ApiResult.Status.NETWORK_ERROR -> {
                    exchangeState.error.value = NO_INTERNET_CONNECTION
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
                                val market = btcMarketCodeModel.market
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
                        exchangeState.error.value = NETWORK_ERROR
                    }
                }
                ApiResult.Status.API_ERROR -> {
                    exchangeState.error.value = NETWORK_ERROR
                }
                ApiResult.Status.NETWORK_ERROR -> {
                    exchangeState.error.value = NO_INTERNET_CONNECTION
                    NetworkMonitorUtil.currentNetworkState = NO_INTERNET_CONNECTION
                }
            }
        }
    }

    suspend fun requestUSDTPrice() {
        remoteRepository.getUSDTPrice().collect() {
            when (it.status) {
                ApiResult.Status.LOADING -> {}
                ApiResult.Status.SUCCESS -> {
                    val data = it.data
                    if (data != null) {
                        val model = gson.fromJson(data, UsdtPriceModel::class.java)
                        MoeuiBitDataStore.usdPrice = model.krw
                        Log.d("usdPrice => ", MoeuiBitDataStore.usdPrice.toString())
                    }
                }
                ApiResult.Status.NETWORK_ERROR -> {
                    MoeuiBitDataStore.usdPrice = 1285.0
                }
                ApiResult.Status.API_ERROR -> {
                    MoeuiBitDataStore.usdPrice = 1285.0
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
            when (exchangeState.selectedMarket.value) {
                SELECTED_KRW_MARKET -> {
                    for (i in krwExchangeModelMutableStateList.indices) {
                        krwExchangeModelMutableStateList[i] = krwExchangeModelList[i]
                    }
                }
                SELECTED_BTC_MARKET -> {
                    for (i in btcExchangeModelMutableStateList.indices) {
                        btcExchangeModelMutableStateList[i] = btcExchangeModelList[i]
                    }
                }
                SELECTED_FAVORITE -> {
                    for (i in favoriteExchangeModelMutableStateList.indices) {
                        favoriteExchangeModelMutableStateList[i] = favoriteExchangeModelList[i]
                    }
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
        UpBitTickerWebSocket.requestKrwCoinList(exchangeState.selectedMarket.value)
    }

    /**
     * 관심코인 목록 가져오기
     */
    fun requestFavoriteData(selectedMarketState: Int) {
        viewModelScope.launch(ioDispatcher) {
            val favoriteMarketListStringBuffer = StringBuffer()
            favoritePreItemArray.clear()
            favoriteExchangeModelList.clear()
            favoriteExchangeModelMutableStateList.clear()
            val favoriteList = localRepository.getFavoriteDao().all ?: emptyList<Favorite>()
            if (favoriteList.isNotEmpty()) {
                for (i in favoriteList.indices) {
                    val market = favoriteList[i]?.market ?: ""
                    favoriteHashMap[market] = 0
                    if (market.startsWith(SYMBOL_KRW)) {
                        val model = krwExchangeModelList[krwExchangeModelListPosition[market]!!]
                        favoritePreItemArray.add(model)
                        favoriteExchangeModelMutableStateList.add(model)
                        favoriteExchangeModelList.add(model)
                        favoriteExchangeModelListPosition[market] = i
                        favoriteMarketListStringBuffer.append("$market,")
                    } else {
                        val model = btcExchangeModelList[btcExchangeModelListPosition[market]!!]
                        favoritePreItemArray.add(model)
                        favoriteExchangeModelMutableStateList.add(model)
                        favoriteExchangeModelList.add(model)
                        favoriteExchangeModelListPosition[market] = i
                        favoriteMarketListStringBuffer.append("$market,")
                    }
                }
                favoriteMarketListStringBuffer.deleteCharAt(favoriteMarketListStringBuffer.lastIndex)
                if (favoriteHashMap[BTC_MARKET] == null) {
                    UpBitTickerWebSocket.setFavoriteMarkets("$favoriteMarketListStringBuffer,$BTC_MARKET")
                } else {
                    UpBitTickerWebSocket.setFavoriteMarkets(favoriteMarketListStringBuffer.toString())
                }
            }
            sortList(selectedMarketState)
            Handler(Looper.getMainLooper()).post {
                exchangeState.loadingFavorite.value = false
            }
        }
    }

    /**
     * 사용자 관심코인 변경사항 업데이트
     */
    fun updateFavorite(
        market: String,
        isFavorite: Boolean,
        viewModelScope: CoroutineScope,
    ) {
        viewModelScope.launch(ioDispatcher) {
            when {
                favoriteHashMap[market] == null && isFavorite -> {
                    favoriteHashMap[market] = 0
                    try {
                        localRepository.getFavoriteDao().insert(market)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
                favoriteHashMap[market] != null && !isFavorite -> {
                    favoriteHashMap.remove(market)
                    try {
                        localRepository.getFavoriteDao().delete(market)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }

                    if (exchangeState.selectedMarket.value == SELECTED_FAVORITE) {
                        UpBitTickerWebSocket.getListener().setTickerMessageListener(null)
                        UpBitTickerWebSocket.onPause()
                        requestFavoriteData(SELECTED_FAVORITE)
                        Handler(Looper.getMainLooper()).post {
                            requestCoinListToWebSocket()
                        }
                        updateExchange()
                    }
                }
            }
        }
    }

    fun marketChangeAction(marketState: Int) {
        if (UpBitTickerWebSocket.currentMarket != marketState) {
            if (marketState == SELECTED_FAVORITE) {
                requestFavoriteData(marketState)
            }
            UpBitTickerWebSocket.getListener().setTickerMessageListener(null)
            UpBitTickerWebSocket.onPause()
            if (marketState != SELECTED_FAVORITE) {
                sortList(marketState)
            }
            requestCoinListToWebSocket()
        }
    }

    fun sortList(marketState: Int) {
        viewModelScope.launch(defaultDispatcher) {
            exchangeState.selectedMarket.value = marketState
            when (exchangeState.sortButton.value) {
                SORT_PRICE_DEC -> {
                    when (exchangeState.selectedMarket.value) {
                        SELECTED_KRW_MARKET -> {
                            krwExchangeModelList.sortByDescending { element ->
                                element.tradePrice
                            }
                        }
                        SELECTED_BTC_MARKET -> {
                            btcExchangeModelList.sortByDescending { element ->
                                element.tradePrice
                            }
                        }
                        else -> {
                            favoriteExchangeModelList.sortByDescending { element ->
                                if (element.market.startsWith(SYMBOL_BTC)) {
                                    element.tradePrice * exchangeState.btcPrice.value
                                } else {
                                    element.tradePrice
                                }
                            }
                        }
                    }
                }
                SORT_PRICE_ASC -> {
                    when (exchangeState.selectedMarket.value) {
                        SELECTED_KRW_MARKET -> {
                            krwExchangeModelList.sortBy { element ->
                                element.tradePrice
                            }
                        }
                        SELECTED_BTC_MARKET -> {
                            btcExchangeModelList.sortBy { element ->
                                element.tradePrice
                            }
                        }
                        else -> {
                            favoriteExchangeModelList.sortBy { element ->
                                if (element.market.startsWith(SYMBOL_BTC)) {
                                    element.tradePrice * exchangeState.btcPrice.value
                                } else {
                                    element.tradePrice
                                }
                            }
                        }
                    }
                }
                SORT_RATE_DEC -> {
                    when (exchangeState.selectedMarket.value) {
                        SELECTED_KRW_MARKET -> {
                            krwExchangeModelList.sortByDescending { element ->
                                element.signedChangeRate
                            }
                        }
                        SELECTED_BTC_MARKET -> {
                            btcExchangeModelList.sortByDescending { element ->
                                element.signedChangeRate
                            }
                        }
                        else -> {
                            favoriteExchangeModelList.sortByDescending { element ->
                                element.signedChangeRate
                            }
                        }
                    }
                }
                SORT_RATE_ASC -> {
                    when (exchangeState.selectedMarket.value) {
                        SELECTED_KRW_MARKET -> {
                            krwExchangeModelList.sortBy { element ->
                                element.signedChangeRate
                            }
                        }
                        SELECTED_BTC_MARKET -> {
                            btcExchangeModelList.sortBy { element ->
                                element.signedChangeRate
                            }
                        }
                        else -> {
                            favoriteExchangeModelList.sortBy { element ->
                                element.signedChangeRate
                            }
                        }
                    }
                }
                SORT_AMOUNT_DEC -> {
                    when (exchangeState.selectedMarket.value) {
                        SELECTED_KRW_MARKET -> {
                            krwExchangeModelList.sortByDescending { element ->
                                element.accTradePrice24h
                            }
                        }
                        SELECTED_BTC_MARKET -> {
                            btcExchangeModelList.sortByDescending { element ->
                                element.accTradePrice24h
                            }
                        }
                        else -> {
                            favoriteExchangeModelList.sortByDescending { element ->
                                if (element.market.startsWith(SYMBOL_BTC)) {
                                    element.accTradePrice24h * exchangeState.btcPrice.value
                                } else {
                                    element.accTradePrice24h
                                }
                            }
                        }
                    }
                }
                SORT_AMOUNT_ASC -> {
                    when (exchangeState.selectedMarket.value) {
                        SELECTED_KRW_MARKET -> {
                            krwExchangeModelList.sortBy { element ->
                                element.accTradePrice24h
                            }
                        }
                        SELECTED_BTC_MARKET -> {
                            btcExchangeModelList.sortBy { element ->
                                element.accTradePrice24h
                            }
                        }
                        else -> {
                            favoriteExchangeModelList.sortBy { element ->
                                if (element.market.startsWith(SYMBOL_BTC)) {
                                    element.accTradePrice24h * exchangeState.btcPrice.value
                                } else {
                                    element.accTradePrice24h
                                }
                            }
                        }
                    }
                }
                else -> {
                    when (exchangeState.selectedMarket.value) {
                        SELECTED_KRW_MARKET -> {
                            krwExchangeModelList.sortByDescending { element ->
                                element.accTradePrice24h
                            }
                        }
                        SELECTED_BTC_MARKET -> {
                            btcExchangeModelList.sortByDescending { element ->
                                element.accTradePrice24h
                            }
                        }
                        else -> {
                            favoriteExchangeModelList.sortByDescending { element ->
                                if (element.market.startsWith(SYMBOL_BTC)) {
                                    element.accTradePrice24h * exchangeState.btcPrice.value
                                } else {
                                    element.accTradePrice24h
                                }
                            }
                        }
                    }
                }
            }
            when (exchangeState.selectedMarket.value) {
                SELECTED_KRW_MARKET -> {
                    for (i in krwPreItemArray.indices) {
                        krwPreItemArray[i] =
                            krwExchangeModelList[i]
                    }
                    for (i in krwExchangeModelList.indices) {
                        krwExchangeModelListPosition[krwExchangeModelList[i].market] =
                            i
                    }
                    for (i in krwExchangeModelList.indices) {
                        krwExchangeModelMutableStateList[i] = krwExchangeModelList[i]
                    }
                }
                SELECTED_BTC_MARKET -> {
                    for (i in btcPreItemArray.indices) {
                        btcPreItemArray[i] =
                            btcExchangeModelList[i]
                    }
                    for (i in btcExchangeModelList.indices) {
                        btcExchangeModelListPosition[btcExchangeModelList[i].market] =
                            i
                    }
                    for (i in btcExchangeModelList.indices) {
                        btcExchangeModelMutableStateList[i] = btcExchangeModelList[i]
                    }
                }
                else -> {
                    for (i in favoritePreItemArray.indices) {
                        favoritePreItemArray[i] =
                            favoriteExchangeModelList[i]
                    }
                    for (i in favoriteExchangeModelList.indices) {
                        favoriteExchangeModelListPosition[favoriteExchangeModelList[i].market] =
                            i
                    }
                    for (i in favoriteExchangeModelList.indices) {
                        favoriteExchangeModelMutableStateList[i] = favoriteExchangeModelList[i]
                    }
                }
            }
            updateExchange = true
        }
    }

    fun getFilteredCoinList(): SnapshotStateList<CommonExchangeModel> {
        val textFieldValue = exchangeState.searchTextFieldValue
        val selectedMarket = exchangeState.selectedMarket
        return when {
            //검색 X 관심코인 X
            textFieldValue.value.isEmpty() -> {
                when (selectedMarket.value) {
                    SELECTED_KRW_MARKET -> {
                        krwExchangeModelMutableStateList
                    }
                    SELECTED_BTC_MARKET -> {
                        btcExchangeModelMutableStateList
                    }
                    else -> {
                        favoriteExchangeModelMutableStateList
                    }
                }
            }
            else -> {
                val resultList = SnapshotStateList<CommonExchangeModel>()
                val targetList = when (selectedMarket.value) {
                    SELECTED_KRW_MARKET -> {
                        krwExchangeModelMutableStateList
                    }
                    SELECTED_BTC_MARKET -> {
                        btcExchangeModelMutableStateList
                    }
                    else -> {
                        favoriteExchangeModelMutableStateList
                    }
                }
                for (element in targetList) {
                    if (
                        element.koreanName.contains(textFieldValue.value) ||
                        element.EnglishName.uppercase()
                            .contains(textFieldValue.value.uppercase()) ||
                        element.symbol.uppercase()
                            .contains(textFieldValue.value.uppercase())
                    ) {
                        resultList.add(element)
                    }
                }
                resultList
            }
        }
    }

    fun checkErrorScreen() {
        if (krwPreItemArray.isEmpty() || btcPreItemArray.isEmpty()) {
            initExchangeData()
        } else if (NetworkMonitorUtil.currentNetworkState == INTERNET_CONNECTION || NetworkMonitorUtil.currentNetworkState == NETWORK_ERROR) {
            exchangeState.error.value = NetworkMonitorUtil.currentNetworkState
            UpBitTickerWebSocket.onPause()
            initExchangeData()
        }
    }

    fun getPreCoinListAndPosition(): Pair<ArrayList<CommonExchangeModel>, HashMap<String, Int>> {
        return when (exchangeState.selectedMarket.value) {
            SELECTED_KRW_MARKET -> {
                Pair(krwPreItemArray, krwExchangeModelListPosition)
            }
            SELECTED_BTC_MARKET -> {
                Pair(btcPreItemArray, btcExchangeModelListPosition)
            }
            else -> {
                Pair(favoritePreItemArray, favoriteExchangeModelListPosition)
            }
        }
    }

    fun getFavoriteLoadingState(): MutableState<Boolean>? {
        return if (exchangeState.selectedMarket.value == SELECTED_FAVORITE) {
            exchangeState.loadingFavorite
        } else {
            null
        }
    }

    fun getBtcPrice(): Double {
        return if (exchangeState.selectedMarket.value == SELECTED_KRW_MARKET) {
            exchangeState.btcPrice.value
        } else {
            0.0
        }
    }

    /**
     * 웹소켓 메세지
     */
    override fun onTickerMessageReceiveListener(tickerJsonObject: String) {
        val model = gson.fromJson(tickerJsonObject, TickerModel::class.java)
        val marketState = exchangeState.selectedMarket.value
        var position = 0
        var targetModelList: ArrayList<CommonExchangeModel>? = null
        // krw 코인 일 때
        if (updateExchange && model.code.startsWith(SYMBOL_KRW)) {
            when {
                // BTC 마켓 일떄 비트코인 가격 받아오기 위해
                marketState == SELECTED_BTC_MARKET && model.code == BTC_MARKET -> {
                    exchangeState.btcPrice.value = model.tradePrice
                }
                // 관심 코인 화면에서 비트코인 가격 받아올 때
                marketState == SELECTED_FAVORITE && model.code == BTC_MARKET -> {
                    exchangeState.btcPrice.value = model.tradePrice
                    // 관심코인에 비트코인이 있을 시
                    if (favoriteHashMap[BTC_MARKET] != null) {
                        position = favoriteExchangeModelListPosition[model.code] ?: 0
                        targetModelList = favoriteExchangeModelList
                    }
                }
                // 관심코인일 때
                marketState == SELECTED_FAVORITE -> {
                    position = favoriteExchangeModelListPosition[model.code] ?: 0
                    targetModelList = favoriteExchangeModelList
                }
                // krw 마켓 일 때
                else -> {
                    Log.e("test", model.code)
                    position = krwExchangeModelListPosition[model.code] ?: 0
                    targetModelList = krwExchangeModelList
                }
            }
        } else if (updateExchange && model.code.startsWith(SYMBOL_BTC)) { // BTC 마켓 일 떄
            position = btcExchangeModelListPosition[model.code] ?: 0
            targetModelList = when (marketState) {
                SELECTED_FAVORITE -> {
                    favoriteExchangeModelList
                }
                else -> {
                    btcExchangeModelList
                }
            }
        }

        targetModelList?.let {
            targetModelList[position] = CommonExchangeModel(
                coinKrNameEngName[model.code]?.first ?: "",
                coinKrNameEngName[model.code]?.second ?: "",
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

    companion object {
        fun provideFactory(
            remoteRepository: RemoteRepository,
            localRepository: LocalRepository,
        ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return ExchangeViewModel(remoteRepository, localRepository) as T
            }
        }
    }
}