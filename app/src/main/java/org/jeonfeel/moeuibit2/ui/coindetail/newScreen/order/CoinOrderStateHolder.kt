package org.jeonfeel.moeuibit2.ui.coindetail.newScreen.order

import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import org.jeonfeel.moeuibit2.data.network.retrofit.model.upbit.CommonExchangeModel
import org.jeonfeel.moeuibit2.data.usecase.OrderBookKind
import org.jeonfeel.moeuibit2.utils.calculator.Calculator
import org.jeonfeel.moeuibit2.utils.commaFormat
import org.jeonfeel.moeuibit2.utils.secondDecimal
import org.jeonfeel.moeuibit2.utils.thirdDecimal
import java.math.BigDecimal
import kotlin.math.round

class CoinOrderStateHolder(
    private val commonExchangeModelState: State<CommonExchangeModel?>,
    private val coinPrice: BigDecimal,
    private val maxOrderBookSize: State<Double>
//    private val quantityState: Int
) {
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

    fun getIsMatchedTradePrice(
        orderBookPrice: BigDecimal
    ): Boolean {
        return orderBookPrice == commonExchangeModelState.value?.tradePrice
    }

    fun getOrderBookIndicationText(orderBookIndicationState: String, quantity: Double): String {
        return if (orderBookIndicationState == "quantity") {
            quantity.thirdDecimal()
        } else {
            commonExchangeModelState.value?.let {
                ((it.tradePrice.toDouble() * (quantity))).commaFormat()
            } ?: ""
        }
    }

    fun getOrderBookIndicationText(orderBookIndicationState: String): String {
        return if (orderBookIndicationState == "quantity") {
            "총액 기준 보기"
        } else {
            "수량 기준 보기"
        }
    }

//    fun getOrderBookText(orderBookSize: Double): String {
//        return when (quantityState) {
//            1 -> {
//                orderBookSize
//            }
//
//            2 -> {
//                (orderBookSize.newBigDecimal() * coinPrice)
//            }
//
//            else -> {
//
//            }
//        }
}

@Composable
fun rememberCoinOrderStateHolder(
    commonExchangeModelState: State<CommonExchangeModel?>,
    coinPrice: BigDecimal,
    maxOrderBookSize: State<Double>
) = remember {
    CoinOrderStateHolder(
        commonExchangeModelState = commonExchangeModelState,
        coinPrice = coinPrice,
        maxOrderBookSize = maxOrderBookSize
    )
}