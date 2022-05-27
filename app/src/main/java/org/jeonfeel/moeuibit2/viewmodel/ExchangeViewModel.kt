package org.jeonfeel.moeuibit2.viewmodel

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
import kotlinx.coroutines.withTimeoutOrNull
import org.jeonfeel.moeuibit2.INTERNET_CONNECTION
import org.jeonfeel.moeuibit2.NETWORK_ERROR
import org.jeonfeel.moeuibit2.SELECTED_KRW_MARKET
import org.jeonfeel.moeuibit2.data.local.room.entity.Favorite
import org.jeonfeel.moeuibit2.data.local.room.entity.MyCoin
import org.jeonfeel.moeuibit2.data.remote.retrofit.model.ExchangeModel
import org.jeonfeel.moeuibit2.data.remote.retrofit.model.KrwExchangeModel
import org.jeonfeel.moeuibit2.data.remote.retrofit.model.MarketCodeModel
import org.jeonfeel.moeuibit2.data.remote.retrofit.model.TickerModel
import org.jeonfeel.moeuibit2.data.remote.websocket.UpBitPortfolioWebSocket
import org.jeonfeel.moeuibit2.data.remote.websocket.UpBitTickerWebSocket
import org.jeonfeel.moeuibit2.dtos.UserHoldCoinDTO
import org.jeonfeel.moeuibit2.listener.OnTickerMessageReceiveListener
import org.jeonfeel.moeuibit2.listener.PortfolioOnTickerMessageReceiveListener
import org.jeonfeel.moeuibit2.repository.local.LocalRepository
import org.jeonfeel.moeuibit2.repository.remote.RemoteRepository
import org.jeonfeel.moeuibit2.util.NetworkMonitorUtil.Companion.currentNetworkState
import javax.inject.Inject

@HiltViewModel
class ExchangeViewModel @Inject constructor(
    private val remoteRepository: RemoteRepository,
    private val localRepository: LocalRepository,
) : ViewModel(), OnTickerMessageReceiveListener, PortfolioOnTickerMessageReceiveListener {

    private val TAG = ExchangeViewModel::class.java.simpleName
    private val gson = Gson()
    val selectedMarket = mutableStateOf(SELECTED_KRW_MARKET)
    var isSocketRunning = true
    var isPortfolioSocketRunning = true

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
    val selectedButtonState = mutableStateOf(-1)
    val loading = mutableStateOf(true)

    val favoriteHashMap = HashMap<String, Int>()
    var showFavorite = mutableStateOf(false)

    val userSeedMoney = mutableStateOf(0L)
    var userHoldCoinList = emptyList<MyCoin?>()
    var totalPurchase = mutableStateOf(0.0)
    val TotalHoldings = mutableStateOf("")
    var userHoldCoinsMarket = StringBuffer()
    val userHoldCoinDtoListPositionHashMap = HashMap<String, Int>()
    val tempUserHoldCoinDtoList = ArrayList<UserHoldCoinDTO>()
    val userHoldCoinDtoList = mutableStateListOf<UserHoldCoinDTO>()
    val totalValuedAssets = mutableStateOf(0.0)
    val loadingComplete = mutableStateOf(false)


    fun initViewModel() {
        if (krwExchangeModelMutableStateList.isEmpty()) {
            setWebSocketMessageListener()
            requestData()
            requestFavoriteData()
        } else {
            requestKrwCoinList()
        }
    }

    private fun setWebSocketMessageListener() {
        UpBitTickerWebSocket.getListener().setTickerMessageListener(this)
        UpBitPortfolioWebSocket.getListener().setPortfolioMessageListener(this)
    }

    /**
     * request data
     * */
    fun requestData() {
        if (!loading.value) loading.value = true
        when (currentNetworkState) {
            INTERNET_CONNECTION -> {
                viewModelScope.launch {
                    val loadingJob = withTimeoutOrNull(4900L) {
                        requestKrwMarketCode()
                        requestKrwTicker(krwCoinListStringBuffer.toString())
                        createKrwExchangeModelList()
                        updateExchange()
                        if (errorState.value != INTERNET_CONNECTION) {
                            errorState.value = INTERNET_CONNECTION
                        }
                        loading.value = false
                    }
                    if (loadingJob == null) {
                        errorState.value = NETWORK_ERROR
                        loading.value = false
                    }
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
        val resultMarketCode = remoteRepository.getMarketCodeService()
        if (resultMarketCode.isSuccessful) {
            try {
                val indices = resultMarketCode.body()!!.size()
                for (i in 0 until indices) {
                    val krwMarketCode =
                        gson.fromJson(resultMarketCode.body()!![i], MarketCodeModel::class.java)
                    if (krwMarketCode.market.contains("KRW-")) {
                        krwCoinListStringBuffer.append("${krwMarketCode.market},")
                        krwMarketCodeList.add(krwMarketCode)
                    }
                }
                krwCoinListStringBuffer.deleteCharAt(krwCoinListStringBuffer.lastIndex)
                UpBitTickerWebSocket.setKrwMarkets(krwCoinListStringBuffer.toString())
                val marketCodeIndices = krwMarketCodeList.size
                for (i in 0 until marketCodeIndices) {
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
            remoteRepository.getKrwTickerService(markets)
        if (resultKrwTicker.isSuccessful) {
            try {
                val indices = resultKrwTicker.body()!!.size()
                for (i in 0 until indices) {
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
        val indices = krwMarketCodeList.size
        for (i in 0 until indices) {
            val koreanName = krwMarketCodeList[i].korean_name
            val englishName = krwMarketCodeList[i].english_name
            val market = krwMarketCodeList[i].market
            val tradePrice = krwTickerList[i].tradePrice
            val signedChangeRate = krwTickerList[i].signedChangePrice
            val accTradePrice24h = krwTickerList[i].accTradePrice24h
            val symbol = market.substring(4)
            val openingPrice = krwTickerList[i].preClosingPrice
            krwExchangeModelList.add(
                KrwExchangeModel(
                    koreanName,
                    englishName,
                    market,
                    symbol,
                    openingPrice,
                    tradePrice,
                    signedChangeRate,
                    accTradePrice24h
                )
            )
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
                for (i in krwExchangeModelMutableStateList.indices) {
                    krwExchangeModelMutableStateList[i] = krwExchangeModelList[i]
                }
                delay(300)
            }
        }
    }

    private fun requestKrwCoinList() {
        UpBitTickerWebSocket.getListener().setTickerMessageListener(this)
        UpBitTickerWebSocket.requestKrwCoinList()
    }

    private fun requestFavoriteData() {
        viewModelScope.launch(Dispatchers.IO) {
            val favoriteList = localRepository.getFavoriteDao().all ?: emptyList<Favorite>()
            if (favoriteList.isNotEmpty()) {
                for (i in favoriteList.indices)
                    favoriteHashMap[favoriteList[i]?.market ?: ""] = 0
            }
        }
    }

    fun updateFavorite(market: String, isFavorite: Boolean) {
        if (favoriteHashMap[market] == null && isFavorite) {
            viewModelScope.launch(Dispatchers.IO) {
                favoriteHashMap[market] = 0
                localRepository.getFavoriteDao().insert(market)
            }
        } else if (favoriteHashMap[market] != null && !isFavorite) {
            viewModelScope.launch(Dispatchers.IO) {
                favoriteHashMap.remove(market)
                localRepository.getFavoriteDao().delete(market)
            }
        }
    }

    /**
     * data sorting, filter
     * */
    fun getFilteredKrwCoinList(): SnapshotStateList<KrwExchangeModel> {
        return if (searchTextFieldValue.value.isEmpty() && !showFavorite.value) {
            krwExchangeModelMutableStateList
        } else if (searchTextFieldValue.value.isEmpty() && showFavorite.value) {
            val favoriteList = SnapshotStateList<KrwExchangeModel>()
            val tempArray = ArrayList<Int>()
            for (i in favoriteHashMap) {
                tempArray.add(krwExchangeModelListPosition[i.key]!!)
            }
            tempArray.sort()
            for (i in tempArray) {
                favoriteList.add(krwExchangeModelMutableStateList[i])
            }
            favoriteList
        } else if (searchTextFieldValue.value.isNotEmpty() && !showFavorite.value) {
            val resultList = SnapshotStateList<KrwExchangeModel>()
            for (element in krwExchangeModelMutableStateList) {
                if (
                    element.koreanName.contains(searchTextFieldValue.value) ||
                    element.EnglishName.uppercase()
                        .contains(searchTextFieldValue.value.uppercase()) ||
                    element.symbol.uppercase().contains(searchTextFieldValue.value.uppercase())
                ) {
                    resultList.add(element)
                }
            }
            resultList
        } else {
            val tempFavoriteList = SnapshotStateList<KrwExchangeModel>()
            val favoriteList = SnapshotStateList<KrwExchangeModel>()
            val tempArray = ArrayList<Int>()
            for (i in favoriteHashMap) {
                tempArray.add(krwExchangeModelListPosition[i.key]!!)
            }
            tempArray.sort()
            for (i in tempArray) {
                tempFavoriteList.add(krwExchangeModelMutableStateList[i])
            }
            for (element in tempFavoriteList) {
                if (
                    element.koreanName.contains(searchTextFieldValue.value) ||
                    element.EnglishName.uppercase()
                        .contains(searchTextFieldValue.value.uppercase()) ||
                    element.symbol.uppercase().contains(searchTextFieldValue.value.uppercase())
                ) {
                    favoriteList.add(element)
                }
            }
            favoriteList
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

        for (i in preItemArray.indices) {
            preItemArray[i] =
                krwExchangeModelList[i]
        }

        for (i in krwExchangeModelList.indices) {
            krwExchangeModelListPosition[krwExchangeModelList[i].market] =
                i
        }

        for (i in krwExchangeModelList.indices) {
            krwExchangeModelMutableStateList[i] =
                krwExchangeModelList[i]
        }

        isSocketRunning = true
    }

    fun getUserSeedMoney() {
        viewModelScope.launch(Dispatchers.IO) {
            userSeedMoney.value = localRepository.getUserDao().all?.krw ?: 0L
        }
    }

    fun getUserHoldCoins() {
        viewModelScope.launch(Dispatchers.IO) {
            if(loadingComplete.value) {
                loadingComplete.value = false
            }
            var localTotalPurchase = 0.0
            userHoldCoinDtoList.clear()
            userHoldCoinsMarket = StringBuffer()
            userHoldCoinDtoListPositionHashMap.clear()
            tempUserHoldCoinDtoList.clear()
            userHoldCoinList = localRepository.getMyCoinDao().all ?: emptyList()
            if (userHoldCoinList.isNotEmpty()) {
                for (i in userHoldCoinList.indices) {
                    val userHoldCoin = userHoldCoinList[i]!!
                    val koreanName = krwCoinKoreanNameAndEngName[userHoldCoin.market]?.get(0) ?: ""
                    val symbol = userHoldCoin.symbol
                    val quantity = userHoldCoin.quantity
                    val purchaseAverage = userHoldCoin.purchasePrice
                    localTotalPurchase += (userHoldCoin.quantity * userHoldCoin.purchasePrice)
                    userHoldCoinsMarket.append(userHoldCoin.market).append(",")
                    userHoldCoinDtoList.add(
                        UserHoldCoinDTO(
                            koreanName,
                            symbol,
                            quantity,
                            purchaseAverage
                        )
                    )
                    tempUserHoldCoinDtoList.add(
                        UserHoldCoinDTO(
                            koreanName,
                            symbol,
                            quantity,
                            purchaseAverage
                        )
                    )
                    userHoldCoinDtoListPositionHashMap[userHoldCoin.market] = i
                }
                userHoldCoinsMarket.deleteCharAt(userHoldCoinsMarket.lastIndex)
                UpBitPortfolioWebSocket.setMarkets(userHoldCoinsMarket.toString())
                totalPurchase.value = localTotalPurchase
                UpBitPortfolioWebSocket.requestKrwCoinList()
                updateUserHoldCoins()
                loadingComplete.value = true
            } else {
                totalPurchase.value = 0.0
                loadingComplete.value = true
            }
        }
    }

    private fun updateUserHoldCoins() {
        viewModelScope.launch(Dispatchers.Main) {
            while (isPortfolioSocketRunning) {
                var tempTotalValuedAssets = 0.0
                for (i in tempUserHoldCoinDtoList.indices) {
                    val userHoldCoinDTO = tempUserHoldCoinDtoList[i]
                    userHoldCoinDtoList[i] = userHoldCoinDTO
                    tempTotalValuedAssets += userHoldCoinDTO.currentPrice * userHoldCoinDTO.myCoinsQuantity
                }
                totalValuedAssets.value = tempTotalValuedAssets
                delay(300)
            }
        }
    }


    override fun onTickerMessageReceiveListener(tickerJsonObject: String) {
        if (isSocketRunning) {
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
                    model.accTradePrice24h
                )
        }
    }

    override fun portfolioOnTickerMessageReceiveListener(tickerJsonObject: String) {
        if (isPortfolioSocketRunning) {
            val model = gson.fromJson(tickerJsonObject, TickerModel::class.java)
            val position = userHoldCoinDtoListPositionHashMap[model.code] ?: -1
            val userHoldCoin = userHoldCoinList[position]!!
            tempUserHoldCoinDtoList[position] =
                UserHoldCoinDTO(
                    krwCoinKoreanNameAndEngName[model.code]?.get(0) ?: "",
                    userHoldCoin.symbol,
                    userHoldCoin.quantity,
                    userHoldCoin.purchasePrice,
                    model.tradePrice,
                )
        }
    }
}