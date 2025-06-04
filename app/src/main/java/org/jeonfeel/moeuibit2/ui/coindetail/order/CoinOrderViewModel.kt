package org.jeonfeel.moeuibit2.ui.coindetail.order

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import org.jeonfeel.moeuibit2.GlobalState
import org.jeonfeel.moeuibit2.constants.EXCHANGE_BITTHUMB
import org.jeonfeel.moeuibit2.constants.EXCHANGE_UPBIT
import org.jeonfeel.moeuibit2.constants.ioDispatcher
import org.jeonfeel.moeuibit2.data.local.preferences.PreferencesManager
import org.jeonfeel.moeuibit2.data.local.room.entity.MyCoin
import org.jeonfeel.moeuibit2.data.local.room.entity.TransactionInfo
import org.jeonfeel.moeuibit2.data.network.retrofit.model.upbit.OrderBookModel
import org.jeonfeel.moeuibit2.ui.coindetail.order.coin_order.UpbitCoinOrder
import java.math.BigDecimal
import javax.inject.Inject

@HiltViewModel
class CoinOrderViewModel @Inject constructor(
    private val preferenceManager: PreferencesManager,
    private val upBitCoinOrder: UpbitCoinOrder,
//    private val bitThumbCoinOrder: BitThumbCoinOrder,
) : ViewModel() {

    val rootExchange = GlobalState.globalExchangeState.value

    private val _orderBookIndication = mutableStateOf("quantity")
    val orderBookIndication: State<String> get() = _orderBookIndication

    private val _transactionInfoList = mutableStateListOf<TransactionInfo>()
    val transactionInfo: List<TransactionInfo> get() = _transactionInfoList

    var btcPrice: State<BigDecimal> = mutableStateOf(BigDecimal.ZERO)
    var koreanCoinName: State<String>? = null

    private var orderBookRealTimeJob: Job? = null
    private var orderBookCollectJob: Job? = null

    var isCoinOrderStarted = mutableStateOf(false)

    /**
     * coin Order 화면 초기화
     */
    fun initCoinOrder(market: String, btcPrice: State<BigDecimal>, koreanCoinName: State<String>) {
        this.btcPrice = btcPrice
        this.koreanCoinName = koreanCoinName

        viewModelScope.launch(ioDispatcher) {
            when (rootExchange) {
                EXCHANGE_UPBIT -> {
                    upBitCoinOrder.initCoinOrder(market)
                }

                EXCHANGE_BITTHUMB -> {

                }
            }
        }.also { it.start() }
    }

    fun coinOrderScreenOnStart(market: String) {
        orderBookRealTimeJob?.cancel()
        isCoinOrderStarted.value = true

        orderBookRealTimeJob = viewModelScope.launch {
            when (rootExchange) {
                EXCHANGE_UPBIT -> {
                    upBitCoinOrder.onStart(market)
                }

                EXCHANGE_BITTHUMB -> {

                }
            }
        }.also { it.start() }

        orderBookCollectJob = viewModelScope.launch {
            when (rootExchange) {
                EXCHANGE_UPBIT -> {
                    upBitCoinOrder.collectOrderBook()
                }

                EXCHANGE_BITTHUMB -> {

                }
            }
        }.also { it.start() }
    }

    /**
     * 코인 주문 화면 pause
     */
    fun coinOrderScreenOnStop() {
        isCoinOrderStarted.value = false
        viewModelScope.launch {
            upBitCoinOrder.onStop()
            orderBookRealTimeJob?.cancel()
            orderBookRealTimeJob = null
        }
    }

    /**
     * orderBookList 받아옴
     */
    fun getOrderBookList(): List<OrderBookModel> {
        return when (rootExchange) {
            EXCHANGE_UPBIT -> {
                upBitCoinOrder.orderBookList
            }

            EXCHANGE_BITTHUMB -> {
                upBitCoinOrder.orderBookList
            }

            else -> {
                upBitCoinOrder.orderBookList
            }
        }
    }

    fun getMaxOrderBookSize(): State<Double> {
        return when (rootExchange) {
            EXCHANGE_UPBIT -> {
                upBitCoinOrder.maxOrderBookSize
            }

            EXCHANGE_BITTHUMB -> {
                upBitCoinOrder.maxOrderBookSize
            }

            else -> {
                upBitCoinOrder.maxOrderBookSize
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

    /**
     * 사용자 시드머니 받아옴
     */
    fun getUserSeedMoney(): State<Double> {
        return when (rootExchange) {
            EXCHANGE_UPBIT -> {
                upBitCoinOrder.userSeedMoney
            }

            EXCHANGE_BITTHUMB -> {
                upBitCoinOrder.userSeedMoney
            }

            else -> {
                upBitCoinOrder.userSeedMoney
            }
        }
    }

    fun requestBid(
        market: String,
        quantity: Double,
        price: BigDecimal,
        totalPrice: Double,
    ) {
        viewModelScope.launch(ioDispatcher) {
            when (rootExchange) {
                EXCHANGE_UPBIT -> {
                    upBitCoinOrder.requestBid(
                        market = market,
                        totalPrice = totalPrice,
                        quantity = quantity,
                        coinPrice = price,
                        koreanName = koreanCoinName?.value ?: "",
                        btcPrice = btcPrice?.value?.toDouble() ?: 0.0
                    )
                }

                EXCHANGE_BITTHUMB -> {

                }

                else -> {

                }
            }
        }
    }

    fun requestAsk(
        market: String,
        quantity: Double,
        totalPrice: Double = 0.0,
        price: BigDecimal,
        totalPriceBTC: Double = 0.0,
    ) {
        viewModelScope.launch(ioDispatcher) {
            when (rootExchange) {
                EXCHANGE_UPBIT -> {
                    upBitCoinOrder.requestAsk(
                        market = market,
                        totalPrice = totalPrice,
                        quantity = quantity,
                        coinPrice = price,
                        totalPriceBTC = totalPriceBTC,
                        btcPrice = btcPrice?.value?.toDouble() ?: 0.0
                    )
                }

                EXCHANGE_BITTHUMB -> {

                }

                else -> {

                }
            }
        }
    }

    /**
     * 사용자 코인 받아옴
     */
    fun getUserCoin(): State<MyCoin> {
        return when (rootExchange) {
            EXCHANGE_UPBIT -> {
                upBitCoinOrder.userCoin
            }

            EXCHANGE_BITTHUMB -> {
                upBitCoinOrder.userCoin
            }

            else -> {
                upBitCoinOrder.userCoin
            }
        }
    }

    /**
     * BTC마켓 일 때 필요한데, 사용자 BTC 코인 받아옴
     */
    fun getUserBtcCoin(): State<MyCoin> {
        return when (rootExchange) {
            EXCHANGE_UPBIT -> {
                upBitCoinOrder.userBtcCoin
            }

            EXCHANGE_BITTHUMB -> {
                upBitCoinOrder.userBtcCoin
            }

            else -> {
                upBitCoinOrder.userBtcCoin
            }
        }
    }

    fun getOrderBookInitSuccess(): State<Boolean> {
        return when (rootExchange) {
            EXCHANGE_UPBIT -> {
                upBitCoinOrder.orderBookInitSuccess
            }

            EXCHANGE_BITTHUMB -> {
                upBitCoinOrder.orderBookInitSuccess
            }

            else -> {
                upBitCoinOrder.orderBookInitSuccess
            }
        }
    }

    fun getTransactionInfoList(market: String) {
        viewModelScope.launch {
            _transactionInfoList.clear()
            _transactionInfoList.addAll(upBitCoinOrder.getTransactionInfoList(market = market))
        }
    }
}