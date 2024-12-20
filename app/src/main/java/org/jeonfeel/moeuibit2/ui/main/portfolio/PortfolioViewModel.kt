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
import org.jeonfeel.moeuibit2.data.network.retrofit.request.upbit.GetUpbitMarketTickerReq
import org.jeonfeel.moeuibit2.data.network.retrofit.response.upbit.GetUpbitMarketTickerRes
import org.jeonfeel.moeuibit2.data.network.websocket.model.upbit.UpbitSocketTickerRes
import org.jeonfeel.moeuibit2.data.repository.local.LocalRepository
import org.jeonfeel.moeuibit2.data.usecase.UpbitExchangeUseCase
import org.jeonfeel.moeuibit2.ui.base.BaseViewModel
import org.jeonfeel.moeuibit2.ui.main.portfolio.dto.UserHoldCoinDTO
import org.jeonfeel.moeuibit2.utils.calculator.Calculator
import org.jeonfeel.moeuibit2.utils.manager.AdMobManager
import org.jeonfeel.moeuibit2.utils.manager.CacheManager
import org.jeonfeel.moeuibit2.utils.manager.PreferencesManager
import org.jeonfeel.moeuibit2.utils.mapToMarketCodesRequest
import java.math.BigDecimal
import javax.inject.Inject

class PortfolioState {
}

@HiltViewModel
class PortfolioViewModel @Inject constructor(
    val adMobManager: AdMobManager,
    val preferenceManager: PreferencesManager,
    private val localRepository: LocalRepository,
    private val upbitUseCase: UpbitExchangeUseCase,
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
    private val myCoinHashMap = HashMap<String, MyCoin>()
    private val userHoldCoinDtoListPositionHashMap = HashMap<String, Int>()

    private val _tickerResponse = MutableStateFlow<UpbitSocketTickerRes?>(null)
    private var realTimeUpdateJob: Job? = null

    init {
        _loadingState.value = true
        viewModelScope.launch {
            getCoinName()
            resetPortfolio()
        }
    }

    fun onResume() {
        realTimeUpdateJob = viewModelScope.launch(ioDispatcher) {
//            _isPortfolioSocketRunning.value = true
            getCoinName()
            compareUserHoldCoinList()
        }.also { it.start() }
    }

    fun onPause() {
        viewModelScope.launch {
//            _loadingState.value = true
            _isPortfolioSocketRunning.value = false
            requestSubscribeTicker(listOf(""))
            realTimeUpdateJob?.cancelAndJoin()
        }
    }

    fun compareUserHoldCoinList() {
        viewModelScope.launch(ioDispatcher) {
            _isPortfolioSocketRunning.value = false
            getUserSeedMoney()
            if (myCoinList.isNotEmpty()) {
                val beforeMap = myCoinList.associateBy { it?.market }
                val beforeMap2 = myCoinList.associateBy { Pair(it?.market, it) }
                getUserHoldCoins()
                val tempMyCoinMap = myCoinList.associateBy { it?.market }

                val addCoinList = tempMyCoinMap.keys - beforeMap.keys
                val removeCoinList = beforeMap.keys - tempMyCoinMap.keys

                val realAddCoinList = arrayListOf<MyCoin?>()

                Logger.e("추가된 코인: ${addCoinList}")
                Logger.e("제거된 코인: ${removeCoinList}")

                removeCoinList.forEach { removeCoin ->
                    _userHoldCoinDtoList.removeIf { it.market == removeCoin }
                    myCoinList.removeIf { it?.market == removeCoin }
                    myCoinHashMap.remove(removeCoin)
                    userHoldCoinDtoListPositionHashMap.remove(removeCoin)
                }

                addCoinList.forEach { addCoin ->
                    tempMyCoinMap[addCoin]?.let {
                        myCoinList.add(it)
                        realAddCoinList.add(it)
                    }
                }

                myCoinList.forEachIndexed { index, myCoin ->
                    myCoin?.let {
                        userHoldCoinDtoListPositionHashMap[it.market] = index
                    }
                }

//                userHoldCoinDtoListPositionHashMap


                parseMyCoinToUserHoldCoin(realAddCoinList.toList())
                requestTicker()
                setETC()

                userHoldCoinsMarkets.clear()
                myCoinList.forEach {
                    userHoldCoinsMarkets.append(it?.market).append(",")
                }

                if (_userHoldCoinDtoList.find { it.market.startsWith(UPBIT_BTC_SYMBOL_PREFIX) } != null
                    && myCoinHashMap[BTC_MARKET] == null) {
                    userHoldCoinsMarkets.append(BTC_MARKET)
                }

//                sortUserHoldCoin(sortStandard = portfolioOrderState.value)

                requestSubscribeTicker(userHoldCoinsMarkets.split(","))
                collectTicker()
                _isPortfolioSocketRunning.value = true
            } else {
                resetPortfolio()
                getUserSeedMoney()
                getUserHoldCoins()
                parseMyCoinToUserHoldCoin()
                requestTicker()
                setETC()
                requestSubscribeTicker(userHoldCoinsMarkets.split(","))
                collectTicker()
                _isPortfolioSocketRunning.value = true
            }
        }
    }

    private suspend fun getCoinName() {
        if (_koreanCoinNameMap.isEmpty() || _engCoinNameMap.isEmpty()) {
            _koreanCoinNameMap.putAll(cacheManager.readKoreanCoinNameMap())
            _engCoinNameMap.putAll(cacheManager.readEnglishCoinNameMap())
        }
    }

    private suspend fun resetPortfolio() {
        _isPortfolioSocketRunning.value = false
        _totalPurchase.value = BigDecimal(0.0)
        _totalValuedAssets.value = BigDecimal(0.0)
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
        myCoinList.clear()
        myCoinList.addAll(localRepository.getMyCoinDao().all ?: emptyList())
    }

    private suspend fun parseMyCoinToUserHoldCoin(list: List<MyCoin?>? = null) {
        val targetList = list ?: myCoinList

        if(list == null) {
            targetList.ifEmpty {
                _totalValuedAssets.value = BigDecimal(0.0)
                _totalPurchase.value = BigDecimal(0.0)
                return
            }
        }

        val isFavorite = 0

        targetList.forEachIndexed { index, myCoin ->
            val userHoldCoinDTO = myCoin!!.parseUserHoldsModel().apply {
                this.myCoinKoreanName = _koreanCoinNameMap[myCoin.symbol] ?: ""
                this.myCoinEngName = _engCoinNameMap[myCoin.symbol] ?: ""
                this.isFavorite = isFavorite
                this.myCoinsBuyingAverage = myCoin.purchasePrice
                this.purchaseAverageBtcPrice = myCoin.purchaseAverageBtcPrice
                this.myCoinsSymbol = myCoin.symbol
                this.market = myCoin.market
            }
            _userHoldCoinDtoList.add(userHoldCoinDTO)
            myCoinHashMap[myCoin.market] = myCoin
            _totalPurchase.value = _totalPurchase.value.plus(Calculator.getTotalPurchase(myCoin))
            userHoldCoinsMarkets.append(myCoin.market).append(",")
            userHoldCoinDtoListPositionHashMap[myCoin.market] = index
        }

        if (_userHoldCoinDtoList.find { it.market.startsWith(UPBIT_BTC_SYMBOL_PREFIX) } != null
            && myCoinHashMap[BTC_MARKET] == null) {
            userHoldCoinsMarkets.append(BTC_MARKET)
        }
    }

    private suspend fun requestTicker() {
        val marketCodes = myCoinList.map { it?.market ?: "" }.toList().mapToMarketCodesRequest()
        val req = GetUpbitMarketTickerReq(
            marketCodes = marketCodes
        )
        executeUseCase<List<GetUpbitMarketTickerRes>>(
            target = upbitUseCase.getMarketTicker(
                getUpbitMarketTickerReq = req,
                isList = true
            ),
            onComplete = { result ->
                runCatching {
                    result.forEach loop@{
                        if (it.market == BTC_MARKET && userHoldCoinDtoListPositionHashMap[BTC_MARKET] == null) {
                            _btcTradePrice.doubleValue = it.tradePrice
                            return@loop
                        } else if (it.market == BTC_MARKET) {
                            _btcTradePrice.doubleValue = it.tradePrice
                        }
                        val position = userHoldCoinDtoListPositionHashMap[it.market] ?: 0
                        val tempDto = userHoldCoinDtoList[position]
                        _userHoldCoinDtoList[position] =
                            UserHoldCoinDTO(
                                currentPrice = it.tradePrice,
                                openingPrice = it.prevClosingPrice,
                                warning = "",
                                market = it.market,
                                isFavorite = 0,
                                myCoinKoreanName = tempDto.myCoinKoreanName,
                                myCoinEngName = tempDto.myCoinEngName,
                                myCoinsQuantity = tempDto.myCoinsQuantity,
                                myCoinsBuyingAverage = tempDto.myCoinsBuyingAverage,
                                purchaseAverageBtcPrice = tempDto.purchaseAverageBtcPrice,
                                myCoinsSymbol = tempDto.myCoinsSymbol
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
                        _loadingState.value = false
                    },
                    onFailure = {
                        Logger.e(it.message.toString())
                        _loadingState.value = false
                    }
                )
            }
        )
    }

    private suspend fun setETC() {
//        sortUserHoldCoin(SORT_DEFAULT)
        if (userHoldCoinDtoListPositionHashMap[BTC_MARKET] == null) {
            userHoldCoinsMarkets.append(BTC_MARKET)
        } else {
            userHoldCoinsMarkets.deleteCharAt(userHoldCoinsMarkets.lastIndex)
        }
    }

    private suspend fun requestSubscribeTicker(markets: List<String>) {
        upbitUseCase.requestPortfolioSubscribeTicker(marketCodes = markets)
    }

    fun sortUserHoldCoin(sortStandard: Int) {
        _isPortfolioSocketRunning.value = false
        _portfolioOrderState.intValue = sortStandard

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

    private suspend fun collectTicker() {
        upbitUseCase.observePotfolioTickerResponse().onEach { result ->
            if (!isPortfolioSocketRunning.value) return@onEach

            _tickerResponse.update {
                result
            }
        }.collect { upbitSocketTickerRes ->
            runCatching {
                if (upbitSocketTickerRes.code == BTC_MARKET) {
                    _btcTradePrice.doubleValue = upbitSocketTickerRes.tradePrice
                    if (userHoldCoinDtoListPositionHashMap[BTC_MARKET] == null) return@collect
                }

                if (userHoldCoinDtoListPositionHashMap[upbitSocketTickerRes.code] == null) return@collect

                val position =
                    userHoldCoinDtoListPositionHashMap[upbitSocketTickerRes.code] ?: 0
                val tempDto = userHoldCoinDtoList[position]

                _userHoldCoinDtoList[position] =
                    UserHoldCoinDTO(
                        currentPrice = upbitSocketTickerRes.tradePrice,
                        openingPrice = upbitSocketTickerRes.prevClosingPrice,
                        warning = "",
                        market = upbitSocketTickerRes.code,
                        isFavorite = 0,
                        myCoinKoreanName = tempDto.myCoinKoreanName,
                        myCoinEngName = tempDto.myCoinEngName,
                        myCoinsQuantity = tempDto.myCoinsQuantity,
                        myCoinsBuyingAverage = tempDto.myCoinsBuyingAverage,
                        purchaseAverageBtcPrice = tempDto.purchaseAverageBtcPrice,
                        myCoinsSymbol = tempDto.myCoinsSymbol
                    )
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