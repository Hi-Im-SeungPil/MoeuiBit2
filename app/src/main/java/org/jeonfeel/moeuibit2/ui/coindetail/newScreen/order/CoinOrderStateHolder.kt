package org.jeonfeel.moeuibit2.ui.coindetail.newScreen.order

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import com.orhanobut.logger.Logger
import org.jeonfeel.moeuibit2.data.network.retrofit.model.upbit.CommonExchangeModel
import org.jeonfeel.moeuibit2.data.usecase.OrderBookKind
import org.jeonfeel.moeuibit2.utils.BigDecimalMapper.formattedString
import org.jeonfeel.moeuibit2.utils.BigDecimalMapper.formattedStringForQuantity
import org.jeonfeel.moeuibit2.utils.BigDecimalMapper.newBigDecimal
import org.jeonfeel.moeuibit2.utils.calculator.Calculator
import org.jeonfeel.moeuibit2.utils.commaFormat
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
    val getUserSeedMoney: () -> Long
) {
    private val _bidQuantity = mutableStateOf("0")
    val bidQuantity: State<String> get() = _bidQuantity
    private val _askQuantity = mutableStateOf("0")
    val askQuantity: State<String> get() = _askQuantity

    /**
     * 호가창 전일대비 값 받아옴
     */
    fun getOrderBookItemFluctuateRate(orderBookPrice: Double): String {
        return Calculator.orderBookRateCalculator(
            preClosingPrice = commonExchangeModelState.value?.openingPrice ?: 1.0,
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
            commonExchangeModelState.value?.let {
                ((it.tradePrice.toDouble() * (quantity))).commaFormat()
            } ?: ""
        }
    }

    /**
     * 호가창 수량 / 총액 텍스트
     */
    fun getOrderBookIndicationText(orderBookIndicationState: String): String {
        return if (orderBookIndicationState == "quantity") {
            "총액 기준 보기"
        } else {
            "수량 기준 보기"
        }
    }

    fun quantityOnValueChanged(value: String, isBid: Boolean) {
        if (value.toDoubleOrNull() == null && value != "") {
            if(isBid) {
                _bidQuantity.value = "0"
            } else {
                _askQuantity.value = "0"
            }
            context.showToast("숫자만 입력 가능합니다.")
        } else if (commonExchangeModelState.value == null || commonExchangeModelState.value?.tradePrice?.toDouble()
            == 0.0
        ) {
            context.showToast("네트워크 통신 오류입니다.")
        } else {
            if(isBid) {
                _bidQuantity.value = value
            } else {
                _askQuantity.value = value
            }
        }
    }

    fun updateBidCoinQuantity(percentage: Double) {
        if (commonExchangeModelState.value != null) {
            val seedMoney = (getUserSeedMoney() * percentage).newBigDecimal(
                scale = 0,
                roundingMode = RoundingMode.FLOOR
            )
            val quantity =
                seedMoney.divide(commonExchangeModelState.value?.tradePrice, 8, RoundingMode.FLOOR)
            _bidQuantity.value = quantity.formattedStringForQuantity()
        } else {
            _bidQuantity.value = "0"
        }
    }

    fun updateAskCoinQuantity(percentage: Double) {
        // 유저가 가지고 있는 코인을 나눠서 세팅
    }

    fun updateBidQuantity() {

    }

    fun updateAskQuantity() {

    }
}

@Composable
fun rememberCoinOrderStateHolder(
    commonExchangeModelState: State<CommonExchangeModel?>,
    maxOrderBookSize: State<Double>,
    getUserSeedMoney: () -> Long,
    context: Context = LocalContext.current
) = remember {
    CoinOrderStateHolder(
        commonExchangeModelState = commonExchangeModelState,
        maxOrderBookSize = maxOrderBookSize,
        getUserSeedMoney = getUserSeedMoney,
        context = context
    )
}