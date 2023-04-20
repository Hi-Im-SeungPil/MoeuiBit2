package org.jeonfeel.moeuibit2.ui.viewmodels

import android.app.Activity
import android.content.Context
import android.os.Handler
import android.os.Looper
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.jeonfeel.moeuibit2.MoeuiBitDataStore
import org.jeonfeel.moeuibit2.R
import org.jeonfeel.moeuibit2.constants.*
import org.jeonfeel.moeuibit2.data.local.room.entity.Favorite
import org.jeonfeel.moeuibit2.data.local.room.entity.MyCoin
import org.jeonfeel.moeuibit2.data.remote.websocket.UpBitTickerWebSocket
import org.jeonfeel.moeuibit2.data.remote.websocket.listener.OnTickerMessageReceiveListener
import org.jeonfeel.moeuibit2.data.remote.websocket.model.PortfolioTickerModel
import org.jeonfeel.moeuibit2.data.repository.local.LocalRepository
import org.jeonfeel.moeuibit2.ui.base.BaseViewModel
import org.jeonfeel.moeuibit2.ui.main.portfolio.dto.UserHoldCoinDTO
import org.jeonfeel.moeuibit2.utils.NetworkMonitorUtil
import org.jeonfeel.moeuibit2.utils.Utils
import org.jeonfeel.moeuibit2.utils.manager.AdMobManager
import org.jeonfeel.moeuibit2.utils.showToast
import javax.inject.Inject

class PortfolioState {
    val userSeedMoney = mutableStateOf(0L)
    var totalPurchase = mutableStateOf(0.0)
    val userHoldCoinDtoList = mutableStateOf(SnapshotStateList<UserHoldCoinDTO>())
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
    val isPortfolioSocketRunning = mutableStateOf(true)
}

class PortfolioViewModel constructor(
    private val localRepository: LocalRepository,
    private val adMobManager: AdMobManager
) : BaseViewModel(), OnTickerMessageReceiveListener {
    val state = PortfolioState()
    var userHoldCoinsMarket = StringBuffer()
    var userHoldCoinList = emptyList<MyCoin?>()
    private val tempUserHoldCoinDtoList = ArrayList<UserHoldCoinDTO>()
    private val userHoldCoinHashMap = HashMap<String, MyCoin>()
    private val userHoldCoinDtoListPositionHashMap = HashMap<String, Int>()
    fun init() {
        UpBitTickerWebSocket.portfolioListener = this
        getUserSeedMoney()
        getUserHoldCoins()
    }
    private fun getUserSeedMoney() {
        viewModelScope.launch(ioDispatcher) {
            state.userSeedMoney.value = localRepository.getUserDao().all?.krw ?: 0L
        }
    }

    private fun getUserHoldCoins() {
        viewModelScope.launch(ioDispatcher) {
            resetPortfolio()
            var localTotalPurchase = 0.0
            if (userHoldCoinList.isNotEmpty()) {
                for (i in userHoldCoinList.indices) {
                    val userHoldCoin = userHoldCoinList[i] ?: MyCoin("", 0.0, "", "", 0.0)
                    val market = userHoldCoin.market
                    val marketState = Utils.getSelectedMarket(market)
                    val isFavorite = MoeuiBitDataStore.favoriteHashMap[market]
                    userHoldCoinHashMap[market] = userHoldCoin
                    localTotalPurchase = if (marketState == SELECTED_KRW_MARKET) {
                        localTotalPurchase + (userHoldCoin.quantity * userHoldCoin.purchasePrice)
                    } else {
                        localTotalPurchase + (userHoldCoin.quantity * userHoldCoin.purchasePrice * userHoldCoin.PurchaseAverageBtcPrice)
                    }
                    userHoldCoinsMarket.append(userHoldCoin.market).append(",")
                    userHoldCoinDtoListPositionHashMap[userHoldCoin.market] = i
                    tempUserHoldCoinDtoList.add(
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
                            isFavorite = isFavorite,
                            market = userHoldCoin.market,
                            purchaseAverageBtcPrice = userHoldCoin.PurchaseAverageBtcPrice
                        )
                    )
                }
//                swapList()
                sortUserHoldCoin(SORT_DEFAULT)
                if (userHoldCoinDtoListPositionHashMap[BTC_MARKET] == null) {
                    userHoldCoinsMarket.append(BTC_MARKET)
                } else {
                    userHoldCoinsMarket.deleteCharAt(userHoldCoinsMarket.lastIndex)
                }
                state.totalPurchase.value = localTotalPurchase
                UpBitTickerWebSocket
                    .getListener()
                    .setTickerMessageListener(this@PortfolioViewModel)
                UpBitTickerWebSocket.portfolioMarket = userHoldCoinsMarket.toString()
                UpBitTickerWebSocket.requestTicker(userHoldCoinsMarket.toString())
                updateUserHoldCoins()
            } else {
                swapList()
                state.totalValuedAssets.value = 0.0
                state.totalPurchase.value = 0.0
            }
        }
    }

    private suspend fun resetPortfolio() {
        state.isPortfolioSocketRunning.value = false
        state.portfolioOrderState.value = SORT_DEFAULT
        userHoldCoinsMarket = StringBuffer()
        userHoldCoinDtoListPositionHashMap.clear()
        tempUserHoldCoinDtoList.clear()
        userHoldCoinList = localRepository.getMyCoinDao().all ?: emptyList()
    }

    fun sortUserHoldCoin(sortStandard: Int) {
        state.isPortfolioSocketRunning.value = false
        viewModelScope.launch(defaultDispatcher) {
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
            swapList()

            state.isPortfolioSocketRunning.value = true
        }
    }

    fun editUserHoldCoin() {
        var count = 0
        state.isPortfolioSocketRunning.value = false

        viewModelScope.launch(ioDispatcher) {
            if (UpBitTickerWebSocket.currentSocketState != SOCKET_IS_CONNECTED || NetworkMonitorUtil.currentNetworkState != INTERNET_CONNECTION) {
                state.removeCoinCount.value = -1
                delay(100L)
                state.removeCoinCount.value = 0
            } else {
                for (i in userHoldCoinList) {
                    val targetList = if (i!!.market.startsWith(SYMBOL_KRW)) {
                        MoeuiBitDataStore.krwMarkets
                    } else {
                        MoeuiBitDataStore.btcMarkets
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
                    state.isPortfolioSocketRunning.value = false
                    getUserHoldCoins()
                }
                state.removeCoinCount.value = count
                delay(100L)
                state.removeCoinCount.value = 0
            }
        }
    }

    private suspend fun updateUserHoldCoins() {
        state.isPortfolioSocketRunning.value = true
        viewModelScope.launch {
            while (state.isPortfolioSocketRunning.value) {
                var tempTotalValuedAssets = 0.0
                try {
                    swapList()
                    for (i in tempUserHoldCoinDtoList.indices) {
                        val userHoldCoinDTO = tempUserHoldCoinDtoList[i]
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

    private fun swapList() {
        val tempList = mutableStateListOf<UserHoldCoinDTO>()
        tempList.addAll(tempUserHoldCoinDtoList)
        state.userHoldCoinDtoList.value = tempList
    }

    private fun earnReward() {
        viewModelScope.launch(ioDispatcher) {
            val userDao = localRepository.getUserDao()
            if (userDao.all == null) {
                userDao.insert()
            } else {
                userDao.updatePlusMoney(10_000_000)
            }
        }
    }

    private fun errorReward() {
        viewModelScope.launch(ioDispatcher) {
            val userDao = localRepository.getUserDao()
            if (userDao.all == null) {
                userDao.errorInsert()
            } else {
                userDao.updatePlusMoney(1_000_000)
            }
        }
    }

    fun updateFavorite(market: String, isFavorite: Boolean) {
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
                    }
                }
            }
        }
    }

    override fun onTickerMessageReceiveListener(tickerJsonObject: String) {
        if (state.isPortfolioSocketRunning.value && UpBitTickerWebSocket.currentPage == IS_PORTFOLIO_SCREEN) {
            val model = gson.fromJson(tickerJsonObject, PortfolioTickerModel::class.java)
            if (model.code == BTC_MARKET && userHoldCoinDtoListPositionHashMap[BTC_MARKET] == null) {
                state.btcTradePrice.value = model.tradePrice
                return
            } else if (model.code == BTC_MARKET) {
                state.btcTradePrice.value = model.tradePrice
            }
            val position = userHoldCoinDtoListPositionHashMap[model.code] ?: 0
            val userHoldCoin = userHoldCoinHashMap[model.code]!!
            val isFavorite = MoeuiBitDataStore.favoriteHashMap[model.code]
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
                    isFavorite = isFavorite,
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