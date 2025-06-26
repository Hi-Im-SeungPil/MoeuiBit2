package org.jeonfeel.moeuibit2.ui.coindetail.order.coin_order

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import org.jeonfeel.moeuibit2.constants.BTC_SYMBOL_PREFIX
import org.jeonfeel.moeuibit2.constants.EXCHANGE_BITTHUMB
import org.jeonfeel.moeuibit2.data.local.room.entity.MyCoin
import org.jeonfeel.moeuibit2.data.local.room.entity.TransactionInfo
import org.jeonfeel.moeuibit2.data.network.retrofit.model.upbit.OrderBookModel
import org.jeonfeel.moeuibit2.data.network.websocket.model.upbit.UpbitSocketOrderBookRes
import org.jeonfeel.moeuibit2.data.usecase.BiThumbCoinOrderUseCase
import org.jeonfeel.moeuibit2.ui.common.ResultState
import org.jeonfeel.moeuibit2.utils.eighthDecimal
import org.jeonfeel.moeuibit2.utils.isKrwTradeCurrency
import java.math.BigDecimal
import javax.inject.Inject

class BithumbCoinOrder @Inject constructor(
    private val biThumbCoinOrderUseCase: BiThumbCoinOrderUseCase
) {
    private val _orderBookList = mutableStateListOf<OrderBookModel>()
    val orderBookList: List<OrderBookModel> get() = _orderBookList

    private val _userSeedMoney = mutableDoubleStateOf(0.0)
    val userSeedMoney: State<Double> get() = _userSeedMoney

    private val _userCoin = mutableStateOf(MyCoin())
    val userCoin: State<MyCoin> get() = _userCoin

    private val _userBtcCoin = mutableStateOf(MyCoin())
    val userBtcCoin: State<MyCoin> get() = _userBtcCoin

    private val _tickerResponse = MutableStateFlow<UpbitSocketOrderBookRes?>(null)
    private val _maxOrderBookSize = mutableDoubleStateOf(0.0)
    val maxOrderBookSize: State<Double> get() = _maxOrderBookSize

    private val _orderBookInitSuccess = mutableStateOf(false)
    val orderBookInitSuccess: State<Boolean> get() = _orderBookInitSuccess

    suspend fun initCoinOrder(market: String) {
        requestOrderBook(market)
        getUserSeedMoney()
        getUserCoin(market)
        getUserBtcCoin(market)
    }

    suspend fun onStart(market: String) {
        biThumbCoinOrderUseCase.onStart("\"$market\"")
        collectOrderBook()
    }

    suspend fun onStop() {
        biThumbCoinOrderUseCase.onStop()
    }

    private suspend fun requestOrderBook(market: String) {
        if (_orderBookList.isEmpty()) {
            biThumbCoinOrderUseCase.fetchOrderBook(market).collect { res ->
                when (res) {
                    is ResultState.Success -> {
                        _orderBookList.addAll(res.data)
                        _maxOrderBookSize.doubleValue = res.data.maxOf { it.size }
                        _orderBookInitSuccess.value = true
                    }

                    is ResultState.Error -> {
                        _orderBookInitSuccess.value = false
                    }

                    is ResultState.Loading -> {
                        _orderBookInitSuccess.value = false
                    }
                }
            }
        }
    }

    /**
     * 호가 수집
     */
    suspend fun collectOrderBook() {
        biThumbCoinOrderUseCase.requestObserveOrderBook().collect { bithumbSocketOrderBookRes ->
            if (bithumbSocketOrderBookRes?.type == "orderbook") {
                val realTimeOrderBook = bithumbSocketOrderBookRes.mapToOrderBookModel()
                for (i in _orderBookList.indices) {
                    _orderBookList[i] = realTimeOrderBook[i]
                }
                _maxOrderBookSize.doubleValue = realTimeOrderBook.maxOf { it.size }
            }
        }
    }

    /**
     * 사용자 시드머니 받아옴
     */
    private suspend fun getUserSeedMoney() {
        biThumbCoinOrderUseCase.getUserSeedMoney()?.let {
            _userSeedMoney.doubleValue = it.krw
        }
    }

    /**
     * 사용자가 구매한 코인 정보
     */
    private suspend fun getUserCoin(market: String) {
        biThumbCoinOrderUseCase.getUserCoin(market)?.let {
            _userCoin.value = it
        }
    }

    /**
     * BTC 마켓일 때 사용자의 btc 가져오기
     */
    private suspend fun getUserBtcCoin(market: String) {
        if (!market.isKrwTradeCurrency()) {
            if (biThumbCoinOrderUseCase.getUserBtcCoin() == null) {
                _userBtcCoin.value = MyCoin()
            } else {
                biThumbCoinOrderUseCase.getUserBtcCoin()?.let {
                    _userBtcCoin.value = it
                }
            }
        }
    }

    suspend fun requestBid(
        market: String,
        totalPrice: Double,
        quantity: Double,
        coinPrice: BigDecimal,
        koreanName: String,
        btcPrice: Double = 1.0
    ) {
        if (market.isKrwTradeCurrency()) {
            biThumbCoinOrderUseCase.requestKRWBid(
                market = market,
                totalPrice = totalPrice,
                coin = MyCoin(
                    market = market,
                    purchasePrice = coinPrice.toDouble(),
                    koreanCoinName = koreanName,
                    symbol = market.substring(4),
                    quantity = quantity,
                    exchange = EXCHANGE_BITTHUMB
                ),
                userSeedMoney = _userSeedMoney.doubleValue
            )
            getUserSeedMoney()
            getUserCoin(market)
        } else {
            biThumbCoinOrderUseCase.requestBTCBid(
                market = market,
                totalPrice = totalPrice,
                coin = MyCoin(
                    market = market,
                    purchasePrice = coinPrice.toDouble(),
                    koreanCoinName = koreanName,
                    symbol = market.substring(4),
                    quantity = quantity,
                    exchange = EXCHANGE_BITTHUMB
                ),
                userSeedBTC = _userBtcCoin.value.quantity,
                btcPrice = btcPrice
            )
            getUserBtcCoin(market = BTC_SYMBOL_PREFIX)
            getUserCoin(market)
        }
    }

    suspend fun requestAsk(
        market: String,
        quantity: Double,
        totalPrice: Double,
        coinPrice: BigDecimal,
        totalPriceBTC: Double = 0.0,
        btcPrice: Double
    ) {
        val updateUserCoin = MyCoin(
            market = market,
            purchasePrice = _userCoin.value.purchasePrice,
            koreanCoinName = _userCoin.value.koreanCoinName,
            symbol = _userCoin.value.symbol,
            quantity = minusQuantity(
                currentQuantity = _userCoin.value.quantity,
                quantity = quantity
            )
        )
        if (market.isKrwTradeCurrency()) {
            biThumbCoinOrderUseCase.requestKRWAsk(
                market = market,
                quantity = quantity,
                totalPrice = totalPrice,
                userCoinQuantity = userCoin.value.quantity.eighthDecimal().toDouble(),
                currentPrice = coinPrice
            )
            getUserSeedMoney()
        } else {
            biThumbCoinOrderUseCase.requestBTCAsk(
                market = market,
                quantity = quantity,
                totalPrice = totalPriceBTC,
                userCoinQuantity = userCoin.value.quantity.eighthDecimal().toDouble(),
                currentPrice = coinPrice,
                btcPrice = btcPrice
            )
            getUserBtcCoin(market = BTC_SYMBOL_PREFIX)
        }
        _userCoin.value = updateUserCoin
    }

    private fun minusQuantity(currentQuantity: Double, quantity: Double): Double {
        val minusValue = currentQuantity - quantity
        return if (minusValue <= 0.0000001) {
            0.0
        } else {
            minusValue
        }
    }

    suspend fun getTransactionInfoList(market: String): List<TransactionInfo> {
        return biThumbCoinOrderUseCase.getTransactionInfoList(market = market)
    }
}