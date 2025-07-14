package org.jeonfeel.moeuibit2.ui.main.portfolio.root_exchange

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import com.orhanobut.logger.Logger
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import org.jeonfeel.moeuibit2.constants.BTC_MARKET
import org.jeonfeel.moeuibit2.constants.EXCHANGE_UPBIT
import org.jeonfeel.moeuibit2.constants.KRW_SYMBOL_PREFIX
import org.jeonfeel.moeuibit2.data.local.room.entity.MyCoin
import org.jeonfeel.moeuibit2.data.network.retrofit.request.upbit.GetUpbitMarketTickerReq
import org.jeonfeel.moeuibit2.data.network.retrofit.response.upbit.UpbitMarketCodeRes
import org.jeonfeel.moeuibit2.data.usecase.UpbitPortfolioUsecase
import org.jeonfeel.moeuibit2.ui.common.ResultState
import org.jeonfeel.moeuibit2.ui.main.portfolio.PortfolioViewModel.Companion.SORT_DEFAULT
import org.jeonfeel.moeuibit2.ui.main.portfolio.PortfolioViewModel.Companion.SORT_NAME_ASC
import org.jeonfeel.moeuibit2.ui.main.portfolio.PortfolioViewModel.Companion.SORT_NAME_DEC
import org.jeonfeel.moeuibit2.ui.main.portfolio.PortfolioViewModel.Companion.SORT_NONE
import org.jeonfeel.moeuibit2.ui.main.portfolio.PortfolioViewModel.Companion.SORT_RATE_ASC
import org.jeonfeel.moeuibit2.ui.main.portfolio.PortfolioViewModel.Companion.SORT_RATE_DEC
import org.jeonfeel.moeuibit2.ui.main.portfolio.dto.UserHoldCoinDTO
import org.jeonfeel.moeuibit2.utils.Utils
import org.jeonfeel.moeuibit2.utils.calculator.Calculator
import org.jeonfeel.moeuibit2.utils.ext.mapToMarketCodesRequest
import org.jeonfeel.moeuibit2.utils.manager.CacheManager
import java.math.BigDecimal
import javax.inject.Inject

class UpBitPortfolio @Inject constructor(
    private val upbitPortfolioUseCase: UpbitPortfolioUsecase,
    private val cacheManager: CacheManager
) {

    private val _koreanCoinNameMap = mutableMapOf<String, String>()
    private val _engCoinNameMap = mutableMapOf<String, String>()

    private val _isPortfolioSocketRunning = mutableStateOf(true)
    val isPortfolioSocketRunning: State<Boolean> get() = _isPortfolioSocketRunning

    private val _userSeedMoney = mutableDoubleStateOf(0.0)
    val userSeedMoney: State<Double> get() = _userSeedMoney

    private val _marketCodeRes = mutableMapOf<String, UpbitMarketCodeRes>()
    val marketCodeRes: Map<String, UpbitMarketCodeRes> get() = _marketCodeRes

    private val _totalPurchase = mutableStateOf(BigDecimal(0.0))
    val totalPurchase: State<BigDecimal> get() = _totalPurchase

    private val _btcTradePrice = mutableDoubleStateOf(0.0)
    val btcTradePrice: State<Double> get() = _btcTradePrice

    private val _totalValuedAssets = mutableStateOf(BigDecimal(0.0))
    val totalValuedAssets: State<BigDecimal> get() = _totalValuedAssets

    private val _userHoldCoinDtoList = ArrayList<UserHoldCoinDTO>()
    private val _userHoldCoinDtoListState: MutableState<List<UserHoldCoinDTO>> =
        mutableStateOf(emptyList())
    val userHoldCoinDtoListState: State<List<UserHoldCoinDTO>> get() = _userHoldCoinDtoListState

    private val _portfolioOrderState = mutableIntStateOf(SORT_NONE)
    val portfolioOrderState: State<Int> get() = _portfolioOrderState

    private val _removeCoinInfo = SnapshotStateList<Pair<String, String>>()
    val removeCoinInfo: List<Pair<String, String>> get() = _removeCoinInfo // market reason

    private val _removeCoinCheckedList = SnapshotStateList<Boolean>()
    val removeCoinCheckedState: List<Boolean> get() = _removeCoinCheckedList

    private val _showRemoveCoinDialog = mutableStateOf(false)
    val showRemoveCoinDialogState: State<Boolean> get() = _showRemoveCoinDialog

    private val userHoldCoinsMarkets = StringBuilder()
    private var myCoinList = ArrayList<MyCoin?>()
    private val userHoldCoinDtoListPositionHashMap = HashMap<String, Int>()
    private val myCoinHashMap = HashMap<String, MyCoin>()
    val loading = MutableStateFlow<Boolean>(false)

    suspend fun onStart() {
        getCoinName()
        getMarketCodeRes()
        compareUserHoldCoinList()
    }

    suspend fun onStop() {
        upbitPortfolioUseCase.onStop()
    }

    fun portfolioStop() {
        _isPortfolioSocketRunning.value = false
    }

    private suspend fun getCoinName() {
        if (_koreanCoinNameMap.isEmpty() || _engCoinNameMap.isEmpty()) {
            _koreanCoinNameMap.putAll(cacheManager.readKoreanCoinNameMap())
            _engCoinNameMap.putAll(cacheManager.readEnglishCoinNameMap())
        }
    }

    private suspend fun getMarketCodeRes() {
        if (_marketCodeRes.isNotEmpty()) return

        upbitPortfolioUseCase.fetchMarketCode().collect { res ->
            when (res) {
                is ResultState.Success -> {
                    res.data.forEach { marketCodeRes ->
                        _marketCodeRes[marketCodeRes.market] = marketCodeRes
                    }
                }

                is ResultState.Error -> {

                }

                is ResultState.Loading -> {

                }
            }
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

    private fun setETC() {
        if (userHoldCoinDtoListPositionHashMap[BTC_MARKET] == null) {
            userHoldCoinsMarkets.append(BTC_MARKET)
        } else {
            userHoldCoinsMarkets.deleteCharAt(userHoldCoinsMarkets.lastIndex)
        }
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

    private suspend fun getBeforeCoinMapAndNewCoinMap(): Pair<Map<String?, MyCoin?>, Map<String?, MyCoin?>> {
        val beforeMyCoinMap = myCoinList.associateBy { it?.market }
        getUserHoldCoins()
        val newMyCoinMap = myCoinList.associateBy { it?.market }
        return beforeMyCoinMap to newMyCoinMap
    }

    private fun resetPortfolio() {
        _isPortfolioSocketRunning.value = false
        _totalPurchase.value = BigDecimal(0.0)
        _totalValuedAssets.value = BigDecimal(0.0)
        _portfolioOrderState.intValue = SORT_DEFAULT
        userHoldCoinsMarkets.clear()
        userHoldCoinDtoListPositionHashMap.clear()
        _userHoldCoinDtoList.clear()
        myCoinList.clear()
        _userHoldCoinDtoListState.value = emptyList()
    }

    suspend fun findWrongCoin() {
        upbitPortfolioUseCase.fetchMarketCode().collect { res ->
            when (res) {
                is ResultState.Success -> {
                    val data = res.data

                    _removeCoinInfo.clear()
                    _removeCoinCheckedList.clear()

                    data.associateBy { it.market }.forEach { (key, value) ->
                        _marketCodeRes[key] = value
                    }

                    val marketAll = data.map { it.market }.associateBy { it }

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
                    _showRemoveCoinDialog.value = true
                }

                is ResultState.Error -> {

                }

                is ResultState.Loading -> {

                }
            }
        }
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

        _userHoldCoinDtoListState.value = _userHoldCoinDtoList.toList()
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

        upbitPortfolioUseCase.fetchMarketTicker(getUpbitMarketTickerReq = req)
            .collect { res ->
                when (res) {
                    is ResultState.Success -> {
                        val result = res.data

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
                                        warning = marketCodeRes[it.market]?.marketEvent?.warning
                                            ?: false,
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
                                _userHoldCoinDtoListState.value = _userHoldCoinDtoList.toList()
                                _totalValuedAssets.value = _userHoldCoinDtoListState.value.sumOf {
                                    if (it.market.startsWith(KRW_SYMBOL_PREFIX)) {
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

                    is ResultState.Error -> {

                    }

                    is ResultState.Loading -> {

                    }
                }
            }
    }

    private suspend fun getUserSeedMoney() {
        _userSeedMoney.doubleValue = upbitPortfolioUseCase.getUserSeedMoney()
    }

    private suspend fun getUserHoldCoins() {
        val tempList = arrayListOf<MyCoin?>()
        tempList.addAll(upbitPortfolioUseCase.getMyCoins())
        myCoinList = tempList
    }

    suspend fun sortUserHoldCoin(sortStandard: Int) {
        _isPortfolioSocketRunning.value = false
        _portfolioOrderState.intValue = sortStandard

        val sortList = arrayListOf<UserHoldCoinDTO>().apply { addAll(_userHoldCoinDtoList) }

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

        _userHoldCoinDtoListState.value = _userHoldCoinDtoList.toList()

        _isPortfolioSocketRunning.value = true

    }

    fun hideBottomSheet() {
        _showRemoveCoinDialog.value = false
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

    suspend fun editUserHoldCoin() {
        _isPortfolioSocketRunning.value = false
        removeCoinInfo.forEachIndexed { index, pair ->
            if (_removeCoinCheckedList[index]) {
                upbitPortfolioUseCase.removeCoin(pair.first)
            }
        }

        myCoinList.clear()
        compareUserHoldCoinList()
    }

    suspend fun earnReward() {
        val userDao = upbitPortfolioUseCase.getUserDao()

        if (userDao.getUserByExchange(EXCHANGE_UPBIT) == null) {
            userDao.insert(EXCHANGE_UPBIT, 10_000_000.0)
        } else {
            userDao.updatePlusMoney(exchange = EXCHANGE_UPBIT, 10_000_000.0)
        }

        _userSeedMoney.doubleValue = userDao.getUserByExchange(EXCHANGE_UPBIT)?.krw ?: 0.0
    }

    suspend fun errorReward() {
        val userDao = upbitPortfolioUseCase.getUserDao()

        if (userDao.getUserByExchange(EXCHANGE_UPBIT) == null) {
            userDao.insert(EXCHANGE_UPBIT, 1_000_000.0)
        } else {
            userDao.updatePlusMoney(EXCHANGE_UPBIT, 1_000_000.0)
        }

        _userSeedMoney.doubleValue = userDao.getUserByExchange(EXCHANGE_UPBIT)?.krw ?: 0.0
    }

    suspend fun collectTicker() {
        upbitPortfolioUseCase.observeTickerResponse().collect { upbitSocketTickerRes ->
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
                    _userHoldCoinDtoListState.value = _userHoldCoinDtoList.toList()
                    _totalValuedAssets.value = _userHoldCoinDtoListState.value.sumOf {
                        if (it.market.startsWith(KRW_SYMBOL_PREFIX)) {
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
}