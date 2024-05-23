package org.jeonfeel.moeuibit2.ui.main.exchange.root_exchange

import android.os.Handler
import android.os.Looper
import androidx.compose.runtime.MutableIntState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableDoubleStateOf
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.orhanobut.logger.Logger
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.jeonfeel.moeuibit2.MoeuiBitDataStore
import org.jeonfeel.moeuibit2.constants.BITTHUMB_BTC_MARKET
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
import org.jeonfeel.moeuibit2.data.remote.retrofit.model.bitthumb.BitthumbCommonExchangeModel
import org.jeonfeel.moeuibit2.data.remote.retrofit.model.bitthumb.BitthumbTickerModel
import org.jeonfeel.moeuibit2.data.remote.retrofit.model.upbit.CommonExchangeModel
import org.jeonfeel.moeuibit2.data.remote.retrofit.model.upbit.MarketCodeModel
import org.jeonfeel.moeuibit2.data.remote.websocket.bitthumb.BitthumbTickerWebSocket
import org.jeonfeel.moeuibit2.data.remote.websocket.listener.upbit.OnTickerMessageReceiveListener
import org.jeonfeel.moeuibit2.data.remote.websocket.upbit.UpBitTickerWebSocket
import org.jeonfeel.moeuibit2.data.repository.local.LocalRepository
import org.jeonfeel.moeuibit2.data.repository.remote.RemoteRepository
import org.jeonfeel.moeuibit2.ui.main.exchange.ExchangeViewModel
import org.jeonfeel.moeuibit2.utils.NetworkMonitorUtil
import org.jeonfeel.moeuibit2.utils.Utils

class BitThumb(
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
    private var bitthumbCoinNameJson: JsonObject? = null

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

    suspend fun initBitThumbData() {
        if (krwMarketCodeList.isEmpty()
            || btcMarketCodeList.isEmpty()
        ) {
            requestCoinName()
            requestKRWMarketCode()
            requestBTCMarketCode()
            withContext(ioDispatcher) {
                initFavoriteData()
            }
        }
        setSocketFavoriteData()
        requestCoinListToWebSocket()
    }

    private suspend fun requestCoinName() {
        remoteRepository.getBitthumbCoinNameService().collect {
            when (it.status) {
                ApiResult.Status.LOADING -> {}
                ApiResult.Status.SUCCESS -> {
                    bitthumbCoinNameJson = it.data
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
     * get market, koreanName, englishName, warning
     */
    private suspend fun requestKRWMarketCode() {
        remoteRepository.getBitThumbKRWMarketCodeService().collect {
            when (it.status) {
                ApiResult.Status.LOADING -> {}
                ApiResult.Status.SUCCESS -> {
                    try {
                        val data = it.data ?: JsonObject()
                        val dataObject = data["data"] as JsonObject
                        val marketCodeList: List<String> =
                            Utils.extractCryptoKeysWithGson(dataObject)
                        val indices = marketCodeList.size
                        Logger.e("indices -> ${indices}")
                        for (i in 0 until indices) {
                            val marketCode = "KRW-${marketCodeList[i]}"
                            val onlyMarketCode = marketCodeList[i]
                            val coinNames = parseCoinName(marketCode = onlyMarketCode)
                            MoeuiBitDataStore.bitThumbCoinName[marketCode] = coinNames
                            val bitthumbCommonExchangeModel = gson.fromJson(
                                dataObject[onlyMarketCode],
                                BitthumbCommonExchangeModel::class.java
                            ).apply {
                                this.market = marketCode
                                this.symbol = onlyMarketCode
                                this.koreanName = coinNames.first
                                this.englishName = coinNames.second
                            }
                            krwMarketListStringBuffer.append("\"${onlyMarketCode}_KRW\",")
                            krwMarketCodeList.add(
                                MarketCodeModel(
                                    market = marketCode,
                                    korean_name = coinNames.first,
                                    english_name = coinNames.second,
                                    market_warning = ""
                                )
                            )
                            krwExchangeModelList.add(
                                CommonExchangeModel(
                                    koreanName = bitthumbCommonExchangeModel.koreanName,
                                    englishName = bitthumbCommonExchangeModel.englishName,
                                    market = bitthumbCommonExchangeModel.market,
                                    symbol = bitthumbCommonExchangeModel.symbol,
                                    opening_price = bitthumbCommonExchangeModel.opening_price.toDouble(),
                                    tradePrice = bitthumbCommonExchangeModel.tradePrice.toDouble(),
                                    signedChangeRate = bitthumbCommonExchangeModel.signedChangeRate * 0.01,
                                    accTradePrice24h = bitthumbCommonExchangeModel.accTradePrice24h.toDouble(),
                                    warning = ""
                                )
                            )
                        }
                        krwMarketListStringBuffer.deleteCharAt(krwMarketListStringBuffer.lastIndex)
                        krwExchangeModelList.sortByDescending { model ->
                            model.accTradePrice24h
                        }
                        for (i in krwExchangeModelList.indices) {
                            krwExchangeModelListPosition[krwExchangeModelList[i].market] = i
                        }
                        MoeuiBitDataStore.bitThumbKrwMarkets = krwExchangeModelListPosition
                        swapList(SELECTED_KRW_MARKET)
                        krwPreItemArray.addAll(krwExchangeModelList)

                        Logger.e("list -> ${krwExchangeModelList.toString()}")
                    } catch (e: Exception) {
                        Logger.e("error -> ${e.message}")
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
     * 사용자 관심코인 변경사항 업데이트
     */
    suspend fun updateFavorite(
        market: String = "",
        isFavorite: Boolean = false,
    ) {
        if (market.isNotEmpty()) {
            when {
                MoeuiBitDataStore.bitThumbFavoriteHashMap[market] == null && isFavorite -> {
                    MoeuiBitDataStore.bitThumbFavoriteHashMap[market] = 0
                    try {
                        localRepository.getFavoriteDao().insert(market)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }

                MoeuiBitDataStore.bitThumbFavoriteHashMap[market] != null && !isFavorite -> {
                    MoeuiBitDataStore.bitThumbFavoriteHashMap.remove(market)
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

    /**
     * get market, koreanName, englishName, warning
     */
    private suspend fun requestBTCMarketCode() {
        remoteRepository.getBitThumbBTCMarketCodeService().collect {
            when (it.status) {
                ApiResult.Status.LOADING -> {}
                ApiResult.Status.SUCCESS -> {
                    try {
                        val data = it.data ?: JsonObject()
                        val dataObject = data["data"] as JsonObject
                        val marketCodeList: List<String> =
                            Utils.extractCryptoKeysWithGson(dataObject)
                        val indices = marketCodeList.size
                        for (i in 0 until indices) {
                            val marketCode = marketCodeList[i]
                            val bitthumbCommonExchangeModel = gson.fromJson(
                                dataObject[marketCode],
                                BitthumbCommonExchangeModel::class.java
                            ).apply {
                                this.market = "BTC-$marketCode"
                                this.symbol = marketCode
                                this.koreanName =
                                    MoeuiBitDataStore.bitThumbCoinName[marketCode]?.first ?: ""
                                this.englishName =
                                    MoeuiBitDataStore.bitThumbCoinName[marketCode]?.second ?: ""
                            }
                            btcMarketListStringBuffer.append("\"${marketCode}_BTC\",")
                            btcMarketCodeList.add(
                                MarketCodeModel(
                                    market = "BTC-$marketCode",
                                    korean_name = MoeuiBitDataStore.bitThumbCoinName[marketCode]?.first
                                        ?: "",
                                    english_name = MoeuiBitDataStore.bitThumbCoinName[marketCode]?.second
                                        ?: "",
                                    market_warning = ""
                                )
                            )
                            btcExchangeModelList.add(
                                CommonExchangeModel(
                                    koreanName = bitthumbCommonExchangeModel.koreanName,
                                    englishName = bitthumbCommonExchangeModel.englishName,
                                    market = bitthumbCommonExchangeModel.market,
                                    symbol = bitthumbCommonExchangeModel.symbol,
                                    opening_price = bitthumbCommonExchangeModel.opening_price.toDouble(),
                                    tradePrice = bitthumbCommonExchangeModel.tradePrice.toDouble(),
                                    signedChangeRate = bitthumbCommonExchangeModel.signedChangeRate * 0.01,
                                    accTradePrice24h = bitthumbCommonExchangeModel.accTradePrice24h.toDouble(),
                                    warning = ""
                                )
                            )
                        }
                        btcMarketListStringBuffer.deleteCharAt(btcMarketListStringBuffer.lastIndex)
                        btcExchangeModelList.sortByDescending { model ->
                            model.accTradePrice24h
                        }
                        for (i in btcExchangeModelList.indices) {
                            btcExchangeModelListPosition[btcExchangeModelList[i].market] = i
                        }
                        MoeuiBitDataStore.bitThumbBtcMarkets = btcExchangeModelListPosition
                        Logger.e("krwMarketStringBuffer -> ${krwMarketListStringBuffer.toString()}")
                        Logger.e("btcMarketListStringBuffer -> ${btcMarketListStringBuffer.toString()}")
                        BitthumbTickerWebSocket.setMarkets(
                            krwMarkets = krwMarketListStringBuffer.toString(),
                            btcMarkets = btcMarketListStringBuffer.toString()
                        )
                        swapList(SELECTED_BTC_MARKET)
                        btcPreItemArray.addAll(btcExchangeModelList)
                    } catch (e: Exception) {
                        e.printStackTrace()
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
        if (BitthumbTickerWebSocket.currentMarket != marketState) {
            if (marketState == SELECTED_FAVORITE) {
                viewModelScope.launch(ioDispatcher) {
                    requestFavoriteData(
                        sortButtonState = sortButtonState
                    )
                }
            }
            BitthumbTickerWebSocket.onPause()
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

    private fun requestCoinListToWebSocket() {
        BitthumbTickerWebSocket.tickerListener = this
        BitthumbTickerWebSocket.requestKrwCoinList(selectedMarketState.value)
    }

    private fun parseCoinName(marketCode: String): Pair<String, String> {
        return try {
            val names = bitthumbCoinNameJson?.let { json ->
                json[marketCode]
            }
            val krName = (names as JsonObject)["kr_name"].asString
            val engName = (names as JsonObject)["eng_name"].asString
            Pair(krName, engName)
        } catch (e: Exception) {
            Pair("", "")
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

    private fun setSocketFavoriteData() {
        val list = MoeuiBitDataStore.bitThumbFavoriteHashMap.keys.toList()
        if (list.isNotEmpty()) {
            val favoriteMarketListStringBuffer = StringBuffer()
            for (i in list) {
                favoriteMarketListStringBuffer.append("${i},")
            }

            favoriteMarketListStringBuffer.deleteCharAt(favoriteMarketListStringBuffer.lastIndex)

            if (MoeuiBitDataStore.bitThumbFavoriteHashMap[BTC_MARKET] == null) {
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
            BitthumbTickerWebSocket.setFavoriteMarkets("pause")
            swapList(SELECTED_FAVORITE)
        }
        val favoriteMarketListStringBuffer = StringBuffer()
        val tempList = arrayListOf<CommonExchangeModel>()
        for (i in favoriteList.indices) {
            val market = favoriteList[i]?.market ?: ""
            MoeuiBitDataStore.bitThumbFavoriteHashMap[market] = 0
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

        if (MoeuiBitDataStore.bitThumbFavoriteHashMap[BTC_MARKET] == null) {
            BitthumbTickerWebSocket.setFavoriteMarkets("$favoriteMarketListStringBuffer,$BTC_MARKET")
        } else {
            BitthumbTickerWebSocket.setFavoriteMarkets(favoriteMarketListStringBuffer.toString())
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
        val model = gson.fromJson(tickerJsonObject, BitthumbTickerModel::class.java)
        Logger.e("model -> ${model.toString()}")
        if (model.content != null && model.content.closePrice != "0") {
            val marketState = selectedMarketState.value
            var position = 0
            var targetModelList: ArrayList<CommonExchangeModel>? = null
            val modelCode = Utils.bitthumbMarketToUpbitMarket(model.content.symbol)
            Logger.e("modelCode -> ${modelCode}")
            if (UpBitTickerWebSocket.currentPage == IS_EXCHANGE_SCREEN) {
                if (isUpdateExchange.value && modelCode.startsWith(SYMBOL_KRW)) {
                    when { // BTC 마켓 일떄 비트코인 가격 받아오기 위해
                        marketState == SELECTED_BTC_MARKET && model.content.symbol == BITTHUMB_BTC_MARKET -> {
                            btcTradePrice.doubleValue = model.content.closePrice.toDouble()
                        } // 관심 코인 화면에서 비트코인 가격 받아올 때
                        marketState == SELECTED_FAVORITE && model.content.symbol == BITTHUMB_BTC_MARKET -> {
                            btcTradePrice.doubleValue =
                                model.content.closePrice.toDouble() // 관심코인에 비트코인이 있을 시
                            if (MoeuiBitDataStore.bitThumbFavoriteHashMap[BTC_MARKET] != null) {
                                position = favoriteExchangeModelListPosition[modelCode] ?: 0
                                targetModelList = favoriteExchangeModelList
                            }
                        } // 관심코인일 때
                        marketState == SELECTED_FAVORITE -> {
                            position = favoriteExchangeModelListPosition[modelCode] ?: 0
                            targetModelList = favoriteExchangeModelList
                        } // krw 마켓 일 때
                        else -> {
                            position = krwExchangeModelListPosition[modelCode] ?: 0
                            targetModelList = krwExchangeModelList
                        }
                    }
                } else if (isUpdateExchange.value && modelCode.startsWith(SYMBOL_BTC)) { // BTC 마켓 일 떄
                    targetModelList = when (marketState) {
                        SELECTED_FAVORITE -> {
                            position = favoriteExchangeModelListPosition[modelCode] ?: 0
                            favoriteExchangeModelList
                        }

                        else -> {
                            position = btcExchangeModelListPosition[modelCode] ?: 0
                            btcExchangeModelList
                        }
                    }
                }
                if (isUpdateExchange.value) {
                    targetModelList?.let {
                        targetModelList.ifEmpty { return@let }
                        targetModelList[position] = CommonExchangeModel(
                            koreanName = MoeuiBitDataStore.bitThumbCoinName[modelCode]?.first ?: "",
                            englishName = MoeuiBitDataStore.bitThumbCoinName[modelCode]?.second
                                ?: "",
                            market = modelCode,
                            symbol = modelCode.substring(4),
                            opening_price = model.content.openPrice.toDouble(),
                            tradePrice = model.content.closePrice.toDouble(),
                            signedChangeRate = model.content.chgRate.toDouble() * 0.01,
                            accTradePrice24h = model.content.value.toDouble(),
                            warning = ""
                        )
                    }
                }
            }
        }
    }
}