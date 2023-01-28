package org.jeonfeel.moeuibit2.ui.viewmodels

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.lifecycle.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.jeonfeel.moeuibit2.constants.*
import org.jeonfeel.moeuibit2.data.local.room.entity.MyCoin
import org.jeonfeel.moeuibit2.data.remote.retrofit.model.TickerModel
import org.jeonfeel.moeuibit2.data.remote.websocket.UpBitPortfolioWebSocket
import org.jeonfeel.moeuibit2.data.remote.websocket.listener.PortfolioOnTickerMessageReceiveListener
import org.jeonfeel.moeuibit2.data.repository.local.LocalRepository
import org.jeonfeel.moeuibit2.data.repository.remote.RemoteRepository
import org.jeonfeel.moeuibit2.ui.base.BaseViewModel
import org.jeonfeel.moeuibit2.ui.main.portfolio.dto.UserHoldCoinDTO
import org.jeonfeel.moeuibit2.utils.NetworkMonitorUtil
import org.jeonfeel.moeuibit2.utils.Utils
import javax.inject.Inject

class PortfolioState {
    val userSeedMoney = mutableStateOf(0L)
    var totalPurchase = mutableStateOf(0.0)
    val userHoldCoinDtoList = mutableStateListOf<UserHoldCoinDTO>()
    val totalValuedAssets = mutableStateOf(0.0)
    var removeCoinCount = mutableStateOf(0)
    val adLoadingDialogState = mutableStateOf(false)
    val adDialogState = mutableStateOf(false)
    val dialogState = mutableStateOf(false)
    val editHoldCoinDialogState = mutableStateOf(false)
    val portfolioOrderState = mutableStateOf(-1)
    val selectedCoinKoreanName = mutableStateOf("")
    val pieChartState = mutableStateOf(false)
    val btcTradePrice = mutableStateOf(0.0)
}

@HiltViewModel
class PortfolioViewModel @Inject constructor(
    private val remoteRepository: RemoteRepository,
    private val localRepository: LocalRepository
) : BaseViewModel(), PortfolioOnTickerMessageReceiveListener {
    val state = PortfolioState()
    var userHoldCoinsMarket = StringBuffer()
    var userHoldCoinList = emptyList<MyCoin?>()
    val tempUserHoldCoinDtoList = ArrayList<UserHoldCoinDTO>()
    val userHoldCoinHashMap = HashMap<String, MyCoin>()
    var isPortfolioSocketRunning = false
    val userHoldCoinDtoListPositionHashMap = HashMap<String, Int>()

    private val _adMutableLiveData = MutableLiveData<Int>()
    val adLiveData: LiveData<Int> get() = _adMutableLiveData

    fun getUserSeedMoney() {
        viewModelScope.launch(ioDispatcher) {
            state.userSeedMoney.value = localRepository.getUserDao().all?.krw ?: 0L
        }
    }

    fun getUserHoldCoins() {
        viewModelScope.launch(ioDispatcher) {
            initPortfolio()
            state.btcTradePrice.value =
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
                    state.userHoldCoinDtoList.add(
                        UserHoldCoinDTO(
                            myCoinsKoreanName = koreanName,
                            myCoinsEngName = engName,
                            myCoinsSymbol = symbol,
                            myCoinsQuantity = quantity,
                            myCoinsBuyingAverage = purchaseAverage,
                            openingPrice = openingPrice,
                            warning = warning,
                            isFavorite = isFavorite,
                            market = market,
                            purchaseAverageBtcPrice = purchaseAverageBtcPrice
                        )
                    )
                    tempUserHoldCoinDtoList.add(
                        UserHoldCoinDTO(
                            myCoinsKoreanName = koreanName,
                            myCoinsEngName = engName,
                            myCoinsSymbol = symbol,
                            myCoinsQuantity = quantity,
                            myCoinsBuyingAverage = purchaseAverage,
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
                state.totalPurchase.value = localTotalPurchase
                UpBitPortfolioWebSocket.setMarkets(userHoldCoinsMarket.toString())
                UpBitPortfolioWebSocket
                    .getListener()
                    .setPortfolioMessageListener(this@PortfolioViewModel)
                UpBitPortfolioWebSocket.requestKrwCoinList()
                updateUserHoldCoins()
            } else {
                state.totalValuedAssets.value = 0.0
                state.totalPurchase.value = 0.0
            }
        }
    }

    private suspend fun initPortfolio() {
        isPortfolioSocketRunning = false
        var localTotalPurchase = 0.0
        state.userHoldCoinDtoList.clear()
        userHoldCoinsMarket = StringBuffer()
        userHoldCoinDtoListPositionHashMap.clear()
        tempUserHoldCoinDtoList.clear()
        userHoldCoinList = localRepository.getMyCoinDao().all ?: emptyList()
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
            state.userHoldCoinDtoList[i] = tempUserHoldCoinDtoList[i]
        }

        isPortfolioSocketRunning = true
    }

    fun editUserHoldCoin() {
        var count = 1
        isPortfolioSocketRunning = false

        viewModelScope.launch(ioDispatcher) {
            if (UpBitPortfolioWebSocket.currentSocketState != SOCKET_IS_CONNECTED || NetworkMonitorUtil.currentNetworkState != INTERNET_CONNECTION) {
                state.removeCoinCount.value = -1
                delay(100L)
                state.removeCoinCount.value = 0
            } else {
                for (i in userHoldCoinList) {
                    val targetList = if (i!!.market.startsWith(SYMBOL_KRW)) {
                        krwExchangeModelListPosition
                    } else {
                        btcExchangeModelListPosition
                    }
                    if (targetList[i.market] == null) {
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
                state.removeCoinCount.value = count
                delay(100L)
                state.removeCoinCount.value = 0
            }
        }
    }

    private fun updateUserHoldCoins() {
        isPortfolioSocketRunning = true
        viewModelScope.launch {
            while (isPortfolioSocketRunning) {
                var tempTotalValuedAssets = 0.0
                try {
                    for (i in tempUserHoldCoinDtoList.indices) {
                        val userHoldCoinDTO = tempUserHoldCoinDtoList[i]
                        state.userHoldCoinDtoList[i] = userHoldCoinDTO
                        tempTotalValuedAssets = if (userHoldCoinDTO.market.startsWith(SYMBOL_KRW)) {
                            tempTotalValuedAssets + userHoldCoinDTO.currentPrice * userHoldCoinDTO.myCoinsQuantity
                        } else {
                            tempTotalValuedAssets + (userHoldCoinDTO.currentPrice * userHoldCoinDTO.myCoinsQuantity * state.btcTradePrice.value)
                        }
                    }
                    state.totalValuedAssets.value = tempTotalValuedAssets
                    delay(300)
                } catch (e: Exception) {
                    delay(300)
                }
            }
        }
    }

    fun updateAdLiveData() {
        state.adLoadingDialogState.value = true
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

    override fun portfolioOnTickerMessageReceiveListener(tickerJsonObject: String) {
        if (isPortfolioSocketRunning) {
            val model = gson.fromJson(tickerJsonObject, TickerModel::class.java)
            if (model.code.startsWith(SYMBOL_KRW)) {
                if (model.code == BTC_MARKET && userHoldCoinDtoListPositionHashMap[BTC_MARKET] == null) {
                    state.btcTradePrice.value = model.tradePrice
                    return
                } else if (model.code == BTC_MARKET) {
                    state.btcTradePrice.value = model.tradePrice
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


    companion object {
        fun provideFactory(
            remoteRepository: RemoteRepository,
            localRepository: LocalRepository,
        ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return PortfolioViewModel(remoteRepository, localRepository) as T
            }
        }
    }
}