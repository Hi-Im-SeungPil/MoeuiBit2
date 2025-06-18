package org.jeonfeel.moeuibit2.data.network.retrofit.response.bitthumb

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.jeonfeel.moeuibit2.data.network.retrofit.model.upbit.OrderBookModel
import org.jeonfeel.moeuibit2.data.usecase.OrderBookKind

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
        val askList = mutableListOf<OrderBookModel>()
        val bidList = mutableListOf<OrderBookModel>()

        for (i in orderbookUnits.indices) {
            val askOrderBookModel = OrderBookModel(
                price = orderbookUnits[i].askPrice.toBigDecimal(),
                size = orderbookUnits[i].askSize,
                kind = OrderBookKind.ASK
            )

            val bidOrderBookModel = OrderBookModel(
                price = orderbookUnits[i].bidPrice.toBigDecimal(),
                size = orderbookUnits[i].bidSize,
                kind = OrderBookKind.BID
            )

            askList.add(askOrderBookModel)
            bidList.add(bidOrderBookModel)
        }

        return (askList + bidList)
    }
}
