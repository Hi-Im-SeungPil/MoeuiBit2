package org.jeonfeel.moeuibit2.data.network.websocket.model.bitthumb

import androidx.compose.ui.util.fastForEachReversed
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.jeonfeel.moeuibit2.constants.EXCHANGE_UPBIT
import org.jeonfeel.moeuibit2.data.network.retrofit.model.upbit.OrderBookModel
import org.jeonfeel.moeuibit2.data.usecase.OrderBookKind
import org.jeonfeel.moeuibit2.utils.BigDecimalMapper.newBigDecimal

@Serializable
data class BithumbSocketOrderbookRes(
    @SerialName("type")
    val type: String, // 타입

    @SerialName("code")
    val code: String, // 마켓 코드 (ex. KRW-BTC)

    @SerialName("orderbook")
    val orderbook: String, // 호가

    @SerialName("total_ask_size")
    val totalAskSize: Double, // 호가 매도 총 잔량

    @SerialName("total_bid_size")
    val totalBidSize: Double, // 호가 매수 총 잔량

    @SerialName("orderbook_units")
    val orderbookUnits: List<OrderBookUnit>, // 호가 목록

    @SerialName("timestamp")
    val timestamp: Long, // 타임스탬프 (ms)

    @SerialName("level")
    val level: Double // 호가 모아보기 단위
) {
    @Serializable
    data class OrderBookUnit(
        @SerialName("ask_price")
        val askPrice: Double, // 매도 호가

        @SerialName("bid_price")
        val bidPrice: Double, // 매수 호가

        @SerialName("ask_size")
        val askSize: Double, // 매도 잔량

        @SerialName("bid_size")
        val bidSize: Double // 매수 잔량
    )

    fun mapToOrderBookModel(): List<OrderBookModel> {
        val askList = ArrayList<OrderBookModel>()
        val bidList = ArrayList<OrderBookModel>()
        orderbookUnits.fastForEachReversed { orderBookUnit ->
            askList.add(
                OrderBookModel(
                    price = orderBookUnit.askPrice.newBigDecimal(
                        rootExchange = EXCHANGE_UPBIT,
                        market = code
                    ),
                    size = orderBookUnit.askSize,
                    kind = OrderBookKind.ASK
                )
            )
            bidList.add(
                OrderBookModel(
                    price = orderBookUnit.bidPrice.newBigDecimal(
                        rootExchange = EXCHANGE_UPBIT,
                        market = code
                    ),
                    size = orderBookUnit.bidSize,
                    kind = OrderBookKind.BID
                )
            )
        }
        return (askList + bidList.reversed()).toList()
    }
}