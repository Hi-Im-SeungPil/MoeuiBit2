package org.jeonfeel.moeuibit2.activity.main

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeoutOrNull
import org.jeonfeel.moeuibit2.constant.*
import org.jeonfeel.moeuibit2.data.local.room.entity.Favorite
import org.jeonfeel.moeuibit2.data.local.room.entity.MyCoin
import org.jeonfeel.moeuibit2.data.remote.retrofit.model.ExchangeModel
import org.jeonfeel.moeuibit2.data.remote.retrofit.model.KrwExchangeModel
import org.jeonfeel.moeuibit2.data.remote.retrofit.model.MarketCodeModel
import org.jeonfeel.moeuibit2.data.remote.retrofit.model.TickerModel
import org.jeonfeel.moeuibit2.data.remote.websocket.UpBitPortfolioWebSocket
import org.jeonfeel.moeuibit2.data.remote.websocket.UpBitTickerWebSocket
import org.jeonfeel.moeuibit2.data.remote.websocket.listener.OnTickerMessageReceiveListener
import org.jeonfeel.moeuibit2.data.remote.websocket.listener.PortfolioOnTickerMessageReceiveListener
import org.jeonfeel.moeuibit2.repository.local.LocalRepository
import org.jeonfeel.moeuibit2.repository.remote.RemoteRepository
import org.jeonfeel.moeuibit2.ui.mainactivity.portfolio.dto.UserHoldCoinDTO
import org.jeonfeel.moeuibit2.util.NetworkMonitorUtil.Companion.currentNetworkState
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val remoteRepository: RemoteRepository,
    private val localRepository: LocalRepository,
) : ViewModel(), OnTickerMessageReceiveListener, PortfolioOnTickerMessageReceiveListener {

    private val gson = Gson()
    val selectedMarket = mutableStateOf(SELECTED_KRW_MARKET)
    var isSocketRunning = false
    var isPortfolioSocketRunning = false

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
    private val userHoldCoinHashMap = HashMap<String, MyCoin>()
    var totalPurchase = mutableStateOf(0.0)
    private var userHoldCoinsMarket = StringBuffer()
    private val userHoldCoinDtoListPositionHashMap = HashMap<String, Int>()
    private val tempUserHoldCoinDtoList = ArrayList<UserHoldCoinDTO>()
    val userHoldCoinDtoList = mutableStateListOf<UserHoldCoinDTO>()
    val totalValuedAssets = mutableStateOf(0.0)
    val portfolioLoadingComplete = mutableStateOf(false)
    var removeCoinCount = mutableStateOf(0)

    val adLoadingDialogState = mutableStateOf(false)
    val adDialogState = mutableStateOf(false)
    private val _adMutableLiveData = MutableLiveData<Int>()
    val adLiveData: LiveData<Int> get() = _adMutableLiveData

    fun initViewModel() {
        if (krwExchangeModelMutableStateList.isEmpty()) {
            requestData()
            requestFavoriteData()
        } else {
            requestKrwCoinList()
        }
    }

    /**
     * request data
     * */
    fun requestData() {
        if (!loading.value) loading.value = true
        when (currentNetworkState) {
            INTERNET_CONNECTION -> {
                viewModelScope.launch {
                    val loadingJob = withTimeoutOrNull(9900L) {
                        viewModelScope.launch {
                            requestKrwMarketCode()
                            requestKrwTicker(krwCoinListStringBuffer.toString())
                        }.join()
                        createKrwExchangeModelList()
                        if (!isSocketRunning) {
                            isSocketRunning = true
                            updateExchange()
                        }
                        if (errorState.value != INTERNET_CONNECTION) {
                            errorState.value = INTERNET_CONNECTION
                        }
                        loading.value = false
                    }
                    if (loadingJob == null) {
                        errorState.value = NETWORK_ERROR
                        loading.value = false
                        isSocketRunning = false
                    }
                }
            }
            else -> {
                loading.value = false
                isSocketRunning = false
                errorState.value = currentNetworkState
            }
        }
    }

    // get market, koreanName, englishName, warning
    private suspend fun requestKrwMarketCode() {
        delay(50L)
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
        delay(50L)
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
            val warning = krwMarketCodeList[i].market_warning
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
        krwExchangeModelList.sortByDescending { it.accTradePrice24h }
        for (i in krwExchangeModelList.indices) {
            krwExchangeModelListPosition[krwExchangeModelList[i].market] = i
        }
        krwExchangeModelMutableStateList.addAll(krwExchangeModelList)
        preItemArray.addAll(krwExchangeModelList)
        requestKrwCoinList()
    }

    private fun updateExchange() {
        viewModelScope.launch {
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
        if (!isSocketRunning) {
            isSocketRunning = true
            updateExchange()
        }
    }

    private fun requestFavoriteData() {
        viewModelScope.launch(ioDispatcher) {
            val favoriteList = localRepository.getFavoriteDao().all ?: emptyList<Favorite>()
            if (favoriteList.isNotEmpty()) {
                for (i in favoriteList.indices)
                    favoriteHashMap[favoriteList[i]?.market ?: ""] = 0
            }
        }
    }

    fun updateFavorite(market: String, isFavorite: Boolean) {
        if (favoriteHashMap[market] == null && isFavorite) {
            viewModelScope.launch(ioDispatcher) {
                favoriteHashMap[market] = 0
                localRepository.getFavoriteDao().insert(market)
            }
        } else if (favoriteHashMap[market] != null && !isFavorite) {
            viewModelScope.launch(ioDispatcher) {
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

    //ㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡㅡ
    fun getUserSeedMoney() {
        viewModelScope.launch(ioDispatcher) {
            userSeedMoney.value = localRepository.getUserDao().all?.krw ?: 0L
        }
    }

    suspend fun getUserHoldCoins() {
        if (portfolioLoadingComplete.value) {
            portfolioLoadingComplete.value = false
        }
        isPortfolioSocketRunning = false
        var localTotalPurchase = 0.0
        userHoldCoinDtoList.clear()
        viewModelScope.launch(ioDispatcher) {
            userHoldCoinList = localRepository.getMyCoinDao().all ?: emptyList()
        }.join()
        userHoldCoinsMarket = StringBuffer()
        userHoldCoinDtoListPositionHashMap.clear()
        tempUserHoldCoinDtoList.clear()
        if (userHoldCoinList.isNotEmpty()) {
            for (i in userHoldCoinList.indices) {
                val userHoldCoin = userHoldCoinList[i] ?: MyCoin("", 0.0, "", "", 0.0)
                val koreanName = krwCoinKoreanNameAndEngName[userHoldCoin.market]?.get(0) ?: ""
                val symbol = userHoldCoin.symbol
                val position = krwExchangeModelListPosition[userHoldCoin.market] ?: 0
                val quantity = userHoldCoin.quantity
                val purchaseAverage = userHoldCoin.purchasePrice
                val openingPrice = krwExchangeModelList[position].opening_price
                val warning = krwExchangeModelList[position].warning
                val isFavorite = favoriteHashMap["KRW-".plus(symbol)]
                userHoldCoinHashMap["KRW-".plus(symbol)] = userHoldCoin
                localTotalPurchase += (userHoldCoin.quantity * userHoldCoin.purchasePrice)
                userHoldCoinsMarket.append(userHoldCoin.market).append(",")
                userHoldCoinDtoList.add(
                    UserHoldCoinDTO(
                        koreanName,
                        symbol,
                        quantity,
                        purchaseAverage,
                        openingPrice = openingPrice,
                        warning = warning,
                        isFavorite = isFavorite
                    )
                )
                tempUserHoldCoinDtoList.add(
                    UserHoldCoinDTO(
                        koreanName,
                        symbol,
                        quantity,
                        purchaseAverage,
                        openingPrice = openingPrice,
                        warning = warning,
                        isFavorite = isFavorite
                    )
                )
                userHoldCoinDtoListPositionHashMap[userHoldCoin.market] = i
            }
            sortUserHoldCoin(-1)
            userHoldCoinsMarket.deleteCharAt(userHoldCoinsMarket.lastIndex)
            UpBitPortfolioWebSocket.setMarkets(userHoldCoinsMarket.toString())
            totalPurchase.value = localTotalPurchase
            UpBitPortfolioWebSocket.getListener()
                .setPortfolioMessageListener(this@MainViewModel)
            UpBitPortfolioWebSocket.requestKrwCoinList()
            updateUserHoldCoins()
            portfolioLoadingComplete.value = true
        } else {
            totalValuedAssets.value = 0.0
            totalPurchase.value = 0.0
            portfolioLoadingComplete.value = true
        }

    }

    fun sortUserHoldCoin(sortStandard: Int) {
        isPortfolioSocketRunning = false
        when (sortStandard) {
            0 -> {
                tempUserHoldCoinDtoList.sortByDescending { element ->
                    element.myCoinsKoreanName
                }
            }
            1 -> {
                tempUserHoldCoinDtoList.sortBy { element ->
                    element.myCoinsKoreanName
                }
            }
            2 -> {
                tempUserHoldCoinDtoList.sortBy { element ->
                    element.myCoinsBuyingAverage / element.currentPrice
                }
            }
            3 -> {
                tempUserHoldCoinDtoList.sortByDescending { element ->
                    element.myCoinsBuyingAverage / element.currentPrice
                }
            }
            else -> {
                tempUserHoldCoinDtoList.sortBy { element ->
                    element.myCoinsKoreanName
                }
            }
        }

        for (i in tempUserHoldCoinDtoList.indices) {
            userHoldCoinDtoListPositionHashMap["KRW-".plus(tempUserHoldCoinDtoList[i].myCoinsSymbol)] =
                i
        }

        for (i in tempUserHoldCoinDtoList.indices) {
            userHoldCoinDtoList[i] = tempUserHoldCoinDtoList[i]
        }

        isPortfolioSocketRunning = true
    }

    fun editUserHoldCoin() {
        var count = 1
        isPortfolioSocketRunning = false

        viewModelScope.launch(ioDispatcher) {
            if (UpBitPortfolioWebSocket.currentSocketState != SOCKET_IS_CONNECTED || currentNetworkState != INTERNET_CONNECTION) {
                removeCoinCount.value = -1
                delay(100L)
                removeCoinCount.value = 0
            } else {
                if (krwExchangeModelListPosition.isNotEmpty()) {
                    for (i in userHoldCoinList) {
                        if (krwExchangeModelListPosition[i!!.market] == null) {
                            localRepository.getFavoriteDao().delete(i.market)
                            localRepository.getMyCoinDao().delete(i.market)
                            localRepository.getTransactionInfoDao().delete(i.market)
                            count += 1
                        } else if(i.quantity == 0.0 || i.purchasePrice == 0.0 || i.quantity == Double.POSITIVE_INFINITY || i.quantity == Double.NEGATIVE_INFINITY) {
                            localRepository.getMyCoinDao().delete(i.market)
                            localRepository.getTransactionInfoDao().delete(i.market)
                            count += 1
                        }
                    }
                    if (count > 1) {
                        isPortfolioSocketRunning = false
                        UpBitPortfolioWebSocket.getListener().setPortfolioMessageListener(null)
                        UpBitPortfolioWebSocket.onPause()
                        getUserHoldCoins()
                    }
                    removeCoinCount.value = count
                    delay(100L)
                    removeCoinCount.value = 0
                }
            }
        }
    }

    private fun updateUserHoldCoins() {
        if(!isPortfolioSocketRunning) {
            isPortfolioSocketRunning = !isPortfolioSocketRunning
        }
        viewModelScope.launch {
            while (isPortfolioSocketRunning) {
                var tempTotalValuedAssets = 0.0
                try{
                    for (i in tempUserHoldCoinDtoList.indices) {
                        val userHoldCoinDTO = tempUserHoldCoinDtoList[i]
                        userHoldCoinDtoList[i] = userHoldCoinDTO
                        tempTotalValuedAssets += userHoldCoinDTO.currentPrice * userHoldCoinDTO.myCoinsQuantity
                    }
                    totalValuedAssets.value = tempTotalValuedAssets
                    delay(300)
                }catch (e:Exception) {
                    delay(300)
                }
            }
        }
    }

    fun resetAll() {
        viewModelScope.launch(ioDispatcher) {
            localRepository.getUserDao().deleteAll()
            localRepository.getFavoriteDao().deleteAll()
            localRepository.getTransactionInfoDao().deleteAll()
            localRepository.getMyCoinDao().deleteAll()
        }
    }

    fun resetTransactionInfo() {
        viewModelScope.launch(ioDispatcher) {
            localRepository.getTransactionInfoDao().deleteAll()
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
                    model.accTradePrice24h,
                    model.marketWarning
                )
        }
    }

    override fun portfolioOnTickerMessageReceiveListener(tickerJsonObject: String) {
        if (isPortfolioSocketRunning) {
            val model = gson.fromJson(tickerJsonObject, TickerModel::class.java)
            val tickerListPosition = krwExchangeModelListPosition[model.code] ?: 0
            val position = userHoldCoinDtoListPositionHashMap[model.code] ?: 0
            val userHoldCoin = userHoldCoinHashMap[model.code]!!
            val openingPrice = krwExchangeModelList[tickerListPosition].opening_price
            val warning = model.marketWarning
            val isFavorite = favoriteHashMap[model.code]
            tempUserHoldCoinDtoList[position] =
                UserHoldCoinDTO(
                    krwCoinKoreanNameAndEngName[model.code]?.get(0) ?: "",
                    userHoldCoin.symbol,
                    userHoldCoin.quantity,
                    userHoldCoin.purchasePrice,
                    model.tradePrice,
                    openingPrice = openingPrice,
                    warning = warning,
                    isFavorite = isFavorite
                )
        }
    }

    fun updateAdLiveData() {
        adLoadingDialogState.value = true
        _adMutableLiveData.value = 1
    }

    fun earnReward() {
        viewModelScope.launch(ioDispatcher) {
            val userDao = localRepository.getUserDao()
            if (userDao.all == null) {
                userDao.insert()
            } else {
                userDao.updatePlusMoney(10_000_000)
            }
        }
    }

    fun errorReward() {
        viewModelScope.launch(ioDispatcher) {
            val userDao = localRepository.getUserDao()
            if (userDao.all == null) {
                userDao.errorInsert()
            } else {
                userDao.updatePlusMoney(1_000_000)
            }
        }
    }
}