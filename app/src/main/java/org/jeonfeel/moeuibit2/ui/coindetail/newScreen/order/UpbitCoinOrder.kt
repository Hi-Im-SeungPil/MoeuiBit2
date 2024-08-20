package org.jeonfeel.moeuibit2.ui.coindetail.newScreen.order

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import com.orhanobut.logger.Logger
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import org.jeonfeel.moeuibit2.data.local.room.entity.MyCoin
import org.jeonfeel.moeuibit2.data.network.retrofit.model.upbit.OrderBookModel
import org.jeonfeel.moeuibit2.data.network.websocket.model.upbit.UpbitSocketOrderBookRes
import org.jeonfeel.moeuibit2.data.usecase.UpbitCoinOrderUseCase
import org.jeonfeel.moeuibit2.ui.main.exchange.root_exchange.BaseCommunicationModule
import org.jeonfeel.moeuibit2.utils.isTradeCurrencyKrw
import java.math.BigDecimal
import javax.inject.Inject

class UpbitCoinOrder @Inject constructor(private val upbitCoinOrderUseCase: UpbitCoinOrderUseCase) :
    BaseCommunicationModule() {
    private val _orderBookList = mutableStateListOf<OrderBookModel>()
    val orderBookList: List<OrderBookModel> get() = _orderBookList
    private val _userSeedMoney = mutableLongStateOf(0L)
    val userSeedMoney: Long get() = _userSeedMoney.longValue
    private val _userCoin = mutableStateOf(MyCoin())
    val userCoin: MyCoin get() = _userCoin.value
    private val _userBtcCoin = mutableStateOf(MyCoin())
    val userBtcCoin: MyCoin get() = _userBtcCoin.value
    private val _tickerResponse = MutableStateFlow<UpbitSocketOrderBookRes?>(null)
    private val _maxOrderBookSize = mutableDoubleStateOf(0.0)
    val maxOrderBookSize: State<Double> get() = _maxOrderBookSize

    suspend fun initCoinOrder(market: String) {
        requestOrderBook(market)
        getUserSeedMoney()
        getUserCoin(market)
        getUserBtcCoin(market)
        requestSubscribeOrderBook(market)
        collectOrderBook()
    }

    /**
     * 호가 요청
     */
    private suspend fun requestOrderBook(market: String) {
        executeUseCase<List<OrderBookModel>>(
            target = upbitCoinOrderUseCase.getOrderBook(market),
            onComplete = {
                _maxOrderBookSize.doubleValue = it.maxOf { unit -> unit.size }
                _orderBookList.addAll(it)
            }
        )
    }

    /**
     * 호가 구독 요청
     */
    private suspend fun requestSubscribeOrderBook(market: String) {
        upbitCoinOrderUseCase.requestSubscribeOrderBook(listOf(market))
    }

    /**
     * 호가 수집
     */
    private suspend fun collectOrderBook() {
        upbitCoinOrderUseCase.requestObserveOrderBook().onEach { result ->
            _tickerResponse.update {
                result
            }
        }.collect { upbitSocketOrderBookRes ->
            if (upbitSocketOrderBookRes.type == "orderbook") {
                val realTimeOrderBook = upbitSocketOrderBookRes.mapTo()
                for (i in _orderBookList.indices) {
                    _orderBookList[i] = realTimeOrderBook[i]
                }
                _maxOrderBookSize.doubleValue = realTimeOrderBook.maxOf { it.size }
            }
        }
    }

    suspend fun onPause() {
        requestSubscribeOrderBook("")
    }

    suspend fun onResume(market: String) {
        upbitCoinOrderUseCase.requestSubscribeOrderBook(listOf(market))
    }

    /**
     * 사용자 시드머니 받아옴
     */
    private suspend fun getUserSeedMoney() {
        upbitCoinOrderUseCase.getUserSeedMoney()?.let {
            _userSeedMoney.longValue = it.krw
        }
    }

    /**
     * 사용자가 구매한 코인 정보
     */
    private suspend fun getUserCoin(market: String) {
        upbitCoinOrderUseCase.getUserCoin(market)?.let {
            _userCoin.value = it
        }
    }

    /**
     * BTC 마켓일 때 사용자의 btc 가져오기
     */
    private suspend fun getUserBtcCoin(market: String) {
        if (!market.isTradeCurrencyKrw()) {
            upbitCoinOrderUseCase.getUserBtcCoin()?.let {
                _userBtcCoin.value = it
            }
        }
    }

    /**
     * if(시드 머니 < 코인 총액) return
     * else if(시드 머니 >= 코인 총 액) {
     *  구매 해야함.
     *  db에 코인 수량 만큼 저장 하고, 시드 머니 빼기
     *
     * }
     *
     * btc 일때는?
     */
    suspend fun requestBid(
        market: String,
        totalPrice: Long,
        quantity: Double,
        price: BigDecimal,
        koreanName: String,
    ) {
        if (market.isTradeCurrencyKrw()) {
            upbitCoinOrderUseCase.requestKRWBid(
                market = market, totalPrice = totalPrice, coin = MyCoin(
                    market = market,
                    purchasePrice = price.toDouble(),
                    koreanCoinName = koreanName,
                    symbol = market.substring(4),
                    quantity = quantity
                )
            )
            _userSeedMoney.longValue -= totalPrice
        } else {

        }
    }

    fun plusUserSeedMoney(totalPrice: Long) {
        _userSeedMoney.longValue += totalPrice
    }
}