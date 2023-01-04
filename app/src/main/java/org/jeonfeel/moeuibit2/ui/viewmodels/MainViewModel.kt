package org.jeonfeel.moeuibit2.ui.viewmodels

import android.os.Handler
import android.os.Looper
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
import org.jeonfeel.moeuibit2.constants.*
import org.jeonfeel.moeuibit2.data.local.room.entity.MyCoin
import org.jeonfeel.moeuibit2.data.remote.retrofit.model.CommonExchangeModel
import org.jeonfeel.moeuibit2.data.remote.retrofit.model.TickerModel
import org.jeonfeel.moeuibit2.data.remote.websocket.UpBitPortfolioWebSocket
import org.jeonfeel.moeuibit2.data.remote.websocket.UpBitTickerWebSocket
import org.jeonfeel.moeuibit2.data.remote.websocket.listener.PortfolioOnTickerMessageReceiveListener
import org.jeonfeel.moeuibit2.data.repository.local.LocalRepository
import org.jeonfeel.moeuibit2.ui.main.portfolio.dto.UserHoldCoinDTO
import org.jeonfeel.moeuibit2.utils.Utils
import org.jeonfeel.moeuibit2.utils.NetworkMonitorUtil.Companion.currentNetworkState
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    val remoteRepository: RemoteRepository,
    val localRepository: LocalRepository,
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
    val loadingFavorite get() = exchangeUseCase.loadingFavorite
    var updateExchange: Boolean
        get() = exchangeUseCase.updateExchange
        set(value) {
            exchangeUseCase.updateExchange = value
        }
    private val krwExchangeModelList get() = exchangeUseCase.krwExchangeModelList
    private val krwExchangeModelMutableStateList get() = exchangeUseCase.krwExchangeModelMutableStateList
    private val krwCoinKoreanNameAndEngName get() = exchangeUseCase.krwCoinKoreanNameAndEngName
    val krwExchangeModelListPosition get() = exchangeUseCase.krwExchangeModelListPosition // 원화 코인 정렬 포지션
    val krwPreItemArray get() = exchangeUseCase.krwPreItemArray // 원화 이전 거래소 정보

    private val btcExchangeModelList get() = exchangeUseCase.btcExchangeModelList
    private val btcExchangeModelMutableStateList get() = exchangeUseCase.btcExchangeModelMutableStateList
    private val btcCoinKoreanNameAndEngName get() = exchangeUseCase.btcCoinKoreanNameAndEngName
    val btcPreItemArray get() = exchangeUseCase.btcPreItemArray // BTC 이전 거래소 정보
    val btcExchangeModelListPosition get() = exchangeUseCase.btcExchangeModelListPosition // BTC 코인 정렬 포지션

    private val favoriteExchangeModelMutableStateList get() = exchangeUseCase.favoriteExchangeModelMutableStateList
    val favoritePreItemArray get() = exchangeUseCase.favoritePreItemArray
    val favoriteHashMap get() = exchangeUseCase.favoriteHashMap
    val favoriteExchangeModelListPosition get() = exchangeUseCase.favoriteExchangeModelListPosition

    val selectedMarketState get() = exchangeUseCase.selectedMarketState
    val errorState get() = exchangeUseCase.errorState // error화면 노출
    val searchTextFieldValueState get() = exchangeUseCase.searchTextFieldValueState // value가 바뀔때마다 검색 되야하니까
    val sortButtonState get() = exchangeUseCase.sortButtonState // 정렬버튼
    val loadingState get() = exchangeUseCase.loadingState // 로딩 state

    val btcTradePrice get() = exchangeUseCase.btcTradePrice // BTC 코인 원화가격 때문에

    fun requestExchangeData() {
        viewModelScope.launch {
            delay(650L)
            if (krwExchangeModelMutableStateList.isEmpty()) {
                viewModelScope.launch(ioDispatcher) {
                    exchangeUseCase.requestExchangeData()
                }.join()
            }
            requestCoinListToWebSocket()
            exchangeUseCase.updateExchange()
        }
    }

    fun updateFavorite(market: String, isFavorite: Boolean) {
        viewModelScope.launch((ioDispatcher)) {
            exchangeUseCase.updateFavorite(market, isFavorite, viewModelScope)
        }
    }

    private fun requestCoinListToWebSocket() {
        Handler(Looper.getMainLooper()).postDelayed({
            exchangeUseCase.requestCoinListToWebSocket()
        }, 650L)
    }

    /**
     * data sorting, filter
     * */
    fun getFilteredCoinList(): SnapshotStateList<CommonExchangeModel> {
        return when {
            //검색 X 관심코인 X
            searchTextFieldValueState.value.isEmpty() -> {
                when (selectedMarketState.value) {
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
                val targetList = when (selectedMarketState.value) {
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
        }
    }

    fun sortList(marketState: Int) {
        updateExchange = false
        viewModelScope.launch(defaultDispatcher) {
            exchangeUseCase.sortList(marketState = marketState)
        }
    }

    private fun requestFavoriteData(selectedMarketState: Int) {
        viewModelScope.launch(ioDispatcher) {
            exchangeUseCase.requestFavoriteData(selectedMarketState)
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

    fun requestUsdPrice() {
        viewModelScope.launch {
            exchangeUseCase.requestUSDTPrice()
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
        btcTradePrice.value =
            krwExchangeModelList[krwExchangeModelListPosition[BTC_MARKET]!!].tradePrice
        if (userHoldCoinList.isNotEmpty()) {
            for (i in userHoldCoinList.indices) {
                val userHoldCoin = userHoldCoinList[i] ?: MyCoin("", 0.0, "", "", 0.0)
                val marketState = Utils.getSelectedMarket(userHoldCoin.market)
                val market = userHoldCoin.market
                val symbol = userHoldCoin.symbol
                val isFavorite = favoriteHashMap[market]
                val quantity = userHoldCoin.quantity
                val purchaseAverage = userHoldCoin.purchasePrice
                val purchaseAverageBtcPrice = userHoldCoin.PurchaseAverageBtcPrice
                val position = if (marketState == SELECTED_KRW_MARKET) {
                    krwExchangeModelListPosition[market] ?: 0
                } else {
                    btcExchangeModelListPosition[market] ?: 0
                }
                val openingPrice = if (marketState == SELECTED_KRW_MARKET) {
                    krwExchangeModelList[position].opening_price
                } else {
                    btcExchangeModelList[position].opening_price
                }
                val warning = if (marketState == SELECTED_KRW_MARKET) {
                    krwExchangeModelList[position].warning
                } else {
                    btcExchangeModelList[position].warning
                }
                val koreanName = if (marketState == SELECTED_KRW_MARKET) {
                    krwCoinKoreanNameAndEngName[market]?.get(0) ?: ""
                } else {
                    btcCoinKoreanNameAndEngName[market]?.get(0) ?: ""
                }
                val engName = if (marketState == SELECTED_KRW_MARKET) {
                    krwCoinKoreanNameAndEngName[market]?.get(1) ?: ""
                } else {
                    btcCoinKoreanNameAndEngName[market]?.get(1) ?: ""
                }
                userHoldCoinHashMap[market] = userHoldCoin
                localTotalPurchase = if (marketState == SELECTED_KRW_MARKET) {
                    localTotalPurchase + (userHoldCoin.quantity * userHoldCoin.purchasePrice)
                } else {
                    localTotalPurchase + (userHoldCoin.quantity * userHoldCoin.purchasePrice * userHoldCoin.PurchaseAverageBtcPrice)
                }
                userHoldCoinsMarket.append(userHoldCoin.market).append(",")
                userHoldCoinDtoList.add(
                    UserHoldCoinDTO(
                        koreanName,
                        engName,
                        symbol,
                        quantity,
                        purchaseAverage,
                        openingPrice = openingPrice,
                        warning = warning,
                        isFavorite = isFavorite,
                        market = market,
                        purchaseAverageBtcPrice = purchaseAverageBtcPrice
                    )
                )
                tempUserHoldCoinDtoList.add(
                    UserHoldCoinDTO(
                        koreanName,
                        engName,
                        symbol,
                        quantity,
                        purchaseAverage,
                        openingPrice = openingPrice,
                        warning = warning,
                        isFavorite = isFavorite,
                        market = market,
                        purchaseAverageBtcPrice = purchaseAverageBtcPrice
                    )
                )
                userHoldCoinDtoListPositionHashMap[userHoldCoin.market] = i
            }
            sortUserHoldCoin(-1)
            if (userHoldCoinDtoListPositionHashMap[BTC_MARKET] == null) {
                userHoldCoinsMarket.append(BTC_MARKET)
            } else {
                userHoldCoinsMarket.deleteCharAt(userHoldCoinsMarket.lastIndex)
            }
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
            userHoldCoinDtoListPositionHashMap[tempUserHoldCoinDtoList[i].market] =
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
                        if (i!!.market.startsWith(SYMBOL_KRW)) {
                            if (krwExchangeModelListPosition[i.market] == null) {
                                localRepository.getFavoriteDao().delete(i.market)
                                localRepository.getMyCoinDao().delete(i.market)
                                localRepository.getTransactionInfoDao().delete(i.market)
                                count += 1
                            } else if (i.quantity == 0.0 || i.purchasePrice == 0.0 || i.quantity == Double.POSITIVE_INFINITY || i.quantity == Double.NEGATIVE_INFINITY) {
                                localRepository.getMyCoinDao().delete(i.market)
                                localRepository.getTransactionInfoDao().delete(i.market)
                                count += 1
                            }
                        }else {
                            if (btcExchangeModelListPosition[i.market] == null) {
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
                        tempTotalValuedAssets = if (userHoldCoinDTO.market.startsWith(SYMBOL_KRW)) {
                            tempTotalValuedAssets + userHoldCoinDTO.currentPrice * userHoldCoinDTO.myCoinsQuantity
                        } else {
                            tempTotalValuedAssets + (userHoldCoinDTO.currentPrice * userHoldCoinDTO.myCoinsQuantity * btcTradePrice.value)
                        }
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
            if (model.code.startsWith(SYMBOL_KRW)) {
                if (model.code == BTC_MARKET && userHoldCoinDtoListPositionHashMap[BTC_MARKET] == null) {
                    btcTradePrice.value = model.tradePrice
//                    Log.e("btcPort", btcTradePrice.value.toString())
                    return
                } else if (model.code == BTC_MARKET) {
                    btcTradePrice.value = model.tradePrice
                }
                val tickerListPosition = krwExchangeModelListPosition[model.code] ?: 0
                val position = userHoldCoinDtoListPositionHashMap[model.code] ?: 0
                val userHoldCoin = userHoldCoinHashMap[model.code]!!
                val openingPrice = krwExchangeModelList[tickerListPosition].opening_price
                val warning = model.marketWarning
                val isFavorite = favoriteHashMap[model.code]
                val market = krwExchangeModelList[tickerListPosition].market
                tempUserHoldCoinDtoList[position] =
                    UserHoldCoinDTO(
                        krwCoinKoreanNameAndEngName[model.code]?.get(0) ?: "",
                        krwCoinKoreanNameAndEngName[model.code]?.get(1) ?: "",
                        userHoldCoin.symbol,
                        userHoldCoin.quantity,
                        userHoldCoin.purchasePrice,
                        model.tradePrice,
                        openingPrice = openingPrice,
                        warning = warning,
                        isFavorite = isFavorite,
                        market = market
                    )
            } else {
                val tickerListPosition = btcExchangeModelListPosition[model.code] ?: 0
                val position = userHoldCoinDtoListPositionHashMap[model.code] ?: 0
                val userHoldCoin = userHoldCoinHashMap[model.code]!!
                val openingPrice = btcExchangeModelList[tickerListPosition].opening_price
                val warning = model.marketWarning
                val isFavorite = favoriteHashMap[model.code]
                val market = btcExchangeModelList[tickerListPosition].market
                val purchaseAverageBtcPrice = userHoldCoin.PurchaseAverageBtcPrice
                tempUserHoldCoinDtoList[position] =
                    UserHoldCoinDTO(
                        btcCoinKoreanNameAndEngName[model.code]?.get(0) ?: "",
                        btcCoinKoreanNameAndEngName[model.code]?.get(1) ?: "",
                        userHoldCoin.symbol,
                        userHoldCoin.quantity,
                        userHoldCoin.purchasePrice,
                        model.tradePrice,
                        openingPrice = openingPrice,
                        warning = warning,
                        isFavorite = isFavorite,
                        market = market,
                        purchaseAverageBtcPrice = purchaseAverageBtcPrice
                    )
            }
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

    fun test() {
        viewModelScope.launch(ioDispatcher) {
            val userDao = localRepository.getUserDao()
            userDao.updatePlusMoney(10_000_000_0000)
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