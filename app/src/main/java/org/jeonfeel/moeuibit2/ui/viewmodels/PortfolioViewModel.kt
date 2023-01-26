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
import org.jeonfeel.moeuibit2.data.remote.websocket.UpBitPortfolioWebSocket
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
    val portfolioLoadingComplete = mutableStateOf(false)
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
) : BaseViewModel() {
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
            if (UpBitPortfolioWebSocket.currentSocketState != SOCKET_IS_CONNECTED || NetworkMonitorUtil.currentNetworkState != INTERNET_CONNECTION) {
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
                        } else {
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
                    state.removeCoinCount.value = count
                    delay(100L)
                    state.removeCoinCount.value = 0
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