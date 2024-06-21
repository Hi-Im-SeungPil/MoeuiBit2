package org.jeonfeel.moeuibit2.data.network.websocket.model.upbit

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.jeonfeel.moeuibit2.data.network.retrofit.model.upbit.UpbitOrderBookModel
import org.jeonfeel.moeuibit2.data.usecase.OrderBookKind
import org.jeonfeel.moeuibit2.ui.main.exchange.ExchangeViewModel.Companion.ROOT_EXCHANGE_UPBIT
import org.jeonfeel.moeuibit2.utils.BigDecimalMapper.newBigDecimal
import java.math.BigDecimal

@Serializable
data class UpbitSocketOrderBookRes(
    val code: String,
    val level: Int,
    @SerialName("orderbook_units")
    val orderbookUnits: List<OrderBookUnit>,
    @SerialName("stream_type")
    val streamType: String,
    val timestamp: Long,
    @SerialName("total_ask_size")
    val totalAskSize: Double,
    @SerialName("total_bid_size")
    val totalBidSize: Double,
    val type: String
) {
    fun mapTo(): List<UpbitOrderBookModel> {
        val askList = ArrayList<UpbitOrderBookModel>()
        val bidList = ArrayList<UpbitOrderBookModel>()
        orderbookUnits.forEach { orderBookUnit ->
            askList.add(
                UpbitOrderBookModel(
                    price = orderBookUnit.askPrice.newBigDecimal(
                        rootExchange = ROOT_EXCHANGE_UPBIT,
                        market = code
                    ),
                    size = orderBookUnit.askSize,
                    kind = OrderBookKind.ASK
                )
            )
            bidList.add(
                UpbitOrderBookModel(
                    price = orderBookUnit.bidPrice.newBigDecimal(
                        rootExchange = ROOT_EXCHANGE_UPBIT,
                        market = code
                    ),
                    size = orderBookUnit.bidSize,
                    kind = OrderBookKind.BID
                )
            )
        }
        bidList.reverse()
        return (askList + bidList).toList()
    }
}

@Serializable
data class OrderBookUnit(
    @SerialName("ask_price")
    val askPrice: Double,
    @SerialName("ask_size")
    val askSize: Double,
    @SerialName("bid_price")
    val bidPrice: Double,
    @SerialName("bid_size")
    val bidSize: Double
)