package org.jeonfeel.moeuibit2.ui.coindetail.order.ui

import android.content.Context
import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import org.jeonfeel.moeuibit2.constants.BTC_COMMISSION_FEE
import org.jeonfeel.moeuibit2.constants.KRW_COMMISSION_FEE
import org.jeonfeel.moeuibit2.constants.UPBIT_BTC_SYMBOL_PREFIX
import org.jeonfeel.moeuibit2.constants.UPBIT_KRW_SYMBOL_PREFIX
import org.jeonfeel.moeuibit2.data.local.room.entity.MyCoin
import org.jeonfeel.moeuibit2.data.network.retrofit.model.upbit.CommonExchangeModel
import org.jeonfeel.moeuibit2.data.usecase.OrderBookKind
import org.jeonfeel.moeuibit2.ui.theme.newtheme.coindetail.orderBookAskColor
import org.jeonfeel.moeuibit2.ui.theme.newtheme.coindetail.orderBookBidColor
import org.jeonfeel.moeuibit2.utils.BigDecimalMapper.formattedString
import org.jeonfeel.moeuibit2.utils.BigDecimalMapper.formattedStringForKRW
import org.jeonfeel.moeuibit2.utils.BigDecimalMapper.newBigDecimal
import org.jeonfeel.moeuibit2.utils.Utils
import org.jeonfeel.moeuibit2.utils.calculator.Calculator
import org.jeonfeel.moeuibit2.utils.commaFormat
import org.jeonfeel.moeuibit2.utils.eighthDecimal
import org.jeonfeel.moeuibit2.utils.isTradeCurrencyKrw
import org.jeonfeel.moeuibit2.utils.secondDecimal
import org.jeonfeel.moeuibit2.utils.ext.showToast
import org.jeonfeel.moeuibit2.utils.thirdDecimal
import java.math.BigDecimal
import java.math.RoundingMode
import kotlin.math.round

enum class OrderTabState {
    BID, ASK, TRANSACTION_INFO
}

class CoinOrderStateHolder(
    val orderTabState: MutableState<OrderTabState> = mutableStateOf(OrderTabState.BID),
    val totalBidDialogState: MutableState<Boolean> = mutableStateOf(false),
    val totalAskDialogState: MutableState<Boolean> = mutableStateOf(false),
    private val context: Context,
    private val market: String,
    private val requestBid: (market: String, quantity: Double, price: BigDecimal, totalPrice: Double) -> Unit,
    private val requestAsk: (market: String, quantity: Double, totalPrice: Long, price: BigDecimal, totalPriceBTC: Double) -> Unit,
    private val commonExchangeModelState: State<CommonExchangeModel?>,
    private val maxOrderBookSize: State<Double>,
    private val userSeedMoney: State<Long>,
    private val userBTC: State<MyCoin>,
    private val userCoin: State<MyCoin>,
    private val btcPrice: State<BigDecimal>,
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

    fun getOrderBookItemRate(orderBookPrice: Double): Double {
        return Calculator.orderBookRateCalculator(
            preClosingPrice = commonExchangeModelState.value?.openingPrice ?: 1.0,
            orderBookPrice = orderBookPrice
        )
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
        } else if (value == "00") {
            return
        } else {
            val totalPrice = commonExchangeModelState.value?.tradePrice?.multiply(
                value.toDouble().newBigDecimal()
            ) ?: 0.0.newBigDecimal()
            if (isBid) {
                _bidQuantity.value = value
                _bidTotalPrice.value = totalPrice
            } else {
                _askQuantity.value = value
                _askTotalPrice.value = totalPrice
            }
        }
    }

    fun updateBidCoinQuantity(index: Int) {
        if (commonExchangeModelState.value != null && userSeedMoney.value != 0L) {
            var percentageResult = BigDecimal(percentageList[index].toString())
            if (percentageResult == BigDecimal.ONE) {
                percentageResult = if (market.isTradeCurrencyKrw()) {
                    percentageResult.minus(BigDecimal(KRW_COMMISSION_FEE))
                } else {
                    percentageResult.minus(BigDecimal(BTC_COMMISSION_FEE))
                }
            }

            val seedMoney = if (market.isTradeCurrencyKrw()) {
                userSeedMoney.value.toDouble().newBigDecimal(0)
                    .multiply(percentageResult)
                    .setScale(0, RoundingMode.FLOOR)
            } else {
                userBTC.value.quantity.newBigDecimal(8, RoundingMode.HALF_UP)
                    .multiply(percentageResult)
                    .setScale(8, RoundingMode.FLOOR)
            }

            val quantity = seedMoney.divide(
                commonExchangeModelState.value?.tradePrice ?: BigDecimal.ONE,
                8,
                RoundingMode.FLOOR
            )

            _bidQuantityPercentage.value = percentageLabelList[index]
            _bidQuantity.value = quantity.toString()
        } else {
            _bidQuantityPercentage.value = "비율"
            _bidQuantity.value = ""
        }
    }

    fun updateAskCoinQuantity(index: Int) {
        // 유저가 가지고 있는 코인을 나눠서 세팅
        if (commonExchangeModelState.value != null && userCoin.value.quantity != 0.0) {
            val percentageResult = percentageList[index]
            val quantity = userCoin.value.quantity.newBigDecimal(8, RoundingMode.FLOOR)
                .multiply(percentageResult.newBigDecimal(4))
                .setScale(8, RoundingMode.FLOOR)

            _askQuantityPercentage.value = percentageLabelList[index]
            _askQuantity.value = quantity.toString()
        } else {
            _askQuantityPercentage.value = "비율"
            _askQuantity.value = ""
        }
    }

    fun getBidTotalPrice(): String {
        return if (bidQuantity.value.isNotEmpty() && commonExchangeModelState.value != null) {
            val totalPrice =
                bidQuantity.value.toDouble()
                    .newBigDecimal(8, RoundingMode.FLOOR)
                    .multiply(commonExchangeModelState.value!!.tradePrice)

            if (market.isTradeCurrencyKrw()) {
                totalPrice.formattedStringForKRW()
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
                askQuantity.value.toDouble()
                    .newBigDecimal(8, RoundingMode.FLOOR)
                    .multiply(commonExchangeModelState.value!!.tradePrice)
            if (market.isTradeCurrencyKrw()) {
                total.formattedString()
            } else {
                total.setScale(8, RoundingMode.FLOOR).toDouble().eighthDecimal()
            }
        } else {
            "0"
        }
    }

    private fun bidReset() {
        _bidQuantity.value = ""
        _bidQuantityPercentage.value = "비율"
        context.showToast("매수가 완료 되었습니다.")
    }

    private fun askReset() {
        _askQuantity.value = ""
        _askQuantityPercentage.value = "비율"
        context.showToast("매도가 완료 되었습니다.")
    }

    fun bid() {
        if (bidQuantity.value.isNotEmpty() && commonExchangeModelState.value != null) {
            val total =
                bidQuantity.value.toDouble()
                    .newBigDecimal(8, RoundingMode.FLOOR)
                    .multiply(commonExchangeModelState.value!!.tradePrice)

            val totalPrice = if (market.startsWith(UPBIT_KRW_SYMBOL_PREFIX)) {
                total.setScale(0, RoundingMode.FLOOR)
            } else {
                total.setScale(8, RoundingMode.FLOOR)
            }

            when {
                commonExchangeModelState.value == null -> {
                    context.showToast("인터넷 연결을 확인한 후 다시 시도해 주세요.")
                    return
                }

                commonExchangeModelState.value != null && commonExchangeModelState.value?.tradePrice?.toDouble() == 0.0 -> {
                    context.showToast("가격이 0원 입니다. 정상화 후 다시 시도해 주세요")
                    return
                }

                !Utils.isNetworkAvailable(context) -> {
                    context.showToast("인터넷 연결을 확인한 후 다시 시도해 주세요.")
                    return
                }

                else -> {}
            }

            when {
                market.startsWith(UPBIT_KRW_SYMBOL_PREFIX) -> {
                    val commission = totalPrice.multiply(BigDecimal(1 - KRW_COMMISSION_FEE))

                    when {
                        totalPrice > (userSeedMoney.value.toDouble().newBigDecimal()
                            .setScale(0, RoundingMode.HALF_UP)) -> {
                            context.showToast("보유하신 KRW가 부족합니다.")
                            return
                        }

                        totalPrice.toDouble() < 5000 -> {
                            context.showToast("최소 매수 금액은 5000원 입니다.")
                            return
                        }

                        else -> {}
                    }
                }

                market.startsWith(UPBIT_BTC_SYMBOL_PREFIX) -> {
                    val commission = totalPrice.multiply(BigDecimal(1 - BTC_COMMISSION_FEE))

                    when {
                        totalPrice > (userBTC.value.quantity
                            .newBigDecimal(8, RoundingMode.HALF_UP)
                            .setScale(8, RoundingMode.HALF_UP)) -> {
                            context.showToast("보유하신 BTC가 부족합니다.")
                            return
                        }

                        totalPrice.toDouble() < 0.00005 -> {
                            context.showToast("최소 매수 금액은 0.00005BTC 입니다.")
                            return
                        }

                        else -> {}
                    }
                }

                else -> {}
            }
            val commission = if (market.startsWith(UPBIT_KRW_SYMBOL_PREFIX)) {
                bidQuantity.value.toDouble().newBigDecimal(8, RoundingMode.FLOOR)
                    .multiply(BigDecimal(KRW_COMMISSION_FEE))
            } else {
                bidQuantity.value.toDouble().newBigDecimal(8, RoundingMode.FLOOR)
                    .multiply(BigDecimal(BTC_COMMISSION_FEE))
            }

            val bidQuantityResult =
                bidQuantity.value.toDouble().newBigDecimal(8, RoundingMode.FLOOR).minus(commission)

            requestBid(
                market,
                bidQuantityResult.toDouble(),
                commonExchangeModelState.value?.tradePrice ?: 0.0.newBigDecimal(),
                totalPrice.toDouble(),
            )
            bidReset()
        }
    }

    fun ask() {
        val userCoinQuantity = userCoin.value.quantity

        if (askQuantity.value.isNotEmpty() && commonExchangeModelState.value != null) {
            val userAskQuantity =
                askQuantity.value.toDouble().newBigDecimal(8, RoundingMode.FLOOR)

            val tempPrice = if (market.startsWith(UPBIT_KRW_SYMBOL_PREFIX)) {
                userAskQuantity.multiply(commonExchangeModelState.value!!.tradePrice)
                    .setScale(0, RoundingMode.FLOOR)
            } else {
                userAskQuantity.multiply(commonExchangeModelState.value!!.tradePrice)
                    .setScale(8, RoundingMode.FLOOR)
            }

            val commission = if (market.startsWith(UPBIT_KRW_SYMBOL_PREFIX)) {
                tempPrice.multiply(KRW_COMMISSION_FEE.newBigDecimal(4))
            } else {
                tempPrice.multiply(BTC_COMMISSION_FEE.newBigDecimal(4))
            }

            val totalPrice = if (market.startsWith(UPBIT_KRW_SYMBOL_PREFIX)) {
                tempPrice.minus(commission).setScale(0, RoundingMode.FLOOR)
            } else {
                tempPrice.minus(commission).setScale(8, RoundingMode.FLOOR)
            }

            when {
                commonExchangeModelState.value == null -> {
                    context.showToast("인터넷 연결을 확인한 후 다시 시도해 주세요.")
                    return
                }

                commonExchangeModelState.value != null && commonExchangeModelState.value?.tradePrice?.toDouble() == 0.0 -> {
                    context.showToast("가격이 0원 입니다. 정상화 후 다시 시도해 주세요")
                    return
                }

                userAskQuantity > (userCoinQuantity).newBigDecimal(8, RoundingMode.FLOOR) -> {
                    context.showToast("보유하신 수량이 매도 수량보다 적습니다.")
                    return
                }

                !Utils.isNetworkAvailable(context) -> {
                    context.showToast("인터넷 연결을 확인한 후 다시 시도해 주세요.")
                    return
                }

                else -> {}
            }

            when {
                market.startsWith(UPBIT_KRW_SYMBOL_PREFIX) -> {
                    when {
                        totalPrice.toDouble() < 5000 -> {
                            context.showToast("최소 매도 총액은 5000원 입니다.")
                            return
                        }

                        else -> {}
                    }
                }

                market.startsWith(UPBIT_BTC_SYMBOL_PREFIX) -> {
                    when {
                        totalPrice.toDouble() < 0.00005 -> {
                            context.showToast("최소 매도 총액은 0.00005BTC 입니다.")
                            return
                        }

                        else -> {}
                    }
                }

                else -> {}
            }

            requestAsk(
                market,
                userAskQuantity.toDouble(),
                totalPrice.toDouble().toLong(),
                commonExchangeModelState.value!!.tradePrice,
                totalPrice.toDouble()
            )
            askReset()
        }
    }
}

@Composable
fun rememberCoinOrderStateHolder(
    commonExchangeModelState: State<CommonExchangeModel?>,
    context: Context = LocalContext.current,
    maxOrderBookSize: State<Double>,
    market: String,
    requestBid: (market: String, quantity: Double, price: BigDecimal, totalPrice: Double) -> Unit,
    requestAsk: (market: String, quantity: Double, totalPrice: Long, price: BigDecimal, totalPriceBTC: Double) -> Unit,
    userSeedMoney: State<Long>,
    userBTC: State<MyCoin>,
    userCoin: State<MyCoin>,
    btcPrice: State<BigDecimal>
) = remember {
    CoinOrderStateHolder(
        commonExchangeModelState = commonExchangeModelState,
        requestBid = requestBid,
        requestAsk = requestAsk,
        maxOrderBookSize = maxOrderBookSize,
        context = context,
        market = market,
        userCoin = userCoin,
        userBTC = userBTC,
        userSeedMoney = userSeedMoney,
        btcPrice = btcPrice
    )
}