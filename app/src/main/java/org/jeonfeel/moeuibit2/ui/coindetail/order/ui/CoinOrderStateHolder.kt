package org.jeonfeel.moeuibit2.ui.coindetail.order.ui

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import org.jeonfeel.moeuibit2.constants.BTC_COMMISSION_FEE
import org.jeonfeel.moeuibit2.constants.KRW_COMMISSION_FEE
import org.jeonfeel.moeuibit2.constants.BTC_SYMBOL_PREFIX
import org.jeonfeel.moeuibit2.constants.KRW_SYMBOL_PREFIX
import org.jeonfeel.moeuibit2.data.local.room.entity.MyCoin
import org.jeonfeel.moeuibit2.data.network.retrofit.model.upbit.CommonExchangeModel
import org.jeonfeel.moeuibit2.utils.BigDecimalMapper.formattedString
import org.jeonfeel.moeuibit2.utils.BigDecimalMapper.formattedStringForKRW
import org.jeonfeel.moeuibit2.utils.BigDecimalMapper.newBigDecimal
import org.jeonfeel.moeuibit2.utils.NetworkConnectivityObserver
import org.jeonfeel.moeuibit2.utils.Utils
import org.jeonfeel.moeuibit2.utils.calculator.Calculator
import org.jeonfeel.moeuibit2.utils.formatWithComma
import org.jeonfeel.moeuibit2.utils.eighthDecimal
import org.jeonfeel.moeuibit2.utils.isKrwTradeCurrency
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
    private val requestAsk: (market: String, quantity: Double, totalPrice: Double, price: BigDecimal, totalPriceBTC: Double) -> Unit,
    private val commonExchangeModelState: State<CommonExchangeModel?>,
    private val maxOrderBookSize: State<Double>,
    private val userSeedMoney: State<Double>,
    private val userBTC: State<MyCoin>,
    private val userCoin: State<MyCoin>,
    private val btcPrice: State<BigDecimal>,
) {
    val percentageLabelList = listOf("최대", "75%", "50%", "25%", "10%")
    private val percentageList = listOf(1.0, 0.75, 0.5, 0.25, 0.1)

    private val _bidQuantity = mutableStateOf("")
    val bidQuantity: State<String> get() = _bidQuantity

    private val _askQuantity = mutableStateOf("")
    val askQuantity: State<String> get() = _askQuantity

    private val _bidTotalPrice = mutableStateOf(0.0)
    val bidTotalPrice: State<Double> get() = _bidTotalPrice

    private val _askTotalPrice = mutableStateOf(0.0)
    val askTotalPrice: State<Double> get() = _askTotalPrice

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

    fun getOrderBookBlockSize(
        orderBookSize: Double,
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
        orderBookPrice: BigDecimal,
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
            if (market.isKrwTradeCurrency()) {
                commonExchangeModelState.value?.let {
                    ((it.tradePrice.toDouble() * (quantity))).formatWithComma()
                } ?: ""
            } else {
                commonExchangeModelState.value?.let {
                    ((it.tradePrice.toDouble() * (quantity)) * btcPrice.value.toDouble()).formatWithComma()
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
                _bidTotalPrice.value = 0.0
            } else {
                _askQuantity.value = ""
                _askTotalPrice.value = 0.0
            }
            context.showToast("숫자만 입력 가능합니다.")
        } else if (commonExchangeModelState.value == null || commonExchangeModelState.value?.tradePrice?.toDouble()
            == 0.0
        ) {
            context.showToast("네트워크 통신 오류입니다.")
        } else if (value == "") {
            if (isBid) {
                _bidQuantity.value = ""
                _bidTotalPrice.value = 0.0
            } else {
                _askQuantity.value = ""
                _askTotalPrice.value = 0.0
            }
        } else if (value == "00") {
            return
        } else {
            val totalPrice = if (commonExchangeModelState.value?.tradePrice != null) {
                (commonExchangeModelState.value?.tradePrice!!.toDouble() * value.toDouble())
            } else {
                0.0
            }
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
        if (commonExchangeModelState.value != null) {
            if (market.startsWith(KRW_SYMBOL_PREFIX) && userSeedMoney.value == 0.0) {
                return
            }

            if (market.startsWith(BTC_SYMBOL_PREFIX) && userBTC.value.quantity == 0.0) {
                return
            }

            var percentageResult = BigDecimal(percentageList[index].toString())
            if (percentageResult == BigDecimal.ONE) {
                percentageResult = if (market.isKrwTradeCurrency()) {
                    percentageResult.minus(BigDecimal(KRW_COMMISSION_FEE))
                } else {
                    percentageResult.minus(BigDecimal(BTC_COMMISSION_FEE))
                }
            }

            val seedMoney = if (market.isKrwTradeCurrency()) {
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
        if (commonExchangeModelState.value != null && userCoin.value.quantity != 0.0) {
            val percentageResult = percentageList[index]
            val quantity = BigDecimal(userCoin.value.quantity).setScale(8, RoundingMode.FLOOR)
                .multiply(BigDecimal(percentageResult))

            _askQuantityPercentage.value = percentageLabelList[index]
            _askQuantity.value = quantity.setScale(8, RoundingMode.FLOOR).toString()
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

            if (market.isKrwTradeCurrency()) {
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
            if (market.isKrwTradeCurrency()) {
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
                BigDecimal(bidQuantity.value).multiply(commonExchangeModelState.value!!.tradePrice)

            val totalPrice = if (market.startsWith(KRW_SYMBOL_PREFIX)) {
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

                !Utils.isNetworkAvailable(context) || !NetworkConnectivityObserver.isNetworkAvailable.value -> {
                    context.showToast("인터넷 연결을 확인한 후 다시 시도해 주세요.")
                    return
                }

                else -> {}
            }

            when {
                market.startsWith(KRW_SYMBOL_PREFIX) -> {
                    when {
                        totalPrice.minus(BigDecimal(10)) > (BigDecimal(userSeedMoney.value)) -> {
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

                market.startsWith(BTC_SYMBOL_PREFIX) -> {
                    when {
                        totalPrice.toDouble() > (userBTC.value.quantity.eighthDecimal()
                            .toDouble()) -> {
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
            val commission = if (market.startsWith(KRW_SYMBOL_PREFIX)) {
                BigDecimal(bidQuantity.value).multiply(BigDecimal(KRW_COMMISSION_FEE))
                    .setScale(8, RoundingMode.FLOOR)
            } else {
                BigDecimal(bidQuantity.value).multiply(BigDecimal(BTC_COMMISSION_FEE))
                    .setScale(8, RoundingMode.FLOOR)
            }

            val bidQuantityResult =
                BigDecimal(bidQuantity.value).minus(commission)

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

            val tempPrice = if (market.startsWith(KRW_SYMBOL_PREFIX)) {
                userAskQuantity.multiply(commonExchangeModelState.value!!.tradePrice)
                    .setScale(0, RoundingMode.FLOOR)
            } else {
                userAskQuantity.multiply(commonExchangeModelState.value!!.tradePrice)
                    .setScale(8, RoundingMode.FLOOR)
            }

            val commission = if (market.startsWith(KRW_SYMBOL_PREFIX)) {
                tempPrice.multiply(KRW_COMMISSION_FEE.newBigDecimal(4))
            } else {
                tempPrice.multiply(BTC_COMMISSION_FEE.newBigDecimal(4))
            }

            val totalPrice = if (market.startsWith(KRW_SYMBOL_PREFIX)) {
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

                !Utils.isNetworkAvailable(context) || !NetworkConnectivityObserver.isNetworkAvailable.value -> {
                    context.showToast("인터넷 연결을 확인한 후 다시 시도해 주세요.")
                    return
                }

                else -> {}
            }

            when {
                market.startsWith(KRW_SYMBOL_PREFIX) -> {
                    when {
                        totalPrice.toDouble() < 5000 -> {
                            context.showToast("최소 매도 총액은 5000원 입니다.")
                            return
                        }

                        else -> {}
                    }
                }

                market.startsWith(BTC_SYMBOL_PREFIX) -> {
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
                totalPrice.toDouble(),
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
    requestAsk: (market: String, quantity: Double, totalPrice: Double, price: BigDecimal, totalPriceBTC: Double) -> Unit,
    userSeedMoney: State<Double>,
    userBTC: State<MyCoin>,
    userCoin: State<MyCoin>,
    btcPrice: State<BigDecimal>,
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