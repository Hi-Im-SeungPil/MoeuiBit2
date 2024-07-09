package org.jeonfeel.moeuibit2.ui.coindetail.newScreen.order

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import org.jeonfeel.moeuibit2.data.usecase.OrderBookKind
import org.jeonfeel.moeuibit2.utils.BigDecimalMapper.newBigDecimal
import org.jeonfeel.moeuibit2.utils.calculator.Calculator
import org.jeonfeel.moeuibit2.utils.secondDecimal
import java.math.BigDecimal
import kotlin.math.round

class CoinOrderStateHolder(
    private val preClosedPrice: Double,
    private val coinPrice: BigDecimal,
    private val maxOrderBookSize: Double
//    private val quantityState: Int
) {

    /**
     * 호가창 전일대비 값 받아옴
     */
    fun getOrderBookItemFluctuateRate(orderBookPrice: Double): String {
        return Calculator.orderBookRateCalculator(
            preClosingPrice = preClosedPrice,
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
            preClosingPrice = preClosedPrice,
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
        val blockSize = round(orderBookSize / maxOrderBookSize * 100)
        return if (blockSize.isNaN()) {
            0f
        } else {
            blockSize.toFloat() / 100
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
    preClosedPrice: Double,
    coinPrice: BigDecimal,
    maxOrderBookSize: Double
) = remember {
    CoinOrderStateHolder(
        preClosedPrice = preClosedPrice,
        coinPrice = coinPrice,
        maxOrderBookSize = maxOrderBookSize
    )
}