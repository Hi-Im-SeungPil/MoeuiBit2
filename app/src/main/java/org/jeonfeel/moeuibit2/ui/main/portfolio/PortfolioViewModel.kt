package org.jeonfeel.moeuibit2.ui.main.portfolio

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.orhanobut.logger.Logger
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.jeonfeel.moeuibit2.MoeuiBitDataStore
import org.jeonfeel.moeuibit2.constants.BTC_MARKET
import org.jeonfeel.moeuibit2.constants.UPBIT_BTC_SYMBOL_PREFIX
import org.jeonfeel.moeuibit2.constants.UPBIT_KRW_SYMBOL_PREFIX
import org.jeonfeel.moeuibit2.constants.defaultDispatcher
import org.jeonfeel.moeuibit2.constants.ioDispatcher
import org.jeonfeel.moeuibit2.data.local.room.entity.MyCoin
import org.jeonfeel.moeuibit2.data.network.retrofit.request.upbit.GetUpbitMarketTickerReq
import org.jeonfeel.moeuibit2.data.network.retrofit.response.upbit.GetUpbitMarketTickerRes
import org.jeonfeel.moeuibit2.data.network.retrofit.response.upbit.UpbitMarketCodeRes
import org.jeonfeel.moeuibit2.data.network.websocket.model.upbit.UpbitSocketTickerRes
import org.jeonfeel.moeuibit2.data.repository.local.LocalRepository
import org.jeonfeel.moeuibit2.data.usecase.UpbitPortfolioUsecase
import org.jeonfeel.moeuibit2.ui.base.BaseViewModel
import org.jeonfeel.moeuibit2.ui.main.portfolio.dto.UserHoldCoinDTO
import org.jeonfeel.moeuibit2.utils.Utils
import org.jeonfeel.moeuibit2.utils.calculator.Calculator
import org.jeonfeel.moeuibit2.utils.manager.AdMobManager
import org.jeonfeel.moeuibit2.utils.manager.CacheManager
import org.jeonfeel.moeuibit2.utils.manager.PreferencesManager
import org.jeonfeel.moeuibit2.utils.mapToMarketCodesRequest
import java.math.BigDecimal
import javax.inject.Inject
import kotlin.collections.ArrayList
import kotlin.collections.HashMap
import kotlin.collections.List
import kotlin.collections.Map
import kotlin.collections.arrayListOf
import kotlin.collections.associateBy
import kotlin.collections.emptyList
import kotlin.collections.find
import kotlin.collections.forEach
import kotlin.collections.forEachIndexed
import kotlin.collections.get
import kotlin.collections.ifEmpty
import kotlin.collections.isNotEmpty
import kotlin.collections.listOf
import kotlin.collections.map
import kotlin.collections.minus
import kotlin.collections.mutableMapOf
import kotlin.collections.remove
import kotlin.collections.set
import kotlin.collections.sortBy
import kotlin.collections.sortByDescending
import kotlin.collections.sumOf
import kotlin.collections.toList

class PortfolioState {
}

@HiltViewModel
class PortfolioViewModel @Inject constructor(
    val adMobManager: AdMobManager,
    val preferenceManager: PreferencesManager,
    private val upbitUseCase: UpbitPortfolioUsecase,
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

    private val _portfolioOrderState = mutableIntStateOf(SORT_NONE)
    val portfolioOrderState: State<Int> get() = _portfolioOrderState

    val portfolioSearchTextState: MutableState<String> = mutableStateOf("")

    private val _btcTradePrice = mutableDoubleStateOf(0.0)
    val btcTradePrice: State<Double> get() = _btcTradePrice

    private val _isPortfolioSocketRunning = mutableStateOf(true)
    private val isPortfolioSocketRunning: State<Boolean> get() = _isPortfolioSocketRunning

    private val _koreanCoinNameMap = mutableMapOf<String, String>()
    private val _engCoinNameMap = mutableMapOf<String, String>()

    private val userHoldCoinsMarkets = StringBuilder()
    private val myCoinList = ArrayList<MyCoin?>()

    private val myCoinHashMap = HashMap<String, MyCoin>()

    private val userHoldCoinDtoListPositionHashMap = HashMap<String, Int>()

    private val _removeCoinInfo = SnapshotStateList<Pair<String, String>>()
    val removeCoinInfo: List<Pair<String, String>> get() = _removeCoinInfo // market reason

    private val _removeCoinCheckedList = SnapshotStateList<Boolean>()
    val removeCoinCheckedState: List<Boolean> get() = _removeCoinCheckedList

    private val showRemoveCoinDialog = mutableStateOf(false)
    val showRemoveCoinDialogState: State<Boolean> get() = showRemoveCoinDialog

    private val _tickerResponse = MutableStateFlow<UpbitSocketTickerRes?>(null)
    private var realTimeUpdateJob: Job? = null

    init {
        viewModelScope.launch {
            getCoinName()
        }
    }

    fun onResume() {
        realTimeUpdateJob = viewModelScope.launch(ioDispatcher) {
            getCoinName()
            compareUserHoldCoinList()
        }.also { it.start() }
    }

    fun onPause() {
        viewModelScope.launch {
            _isPortfolioSocketRunning.value = false
            requestSubscribeTicker(listOf(""))
            realTimeUpdateJob?.cancelAndJoin()
        }
    }

    private suspend fun compareUserHoldCoinList() {
        _isPortfolioSocketRunning.value = false

        getUserSeedMoney()
        if (myCoinList.isNotEmpty()) {
            val (beforeMyCoinMap, newMyCoinMap) = getBeforeCoinMapAndNewCoinMap()
            modifyCoin(beforeMyCoinMap, newMyCoinMap)
            updateCoin()
            requestTicker()
            setETC()
            requestSubscribeTicker(userHoldCoinsMarkets.split(","))
            _isPortfolioSocketRunning.value = true
            collectTicker()
        } else {
            resetPortfolio()
            getUserSeedMoney()
            getUserHoldCoins()
            parseMyCoinToUserHoldCoin()
            requestTicker()
            setETC()
            requestSubscribeTicker(userHoldCoinsMarkets.split(","))
            _isPortfolioSocketRunning.value = true
            collectTicker()
        }
    }

    private fun modifyCoin(
        beforeMyCoinMap: Map<String?, MyCoin?>,
        newMyCoinMap: Map<String?, MyCoin?>
    ) {
        val removeCoinListMarket = beforeMyCoinMap.keys - newMyCoinMap.keys
        val addCoinListMarket = newMyCoinMap.keys - beforeMyCoinMap.keys

        removeCoinListMarket.forEach { removeCoin ->
            _userHoldCoinDtoList.removeIf { it.market == removeCoin }
            myCoinList.remove(beforeMyCoinMap[removeCoin])
            myCoinHashMap.remove(removeCoin)
            userHoldCoinDtoListPositionHashMap.remove(removeCoin)
        }

        addCoinListMarket.forEachIndexed { index, market ->
            val myCoin = newMyCoinMap[market]

            val userHoldCoinDTO = myCoin!!.parseUserHoldsModel().apply {
                this.myCoinKoreanName = _koreanCoinNameMap[myCoin.symbol] ?: ""
                this.myCoinEngName = _engCoinNameMap[myCoin.symbol] ?: ""
                this.isFavorite = 0
                this.myCoinsBuyingAverage = myCoin.purchasePrice
                this.purchaseAverageBtcPrice = myCoin.purchaseAverageBtcPrice
                this.myCoinsSymbol = myCoin.symbol
                this.market = myCoin.market
            }
            _userHoldCoinDtoList.add(userHoldCoinDTO)
            myCoinHashMap[myCoin.market] = myCoin
        }

        _userHoldCoinDtoList.forEachIndexed { index, userHoldCoin ->
            userHoldCoin.let {
                userHoldCoinDtoListPositionHashMap[it.market] = index
            }
        }
    }

    private fun updateCoin() {
        var tempTotalPurchase = BigDecimal(0L)

        myCoinList.forEachIndexed { index, myCoin ->
            userHoldCoinDtoListPositionHashMap[myCoin?.market]?.let {
                val beforeUserHoldCoinDTO = _userHoldCoinDtoList[it]

                val userHoldCoinDTO = myCoin!!.parseUserHoldsModel().apply {
                    this.myCoinKoreanName = _koreanCoinNameMap[myCoin.symbol] ?: ""
                    this.myCoinEngName = _engCoinNameMap[myCoin.symbol] ?: ""
                    this.isFavorite = 0
                    this.myCoinsBuyingAverage = myCoin.purchasePrice
                    this.purchaseAverageBtcPrice = myCoin.purchaseAverageBtcPrice
                    this.myCoinsSymbol = myCoin.symbol
                    this.market = myCoin.market
                }

                _userHoldCoinDtoList[it] =
                    UserHoldCoinDTO(
                        currentPrice = beforeUserHoldCoinDTO.currentPrice,
                        openingPrice = beforeUserHoldCoinDTO.openingPrice,
                        warning = "",
                        market = userHoldCoinDTO.market,
                        isFavorite = 0,
                        myCoinKoreanName = userHoldCoinDTO.myCoinKoreanName,
                        myCoinEngName = userHoldCoinDTO.myCoinEngName,
                        myCoinsQuantity = userHoldCoinDTO.myCoinsQuantity,
                        myCoinsBuyingAverage = userHoldCoinDTO.myCoinsBuyingAverage,
                        purchaseAverageBtcPrice = userHoldCoinDTO.purchaseAverageBtcPrice,
                        myCoinsSymbol = userHoldCoinDTO.myCoinsSymbol,
                    )
            }

            tempTotalPurchase = tempTotalPurchase.plus(Calculator.getTotalPurchase(myCoin!!))
        }

        _totalPurchase.value = tempTotalPurchase

        userHoldCoinsMarkets.clear()
        myCoinList.forEach {
            userHoldCoinsMarkets.append(it?.market).append(",")
        }
    }

    private suspend fun getBeforeCoinMapAndNewCoinMap(): Pair<Map<String?, MyCoin?>, Map<String?, MyCoin?>> {
        val beforeMyCoinMap = myCoinList.associateBy { it?.market }
        getUserHoldCoins()
        val newMyCoinMap = myCoinList.associateBy { it?.market }
        return beforeMyCoinMap to newMyCoinMap
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
        _userSeedMoney.longValue = upbitUseCase.getUserSeedMoney()
    }

    private suspend fun getUserHoldCoins() {
        myCoinList.clear()
        myCoinList.addAll(upbitUseCase.getMyCoins())
    }

    private suspend fun parseMyCoinToUserHoldCoin(list: List<MyCoin?>? = null) {
        val targetList = list ?: myCoinList

        if (list == null) {
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
                                myCoinsSymbol = tempDto.myCoinsSymbol,
                                initialConstant = Utils.extractInitials(tempDto.myCoinKoreanName)
                            )
                        Logger.e("${Utils.extractInitials(tempDto.myCoinKoreanName)}")
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
        )
    }

    private suspend fun setETC() {
        if (userHoldCoinDtoListPositionHashMap[BTC_MARKET] == null) {
            userHoldCoinsMarkets.append(BTC_MARKET)
        } else {
            userHoldCoinsMarkets.deleteCharAt(userHoldCoinsMarkets.lastIndex)
        }
    }

    private suspend fun requestSubscribeTicker(markets: List<String>) {
        upbitUseCase.requestSubscribeTicker(marketCodes = markets)
    }

    fun sortUserHoldCoin(sortStandard: Int) {
        Logger.e("${isPortfolioSocketRunning.value}")
        _isPortfolioSocketRunning.value = false
        _portfolioOrderState.intValue = sortStandard

        val sortList = arrayListOf<UserHoldCoinDTO>().apply { addAll(_userHoldCoinDtoList) }

        viewModelScope.launch(defaultDispatcher) {
            when (sortStandard) {
                SORT_NAME_DEC -> {
                    sortList.sortBy { element ->
                        element.myCoinKoreanName
                    }
                }

                SORT_NAME_ASC -> {
                    sortList.sortByDescending { element ->
                        element.myCoinKoreanName
                    }
                }

                SORT_RATE_ASC -> {
                    sortList.sortByDescending { element ->
                        element.myCoinsBuyingAverage / element.currentPrice
                    }
                }

                SORT_RATE_DEC -> {
                    sortList.sortBy { element ->
                        element.myCoinsBuyingAverage / element.currentPrice
                    }
                }

                else -> {
                    sortList.sortBy { element ->
                        element.myCoinKoreanName
                    }
                }
            }

            sortList.forEachIndexed { index, userHoldCoinDTO ->
                _userHoldCoinDtoList[index] = userHoldCoinDTO
            }

            _userHoldCoinDtoList.forEachIndexed { index, item ->
                userHoldCoinDtoListPositionHashMap[item.market] = index
            }

            _isPortfolioSocketRunning.value = true
        }
    }

    fun hideBottomSheet() {
        showRemoveCoinDialog.value = false
    }

    fun updateRemoveCoinCheckState(index: Int = 0) { //-1 은 selectAll
        if (index == -1) {
            _removeCoinCheckedList.replaceAll { true }
            return
        }

        if (index == -2) {
            _removeCoinCheckedList.replaceAll { false }
            return
        }

        _removeCoinCheckedList[index] = !_removeCoinCheckedList[index]
    }

    fun findWrongCoin() {
        //TODO TEST해야함 꼭
        viewModelScope.launch {
            _removeCoinInfo.clear()
            _removeCoinCheckedList.clear()

            executeUseCase<List<UpbitMarketCodeRes>>(
                target = upbitUseCase.getMarketCode(),
                onLoading = {

                },
                onComplete = {
                    val marketAll = it.map { it.market }.associateBy { it }

                    myCoinList.forEach {
                        val reason = when {
                            !marketAll.containsKey(it?.market) -> {
                                "거래를 지원하지 않는 코인입니다."
                            }

                            it?.quantity == 0.0 -> {
                                "보유 수량이 0입니다."
                            }

                            it?.purchasePrice == 0.0 -> {
                                "매수 가격이 0입니다."
                            }

                            it?.quantity == Double.POSITIVE_INFINITY
                                    || it?.quantity == Double.NEGATIVE_INFINITY -> {
                                "수량이 무한대 입니다."
                            }

                            else -> {
                                ""
                            }
                        }

                        if (reason.isNotEmpty()) {
                            _removeCoinInfo.add(it!!.market to reason)
                        }
                    }
//                    _removeCoinInfo.add("KRW-BTC" to "보유 수량이 0입니다.")
//                    _removeCoinInfo.add("KRW-BTC" to "매수가격이 0입니다.")
//                    _removeCoinInfo.add("KRW-BTC" to "수량이 무한대 입니다.")
//                    _removeCoinInfo.add("KRW-BTC" to "수량이 무한대 입니다.")
//                    _removeCoinInfo.add("KRW-BTC" to "거래를 지원하지 않는 코인 입니다.")
//                    _removeCoinInfo.add("KRW-BTC" to "거래를 지원하지 않는 코인 입니다.")
//                    _removeCoinInfo.add("KRW-BTC" to "거래를 지원하지 않는 코인 입니다.")
//                    _removeCoinInfo.add("KRW-BTC" to "거래를 지원하지 않는 코인 입니다.")
//                    _removeCoinInfo.add("KRW-BTC" to "거래를 지원하지 않는 코인 입니다.")
//                    _removeCoinInfo.add("KRW-BTC" to "거래를 지원하지 않는 코인 입니다.")
//                    _removeCoinInfo.add("KRW-BTC" to "거래를 지원하지 않는 코인 입니다.")
//                    _removeCoinInfo.add("KRW-BTC" to "거래를 지원하지 않는 코인 입니다.")

                    _removeCoinCheckedList.addAll(List(removeCoinInfo.size) { false })

                    showRemoveCoinDialog.value = true
                }
            )
        }
    }

    fun editUserHoldCoin() {
        _isPortfolioSocketRunning.value = false

        viewModelScope.launch(ioDispatcher) {
            removeCoinInfo.forEachIndexed { index, pair ->
                if (_removeCoinCheckedList[index]) {
                    upbitUseCase.removeCoin(pair.first)
                }
            }

            myCoinList.clear()
            compareUserHoldCoinList()
        }
    }

    fun earnReward() {
        viewModelScope.launch(ioDispatcher) {
            val userDao = upbitUseCase.getUserDao()
            if (userDao.all == null) {
                userDao.insert()
            } else {
                userDao.updatePlusMoney(10_000_000)
            }
        }
    }

    fun errorReward() {
        viewModelScope.launch(ioDispatcher) {
            val userDao = upbitUseCase.getUserDao()
            if (userDao.all == null) {
                userDao.errorInsert()
            } else {
                userDao.updatePlusMoney(1_000_000)
            }
        }
    }

    private suspend fun collectTicker() {
        upbitUseCase.observeTickerResponse().onEach { result ->
            _tickerResponse.update {
                result
            }
        }.collect { upbitSocketTickerRes ->
            if (!isPortfolioSocketRunning.value) return@collect

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
                        myCoinsSymbol = tempDto.myCoinsSymbol,
                        initialConstant = Utils.extractInitials(tempDto.myCoinKoreanName)
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