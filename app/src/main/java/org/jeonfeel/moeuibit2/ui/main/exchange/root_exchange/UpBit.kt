package org.jeonfeel.moeuibit2.ui.main.exchange.root_exchange

import android.os.Handler
import android.os.Looper
import androidx.compose.runtime.MutableIntState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableDoubleStateOf
import com.google.gson.Gson
import com.google.gson.JsonArray
import com.orhanobut.logger.Logger
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.jeonfeel.moeuibit2.MoeuiBitDataStore
import org.jeonfeel.moeuibit2.constants.BTC_MARKET
import org.jeonfeel.moeuibit2.constants.IS_EXCHANGE_SCREEN
import org.jeonfeel.moeuibit2.constants.NETWORK_ERROR
import org.jeonfeel.moeuibit2.constants.NO_INTERNET_CONNECTION
import org.jeonfeel.moeuibit2.constants.SELECTED_BTC_MARKET
import org.jeonfeel.moeuibit2.constants.SELECTED_FAVORITE
import org.jeonfeel.moeuibit2.constants.SELECTED_KRW_MARKET
import org.jeonfeel.moeuibit2.constants.SYMBOL_BTC
import org.jeonfeel.moeuibit2.constants.SYMBOL_KRW
import org.jeonfeel.moeuibit2.constants.defaultDispatcher
import org.jeonfeel.moeuibit2.constants.ioDispatcher
import org.jeonfeel.moeuibit2.data.local.room.entity.Favorite
import org.jeonfeel.moeuibit2.data.remote.retrofit.ApiResult
import org.jeonfeel.moeuibit2.data.remote.retrofit.model.upbit.CommonExchangeModel
import org.jeonfeel.moeuibit2.data.remote.retrofit.model.upbit.ExchangeModel
import org.jeonfeel.moeuibit2.data.remote.retrofit.model.upbit.MarketCodeModel
import org.jeonfeel.moeuibit2.data.remote.retrofit.model.upbit.TickerModel
import org.jeonfeel.moeuibit2.data.remote.websocket.upbit.UpBitTickerWebSocket
import org.jeonfeel.moeuibit2.data.remote.websocket.listener.OnTickerMessageReceiveListener
import org.jeonfeel.moeuibit2.data.repository.local.LocalRepository
import org.jeonfeel.moeuibit2.data.repository.remote.RemoteRepository
import org.jeonfeel.moeuibit2.ui.main.exchange.ExchangeViewModel
import org.jeonfeel.moeuibit2.utils.NetworkMonitorUtil

class UpBit(
    private val remoteRepository: RemoteRepository,
    private val localRepository: LocalRepository,
    private val gson: Gson,
    private val changeNetworkState: (Int) -> Unit,
    private val selectedMarketState: State<Int>,
    private val isUpdateExchange: State<Boolean>,
    private val swapList: (market: Int) -> Unit,
    private val changeIsUpdate: (Boolean) -> Unit,
    private val removeStateList: (index: Int) -> Unit,
    private val updateExchange: () -> Unit
) : BaseRootExchange(), OnTickerMessageReceiveListener {
    private val btcTradePrice = mutableDoubleStateOf(0.0)

    private val krwMarketCodeList: ArrayList<MarketCodeModel> = arrayListOf()
    private val btcMarketCodeList: ArrayList<MarketCodeModel> = arrayListOf()
    private val krwMarketListStringBuffer = StringBuffer()
    private val btcMarketListStringBuffer = StringBuffer()

    private val krwExchangeModelList: ArrayList<CommonExchangeModel> = arrayListOf()
    private val krwPreItemArray: ArrayList<CommonExchangeModel> = arrayListOf()
    private val krwExchangeModelListPosition: HashMap<String, Int> = hashMapOf()

    private val btcExchangeModelList: ArrayList<CommonExchangeModel> = arrayListOf()
    private val btcPreItemArray: ArrayList<CommonExchangeModel> = arrayListOf()
    private val btcExchangeModelListPosition: HashMap<String, Int> = hashMapOf()

    private var favoritePreItemArray: ArrayList<CommonExchangeModel> = arrayListOf()
    private var favoriteExchangeModelList: ArrayList<CommonExchangeModel> = arrayListOf()
    private val favoriteExchangeModelListPosition: HashMap<String, Int> = hashMapOf()

    suspend fun initUpBitData() {
        if (krwMarketCodeList.isEmpty() || btcMarketCodeList.isEmpty()) {
            requestMarketCode()
            requestKrwTicker(krwMarketListStringBuffer.toString())
            requestBtcTicker(btcMarketListStringBuffer.toString())
            withContext(ioDispatcher) {
                initFavoriteData()
            }
        }
        setSocketFavoriteData()
        requestCoinListToWebSocket()
    }

    /**
     * get market, koreanName, englishName, warning
     */
    private suspend fun requestMarketCode() {
        remoteRepository.getUpbitMarketCodeService().collect {
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
                            krwMarkets = krwMarketListStringBuffer.toString(),
                            btcMarkets = "$btcMarketListStringBuffer,$BTC_MARKET"
                        )
                        for (i in krwMarketCodeList.indices) {
                            MoeuiBitDataStore.upBitCoinName[krwMarketCodeList[i].market] = Pair(
                                krwMarketCodeList[i].korean_name, krwMarketCodeList[i].english_name
                            )
                        }
                        for (i in btcMarketCodeList.indices) {
                            MoeuiBitDataStore.upBitCoinName[btcMarketCodeList[i].market] = Pair(
                                btcMarketCodeList[i].korean_name, btcMarketCodeList[i].english_name
                            )
                        }
                    } catch (e: Exception) {
                        changeNetworkState(NETWORK_ERROR)
                    }
                }

                ApiResult.Status.API_ERROR -> {
                    changeNetworkState(NETWORK_ERROR)
                }

                ApiResult.Status.NETWORK_ERROR -> {
                    changeNetworkState(NO_INTERNET_CONNECTION)
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
                            MoeuiBitDataStore.upBitKrwMarkets = krwExchangeModelListPosition
                            swapList(SELECTED_KRW_MARKET)
                            krwPreItemArray.addAll(krwExchangeModelList)
                        } else {
                            changeNetworkState(NETWORK_ERROR)
                        }
                    } catch (e: Exception) {
                        NetworkMonitorUtil.currentNetworkState = NETWORK_ERROR
                        changeNetworkState(NETWORK_ERROR)
                    }
                }

                ApiResult.Status.API_ERROR -> {
                    changeNetworkState(NETWORK_ERROR)
                }

                ApiResult.Status.NETWORK_ERROR -> {
                    changeNetworkState(NO_INTERNET_CONNECTION)
                    NetworkMonitorUtil.currentNetworkState = NO_INTERNET_CONNECTION
                }
            }
        }
    }

    /**
     * BTC ticker 요청
     */
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
                            MoeuiBitDataStore.upBitBtcMarkets = btcExchangeModelListPosition
                            swapList(SELECTED_BTC_MARKET)
                            btcPreItemArray.addAll(btcExchangeModelList)
                        }
                    } catch (e: Exception) {
                        NetworkMonitorUtil.currentNetworkState = NETWORK_ERROR
                        changeNetworkState(NETWORK_ERROR)
                    }
                }

                ApiResult.Status.API_ERROR -> {
                    changeNetworkState(NETWORK_ERROR)
                }

                ApiResult.Status.NETWORK_ERROR -> {
                    changeNetworkState(NO_INTERNET_CONNECTION)
                    NetworkMonitorUtil.currentNetworkState = NO_INTERNET_CONNECTION
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
        if (MoeuiBitDataStore.upBitFavoriteHashMap[BTC_MARKET] == null) {
            UpBitTickerWebSocket.setFavoriteMarkets("$favoriteMarketListStringBuffer,$BTC_MARKET")
        } else {
            UpBitTickerWebSocket.setFavoriteMarkets(favoriteMarketListStringBuffer.toString())
        }
    }

    fun marketChangeAction(
        marketState: Int,
        sortButtonState: MutableIntState,
        viewModelScope: CoroutineScope
    ) {
        if (UpBitTickerWebSocket.currentMarket != marketState) {
            if (marketState == SELECTED_FAVORITE) {
                viewModelScope.launch(ioDispatcher) {
                    requestFavoriteData(
                        sortButtonState = sortButtonState
                    )
                }
            }
            UpBitTickerWebSocket.onPause()
            if (marketState != SELECTED_FAVORITE) {
                viewModelScope.launch(defaultDispatcher) {
                    sortList(
                        sortButtonState = sortButtonState
                    )
                }
            }
            requestCoinListToWebSocket()
        }
    }

    /**
     * 사용자 관심코인 변경사항 업데이트
     */
    suspend fun updateFavorite(
        market: String = "",
        isFavorite: Boolean = false,
    ) {
        if (market.isNotEmpty()) {
            when {
                MoeuiBitDataStore.upBitFavoriteHashMap[market] == null && isFavorite -> {
                    MoeuiBitDataStore.upBitFavoriteHashMap[market] = 0
                    try {
                        localRepository.getFavoriteDao().insert(market)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }

                MoeuiBitDataStore.upBitFavoriteHashMap[market] != null && !isFavorite -> {
                    MoeuiBitDataStore.upBitFavoriteHashMap.remove(market)
                    try {
                        localRepository.getFavoriteDao().delete(market)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }

                    if (selectedMarketState.value == SELECTED_FAVORITE) {
                        changeIsUpdate(false)
                        UpBitTickerWebSocket.getListener().setTickerMessageListener(null)
                        UpBitTickerWebSocket.onPause()
                        val position = favoriteExchangeModelListPosition[market]
                        position?.let {
                            favoritePreItemArray.removeAt(it)
                            favoriteExchangeModelList.removeAt(it)
                            removeStateList(it)
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

    suspend fun sortList(
        sortButtonState: MutableIntState
    ) {
        when (sortButtonState.intValue) {
            ExchangeViewModel.SORT_PRICE_DEC -> {
                when (selectedMarketState.value) {
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
                                element.tradePrice * btcTradePrice.doubleValue
                            } else {
                                element.tradePrice
                            }
                        }
                    }
                }
            }

            ExchangeViewModel.SORT_PRICE_ASC -> {
                when (selectedMarketState.value) {
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
                                element.tradePrice * btcTradePrice.doubleValue
                            } else {
                                element.tradePrice
                            }
                        }
                    }
                }
            }

            ExchangeViewModel.SORT_RATE_DEC -> {
                when (selectedMarketState.value) {
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

            ExchangeViewModel.SORT_RATE_ASC -> {
                when (selectedMarketState.value) {
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

            ExchangeViewModel.SORT_AMOUNT_DEC -> {
                when (selectedMarketState.value) {
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
                                element.accTradePrice24h * btcTradePrice.doubleValue
                            } else {
                                element.accTradePrice24h
                            }
                        }
                    }
                }
            }

            ExchangeViewModel.SORT_AMOUNT_ASC -> {
                when (selectedMarketState.value) {
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
                                element.accTradePrice24h * btcTradePrice.doubleValue
                            } else {
                                element.accTradePrice24h
                            }
                        }
                    }
                }
            }

            else -> {
                when (selectedMarketState.value) {
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
                                element.accTradePrice24h * btcTradePrice.doubleValue
                            } else {
                                element.accTradePrice24h
                            }
                        }
                    }
                }
            }
        }

        when (selectedMarketState.value) {
            SELECTED_KRW_MARKET -> {
                Logger.e(krwExchangeModelList[0].market.toString())
                for (i in krwPreItemArray.indices) {

                    krwPreItemArray[i] = krwExchangeModelList[i]
                }

                for (i in krwExchangeModelList.indices) {
                    krwExchangeModelListPosition[krwExchangeModelList[i].market] = i
                }
                swapList(SELECTED_KRW_MARKET)
            }

            SELECTED_BTC_MARKET -> {
                for (i in btcPreItemArray.indices) {
                    btcPreItemArray[i] = btcExchangeModelList[i]
                }
                for (i in btcExchangeModelList.indices) {
                    btcExchangeModelListPosition[btcExchangeModelList[i].market] = i
                }
                swapList(SELECTED_BTC_MARKET)
            }

            else -> {
                for (i in favoritePreItemArray.indices) {
                    favoritePreItemArray[i] = favoriteExchangeModelList[i]
                }
                for (i in favoriteExchangeModelList.indices) {
                    favoriteExchangeModelListPosition[favoriteExchangeModelList[i].market] =
                        i
                }
                swapList(SELECTED_FAVORITE)
            }
        }
    }

    fun getSwapTempList(marketState: Int): List<CommonExchangeModel> {
        if (marketState == SELECTED_KRW_MARKET) {
            return krwExchangeModelList.toList()
        }

        if (marketState == SELECTED_BTC_MARKET) {
            return btcExchangeModelList.toList()
        }

        if (marketState == SELECTED_FAVORITE) {
            return favoriteExchangeModelList.toList()
        }

        return emptyList()
    }

    fun getPreCoinListAndPosition(marketState: Int): Pair<ArrayList<CommonExchangeModel>, HashMap<String, Int>> {
        return when (marketState) {
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

    private suspend fun initFavoriteData() {
        val favoriteList = localRepository.getFavoriteDao().all ?: emptyList<Favorite>()
        for (i in favoriteList) {
            if (i != null) {
                MoeuiBitDataStore.upBitFavoriteHashMap[i.market] = 0
            }
        }
    }

    /**
     * 웹소켓에 실시간 정보 요청
     */
    private fun requestCoinListToWebSocket() {
        UpBitTickerWebSocket.tickerListener = this
        UpBitTickerWebSocket.requestKrwCoinList(selectedMarketState.value)
    }

    private fun setSocketFavoriteData() {
        val list = MoeuiBitDataStore.upBitFavoriteHashMap.keys.toList()
        if (list.isNotEmpty()) {
            val favoriteMarketListStringBuffer = StringBuffer()
            for (i in list) {
                favoriteMarketListStringBuffer.append("${i},")
            }

            favoriteMarketListStringBuffer.deleteCharAt(favoriteMarketListStringBuffer.lastIndex)

            if (MoeuiBitDataStore.upBitFavoriteHashMap[BTC_MARKET] == null) {
                UpBitTickerWebSocket.setFavoriteMarkets("$favoriteMarketListStringBuffer,$BTC_MARKET")
            } else {
                UpBitTickerWebSocket.setFavoriteMarkets(favoriteMarketListStringBuffer.toString())
            }
        }
    }

    private suspend fun requestFavoriteData(
        sortButtonState: MutableIntState
    ) {
        favoritePreItemArray.clear()
        val favoriteList = localRepository.getFavoriteDao().all ?: emptyList<Favorite>()
        favoriteList.ifEmpty {
            favoriteExchangeModelList.clear()
            UpBitTickerWebSocket.setFavoriteMarkets("pause")
            swapList(SELECTED_FAVORITE)
        }
        val favoriteMarketListStringBuffer = StringBuffer()
        val tempList = arrayListOf<CommonExchangeModel>()
        for (i in favoriteList.indices) {
            val market = favoriteList[i]?.market ?: ""
            MoeuiBitDataStore.upBitFavoriteHashMap[market] = 0
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

        if (MoeuiBitDataStore.upBitFavoriteHashMap[BTC_MARKET] == null) {
            UpBitTickerWebSocket.setFavoriteMarkets("$favoriteMarketListStringBuffer,$BTC_MARKET")
        } else {
            UpBitTickerWebSocket.setFavoriteMarkets(favoriteMarketListStringBuffer.toString())
        }
        sortList(
            sortButtonState = sortButtonState
        )
    }

    fun preItemList(): Pair<ArrayList<CommonExchangeModel>, ArrayList<CommonExchangeModel>> {
        return Pair(krwPreItemArray, btcPreItemArray)
    }

    fun getBtcPrice(): State<Double> {
        return if (selectedMarketState.value == SELECTED_BTC_MARKET || selectedMarketState.value == SELECTED_FAVORITE) {
            btcTradePrice
        } else {
            mutableDoubleStateOf(0.0)
        }
    }

    override fun onTickerMessageReceiveListener(tickerJsonObject: String) {
        val model = gson.fromJson(tickerJsonObject, TickerModel::class.java)
        val marketState = selectedMarketState.value
        var position = 0
        var targetModelList: ArrayList<CommonExchangeModel>? = null
        if (UpBitTickerWebSocket.currentPage == IS_EXCHANGE_SCREEN) {
            if (isUpdateExchange.value && model.code.startsWith(SYMBOL_KRW)) {
                when { // BTC 마켓 일떄 비트코인 가격 받아오기 위해
                    marketState == SELECTED_BTC_MARKET && model.code == BTC_MARKET -> {
                        btcTradePrice.doubleValue = model.tradePrice
                    } // 관심 코인 화면에서 비트코인 가격 받아올 때
                    marketState == SELECTED_FAVORITE && model.code == BTC_MARKET -> {
                        btcTradePrice.doubleValue = model.tradePrice // 관심코인에 비트코인이 있을 시
                        if (MoeuiBitDataStore.upBitFavoriteHashMap[BTC_MARKET] != null) {
                            position = favoriteExchangeModelListPosition[model.code] ?: 0
                            targetModelList = favoriteExchangeModelList
                        }
                    } // 관심코인일 때
                    marketState == SELECTED_FAVORITE -> {
                        position = favoriteExchangeModelListPosition[model.code] ?: 0
                        targetModelList = favoriteExchangeModelList
                    } // krw 마켓 일 때
                    else -> {
                        position = krwExchangeModelListPosition[model.code] ?: 0
                        targetModelList = krwExchangeModelList
                    }
                }
            } else if (isUpdateExchange.value && model.code.startsWith(SYMBOL_BTC)) { // BTC 마켓 일 떄
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

            if (isUpdateExchange.value) {
                targetModelList?.let {
                    targetModelList.ifEmpty { return@let }
                    targetModelList[position] = CommonExchangeModel(
                        koreanName = MoeuiBitDataStore.upBitCoinName[model.code]?.first ?: "",
                        englishName = MoeuiBitDataStore.upBitCoinName[model.code]?.second ?: "",
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
    }
}