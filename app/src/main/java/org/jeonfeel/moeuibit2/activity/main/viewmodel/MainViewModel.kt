package org.jeonfeel.moeuibit2.activity.main.viewmodel

import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.jeonfeel.moeuibit2.activity.main.viewmodel.usecase.ExchangeUseCase
import org.jeonfeel.moeuibit2.constant.*
import org.jeonfeel.moeuibit2.data.local.room.entity.MyCoin
import org.jeonfeel.moeuibit2.data.remote.retrofit.model.exchange.KrwExchangeModel
import org.jeonfeel.moeuibit2.data.remote.retrofit.model.exchange.TickerModel
import org.jeonfeel.moeuibit2.data.remote.websocket.UpBitPortfolioWebSocket
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
    private val exchangeUseCase: ExchangeUseCase,
) : ViewModel(), PortfolioOnTickerMessageReceiveListener {

    val gson = Gson()
    var isPortfolioSocketRunning = false

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

    /**
     * 거래소
     * */
    var updateExchange: Boolean
        get() = exchangeUseCase.updateExchange
        set(value) {
            exchangeUseCase.updateExchange = value
        }
    private val krwExchangeModelList get() = exchangeUseCase.krwExchangeModelList
    private val krwExchangeModelMutableStateList get() = exchangeUseCase.krwExchangeModelMutableStateList
    private val krwCoinKoreanNameAndEngName get() = exchangeUseCase.krwCoinKoreanNameAndEngName
    private val btcExchangeModelList get() = exchangeUseCase.btcExchangeModelList
    private val btcExchangeModelMutableStateList get() = exchangeUseCase.btcExchangeModelMutableStateList
    private val btcCoinKoreanNameAndEngName get() = exchangeUseCase.btcCoinKoreanNameAndEngName
    val selectedMarketState get() = exchangeUseCase.selectedMarketState
    val errorState get() = exchangeUseCase.errorState
    val searchTextFieldValueState get() = exchangeUseCase.searchTextFieldValueState
    val showFavoriteState get() = exchangeUseCase.showFavoriteState
    val selectedButtonState get() = exchangeUseCase.selectedButtonState // 정렬버튼
    val loadingState get() = exchangeUseCase.loadingState
    val krwPreItemArray get() = exchangeUseCase.krwPreItemArray
    val btcPreItemArray get() = exchangeUseCase.btcPreItemArray
    val favoriteHashMap get() = exchangeUseCase.favoriteHashMap
    val krwExchangeModelListPosition get() = exchangeUseCase.krwExchangeModelListPosition
    val btcExchangeModelListPosition get() = exchangeUseCase.btcExchangeModelListPosition
    val btcTradePrice get() = exchangeUseCase.btcTradePrice

    fun requestExchangeData() {
        viewModelScope.launch {
            if (krwExchangeModelMutableStateList.isEmpty()) {
                viewModelScope.launch(ioDispatcher) {
                    exchangeUseCase.requestExchangeData()
                }.join()
            }
            Handler(Looper.getMainLooper()).post {
                exchangeUseCase.requestCoinListToWebSocket()
            }
            exchangeUseCase.updateExchange()
        }
        Log.d("실행","실행")
    }

    fun updateFavorite(market: String, isFavorite: Boolean) {
        viewModelScope.launch((ioDispatcher)) {
            exchangeUseCase.updateFavorite(market, isFavorite)
        }
    }

    fun requestCoinListToWebSocket() {
        exchangeUseCase.requestCoinListToWebSocket()
    }

    /**
     * data sorting, filter
     * */
    fun getFilteredCoinList(): SnapshotStateList<KrwExchangeModel> {
        return when {
            //검색 X 관심코인 X
            searchTextFieldValueState.value.isEmpty() && !showFavoriteState.value -> {
                when (selectedMarketState.value) {
                    SELECTED_KRW_MARKET -> {
                        krwExchangeModelMutableStateList
                    }
                    SELECTED_BTC_MARKET -> {
                        btcExchangeModelMutableStateList
                    }
                    else -> {
                        btcExchangeModelMutableStateList
                    }
                }
            }
            // 검색 X 관심코인 O
            searchTextFieldValueState.value.isEmpty() && showFavoriteState.value -> {
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
            }
            // 검색 O 관심코인 X
            searchTextFieldValueState.value.isNotEmpty() && !showFavoriteState.value -> {
                val resultList = SnapshotStateList<KrwExchangeModel>()
                val targetList = when (selectedMarketState.value) {
                    SELECTED_KRW_MARKET -> {
                        krwExchangeModelMutableStateList
                    }
                    SELECTED_BTC_MARKET -> {
                        btcExchangeModelMutableStateList
                    }
                    else -> {
                        btcExchangeModelMutableStateList
                    }
                }
                for (element in targetList) {
                    if (
                        element.koreanName.contains(searchTextFieldValueState.value) ||
                        element.EnglishName.uppercase()
                            .contains(searchTextFieldValueState.value.uppercase()) ||
                        element.symbol.uppercase()
                            .contains(searchTextFieldValueState.value.uppercase())
                    ) {
                        resultList.add(element)
                    }
                }
                resultList
            }
            // 검색 O 관심코인 O
            else -> {
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
                        element.koreanName.contains(searchTextFieldValueState.value) ||
                        element.EnglishName.uppercase()
                            .contains(searchTextFieldValueState.value.uppercase()) ||
                        element.symbol.uppercase()
                            .contains(searchTextFieldValueState.value.uppercase())
                    ) {
                        favoriteList.add(element)
                    }
                }
                favoriteList
            }
        }
    }

    fun getFilteredBtcCoinList(): SnapshotStateList<KrwExchangeModel> {
        return when {
            //검색 X 관심코인 X
            searchTextFieldValueState.value.isEmpty() && !showFavoriteState.value -> {
                btcExchangeModelMutableStateList
            }
            // 검색 X 관심코인 O
            searchTextFieldValueState.value.isEmpty() && showFavoriteState.value -> {
                val favoriteList = SnapshotStateList<KrwExchangeModel>()
                val tempArray = ArrayList<Int>()
                for (i in favoriteHashMap) {
                    tempArray.add(btcExchangeModelListPosition[i.key]!!)
                }
                tempArray.sort()
                for (i in tempArray) {
                    favoriteList.add(btcExchangeModelMutableStateList[i])
                }
                favoriteList
            }
            // 검색 O 관심코인 O
            searchTextFieldValueState.value.isNotEmpty() && !showFavoriteState.value -> {
                val resultList = SnapshotStateList<KrwExchangeModel>()
                for (element in btcExchangeModelMutableStateList) {
                    if (
                        element.koreanName.contains(searchTextFieldValueState.value) ||
                        element.EnglishName.uppercase()
                            .contains(searchTextFieldValueState.value.uppercase()) ||
                        element.symbol.uppercase()
                            .contains(searchTextFieldValueState.value.uppercase())
                    ) {
                        resultList.add(element)
                    }
                }
                resultList
            }
            // 검색 O 관심코인 X
            else -> {
                val tempFavoriteList = SnapshotStateList<KrwExchangeModel>()
                val favoriteList = SnapshotStateList<KrwExchangeModel>()
                val tempArray = ArrayList<Int>()
                for (i in favoriteHashMap) {
                    tempArray.add(btcExchangeModelListPosition[i.key]!!)
                }
                tempArray.sort()
                for (i in tempArray) {
                    tempFavoriteList.add(btcExchangeModelMutableStateList[i])
                }
                for (element in tempFavoriteList) {
                    if (
                        element.koreanName.contains(searchTextFieldValueState.value) ||
                        element.EnglishName.uppercase()
                            .contains(searchTextFieldValueState.value.uppercase()) ||
                        element.symbol.uppercase()
                            .contains(searchTextFieldValueState.value.uppercase())
                    ) {
                        favoriteList.add(element)
                    }
                }
                favoriteList
            }
        }
    }

    fun sortList(sortStandard: Int) {
        updateExchange = false
        viewModelScope.launch(defaultDispatcher) {
            when (sortStandard) {
                0 -> {
                    if (selectedMarketState.value == SELECTED_KRW_MARKET) {
                        krwExchangeModelList.sortByDescending { element ->
                            element.tradePrice
                        }
                    } else if (selectedMarketState.value == SELECTED_BTC_MARKET) {
                        btcExchangeModelList.sortByDescending { element ->
                            element.tradePrice
                        }
                    }
                }
                1 -> {
                    if (selectedMarketState.value == SELECTED_KRW_MARKET) {
                        krwExchangeModelList.sortBy { element ->
                            element.tradePrice
                        }
                    } else if (selectedMarketState.value == SELECTED_BTC_MARKET) {
                        btcExchangeModelList.sortBy { element ->
                            element.tradePrice
                        }
                    }
                }
                2 -> {
                    if (selectedMarketState.value == SELECTED_KRW_MARKET) {
                        krwExchangeModelList.sortByDescending { element ->
                            element.signedChangeRate
                        }
                    } else if (selectedMarketState.value == SELECTED_BTC_MARKET) {
                        btcExchangeModelList.sortByDescending { element ->
                            element.signedChangeRate
                        }
                    }
                }
                3 -> {
                    if (selectedMarketState.value == SELECTED_KRW_MARKET) {
                        krwExchangeModelList.sortBy { element ->
                            element.signedChangeRate
                        }
                    } else if (selectedMarketState.value == SELECTED_BTC_MARKET) {
                        btcExchangeModelList.sortBy { element ->
                            element.signedChangeRate
                        }
                    }
                }
                4 -> {
                    if (selectedMarketState.value == SELECTED_KRW_MARKET) {
                        krwExchangeModelList.sortByDescending { element ->
                            element.accTradePrice24h
                        }
                    } else if (selectedMarketState.value == SELECTED_BTC_MARKET) {
                        btcExchangeModelList.sortByDescending { element ->
                            element.accTradePrice24h
                        }
                    }
                }
                5 -> {
                    if (selectedMarketState.value == SELECTED_KRW_MARKET) {
                        krwExchangeModelList.sortBy { element ->
                            element.accTradePrice24h
                        }
                    } else if (selectedMarketState.value == SELECTED_BTC_MARKET) {
                        btcExchangeModelList.sortBy { element ->
                            element.accTradePrice24h
                        }
                    }
                }
                else -> {
                    if (selectedMarketState.value == SELECTED_KRW_MARKET) {
                        krwExchangeModelList.sortByDescending { element ->
                            element.accTradePrice24h
                        }
                    } else if (selectedMarketState.value == SELECTED_BTC_MARKET) {
                        btcExchangeModelList.sortByDescending { element ->
                            element.accTradePrice24h
                        }
                    }
                }
            }
            if (selectedMarketState.value == SELECTED_KRW_MARKET) {
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
            } else if(selectedMarketState.value == SELECTED_BTC_MARKET) {
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
            updateExchange = true
        }
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
                        } else if (i.quantity == 0.0 || i.purchasePrice == 0.0 || i.quantity == Double.POSITIVE_INFINITY || i.quantity == Double.NEGATIVE_INFINITY) {
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
        if (!isPortfolioSocketRunning) {
            isPortfolioSocketRunning = !isPortfolioSocketRunning
        }
        viewModelScope.launch {
            while (isPortfolioSocketRunning) {
                var tempTotalValuedAssets = 0.0
                try {
                    for (i in tempUserHoldCoinDtoList.indices) {
                        val userHoldCoinDTO = tempUserHoldCoinDtoList[i]
                        userHoldCoinDtoList[i] = userHoldCoinDTO
                        tempTotalValuedAssets += userHoldCoinDTO.currentPrice * userHoldCoinDTO.myCoinsQuantity
                    }
                    totalValuedAssets.value = tempTotalValuedAssets
                    delay(300)
                } catch (e: Exception) {
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