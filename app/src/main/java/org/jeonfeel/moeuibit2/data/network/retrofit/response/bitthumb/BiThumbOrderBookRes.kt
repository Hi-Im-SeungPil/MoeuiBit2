package org.jeonfeel.moeuibit2.data.network.retrofit.response.bitthumb

import androidx.annotation.Keep
import androidx.compose.ui.util.fastForEachReversed
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.jeonfeel.moeuibit2.constants.EXCHANGE_BITTHUMB
import org.jeonfeel.moeuibit2.constants.EXCHANGE_UPBIT
import org.jeonfeel.moeuibit2.data.network.retrofit.model.upbit.OrderBookModel
import org.jeonfeel.moeuibit2.data.usecase.OrderBookKind
import org.jeonfeel.moeuibit2.utils.BigDecimalMapper.newBigDecimal

@Keep
@Serializable
data class BiThumbOrderBookRes(

    val market: String, // 마켓 코드

    val timestamp: Long, // 호가 생성 시각

    @SerialName("total_ask_size")
    val totalAskSize: Double, // 호가 매도 총 잔량

    @SerialName("total_bid_size")
    val totalBidSize: Double, // 호가 매수 총 잔량

    @SerialName("orderbook_units")
    val orderbookUnits: List<OrderbookUnit> // 호가 리스트
) {
    @Keep
    @Serializable
    data class OrderbookUnit(

        @SerialName("ask_price")
        val askPrice: Double, // 매도호가

        @SerialName("bid_price")
        val bidPrice: Double, // 매수호가

        @SerialName("ask_size")
        val askSize: Double, // 매도 잔량

        @SerialName("bid_size")
        val bidSize: Double  // 매수 잔량
    )

    fun mapToOrderBookModel(): List<OrderBookModel> {
        val askList = ArrayList<OrderBookModel>()
        val bidList = ArrayList<OrderBookModel>()
        orderbookUnits.subList(0, 14).fastForEachReversed { orderBookUnit ->
            askList.add(
                OrderBookModel(
                    price = orderBookUnit.askPrice.newBigDecimal(
                        rootExchange = EXCHANGE_BITTHUMB,
                        market = market
                    ),
                    size = orderBookUnit.askSize,
                    kind = OrderBookKind.ASK
                )
            )
            bidList.add(
                OrderBookModel(
                    price = orderBookUnit.bidPrice.newBigDecimal(
                        rootExchange = EXCHANGE_BITTHUMB,
                        market = market
                    ),
                    size = orderBookUnit.bidSize,
                    kind = OrderBookKind.BID
                )
            )
        }


        return (askList + bidList.reversed())
    }
}
