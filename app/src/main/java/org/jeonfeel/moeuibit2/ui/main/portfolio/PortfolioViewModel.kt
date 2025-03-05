package org.jeonfeel.moeuibit2.ui.main.portfolio

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.viewModelScope
import com.orhanobut.logger.Logger
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.jeonfeel.moeuibit2.constants.BTC_MARKET
import org.jeonfeel.moeuibit2.constants.UPBIT_KRW_SYMBOL_PREFIX
import org.jeonfeel.moeuibit2.constants.defaultDispatcher
import org.jeonfeel.moeuibit2.constants.ioDispatcher
import org.jeonfeel.moeuibit2.data.local.preferences.PreferencesManager
import org.jeonfeel.moeuibit2.data.local.room.entity.MyCoin
import org.jeonfeel.moeuibit2.data.network.retrofit.request.upbit.GetUpbitMarketTickerReq
import org.jeonfeel.moeuibit2.data.network.retrofit.response.upbit.GetUpbitMarketTickerRes
import org.jeonfeel.moeuibit2.data.network.retrofit.response.upbit.UpbitMarketCodeRes
import org.jeonfeel.moeuibit2.data.network.websocket.model.upbit.UpbitSocketTickerRes
import org.jeonfeel.moeuibit2.data.usecase.UpbitPortfolioUsecase
import org.jeonfeel.moeuibit2.ui.base.BaseViewModel
import org.jeonfeel.moeuibit2.ui.main.portfolio.dto.UserHoldCoinDTO
import org.jeonfeel.moeuibit2.utils.Utils
import org.jeonfeel.moeuibit2.utils.calculator.Calculator
import org.jeonfeel.moeuibit2.utils.ext.mapToMarketCodesRequest
import org.jeonfeel.moeuibit2.utils.manager.AdMobManager
import org.jeonfeel.moeuibit2.utils.manager.CacheManager
import java.math.BigDecimal
import javax.inject.Inject
import kotlin.collections.set

@HiltViewModel
class PortfolioViewModel @Inject constructor(
    private val preferenceManager: PreferencesManager,
    private val upbitPortfolioUseCase: UpbitPortfolioUsecase,
    private val cacheManager: CacheManager,
    val adMobManager: AdMobManager,
) : BaseViewModel(preferenceManager) {

    private val _userSeedMoney = mutableLongStateOf(0L)
    val userSeedMoney: State<Long> get() = _userSeedMoney

    private val _totalPurchase = mutableStateOf(BigDecimal(0.0))
    val totalPurchase: State<BigDecimal> get() = _totalPurchase

    private val _userHoldCoinDtoList = ArrayList<UserHoldCoinDTO>()
    val userHoldCoinDtoList: MutableState<List<UserHoldCoinDTO>> = mutableStateOf(emptyList())

    private val _marketCodeRes = mutableMapOf<String, UpbitMarketCodeRes>()
    val marketCodeRes: Map<String, UpbitMarketCodeRes> get() = _marketCodeRes

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
    private var myCoinList = ArrayList<MyCoin?>()

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
    private var collectTickerJob: Job? = null

    val loading = MutableStateFlow<Boolean>(true)

    fun onStart() {
        realTimeUpdateJob?.cancel()
        collectTickerJob?.cancel()

        realTimeUpdateJob = viewModelScope.launch(ioDispatcher) {
            getCoinName()
            getMarketCodeRes()
            compareUserHoldCoinList()
        }.also { it.start() }

        collectTickerJob = viewModelScope.launch {
            collectTicker()
        }.also { it.start() }
    }

    fun onStop() {
        viewModelScope.launch {
            _isPortfolioSocketRunning.value = false
            realTimeUpdateJob?.cancel()
            collectTickerJob?.cancel()
            realTimeUpdateJob = null
            collectTickerJob = null
            upbitPortfolioUseCase.onStop()
        }
    }

    private suspend fun compareUserHoldCoinList() {
        _isPortfolioSocketRunning.value = false

        if (myCoinList.isNotEmpty()) {
            val (beforeMyCoinMap, newMyCoinMap) = getBeforeCoinMapAndNewCoinMap()
            getUserSeedMoney()

            if (myCoinList.isEmpty()) {
                resetPortfolio()
            } else {
                modifyCoin(beforeMyCoinMap, newMyCoinMap)
                updateCoin()
                requestTicker()
                setETC()
                _isPortfolioSocketRunning.value = true
            }
        } else {
            loading.update { true }
            resetPortfolio()
            getUserSeedMoney()
            getUserHoldCoins()
            parseMyCoinToUserHoldCoin()
            requestTicker()
            setETC()
            loading.update { false }
            _isPortfolioSocketRunning.value = true
        }

        upbitPortfolioUseCase.onStart(userHoldCoinsMarkets.split(","))
    }

    private fun modifyCoin(
        beforeMyCoinMap: Map<String?, MyCoin?>,
        newMyCoinMap: Map<String?, MyCoin?>,
    ) {
        val removeCoinListMarket = beforeMyCoinMap.keys - newMyCoinMap.keys
        val addCoinListMarket = newMyCoinMap.keys - beforeMyCoinMap.keys

        removeCoinListMarket.forEach { removeCoin ->
            myCoinList.remove(beforeMyCoinMap[removeCoin])
            myCoinHashMap.remove(removeCoin)
            userHoldCoinDtoListPositionHashMap.remove(removeCoin)
            _userHoldCoinDtoList.removeAll { it.market == removeCoin }
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
                this.initialConstant = Utils.extractInitials(myCoinKoreanName)
            }
            _userHoldCoinDtoList.add(userHoldCoinDTO)
            myCoinHashMap[myCoin.market] = myCoin
        }

        _userHoldCoinDtoList.forEachIndexed { index, userHoldCoin ->
            userHoldCoin.let {
                userHoldCoinDtoListPositionHashMap[it.market] = index
            }
        }

        userHoldCoinDtoList.value = _userHoldCoinDtoList.toList()
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
                    this.initialConstant = Utils.extractInitials(this.myCoinKoreanName)
                }

                _userHoldCoinDtoList[it] =
                    UserHoldCoinDTO(
                        currentPrice = beforeUserHoldCoinDTO.currentPrice,
                        openingPrice = beforeUserHoldCoinDTO.openingPrice,
                        warning = marketCodeRes[beforeUserHoldCoinDTO.market]?.marketEvent?.warning
                            ?: false,
                        caution = marketCodeRes[beforeUserHoldCoinDTO.market]?.marketEvent?.caution,
                        market = userHoldCoinDTO.market,
                        isFavorite = 0,
                        myCoinKoreanName = userHoldCoinDTO.myCoinKoreanName,
                        myCoinEngName = userHoldCoinDTO.myCoinEngName,
                        myCoinsQuantity = userHoldCoinDTO.myCoinsQuantity,
                        myCoinsBuyingAverage = userHoldCoinDTO.myCoinsBuyingAverage,
                        purchaseAverageBtcPrice = userHoldCoinDTO.purchaseAverageBtcPrice,
                        myCoinsSymbol = userHoldCoinDTO.myCoinsSymbol,
                        initialConstant = userHoldCoinDTO.initialConstant
                    )
            }

            tempTotalPurchase = tempTotalPurchase.plus(Calculator.getTotalPurchase(myCoin!!))
        }

        _totalPurchase.value = tempTotalPurchase

        userHoldCoinsMarkets.clear()
        myCoinList.forEach {
            userHoldCoinsMarkets.append("${it?.market},")
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
        userHoldCoinDtoList.value = emptyList()
    }

    private suspend fun getUserSeedMoney() {
        _userSeedMoney.longValue = upbitPortfolioUseCase.getUserSeedMoney()
    }

    private suspend fun getUserHoldCoins() {
        val tempList = arrayListOf<MyCoin?>()
        tempList.addAll(upbitPortfolioUseCase.getMyCoins())
        myCoinList = tempList
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
                this.initialConstant = Utils.extractInitials(myCoinKoreanName)
            }
            _userHoldCoinDtoList.add(userHoldCoinDTO)
            myCoinHashMap[myCoin.market] = myCoin
            _totalPurchase.value = _totalPurchase.value.plus(Calculator.getTotalPurchase(myCoin))
            userHoldCoinDtoListPositionHashMap[myCoin.market] = index
        }

        val marketCodes = myCoinList
            .map {
                if (marketCodeRes[it?.market] == null) return@map null
                it?.market ?: ""
            }.filterNotNull().mapToMarketCodesRequest()
        userHoldCoinsMarkets.append(marketCodes).append(",")
    }

    private suspend fun requestTicker() {
        val marketCodes = myCoinList
            .map {
                if (marketCodeRes[it?.market] == null) return@map null

                it?.market ?: ""
            }
            .filterNotNull().mapToMarketCodesRequest()
            .plus(",KRW-BTC")

        val req = GetUpbitMarketTickerReq(
            marketCodes = marketCodes
        )
        executeUseCase<List<GetUpbitMarketTickerRes>>(
            target = upbitPortfolioUseCase.getMarketTicker(
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
                        val tempDto = _userHoldCoinDtoList[position]
                        _userHoldCoinDtoList[position] =
                            UserHoldCoinDTO(
                                currentPrice = it.tradePrice,
                                openingPrice = it.prevClosingPrice,
                                warning = marketCodeRes[it.market]?.marketEvent?.warning ?: false,
                                market = it.market,
                                isFavorite = 0,
                                myCoinKoreanName = tempDto.myCoinKoreanName,
                                myCoinEngName = tempDto.myCoinEngName,
                                myCoinsQuantity = tempDto.myCoinsQuantity,
                                myCoinsBuyingAverage = tempDto.myCoinsBuyingAverage,
                                purchaseAverageBtcPrice = tempDto.purchaseAverageBtcPrice,
                                myCoinsSymbol = tempDto.myCoinsSymbol,
                                initialConstant = Utils.extractInitials(tempDto.myCoinKoreanName),
                                caution = marketCodeRes[it.market]?.marketEvent?.caution
                            )
                    }
                }.fold(
                    onSuccess = {
                        userHoldCoinDtoList.value = _userHoldCoinDtoList.toList()
                        _totalValuedAssets.value = userHoldCoinDtoList.value.sumOf {
                            if (it.market.startsWith(UPBIT_KRW_SYMBOL_PREFIX)) {
                                it.currentPrice.toBigDecimal()
                                    .multiply(it.myCoinsQuantity.toBigDecimal())
                            } else {
                                it.currentPrice.toBigDecimal()
                                    .multiply(it.myCoinsQuantity.toBigDecimal())
                                    .multiply(btcTradePrice.value.toBigDecimal())
                            }
                        }
                        loading.update { false }
                    },
                    onFailure = {
                        Logger.e(it.message.toString())
                        loading.update { false }
                    }
                )
            }
        )
    }

    private suspend fun setETC() {
        Logger.e(userHoldCoinsMarkets.toString())

        if (userHoldCoinDtoListPositionHashMap[BTC_MARKET] == null) {
            userHoldCoinsMarkets.append(BTC_MARKET)
        } else {
            userHoldCoinsMarkets.deleteCharAt(userHoldCoinsMarkets.lastIndex)
        }

        Logger.e(userHoldCoinsMarkets.toString())
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

            userHoldCoinDtoList.value = _userHoldCoinDtoList.toList()

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

    private suspend fun getMarketCodeRes() {
        if (_marketCodeRes.isNotEmpty()) return

        executeUseCase<List<UpbitMarketCodeRes>>(
            target = upbitPortfolioUseCase.getMarketCode(),
            onComplete = {
                it.associateBy { it.market }.forEach { (key, value) ->
                    _marketCodeRes[key] = value
                }
            }
        )
    }

    fun findWrongCoin() {
        //TODO TEST해야함 꼭
        viewModelScope.launch {
            executeUseCase<List<UpbitMarketCodeRes>>(
                target = upbitPortfolioUseCase.getMarketCode(),
                onLoading = {

                },
                onComplete = {
                    _removeCoinInfo.clear()
                    _removeCoinCheckedList.clear()

                    it.associateBy { it.market }.forEach { (key, value) ->
                        _marketCodeRes[key] = value
                    }

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
                    upbitPortfolioUseCase.removeCoin(pair.first)
                }
            }

            myCoinList.clear()
            compareUserHoldCoinList()
        }
    }

    fun earnReward() {
        viewModelScope.launch(ioDispatcher) {
            val userDao = upbitPortfolioUseCase.getUserDao()
            if (userDao.all == null) {
                userDao.insert()
            } else {
                userDao.updatePlusMoney(10_000_000)
            }
        }
    }

    fun errorReward() {
        viewModelScope.launch(ioDispatcher) {
            val userDao = upbitPortfolioUseCase.getUserDao()
            if (userDao.all == null) {
                userDao.errorInsert()
            } else {
                userDao.updatePlusMoney(1_000_000)
            }
        }
    }

    private suspend fun collectTicker() {
        upbitPortfolioUseCase.observeTickerResponse().onEach { upbitSocketTickerRes ->
            _tickerResponse.update {
                upbitSocketTickerRes
            }
        }.collect { upbitSocketTickerRes ->
            if (upbitSocketTickerRes?.code?.startsWith("KRW-") == false) {
                Logger.e("portfolio message!! ${upbitSocketTickerRes.code}")
            }

            if (!isPortfolioSocketRunning.value || upbitSocketTickerRes == null) return@collect

            runCatching {
                if (upbitSocketTickerRes.code == BTC_MARKET) {
                    _btcTradePrice.doubleValue = upbitSocketTickerRes.tradePrice
                    if (userHoldCoinDtoListPositionHashMap[BTC_MARKET] == null) return@collect
                }

                if (userHoldCoinDtoListPositionHashMap[upbitSocketTickerRes.code] == null) return@collect

                val position =
                    userHoldCoinDtoListPositionHashMap[upbitSocketTickerRes.code] ?: 0
                val tempDto = _userHoldCoinDtoList[position]

                _userHoldCoinDtoList[position] =
                    UserHoldCoinDTO(
                        currentPrice = upbitSocketTickerRes.tradePrice,
                        openingPrice = upbitSocketTickerRes.prevClosingPrice,
                        warning = marketCodeRes[upbitSocketTickerRes.code]?.marketEvent?.warning
                            ?: false,
                        caution = marketCodeRes[upbitSocketTickerRes.code]?.marketEvent?.caution,
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
                    userHoldCoinDtoList.value = _userHoldCoinDtoList.toList()
                    _totalValuedAssets.value = userHoldCoinDtoList.value.sumOf {
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