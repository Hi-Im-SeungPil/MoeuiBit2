package org.jeonfeel.moeuibit2.ui.coindetail.newScreen

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.viewModelScope
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet
import com.orhanobut.logger.Logger
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.jeonfeel.moeuibit2.data.local.room.entity.MyCoin
import org.jeonfeel.moeuibit2.data.network.retrofit.model.upbit.CommonExchangeModel
import org.jeonfeel.moeuibit2.data.network.retrofit.model.upbit.OrderBookModel
import org.jeonfeel.moeuibit2.data.network.retrofit.request.upbit.GetUpbitMarketTickerReq
import org.jeonfeel.moeuibit2.data.network.retrofit.response.upbit.GetUpbitMarketTickerRes
import org.jeonfeel.moeuibit2.data.network.websocket.model.upbit.UpbitSocketTickerRes
import org.jeonfeel.moeuibit2.data.usecase.UpbitUseCase
import org.jeonfeel.moeuibit2.ui.base.BaseViewModel
import org.jeonfeel.moeuibit2.ui.coindetail.chart.utils.upbit.Chart
import org.jeonfeel.moeuibit2.ui.coindetail.newScreen.order.UpbitCoinOrder
import org.jeonfeel.moeuibit2.ui.main.exchange.ExchangeViewModel.Companion.ROOT_EXCHANGE_BITTHUMB
import org.jeonfeel.moeuibit2.ui.main.exchange.ExchangeViewModel.Companion.ROOT_EXCHANGE_UPBIT
import org.jeonfeel.moeuibit2.utils.Utils.coinOrderIsKrwMarket
import org.jeonfeel.moeuibit2.utils.manager.CacheManager
import org.jeonfeel.moeuibit2.utils.manager.PreferencesManager
import java.math.BigDecimal
import javax.inject.Inject

@HiltViewModel
class NewCoinDetailViewModel @Inject constructor(
    private val preferenceManager: PreferencesManager,
    private val upbitCoinOrder: UpbitCoinOrder,
    private val upbitUseCase: UpbitUseCase,
    private val cacheManager: CacheManager,
    val chart: Chart,
) : BaseViewModel(preferenceManager) {
    private val _coinTicker = mutableStateOf<CommonExchangeModel?>(null)
    val coinTicker: State<CommonExchangeModel?> get() = _coinTicker
    private val _koreanCoinName = mutableStateOf("")
    val koreanCoinName: State<String> get() = _koreanCoinName
    private val _orderBookIndication = mutableStateOf("quantity")
    val orderBookIndication: State<String> get() = _orderBookIndication
    private val _userSeedMoney = mutableLongStateOf(0L)
    val userSeedMoney: State<Long> get() = _userSeedMoney

    private val _tickerResponse = MutableStateFlow<UpbitSocketTickerRes?>(null)

    fun init(market: String) {
        getOrderBookIndication()
        rootExchangeCoroutineBranch(
            upbitAction = {
                _userSeedMoney.longValue = getUserSeedMoney()
                _koreanCoinName.value =
                    cacheManager.readKoreanCoinNameMap()[market.substring(4)] ?: ""
                requestCoinTicker(market)
                requestSubscribeTicker(market)
                collectTicker()
            },
            bitthumbAction = {

            }
        )
    }

    private suspend fun requestCoinTicker(market: String) {
        val getUpbitTickerReq = GetUpbitMarketTickerReq(
            market.coinOrderIsKrwMarket()
        )
        executeUseCase<GetUpbitMarketTickerRes>(
            target = upbitUseCase.getMarketTicker(getUpbitTickerReq),
            onComplete = { ticker ->
                _coinTicker.value = ticker.mapTo()
            }
        )
    }

    private suspend fun requestSubscribeTicker(market: String) {
        upbitUseCase.requestSubscribeTicker(marketCodes = listOf(market))
    }

    /**
     * coin Order 화면 초기화
     */
    fun initCoinOrder(market: String) {
        rootExchangeCoroutineBranch(
            upbitAction = {
                upbitCoinOrder.initCoinOrder(market)
            },
            bitthumbAction = {
            },
            dispatcher = Dispatchers.IO
        )
    }

    /**
     * 코인 주문 화면 pause
     */
    fun coinOrderScreenOnPause() {
        rootExchangeCoroutineBranch(
            upbitAction = {
                upbitCoinOrder.onPause()
            },
            bitthumbAction = {

            }
        )
    }

    fun coinOrderScreenOnResume(market: String) {
        rootExchangeCoroutineBranch(
            upbitAction = {
                upbitCoinOrder.onResume(market)
            },
            bitthumbAction = {

            }
        )
    }

    /**
     * orderBookList 받아옴
     */
    fun getOrderBookList(): List<OrderBookModel> {
        return when (rootExchange) {
            ROOT_EXCHANGE_UPBIT -> {
                upbitCoinOrder.orderBookList
            }

            ROOT_EXCHANGE_BITTHUMB -> {
                upbitCoinOrder.orderBookList
            }

            else -> {
                upbitCoinOrder.orderBookList
            }
        }
    }

    fun getMaxOrderBookSize(): State<Double> {
        return when (rootExchange) {
            ROOT_EXCHANGE_UPBIT -> {
                upbitCoinOrder.maxOrderBookSize
            }

            ROOT_EXCHANGE_BITTHUMB -> {
                upbitCoinOrder.maxOrderBookSize
            }

            else -> {
                upbitCoinOrder.maxOrderBookSize
            }
        }
    }

    private fun getOrderBookIndication() {
        viewModelScope.launch {
            preferenceManager.getString("orderBookIndication").collect {
                _orderBookIndication.value = it
            }
        }
    }

    fun changeOrderBookIndication() {
        if (_orderBookIndication.value == "quantity") {
            _orderBookIndication.value = "totalPrice"
        } else {
            _orderBookIndication.value = "quantity"
        }
    }

    fun saveOrderBookIndication() {
        viewModelScope.launch {
            preferenceManager.setValue(
                "orderBookIndication",
                _orderBookIndication.value
            )
        }
    }

    fun onPause() {
        viewModelScope.launch {
            requestSubscribeTicker("")
        }
    }

    fun onResume(market: String) {
        viewModelScope.launch {
            requestSubscribeTicker(market)
        }
    }

    /**
     * 사용자 시드머니 받아옴
     */
    fun getUserSeedMoney(): Long {
        return when (rootExchange) {
            ROOT_EXCHANGE_UPBIT -> {
                upbitCoinOrder.userSeedMoney
            }

            ROOT_EXCHANGE_BITTHUMB -> {
                upbitCoinOrder.userSeedMoney
            }

            else -> {
                0L
            }
        }
    }

    fun requestBid(
        market: String,
        quantity: Double,
        price: BigDecimal,
        totalPrice: Long
    ) {
        rootExchangeCoroutineBranch(
            upbitAction = {
                upbitCoinOrder.requestBid(
                    market = market,
                    totalPrice = totalPrice,
                    quantity = quantity,
                    coinPrice = price,
                    koreanName = koreanCoinName.value
                )
            },
            bitthumbAction = {

            },
            dispatcher = Dispatchers.IO
        )
    }

    fun requestAsk(
        market:String,
        quantity: Double,
        totalPrice: Long,
        price: BigDecimal,
    ) {
        rootExchangeCoroutineBranch(
            upbitAction = {
                upbitCoinOrder.requestAsk(
                    market = market,
                    totalPrice = totalPrice,
                    quantity = quantity,
                    coinPrice = price,
                )
            },
            bitthumbAction = {

            },
            dispatcher = Dispatchers.IO
        )
    }

    /**
     * 사용자 코인 받아옴
     */
    fun getUserCoin(): MyCoin {
        return when (rootExchange) {
            ROOT_EXCHANGE_UPBIT -> {
                upbitCoinOrder.userCoin
            }

            ROOT_EXCHANGE_BITTHUMB -> {
                upbitCoinOrder.userCoin
            }

            else -> {
                upbitCoinOrder.userCoin
            }
        }
    }

    /**
     * BTC마켓 일 때 필요한데, 사용자 BTC 코인 받아옴
     */
    fun getUserBtcCoin(): MyCoin {
        return when (rootExchange) {
            ROOT_EXCHANGE_UPBIT -> {
                upbitCoinOrder.userBtcCoin
            }

            ROOT_EXCHANGE_BITTHUMB -> {
                upbitCoinOrder.userBtcCoin
            }

            else -> {
                upbitCoinOrder.userBtcCoin
            }
        }
    }

    // 차트 화면
    fun requestOldData(
        positiveBarDataSet: IBarDataSet, negativeBarDataSet: IBarDataSet, candleXMin: Float
    ) {
        viewModelScope.launch {
            if (rootExchange == ROOT_EXCHANGE_UPBIT) {
                chart.requestOldData(
                    positiveBarDataSet = positiveBarDataSet,
                    negativeBarDataSet = negativeBarDataSet,
                    candleXMin = candleXMin
                )
            }
        }
    }

    fun requestChartData(market: String) {
        viewModelScope.launch {
            if (rootExchange == ROOT_EXCHANGE_UPBIT) {
                chart.requestUpbitChartData(market = market)
            } else if (rootExchange == ROOT_EXCHANGE_BITTHUMB) {
                Logger.e("requestChartData")
                chart.setBitthumbChart()
//                chart.requestBitthumbChartData(market = market)
            }
        }
    }

    private suspend fun collectTicker() {
        upbitUseCase.observeTickerResponse().onEach { result ->
            _tickerResponse.update {
                result
            }
        }.collect { upbitSocketTickerRes ->
            try {
                _coinTicker.value = upbitSocketTickerRes.mapTo()
            } catch (e: Exception) {
                Logger.e(e.message.toString())
            }
        }
    }
}