package org.jeonfeel.moeuibit2.ui.coindetail.newScreen.order

import android.content.Context
import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import com.orhanobut.logger.Logger
import org.jeonfeel.moeuibit2.constants.KRW_COMMISSION_FEE
import org.jeonfeel.moeuibit2.constants.UPBIT_BTC_SYMBOL_PREFIX
import org.jeonfeel.moeuibit2.constants.UPBIT_KRW_SYMBOL_PREFIX
import org.jeonfeel.moeuibit2.data.local.room.entity.MyCoin
import org.jeonfeel.moeuibit2.data.network.retrofit.model.upbit.CommonExchangeModel
import org.jeonfeel.moeuibit2.data.usecase.OrderBookKind
import org.jeonfeel.moeuibit2.utils.BigDecimalMapper.formattedString
import org.jeonfeel.moeuibit2.utils.BigDecimalMapper.formattedStringForQuantity
import org.jeonfeel.moeuibit2.utils.BigDecimalMapper.newBigDecimal
import org.jeonfeel.moeuibit2.utils.OneTimeNetworkCheck
import org.jeonfeel.moeuibit2.utils.calculator.Calculator
import org.jeonfeel.moeuibit2.utils.commaFormat
import org.jeonfeel.moeuibit2.utils.eighthDecimal
import org.jeonfeel.moeuibit2.utils.isTradeCurrencyKrw
import org.jeonfeel.moeuibit2.utils.secondDecimal
import org.jeonfeel.moeuibit2.utils.showToast
import org.jeonfeel.moeuibit2.utils.thirdDecimal
import java.math.BigDecimal
import java.math.RoundingMode
import kotlin.math.round

enum class OrderTabState {
    BID, ASK, TRANSACTION_INFO
}

class CoinOrderStateHolder(
    private val commonExchangeModelState: State<CommonExchangeModel?>,
    private val maxOrderBookSize: State<Double>,
    private val context: Context,
    val orderTabState: MutableState<OrderTabState> = mutableStateOf(OrderTabState.BID),
    val getUserSeedMoney: () -> Long,
    val getUserBTC: () -> Double,
    private val requestBid: (market: String, quantity: Double, price: BigDecimal, totalPrice: Double) -> Unit,
    private val requestAsk: (market: String, quantity: Double, totalPrice: Long, price: BigDecimal) -> Unit,
    val market: String,
    private val getUserCoin: () -> MyCoin,
    private val btcPrice: State<BigDecimal>
) {
    private val toast: Toast? = null
    val percentageLabelList = listOf("최대", "75%", "50%", "25%", "10%")
    private val percentageList = listOf(1.0, 0.75, 0.5, 0.25, 0.1)
    private val _bidQuantity = mutableStateOf("")
    val bidQuantity: State<String> get() = _bidQuantity
    private val _askQuantity = mutableStateOf("")
    val askQuantity: State<String> get() = _askQuantity
    private val _bidTotalPrice = mutableStateOf(0.0.newBigDecimal())
    val bidTotalPrice: State<BigDecimal> get() = _bidTotalPrice
    private val _askTotalPrice = mutableStateOf(0.0.newBigDecimal())
    val askTotalPrice: State<BigDecimal> get() = _askTotalPrice
    private val _bidQuantityPercentage = mutableStateOf("비율")
    val bidQuantityPercentage: State<String> get() = _bidQuantityPercentage
    private val _askQuantityPercentage = mutableStateOf("비율")
    val askQuantityPercentage: State<String> get() = _askQuantityPercentage

    /**
     * 호가창 전일대비 값 받아옴
     */
    fun getOrderBookItemFluctuateRate(orderBookPrice: Double): String {
        return Calculator.orderBookRateCalculator(
            preClosingPrice = commonExchangeModelState.value?.prevClosingPrice ?: 1.0,
            orderBookPrice = orderBookPrice
        ).secondDecimal().plus("%")
    }


    fun getOrderBookItemBackground(kind: OrderBookKind): Color {
        return when (kind) {
            // 매도
            OrderBookKind.ASK -> {
                Color(0xFFF5F8FD)
            }

            // 매수
            OrderBookKind.BID -> {
                Color(0xFFFDF7F7)
            }
        }
    }

    fun getOrderBookItemTextColor(orderBookPrice: Double): Color {
        val itemRate = Calculator.orderBookRateCalculator(
            preClosingPrice = commonExchangeModelState.value?.openingPrice ?: 1.0,
            orderBookPrice = orderBookPrice
        )
        return when {
            itemRate > 0.0 -> {
                Color(0xFFc68b8d)
            }

            itemRate < 0.0 -> {
                Color(0xFF7c98c0)
            }

            else -> {
                Color.Black
            }
        }
    }

    fun getOrderBookBlockColor(kind: OrderBookKind): Color {
        return when (kind) {
            OrderBookKind.ASK -> {
                Color(0xFFe2ebfa)
            }

            OrderBookKind.BID -> {
                Color(0xFFfbe7e8)
            }
        }
    }

    fun getOrderBookBlockSize(
        orderBookSize: Double
    ): Float {
        val blockSize = round(orderBookSize / maxOrderBookSize.value * 100)
        return if (blockSize.isNaN()) {
            0f
        } else {
            blockSize.toFloat() / 100
        }
    }

    /**
     * 가격이랑 호가창이랑 일치하는지
     */
    fun getIsMatchedTradePrice(
        orderBookPrice: BigDecimal
    ): Boolean {
        return orderBookPrice == commonExchangeModelState.value?.tradePrice
    }

    /**
     * 호가창 수량 / 총액 텍스트
     */
    fun getOrderBookIndicationText(orderBookIndicationState: String, quantity: Double): String {
        return if (orderBookIndicationState == "quantity") {
            quantity.thirdDecimal()
        } else {
            if (market.isTradeCurrencyKrw()) {
                commonExchangeModelState.value?.let {
                    ((it.tradePrice.toDouble() * (quantity))).commaFormat()
                } ?: ""
            } else {
                commonExchangeModelState.value?.let {
                    ((it.tradePrice.toDouble() * (quantity)) * btcPrice.value.toDouble()).commaFormat()
                } ?: ""
            }
        }
    }

    /**
     * 호가창 수량 / 총액 텍스트
     */
    fun getOrderBookIndicationText(orderBookIndicationState: String): String {
        return if (orderBookIndicationState == "quantity") {
            "수량 기준"
        } else {
            "총액 기준"
        }
    }

    fun quantityOnValueChanged(value: String, isBid: Boolean) {
        if (value.toDoubleOrNull() == null && value != "") {
            if (isBid) {
                _bidQuantity.value = ""
                _bidTotalPrice.value = 0.0.newBigDecimal()
            } else {
                _askQuantity.value = ""
                _askTotalPrice.value = 0.0.newBigDecimal()
            }
            context.showToast("숫자만 입력 가능합니다.")
        } else if (commonExchangeModelState.value == null || commonExchangeModelState.value?.tradePrice?.toDouble()
            == 0.0
        ) {
            context.showToast("네트워크 통신 오류입니다.")
        } else if (value == "") {
            if (isBid) {
                _bidQuantity.value = ""
                _bidTotalPrice.value = 0.0.newBigDecimal()
            } else {
                _askQuantity.value = ""
                _askTotalPrice.value = 0.0.newBigDecimal()
            }
        } else {
            if (isBid) {
                val totalPrice = commonExchangeModelState.value?.tradePrice?.multiply(
                    value.toDouble().newBigDecimal()
                ) ?: 0.0.newBigDecimal()
                _bidQuantity.value = value
                _bidTotalPrice.value = (totalPrice + totalPrice.multiply(0.0005.newBigDecimal(4)))
            } else {
                val totalPrice = commonExchangeModelState.value?.tradePrice?.multiply(
                    value.toDouble().newBigDecimal()
                ) ?: 0.0.newBigDecimal()
                _askQuantity.value = value
                _askTotalPrice.value = (totalPrice - totalPrice.multiply(0.0005.newBigDecimal(4)))
            }
        }
    }

    fun getUserSeedMoneyOrBtc(): Double {
        return if (market.isTradeCurrencyKrw()) {
            getUserSeedMoney().toDouble()
        } else {
            getUserBTC()
        }
    }

    fun updateBidCoinQuantity(index: Int) {
        if (commonExchangeModelState.value != null) {
            var percentageResult = percentageList[index]
            if (percentageResult == 1.0) {
                percentageResult -= KRW_COMMISSION_FEE
            }
            val seedMoney = if (market.isTradeCurrencyKrw()) {
                (getUserSeedMoneyOrBtc() * percentageResult).newBigDecimal(
                    scale = 0,
                    roundingMode = RoundingMode.FLOOR
                )
            } else {
                (getUserSeedMoneyOrBtc() * percentageResult).newBigDecimal(
                    scale = 8,
                    roundingMode = RoundingMode.FLOOR
                )
            }
            val quantity =
                seedMoney.divide(commonExchangeModelState.value?.tradePrice, 8, RoundingMode.FLOOR)
            _bidQuantityPercentage.value = percentageLabelList[index]
            _bidQuantity.value = quantity.formattedStringForQuantity()
        } else {
            _bidQuantity.value = "0"
        }
    }

    fun updateAskCoinQuantity(index: Int) {
        // 유저가 가지고 있는 코인을 나눠서 세팅
        if (commonExchangeModelState.value != null) {
            val percentageResult = percentageList[index]
            val quantity = getUserCoin().quantity.newBigDecimal(8, RoundingMode.FLOOR)
                .multiply(percentageResult.newBigDecimal(4))
            _askQuantityPercentage.value = percentageLabelList[index]
            _askQuantity.value = quantity.formattedStringForQuantity()
        } else {
            _askQuantity.value = "0"
        }
    }

    fun getBidTotalPrice(): String {
        return if (bidQuantity.value.isNotEmpty() && commonExchangeModelState.value != null) {
            val totalPrice =
                bidQuantity.value.replace(",", "").toDouble().newBigDecimal(8, RoundingMode.FLOOR)
                    .multiply(commonExchangeModelState.value!!.tradePrice)

            if (market.isTradeCurrencyKrw()) {
                totalPrice.formattedString()
            } else {
                totalPrice.setScale(8, RoundingMode.FLOOR).toDouble().eighthDecimal()
            }
        } else {
            "0"
        }
    }

    fun getAskTotalPrice(): String {
        return if (askQuantity.value.isNotEmpty() && commonExchangeModelState.value != null) {
            val total =
                askQuantity.value.replace(",", "").toDouble().newBigDecimal(8, RoundingMode.FLOOR)
                    .multiply(commonExchangeModelState.value!!.tradePrice)
            val commission = total.multiply(KRW_COMMISSION_FEE.newBigDecimal(4))
            total.minus(commission).formattedString()
        } else {
            "0"
        }
    }

    private fun bidReset() {
        _bidQuantity.value = ""
        _bidQuantityPercentage.value = "비율"
    }

    private fun askReset() {
        _askQuantity.value = ""
        _askQuantityPercentage.value = "비율"
    }

    fun bid() {
        if (bidQuantity.value.isNotEmpty() && commonExchangeModelState.value != null) {
            val totalPrice = if (market.startsWith(UPBIT_KRW_SYMBOL_PREFIX)) {
                bidQuantity.value.replace(",", "").toDouble().newBigDecimal(8, RoundingMode.FLOOR)
                    .multiply(commonExchangeModelState.value!!.tradePrice)
                    .setScale(0, RoundingMode.FLOOR)
            } else {
                bidQuantity.value.replace(",", "").toDouble().newBigDecimal(8, RoundingMode.FLOOR)
                    .multiply(commonExchangeModelState.value!!.tradePrice)
                    .setScale(8, RoundingMode.FLOOR)
            }

            when {
                commonExchangeModelState.value == null -> {
                    Logger.e("requestBid1")
                    return
                }

                commonExchangeModelState.value != null && commonExchangeModelState.value?.tradePrice?.toDouble() == 0.0 -> {
                    Logger.e("requestBid7")
                    return
                }

                OneTimeNetworkCheck.networkCheck(context) == null -> {
                    Logger.e("requestBid4")
                    return
                }

                else -> {}
            }

            when {
                market.startsWith(UPBIT_KRW_SYMBOL_PREFIX) -> {
                    when {
                        totalPrice > (getUserSeedMoney().toDouble().newBigDecimal()
                            .multiply(BigDecimal(0.9995)).setScale(0, RoundingMode.FLOOR)) -> {
                            Logger.e("requestBid2")
                            return
                        }

                        totalPrice.toDouble() < 5000 -> {
                            Logger.e("requestBid3")
                            return
                        }

                        else -> {}
                    }
                }

                market.startsWith(UPBIT_BTC_SYMBOL_PREFIX) -> {
                    when {
                        totalPrice > (getUserBTC().newBigDecimal(8, RoundingMode.FLOOR)
                            .multiply(BigDecimal("0.9975")).setScale(8, RoundingMode.FLOOR)) -> {
                            Logger.e("requestBid2")
                            Logger.e("${getUserBTC().newBigDecimal()} , ${getUserBTC().newBigDecimal()
                                .multiply(BigDecimal(0.9975)).setScale(8, RoundingMode.FLOOR)} , $totalPrice")
                            return
                        }

                        totalPrice.toDouble() < 0.00005 -> {
                            Logger.e("requestBid3")
                            return
                        }

                        else -> {}
                    }
                }

                else -> {}
            }

            Logger.e("requestBid")
            requestBid(
                market,
                bidQuantity.value.replace(",", "").toDouble(),
                commonExchangeModelState.value?.tradePrice ?: 0.0.newBigDecimal(),
                totalPrice.toDouble(),
            )
            bidReset()
        }
    }

    fun ask() {
        val userCoinQuantity = getUserCoinQuantity()
        if (askQuantity.value.isNotEmpty() && commonExchangeModelState.value != null) {
            val userAskQuantity =
                askQuantity.value.replace(",", "").toDouble().newBigDecimal(8, RoundingMode.FLOOR)
            val tempPrice = userAskQuantity.multiply(commonExchangeModelState.value!!.tradePrice)
                .setScale(0, RoundingMode.FLOOR)
            val commission = tempPrice.multiply(KRW_COMMISSION_FEE.newBigDecimal(4))
            val totalPrice = tempPrice.minus(commission).setScale(0, RoundingMode.FLOOR)
            when {
                commonExchangeModelState.value == null -> {
                    Logger.e("requestBid1")
                }

                commonExchangeModelState.value != null && commonExchangeModelState.value?.tradePrice?.toDouble() == 0.0 -> {
                    Logger.e("requestBid7")
                }

                userAskQuantity > (userCoinQuantity).newBigDecimal(8, RoundingMode.FLOOR) -> {
                    Logger.e("requestBid2")
                }

                totalPrice.toDouble() < 5000 -> {
                    Logger.e("requestBid3")
                }

                OneTimeNetworkCheck.networkCheck(context) == null -> {
                    Logger.e("requestBid4")
                }

                else -> {
                    Logger.e("requestAsk")
                    requestAsk(
                        market,
                        userAskQuantity.toDouble(),
                        totalPrice.toLong(),
                        commonExchangeModelState.value!!.tradePrice
                    )
                    askReset()
                }
            }
        }
    }

    fun getUserCoinQuantity(): Double {
        return getUserCoin().quantity
    }
}

@Composable
fun rememberCoinOrderStateHolder(
    commonExchangeModelState: State<CommonExchangeModel?>,
    maxOrderBookSize: State<Double>,
    getUserSeedMoney: () -> Long,
    getUserBTC: () -> Double,
    context: Context = LocalContext.current,
    requestBid: (market: String, quantity: Double, price: BigDecimal, totalPrice: Double) -> Unit,
    requestAsk: (market: String, quantity: Double, totalPrice: Long, price: BigDecimal) -> Unit,
    market: String,
    getUserCoin: () -> MyCoin,
    btcPrice: State<BigDecimal>
) = remember {
    CoinOrderStateHolder(
        commonExchangeModelState = commonExchangeModelState,
        maxOrderBookSize = maxOrderBookSize,
        getUserSeedMoney = getUserSeedMoney,
        context = context,
        requestBid = requestBid,
        requestAsk = requestAsk,
        market = market,
        getUserCoin = getUserCoin,
        getUserBTC = getUserBTC,
        btcPrice = btcPrice
    )
}