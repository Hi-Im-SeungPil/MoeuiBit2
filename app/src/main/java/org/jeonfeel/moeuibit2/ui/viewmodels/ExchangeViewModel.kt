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
import com.google.gson.JsonArray
import com.orhanobut.logger.Logger
import kotlinx.coroutines.*
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
    val searchTextFieldValue = mutableStateOf("")
    val sortButton = mutableStateOf(-1)
    val btcPrice = mutableStateOf(0.0)
}

class ExchangeViewModel constructor(
    private val remoteRepository: RemoteRepository,
    private val localRepository: LocalRepository,
    private val errorState: MutableState<Int>
) : BaseViewModel(), OnTickerMessageReceiveListener {
    var updateExchange = false
    val state = ExchangeViewModelState()

    private val krwMarketCodeList: ArrayList<MarketCodeModel> = arrayListOf()
    private val btcMarketCodeList: ArrayList<MarketCodeModel> = arrayListOf()
    private val krwMarketListStringBuffer = StringBuffer()
    private val btcMarketListStringBuffer = StringBuffer()

    private val krwExchangeModelList: ArrayList<CommonExchangeModel> = arrayListOf()
    private val krwPreItemArray: ArrayList<CommonExchangeModel> = arrayListOf()
    private val krwExchangeModelListPosition: HashMap<String, Int> = hashMapOf()
    private val krwExchangeModelMutableStateList =
        mutableStateOf(mutableStateListOf<CommonExchangeModel>())

    private val btcExchangeModelList: ArrayList<CommonExchangeModel> = arrayListOf()
    private val btcPreItemArray: ArrayList<CommonExchangeModel> = arrayListOf()
    private val btcExchangeModelListPosition: HashMap<String, Int> = hashMapOf()
    private val btcExchangeModelMutableStateList =
        mutableStateOf(mutableStateListOf<CommonExchangeModel>())

    private var favoritePreItemArray: ArrayList<CommonExchangeModel> = arrayListOf()
    private var favoriteExchangeModelList: ArrayList<CommonExchangeModel> = arrayListOf()
    private val favoriteExchangeModelListPosition: HashMap<String, Int> = hashMapOf()
    private val favoriteExchangeModelMutableStateList =
        mutableStateOf(mutableStateListOf<CommonExchangeModel>())

    fun initExchangeData() {
        UpBitTickerWebSocket.tickerListener = this
        viewModelScope.launch {
            if (krwExchangeModelMutableStateList.value.isEmpty()) {
                requestExchangeData()
            }
            setSocketFavoriteData()
            requestCoinListToWebSocket()
            if (!updateExchange) {
                updateExchange()
            }
        }
    }

    private suspend fun requestExchangeData() {
        state.loadingExchange.value = true
        when (NetworkMonitorUtil.currentNetworkState) {
            INTERNET_CONNECTION -> {
                requestMarketCode()
                requestKrwTicker(krwMarketListStringBuffer.toString())
                requestBtcTicker(btcMarketListStringBuffer.toString())
                withContext(ioDispatcher) {
                    initFavoriteData()
                }
                errorState.value = INTERNET_CONNECTION
                state.loadingExchange.value = false
            }
            else -> {
                state.loadingExchange.value = false
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

                        UpBitTickerWebSocket.setMarkets(
                            krwMarketListStringBuffer.toString(),
                            "$btcMarketListStringBuffer,$BTC_MARKET"
                        )
                        for (i in krwMarketCodeList.indices) {
                            MoeuiBitDataStore.coinName[krwMarketCodeList[i].market] =
                                Pair(
                                    krwMarketCodeList[i].korean_name,
                                    krwMarketCodeList[i].english_name
                                )
                        }
                        for (i in btcMarketCodeList.indices) {
                            MoeuiBitDataStore.coinName[btcMarketCodeList[i].market] = Pair(
                                btcMarketCodeList[i].korean_name,
                                btcMarketCodeList[i].english_name
                            )
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
                                krwExchangeModelList.add(
                                    CommonExchangeModel(
                                        koreanName = krwMarketCodeModel.korean_name,
                                        englishName = krwMarketCodeModel.english_name,
                                        market = krwMarketCodeModel.market,
                                        symbol = krwMarketCodeModel.market.substring(4),
                                        opening_price = krwTicker.preClosingPrice,
                                        tradePrice = krwTicker.tradePrice,
                                        signedChangeRate = krwTicker.signedChangePrice,
                                        accTradePrice24h = krwTicker.accTradePrice24h,
                                        warning = krwMarketCodeModel.market_warning
                                    )
                                )
                            }
                            krwExchangeModelList.sortByDescending { model ->
                                model.accTradePrice24h
                            }
                            for (i in krwExchangeModelList.indices) {
                                krwExchangeModelListPosition[krwExchangeModelList[i].market] = i
                            }
                            swapList(SELECTED_KRW_MARKET)
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
                                val market = btcMarketCodeModel.market
                                val warning = btcMarketCodeModel.market_warning
                                val tradePrice = btcTicker.tradePrice
                                val signedChangeRate = btcTicker.signedChangePrice
                                val accTradePrice24h = btcTicker.accTradePrice24h
                                val openingPrice = btcTicker.preClosingPrice
                                val symbol = market.substring(4)
                                btcExchangeModelList.add(
                                    CommonExchangeModel(
                                        koreanName = koreanName,
                                        englishName = englishName,
                                        market = market,
                                        symbol = symbol,
                                        opening_price = openingPrice,
                                        tradePrice = tradePrice,
                                        signedChangeRate = signedChangeRate,
                                        accTradePrice24h = accTradePrice24h,
                                        warning = warning
                                    )
                                )
                            }
                            btcExchangeModelList.sortByDescending { model ->
                                model.accTradePrice24h
                            }
                            for (i in btcExchangeModelList.indices) {
                                btcExchangeModelListPosition[btcExchangeModelList[i].market] = i
                            }
                            swapList(SELECTED_BTC_MARKET)
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

    private suspend fun initFavoriteData() {
        val favoriteList = localRepository.getFavoriteDao().all ?: emptyList<Favorite>()
        for (i in favoriteList) {
            if (i != null) {
                MoeuiBitDataStore.favoriteHashMap[i.market] = 0
            }
        }
    }

    private fun setSocketFavoriteData() {
        val list = MoeuiBitDataStore.favoriteHashMap.keys.toList()
        if (list.isNotEmpty()) {
            val favoriteMarketListStringBuffer = StringBuffer()
            for (i in list) {
                favoriteMarketListStringBuffer.append("${i},")
            }

            favoriteMarketListStringBuffer.deleteCharAt(favoriteMarketListStringBuffer.lastIndex)

            if (MoeuiBitDataStore.favoriteHashMap[BTC_MARKET] == null) {
                UpBitTickerWebSocket.setFavoriteMarkets("$favoriteMarketListStringBuffer,$BTC_MARKET")
            } else {
                UpBitTickerWebSocket.setFavoriteMarkets(favoriteMarketListStringBuffer.toString())
            }
        }
    }

    suspend fun requestUSDTPrice() {
        remoteRepository.getUSDTPrice().collect {
            when (it.status) {
                ApiResult.Status.LOADING -> {
                }
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
    private suspend fun updateExchange() {
        if (!updateExchange) updateExchange = true
        while (updateExchange) {
            when (state.selectedMarket.value) {
                SELECTED_KRW_MARKET -> {
                    swapList(SELECTED_KRW_MARKET)
                }
                SELECTED_BTC_MARKET -> {
                    swapList(SELECTED_BTC_MARKET)
                }
                SELECTED_FAVORITE -> {
                    swapList(SELECTED_FAVORITE)
                }
            }
            delay(300)
        }
    }

    /**
     * 웹소켓에 실시간 정보 요청
     */
    private fun requestCoinListToWebSocket() {
        UpBitTickerWebSocket.getListener().setTickerMessageListener(this)
        UpBitTickerWebSocket.requestKrwCoinList(state.selectedMarket.value)
    }

    /**
     * 관심코인 목록 가져오기
     */
    private fun requestFavoriteData(selectedMarketState: Int) {
        updateExchange = false
        favoritePreItemArray.clear()
        viewModelScope.launch(ioDispatcher) {
            val favoriteList = localRepository.getFavoriteDao().all ?: emptyList<Favorite>()
            favoriteList.ifEmpty {
                favoriteExchangeModelList.clear()
                UpBitTickerWebSocket.setFavoriteMarkets("pause")
                swapList(SELECTED_FAVORITE)
                state.loadingFavorite.value = false
                updateExchange = true
                return@launch
            }
            val favoriteMarketListStringBuffer = StringBuffer()
            val tempList = arrayListOf<CommonExchangeModel>()
            for (i in favoriteList.indices) {
                val market = favoriteList[i]?.market ?: ""
                MoeuiBitDataStore.favoriteHashMap[market] = 0
                try {
                    if (market.startsWith(SYMBOL_KRW)) {
                        val model = krwExchangeModelList[krwExchangeModelListPosition[market]!!]
                        favoriteExchangeModelListPosition[market] = i
                        tempList.add(model)
                        favoritePreItemArray.add(model)
                        favoriteMarketListStringBuffer.append("$market,")
                    } else {
                        val model = btcExchangeModelList[btcExchangeModelListPosition[market]!!]
                        favoriteExchangeModelListPosition[market] = i
                        tempList.add(model)
                        favoritePreItemArray.add(model)
                        favoriteMarketListStringBuffer.append("$market,")
                    }
                } catch (e: java.lang.Exception) {
                    localRepository.getFavoriteDao().delete(market)
                    e.printStackTrace()
                }
            }
            favoriteExchangeModelList = tempList

            if (favoriteMarketListStringBuffer.isNotEmpty()) {
                favoriteMarketListStringBuffer.deleteCharAt(favoriteMarketListStringBuffer.lastIndex)
            }

            if (MoeuiBitDataStore.favoriteHashMap[BTC_MARKET] == null) {
                UpBitTickerWebSocket.setFavoriteMarkets("$favoriteMarketListStringBuffer,$BTC_MARKET")
            } else {
                UpBitTickerWebSocket.setFavoriteMarkets(favoriteMarketListStringBuffer.toString())
            }
            sortList(selectedMarketState)
            state.loadingFavorite.value = false
        }
    }

    /**
     * 사용자 관심코인 변경사항 업데이트
     */
    fun updateFavorite(
        market: String = "",
        isFavorite: Boolean = false,
    ) {
        viewModelScope.launch(ioDispatcher) {
            if (market.isNotEmpty()) {
                when {
                    MoeuiBitDataStore.favoriteHashMap[market] == null && isFavorite -> {
                        MoeuiBitDataStore.favoriteHashMap[market] = 0
                        try {
                            localRepository.getFavoriteDao().insert(market)
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                    MoeuiBitDataStore.favoriteHashMap[market] != null && !isFavorite -> {
                        MoeuiBitDataStore.favoriteHashMap.remove(market)
                        try {
                            localRepository.getFavoriteDao().delete(market)
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }

                        if (state.selectedMarket.value == SELECTED_FAVORITE) {
                            updateExchange = false
                            UpBitTickerWebSocket.getListener().setTickerMessageListener(null)
                            UpBitTickerWebSocket.onPause()
                            val position = favoriteExchangeModelListPosition[market]
                            position?.let {
                                favoritePreItemArray.removeAt(it)
                                favoriteExchangeModelList.removeAt(it)
                                favoriteExchangeModelMutableStateList.value.removeAt(it)
                                favoriteExchangeModelListPosition.remove(market)
                            }
                            tempUpdateFavorite()
                            Handler(Looper.getMainLooper()).post {
                                requestCoinListToWebSocket()
                            }
                            updateExchange()
                        }
                    }
                }
            }
        }
    }

    private fun tempUpdateFavorite() {
        favoriteExchangeModelList.ifEmpty {
            UpBitTickerWebSocket.setFavoriteMarkets("pause")
            return
        }

        val favoriteMarketListStringBuffer = StringBuffer()
        var index = 0
        for (i in favoriteExchangeModelList) {
            favoriteExchangeModelListPosition[i.market] = index++
            favoriteMarketListStringBuffer.append("${i.market},")
        }
        favoriteMarketListStringBuffer.deleteCharAt(favoriteMarketListStringBuffer.lastIndex)
        if (MoeuiBitDataStore.favoriteHashMap[BTC_MARKET] == null) {
            UpBitTickerWebSocket.setFavoriteMarkets("$favoriteMarketListStringBuffer,$BTC_MARKET")
        } else {
            UpBitTickerWebSocket.setFavoriteMarkets(favoriteMarketListStringBuffer.toString())
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
        if (updateExchange || state.selectedMarket.value == SELECTED_FAVORITE) {
            updateExchange = false
            viewModelScope.launch(defaultDispatcher) {
                state.selectedMarket.value = marketState
                when (state.sortButton.value) {
                    SORT_PRICE_DEC -> {
                        when (state.selectedMarket.value) {
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
                                        element.tradePrice * state.btcPrice.value
                                    } else {
                                        element.tradePrice
                                    }
                                }
                            }
                        }
                    }
                    SORT_PRICE_ASC -> {
                        when (state.selectedMarket.value) {
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
                                        element.tradePrice * state.btcPrice.value
                                    } else {
                                        element.tradePrice
                                    }
                                }
                            }
                        }
                    }
                    SORT_RATE_DEC -> {
                        when (state.selectedMarket.value) {
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
                        when (state.selectedMarket.value) {
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
                        when (state.selectedMarket.value) {
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
                                        element.accTradePrice24h * state.btcPrice.value
                                    } else {
                                        element.accTradePrice24h
                                    }
                                }
                            }
                        }
                    }
                    SORT_AMOUNT_ASC -> {
                        when (state.selectedMarket.value) {
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
                                        element.accTradePrice24h * state.btcPrice.value
                                    } else {
                                        element.accTradePrice24h
                                    }
                                }
                            }
                        }
                    }
                    else -> {
                        when (state.selectedMarket.value) {
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
                                        element.accTradePrice24h * state.btcPrice.value
                                    } else {
                                        element.accTradePrice24h
                                    }
                                }
                            }
                        }
                    }
                }
                when (state.selectedMarket.value) {
                    SELECTED_KRW_MARKET -> {

                        for (i in krwPreItemArray.indices) {
                            krwPreItemArray[i] = krwExchangeModelList[i]
                        }

                        for (i in krwExchangeModelList.indices) {
                            krwExchangeModelListPosition[krwExchangeModelList[i].market] =
                                i
                        }
                        swapList(SELECTED_KRW_MARKET)
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
                        swapList(SELECTED_BTC_MARKET)
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
                        swapList(SELECTED_FAVORITE)
                    }
                }
                updateExchange = true
            }
        }
    }

    fun getFilteredCoinList(): SnapshotStateList<CommonExchangeModel> {
        val textFieldValue = state.searchTextFieldValue
        val selectedMarket = state.selectedMarket
        return when {
            //검색 X 관심코인 X
            textFieldValue.value.isEmpty() -> {
                when (selectedMarket.value) {
                    SELECTED_KRW_MARKET -> {
                        krwExchangeModelMutableStateList.value
                    }
                    SELECTED_BTC_MARKET -> {
                        btcExchangeModelMutableStateList.value
                    }
                    else -> {
                        favoriteExchangeModelMutableStateList.value
                    }
                }
            }
            else -> {
                val resultList = mutableStateListOf<CommonExchangeModel>()
                val targetList = when (selectedMarket.value) {
                    SELECTED_KRW_MARKET -> {
                        krwExchangeModelMutableStateList.value
                    }
                    SELECTED_BTC_MARKET -> {
                        btcExchangeModelMutableStateList.value
                    }
                    else -> {
                        favoriteExchangeModelMutableStateList.value
                    }
                }
                for (element in targetList) {
                    if (
                        element.koreanName.contains(textFieldValue.value)
                        || element.englishName.uppercase()
                            .contains(textFieldValue.value.uppercase())
                        || element.symbol.uppercase().contains(textFieldValue.value.uppercase())
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
        } else if (NetworkMonitorUtil.currentNetworkState == INTERNET_CONNECTION
            || NetworkMonitorUtil.currentNetworkState == NETWORK_ERROR
        ) {
            errorState.value = NetworkMonitorUtil.currentNetworkState
            UpBitTickerWebSocket.onlyRebuildSocket()
            initExchangeData()
        }
    }

    fun getPreCoinListAndPosition(): Pair<ArrayList<CommonExchangeModel>, HashMap<String, Int>> {
        return when (state.selectedMarket.value) {
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
        return if (state.selectedMarket.value == SELECTED_FAVORITE) {
            state.loadingFavorite
        } else {
            null
        }
    }

    fun getBtcPrice(): Double {
        return if (state.selectedMarket.value == SELECTED_KRW_MARKET) {
            state.btcPrice.value
        } else {
            0.0
        }
    }

    private fun swapList(marketState: Int) {
        val tempList = mutableStateListOf<CommonExchangeModel>()

        if (marketState == SELECTED_KRW_MARKET) {
            tempList.addAll(krwExchangeModelList)
            krwExchangeModelMutableStateList.value = tempList
            return
        }

        if (marketState == SELECTED_BTC_MARKET) {
            tempList.addAll(btcExchangeModelList)
            btcExchangeModelMutableStateList.value = tempList
            return
        }

        if (marketState == SELECTED_FAVORITE) {
            tempList.addAll(favoriteExchangeModelList)
            favoriteExchangeModelMutableStateList.value = tempList
            return
        }
    }

    /**
     * 웹소켓 메세지
     */
    override fun onTickerMessageReceiveListener(tickerJsonObject: String) {
        val model = gson.fromJson(tickerJsonObject, TickerModel::class.java)
        val marketState = state.selectedMarket.value
        var position = 0
        var targetModelList: ArrayList<CommonExchangeModel>? = null
        Logger.e("aaaaaaa")
        // krw 코인 일 때
        if (updateExchange && model.code.startsWith(SYMBOL_KRW)) {
            when {
                // BTC 마켓 일떄 비트코인 가격 받아오기 위해
                marketState == SELECTED_BTC_MARKET && model.code == BTC_MARKET -> {
                    state.btcPrice.value = model.tradePrice
                }
                // 관심 코인 화면에서 비트코인 가격 받아올 때
                marketState == SELECTED_FAVORITE && model.code == BTC_MARKET -> {
                    state.btcPrice.value = model.tradePrice
                    // 관심코인에 비트코인이 있을 시
                    if (MoeuiBitDataStore.favoriteHashMap[BTC_MARKET] != null) {
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
                    position = krwExchangeModelListPosition[model.code] ?: 0
                    targetModelList = krwExchangeModelList
                }
            }
        } else if (updateExchange && model.code.startsWith(SYMBOL_BTC)) { // BTC 마켓 일 떄
            targetModelList = when (marketState) {
                SELECTED_FAVORITE -> {
                    position = favoriteExchangeModelListPosition[model.code] ?: 0
                    favoriteExchangeModelList
                }
                else -> {
                    position = btcExchangeModelListPosition[model.code] ?: 0
                    btcExchangeModelList
                }
            }
        }

        if (updateExchange) {
            Logger.e("bbbbbbbb")
            targetModelList?.let {
                targetModelList.ifEmpty { return@let }
                targetModelList[position] = CommonExchangeModel(
                    koreanName = MoeuiBitDataStore.coinName[model.code]?.first ?: "",
                    englishName = MoeuiBitDataStore.coinName[model.code]?.second ?: "",
                    market = model.code,
                    symbol = model.code.substring(4),
                    opening_price = model.preClosingPrice,
                    tradePrice = model.tradePrice,
                    signedChangeRate = model.signedChangeRate,
                    accTradePrice24h = model.accTradePrice24h,
                    warning = model.marketWarning
                )
            }
        }
    }

    companion object {
        fun provideFactory(
            remoteRepository: RemoteRepository,
            localRepository: LocalRepository,
            errorState: MutableState<Int>,
        ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return ExchangeViewModel(remoteRepository, localRepository, errorState) as T
            }
        }

        const val SORT_DEFAULT = -1
        const val SORT_PRICE_DEC = 0
        const val SORT_PRICE_ASC = 1
        const val SORT_RATE_DEC = 2
        const val SORT_RATE_ASC = 3
        const val SORT_AMOUNT_DEC = 4
        const val SORT_AMOUNT_ASC = 5
    }
}