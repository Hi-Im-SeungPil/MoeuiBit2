package org.jeonfeel.moeuibit2.ui.viewmodels

import android.app.Activity
import android.content.Context
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.jeonfeel.moeuibit2.MoeuiBitDataStore
import org.jeonfeel.moeuibit2.R
import org.jeonfeel.moeuibit2.constants.*
import org.jeonfeel.moeuibit2.data.local.room.entity.MyCoin
import org.jeonfeel.moeuibit2.data.remote.websocket.UpBitPortfolioWebSocket
import org.jeonfeel.moeuibit2.data.remote.websocket.listener.PortfolioOnTickerMessageReceiveListener
import org.jeonfeel.moeuibit2.data.remote.websocket.model.PortfolioTickerModel
import org.jeonfeel.moeuibit2.data.repository.local.LocalRepository
import org.jeonfeel.moeuibit2.ui.base.BaseViewModel
import org.jeonfeel.moeuibit2.ui.main.portfolio.dto.UserHoldCoinDTO
import org.jeonfeel.moeuibit2.utils.Utils
import org.jeonfeel.moeuibit2.utils.manager.AdMobManager
import org.jeonfeel.moeuibit2.utils.showToast
import javax.inject.Inject

class PortfolioState {
    val userSeedMoney = mutableStateOf(0L)
    var totalPurchase = mutableStateOf(0.0)
    val userHoldCoinDtoList = mutableStateListOf<UserHoldCoinDTO>()
    val totalValuedAssets = mutableStateOf(0.0)
    var removeCoinCount = mutableStateOf(0)
    val adLoadingDialogState = mutableStateOf(false)
    val adConfirmDialogState = mutableStateOf(false)
    val columnItemDialogState = mutableStateOf(false)
    val editHoldCoinDialogState = mutableStateOf(false)
    val portfolioOrderState = mutableStateOf(-1)
    val selectedCoinKoreanName = mutableStateOf("")
    val pieChartState = mutableStateOf(false)
    val btcTradePrice = mutableStateOf(0.0)
    val isAdShowState = mutableStateOf(false)
}

@HiltViewModel
class PortfolioViewModel @Inject constructor(
    private val localRepository: LocalRepository,
    val adMobManager: AdMobManager
) : BaseViewModel(), PortfolioOnTickerMessageReceiveListener {
    val state = PortfolioState()
    var isPortfolioSocketRunning = false
    var userHoldCoinsMarket = StringBuffer()
    var userHoldCoinList = emptyList<MyCoin?>()
    private val tempUserHoldCoinDtoList = ArrayList<UserHoldCoinDTO>()
    private val userHoldCoinHashMap = HashMap<String, MyCoin>()
    private val userHoldCoinDtoListPositionHashMap = HashMap<String, Int>()

    private val _adMutableLiveData = MutableLiveData<Int>()
    val adLiveData: LiveData<Int> get() = _adMutableLiveData
    var isAdShow = false

    fun getUserSeedMoney() {
        viewModelScope.launch(ioDispatcher) {
            state.userSeedMoney.value = localRepository.getUserDao().all?.krw ?: 0L
        }
    }

    fun getUserHoldCoins() {
        viewModelScope.launch(ioDispatcher) {
            resetPortfolio()
            var localTotalPurchase = 0.0
            if (userHoldCoinList.isNotEmpty()) {
                for (i in userHoldCoinList.indices) {
                    val userHoldCoin = userHoldCoinList[i] ?: MyCoin("", 0.0, "", "", 0.0)
                    val market = userHoldCoin.market
                    val marketState = Utils.getSelectedMarket(market)
//                    val isFavorite = favoriteHashMap[market]
                    userHoldCoinHashMap[market] = userHoldCoin
                    localTotalPurchase = if (marketState == SELECTED_KRW_MARKET) {
                        localTotalPurchase + (userHoldCoin.quantity * userHoldCoin.purchasePrice)
                    } else {
                        localTotalPurchase + (userHoldCoin.quantity * userHoldCoin.purchasePrice * userHoldCoin.PurchaseAverageBtcPrice)
                    }
                    userHoldCoinsMarket.append(userHoldCoin.market).append(",")
                    userHoldCoinDtoListPositionHashMap[userHoldCoin.market] = i
                    addUserHoldCoin(
                        UserHoldCoinDTO(
                            myCoinsKoreanName = MoeuiBitDataStore.coinName[userHoldCoin.market]?.first
                                ?: "",
                            myCoinsEngName = MoeuiBitDataStore.coinName[userHoldCoin.market]?.second
                                ?: "",
                            myCoinsSymbol = userHoldCoin.symbol,
                            myCoinsQuantity = 0.0,
                            myCoinsBuyingAverage = 0.0,
                            currentPrice = 0.0,
                            openingPrice = 0.0,
                            warning = "",
                            isFavorite = null,
                            market = userHoldCoin.market,
                            purchaseAverageBtcPrice = userHoldCoin.PurchaseAverageBtcPrice
                        )
                    )
                }
                sortUserHoldCoin(SORT_DEFAULT)
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

    private suspend fun resetPortfolio() {
        isPortfolioSocketRunning = false
        state.userHoldCoinDtoList.clear()
        userHoldCoinsMarket = StringBuffer()
        userHoldCoinDtoListPositionHashMap.clear()
        tempUserHoldCoinDtoList.clear()
        userHoldCoinList = localRepository.getMyCoinDao().all ?: emptyList()
    }

    private fun addUserHoldCoin(userHoldCoinDTO: UserHoldCoinDTO) {
        tempUserHoldCoinDtoList.add(userHoldCoinDTO)
        state.userHoldCoinDtoList.add(userHoldCoinDTO)
    }

    fun sortUserHoldCoin(sortStandard: Int) {
        isPortfolioSocketRunning = false
        when (sortStandard) {
            SORT_NAME_DEC -> {
                tempUserHoldCoinDtoList.sortByDescending { element ->
                    element.myCoinsKoreanName
                }
            }
            SORT_NAME_ASC -> {
                tempUserHoldCoinDtoList.sortBy { element ->
                    element.myCoinsKoreanName
                }
            }
            SORT_RATE_ASC -> {
                tempUserHoldCoinDtoList.sortBy { element ->
                    element.myCoinsBuyingAverage / element.currentPrice
                }
            }
            SORT_RATE_DEC -> {
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
//        var count = 1
//        isPortfolioSocketRunning = false
//
//        viewModelScope.launch(ioDispatcher) {
//            if (UpBitPortfolioWebSocket.currentSocketState != SOCKET_IS_CONNECTED || NetworkMonitorUtil.currentNetworkState != INTERNET_CONNECTION) {
//                state.removeCoinCount.value = -1
//                delay(100L)
//                state.removeCoinCount.value = 0
//            } else {
//                for (i in userHoldCoinList) {
//                    val targetList = if (i!!.market.startsWith(SYMBOL_KRW)) {
//                        krwExchangeModelListPosition
//                    } else {
//                        btcExchangeModelListPosition
//                    }
//                    if (targetList[i.market] == null) {
//                        localRepository.getFavoriteDao().delete(i.market)
//                        localRepository.getMyCoinDao().delete(i.market)
//                        localRepository.getTransactionInfoDao().delete(i.market)
//                        count += 1
//                    } else if (i.quantity == 0.0 || i.purchasePrice == 0.0 || i.quantity == Double.POSITIVE_INFINITY || i.quantity == Double.NEGATIVE_INFINITY) {
//                        localRepository.getMyCoinDao().delete(i.market)
//                        localRepository.getTransactionInfoDao().delete(i.market)
//                        count += 1
//                    }
//                }
//                if (count > 1) {
//                    isPortfolioSocketRunning = false
//                    UpBitPortfolioWebSocket.getListener().setPortfolioMessageListener(null)
//                    UpBitPortfolioWebSocket.onPause()
//                    getUserHoldCoins()
//                }
//                state.removeCoinCount.value = count
//                delay(100L)
//                state.removeCoinCount.value = 0
//            }
//        }
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

    fun showAd(context: Context) {
        adMobManager.loadRewardVideoAd(
            activity = context as Activity,
            onAdLoaded = {
                state.adLoadingDialogState.value = false
            },
            onAdFailedToLoad = {
                context.showToast(context.getString(R.string.NO_INTERNET_CONNECTION))
                state.adLoadingDialogState.value = false
            },
            fullScreenOnAdLoad = {
                state.adLoadingDialogState.value = false
            },
            fullScreenOnAdFailedToLoad = {
                context.showToast(context.getString(R.string.adLoadError))
                errorReward()
                state.adLoadingDialogState.value = false
            },
            rewardListener = {
                earnReward()
            }
        )
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

    override fun portfolioOnTickerMessageReceiveListener(tickerJsonObject: String) {
        if (isPortfolioSocketRunning) {
            val model = gson.fromJson(tickerJsonObject, PortfolioTickerModel::class.java)
            if (model.code == BTC_MARKET && userHoldCoinDtoListPositionHashMap[BTC_MARKET] == null) {
                state.btcTradePrice.value = model.tradePrice
                return
            } else if (model.code == BTC_MARKET) {
                state.btcTradePrice.value = model.tradePrice
            }
            val position = userHoldCoinDtoListPositionHashMap[model.code] ?: 0
            val userHoldCoin = userHoldCoinHashMap[model.code]!!
//                val isFavorite = favoriteHashMap[model.code]
            tempUserHoldCoinDtoList[position] =
                UserHoldCoinDTO(
                    myCoinsKoreanName = MoeuiBitDataStore.coinName[model.code]?.first ?: "",
                    myCoinsEngName = MoeuiBitDataStore.coinName[model.code]?.second ?: "",
                    myCoinsSymbol = userHoldCoin.symbol,
                    myCoinsQuantity = userHoldCoin.quantity,
                    myCoinsBuyingAverage = userHoldCoin.purchasePrice,
                    currentPrice = model.tradePrice,
                    openingPrice = model.preClosingPrice,
                    warning = model.marketWarning,
                    isFavorite = null,
                    market = model.code,
                    purchaseAverageBtcPrice = userHoldCoin.PurchaseAverageBtcPrice
                )
        }
    }

    companion object {
        fun provideFactory(
            adMobManager: AdMobManager,
            localRepository: LocalRepository,
        ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return PortfolioViewModel(localRepository, adMobManager) as T
            }
        }

        const val SORT_DEFAULT = -1
        const val SORT_NAME_DEC = 0
        const val SORT_NAME_ASC = 1
        const val SORT_RATE_DEC = 2
        const val SORT_RATE_ASC = 3
    }
}