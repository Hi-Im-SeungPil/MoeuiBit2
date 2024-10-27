package org.jeonfeel.moeuibit2.ui.main.portfolio

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.*
import com.orhanobut.logger.Logger
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.jeonfeel.moeuibit2.MoeuiBitDataStore
import org.jeonfeel.moeuibit2.constants.*
import org.jeonfeel.moeuibit2.data.local.room.entity.MyCoin
import org.jeonfeel.moeuibit2.data.network.websocket.model.upbit.UpbitSocketTickerRes
import org.jeonfeel.moeuibit2.data.repository.local.LocalRepository
import org.jeonfeel.moeuibit2.data.usecase.UpbitUseCase
import org.jeonfeel.moeuibit2.ui.base.BaseViewModel
import org.jeonfeel.moeuibit2.ui.main.portfolio.dto.UserHoldCoinDTO
import org.jeonfeel.moeuibit2.utils.calculator.Calculator
import org.jeonfeel.moeuibit2.utils.manager.AdMobManager
import org.jeonfeel.moeuibit2.utils.manager.CacheManager
import org.jeonfeel.moeuibit2.utils.manager.PreferencesManager
import java.math.BigDecimal
import javax.inject.Inject

class PortfolioState {
}

@HiltViewModel
class PortfolioViewModel @Inject constructor(
    val adMobManager: AdMobManager,
    val preferenceManager: PreferencesManager,
    private val localRepository: LocalRepository,
    private val upbitUseCase: UpbitUseCase,
    private val cacheManager: CacheManager,
) : BaseViewModel(preferenceManager) {

    private val _userSeedMoney = mutableLongStateOf(0L)
    val userSeedMoney: State<Long> get() = _userSeedMoney

    private val _totalPurchase = mutableStateOf(BigDecimal(0.0))
    val totalPurchase: State<BigDecimal> get() = _totalPurchase

    private val _userHoldCoinDtoList = SnapshotStateList<UserHoldCoinDTO>()
    val userHoldCoinDtoList: List<UserHoldCoinDTO> get() = _userHoldCoinDtoList

    private val _totalValuedAssets = mutableStateOf(BigDecimal(0.0))
    val totalValuedAssets: State<BigDecimal> get() = _totalValuedAssets

    private val _removeCoinCount = mutableIntStateOf(0)
    val removeCoinCount: State<Int> get() = _removeCoinCount

    private val _portfolioOrderState = mutableIntStateOf(SORT_NONE)
    val portfolioOrderState: State<Int> get() = _portfolioOrderState

    private val _btcTradePrice = mutableDoubleStateOf(0.0)
    val btcTradePrice: State<Double> get() = _btcTradePrice

    private val _isPortfolioSocketRunning = mutableStateOf(true)
    val isPortfolioSocketRunning: State<Boolean> get() = _isPortfolioSocketRunning

    private val _koreanCoinNameMap = mutableMapOf<String, String>()

    private val _engCoinNameMap = mutableMapOf<String, String>()

    private val userHoldCoinsMarkets = StringBuilder()
    private val myCoinList = ArrayList<MyCoin?>()
    private val tempUserHoldCoinDtoList = ArrayList<UserHoldCoinDTO>()
    private val myCoinHashMap = HashMap<String, MyCoin>()
    private val userHoldCoinDtoListPositionHashMap = HashMap<String, Int>()

    private val _tickerResponse = MutableStateFlow<UpbitSocketTickerRes?>(null)
    private var realTimeUpdateJob: Job? = null

    init {
        viewModelScope.launch {
            _koreanCoinNameMap.putAll(cacheManager.readKoreanCoinNameMap())
            _engCoinNameMap.putAll(cacheManager.readEnglishCoinNameMap())
        }
    }

    fun onResume() {
        realTimeUpdateJob = viewModelScope.launch(ioDispatcher) {
            _isPortfolioSocketRunning.value = true
            resetPortfolio()
            getUserSeedMoney()
            getUserHoldCoins()
            parseMyCoinToUserHoldCoin()
            setETC()
            requestSubscribeTicker(userHoldCoinsMarkets.split(","))
            collectTicker()
        }.also { it.start() }
    }

    fun onPause() {
        viewModelScope.launch {
            _isPortfolioSocketRunning.value = false
            requestSubscribeTicker(listOf(""))
            realTimeUpdateJob?.cancelAndJoin()
        }
    }

    private fun resetPortfolio() {
        _isPortfolioSocketRunning.value = false
        _portfolioOrderState.intValue = SORT_DEFAULT
        userHoldCoinsMarkets.clear()
        userHoldCoinDtoListPositionHashMap.clear()
        _userHoldCoinDtoList.clear()
        myCoinList.clear()
    }

    private suspend fun getUserSeedMoney() {
        _userSeedMoney.longValue = localRepository.getUserDao().all?.krw ?: 0L
    }

    private suspend fun getUserHoldCoins() {
        myCoinList.addAll(localRepository.getMyCoinDao().all ?: emptyList())
    }

    private fun parseMyCoinToUserHoldCoin() {
        myCoinList.ifEmpty {
            _totalValuedAssets.value = BigDecimal(0.0)
            _totalPurchase.value = BigDecimal(0.0)
            return
        }

        val isFavorite = 0
        myCoinList.forEachIndexed { index, myCoin ->
            val userHoldCoinDTO = myCoin!!.parseUserHoldsModel().apply {
                this.myCoinKoreanName = _koreanCoinNameMap[myCoin.symbol] ?: ""
                this.myCoinEngName = _engCoinNameMap[myCoin.symbol] ?: ""
                this.isFavorite = isFavorite
            }
            tempUserHoldCoinDtoList.add(userHoldCoinDTO)

            myCoinHashMap[myCoin.market] = myCoin
            _totalPurchase.value = _totalPurchase.value.plus(Calculator.getTotalPurchase(myCoin))
            userHoldCoinsMarkets.append(myCoin.market).append(",")
            userHoldCoinDtoListPositionHashMap[myCoin.market] = index
        }
    }

    private fun setETC() {
        sortUserHoldCoin(SORT_DEFAULT)
        if (userHoldCoinDtoListPositionHashMap[BTC_MARKET] == null) {
            userHoldCoinsMarkets.append(BTC_MARKET)
        } else {
            userHoldCoinsMarkets.deleteCharAt(userHoldCoinsMarkets.lastIndex)
        }
    }

    private suspend fun requestSubscribeTicker(markets: List<String>) {
        upbitUseCase.requestSubscribeTicker(marketCodes = markets)
    }

//    private fun getUserHoldCoins() {
//        viewModelScope.launch(ioDispatcher) {
//            resetPortfolio()
//            var localTotalPurchase = 0.0
//            if (myCoinList.isNotEmpty()) {
//                for (i in myCoinList.indices) {
//                    val userHoldCoin = myCoinList[i] ?: MyCoin("", 0.0, "", "", 0.0)
//                    val market = userHoldCoin.market
//                    val marketState = Utils.getSelectedMarket(market)
//                    val isFavorite = MoeuiBitDataStore.upBitFavoriteHashMap[market]
//                    myCoinHashMap[market] = userHoldCoin
//                    localTotalPurchase = if (marketState == SELECTED_KRW_MARKET) {
//                        localTotalPurchase + (userHoldCoin.quantity * userHoldCoin.purchasePrice)
//                    } else {
//                        localTotalPurchase + (userHoldCoin.quantity * userHoldCoin.purchasePrice * userHoldCoin.purchaseAverageBtcPrice)
//                    }
//                    userHoldCoinsMarket.append(userHoldCoin.market).append(",")
//                    userHoldCoinDtoListPositionHashMap[userHoldCoin.market] = i
//                    tempUserHoldCoinDtoList.add(
//                        UserHoldCoinDTO(
//                            myCoinKoreanName = MoeuiBitDataStore.upBitCoinName[userHoldCoin.market]?.first
//                                ?: "",
//                            myCoinEngName = MoeuiBitDataStore.upBitCoinName[userHoldCoin.market]?.second
//                                ?: "",
//                            myCoinsSymbol = userHoldCoin.symbol,
//                            myCoinsQuantity = 0.0,
//                            myCoinsBuyingAverage = 0.0,
//                            currentPrice = 0.0,
//                            openingPrice = 0.0,
//                            warning = "",
//                            isFavorite = isFavorite,
//                            market = userHoldCoin.market,
//                            purchaseAverageBtcPrice = userHoldCoin.purchaseAverageBtcPrice
//                        )
//                    )
//                }
//                sortUserHoldCoin(SORT_DEFAULT)
//                if (userHoldCoinDtoListPositionHashMap[BTC_MARKET] == null) {
//                    userHoldCoinsMarket.append(BTC_MARKET)
//                } else {
//                    userHoldCoinsMarket.deleteCharAt(userHoldCoinsMarket.lastIndex)
//                }
//                state.totalPurchase.value = localTotalPurchase
//                updateUserHoldCoins()
//            } else {
//                swapList()
//                state.totalValuedAssets.value = 0.0
//                state.totalPurchase.value = 0.0
//            }
//        }
//    }

    fun sortUserHoldCoin(sortStandard: Int) {
        _isPortfolioSocketRunning.value = false
        viewModelScope.launch(defaultDispatcher) {
            when (sortStandard) {
                SORT_NAME_DEC -> {
                    _userHoldCoinDtoList.sortBy { element ->
                        element.myCoinKoreanName
                    }
                }

                SORT_NAME_ASC -> {
                    _userHoldCoinDtoList.sortByDescending { element ->
                        element.myCoinKoreanName
                    }
                }

                SORT_RATE_ASC -> {
                    _userHoldCoinDtoList.sortByDescending { element ->
                        element.myCoinsBuyingAverage / element.currentPrice
                    }
                }

                SORT_RATE_DEC -> {
                    _userHoldCoinDtoList.sortBy { element ->
                        element.myCoinsBuyingAverage / element.currentPrice
                    }
                }

                else -> {
                    _userHoldCoinDtoList.sortBy { element ->
                        element.myCoinKoreanName
                    }
                }
            }

            _userHoldCoinDtoList.forEachIndexed { index, item ->
                userHoldCoinDtoListPositionHashMap[item.market] = index
            }

            _isPortfolioSocketRunning.value = true
        }
    }

    fun editUserHoldCoin() {
//        var count = 0
//        _isPortfolioSocketRunning.value = false
//
//        viewModelScope.launch(ioDispatcher) {
//            if (UpBitTickerWebSocket.currentSocketState != SOCKET_IS_CONNECTED || NetworkMonitorUtil.currentNetworkState != INTERNET_CONNECTION) {
//                _removeCoinCount.intValue = -1
//                delay(100L)
//                _removeCoinCount.intValue = -2
//            } else {
//                for (i in myCoinList) {
//                    val targetList = if (i!!.market.startsWith(SYMBOL_KRW)) {
//                        MoeuiBitDataStore.upBitKrwMarkets
//                    } else {
//                        MoeuiBitDataStore.upBitBtcMarkets
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
//                    _isPortfolioSocketRunning.value = false
//                    getUserHoldCoins()
//                }
//                state.removeCoinCount.value = count
//                delay(100L)
//                state.removeCoinCount.value = 0
//            }
//        }
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

    fun updateFavorite(market: String, isFavorite: Boolean) {
        viewModelScope.launch(ioDispatcher) {
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
                    }
                }
            }
        }
    }

//    override fun onTickerMessageReceiveListener(tickerJsonObject: String) {
//        if (state.isPortfolioSocketRunning.value && UpBitTickerWebSocket.currentPage == IS_PORTFOLIO_SCREEN) {
//            val model = Gson().fromJson(tickerJsonObject, PortfolioTickerModel::class.java)
//            if (model.code == BTC_MARKET && userHoldCoinDtoListPositionHashMap[BTC_MARKET] == null) {
//                state.btcTradePrice.doubleValue = model.tradePrice
//                return
//            } else if (model.code == BTC_MARKET) {
//                state.btcTradePrice.doubleValue = model.tradePrice
//            }
//            val position = userHoldCoinDtoListPositionHashMap[model.code] ?: 0
//            val userHoldCoin = myCoinHashMap[model.code]!!
//            val isFavorite = MoeuiBitDataStore.upBitFavoriteHashMap[model.code]
//            tempUserHoldCoinDtoList[position] =
//                UserHoldCoinDTO(
//                    myCoinKoreanName = MoeuiBitDataStore.upBitCoinName[model.code]?.first ?: "",
//                    myCoinEngName = MoeuiBitDataStore.upBitCoinName[model.code]?.second ?: "",
//                    myCoinsSymbol = userHoldCoin.symbol,
//                    myCoinsQuantity = userHoldCoin.quantity,
//                    myCoinsBuyingAverage = userHoldCoin.purchasePrice,
//                    currentPrice = model.tradePrice,
//                    openingPrice = model.preClosingPrice,
//                    warning = model.marketWarning,
//                    isFavorite = isFavorite,
//                    market = model.code,
//                    purchaseAverageBtcPrice = userHoldCoin.purchaseAverageBtcPrice
//                )
//        }
//    }

    private suspend fun collectTicker() {
        upbitUseCase.observeTickerResponse().onEach { result ->
            if (!isPortfolioSocketRunning.value) return@onEach
            _tickerResponse.update {
                result
            }
        }.collect { upbitSocketTickerRes ->
            runCatching {
                if (upbitSocketTickerRes.code == BTC_MARKET) {
                    _btcTradePrice.doubleValue = upbitSocketTickerRes.tradePrice
                    if (userHoldCoinDtoListPositionHashMap[BTC_MARKET] == null) return@collect

                    val position =
                        userHoldCoinDtoListPositionHashMap[upbitSocketTickerRes.code] ?: 0
                    val isFavorite =
                        MoeuiBitDataStore.upBitFavoriteHashMap[upbitSocketTickerRes.code]

                    _userHoldCoinDtoList[position] =
                        userHoldCoinDtoList[position].copy(
                            currentPrice = upbitSocketTickerRes.tradePrice,
                            openingPrice = upbitSocketTickerRes.prevClosingPrice,
                            warning = upbitSocketTickerRes.marketWarning,
                            market = upbitSocketTickerRes.code,
                            isFavorite = isFavorite //TODO
                        )
                }
            }.fold(
                onSuccess = {
                    _totalValuedAssets.value = userHoldCoinDtoList.sumOf {
                        if (it.market.startsWith(UPBIT_KRW_SYMBOL_PREFIX)) {
                            it.currentPrice.toBigDecimal()
                                .multiply(it.myCoinsQuantity.toBigDecimal())
                        } else {
                            it.currentPrice.toBigDecimal()
                                .multiply(it.myCoinsQuantity.toBigDecimal())
                                .multiply(btcTradePrice.value.toBigDecimal())
                        }
                    }
                },
                onFailure = {
                    Logger.e(it.message.toString())
                }
            )
        }
    }

    companion object {
        const val SORT_NONE = -2
        const val SORT_DEFAULT = -1
        const val SORT_NAME_DEC = 0
        const val SORT_NAME_ASC = 1
        const val SORT_RATE_DEC = 2
        const val SORT_RATE_ASC = 3
    }
}