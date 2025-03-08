package org.jeonfeel.moeuibit2.data.network.websocket.model.upbit

import androidx.annotation.Keep
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.jeonfeel.moeuibit2.data.network.retrofit.model.upbit.OrderBookModel
import org.jeonfeel.moeuibit2.data.usecase.OrderBookKind
import org.jeonfeel.moeuibit2.ui.main.exchange.ExchangeViewModel.Companion.ROOT_EXCHANGE_UPBIT
import org.jeonfeel.moeuibit2.utils.BigDecimalMapper.newBigDecimal

@Keep
@Serializable
data class UpbitSocketOrderBookRes(
    val type: String = "",
    val code: String = "",
    val timestamp: Long = 0L,

    @SerialName("total_ask_size")
    val totalAskSize: Double = 0.0,

    @SerialName("total_bid_size")
    val totalBidSize: Double = 0.0,

    @SerialName("orderbook_units")
    val orderbook_units: List<OrderBookUnit> = listOf(),

    @SerialName("stream_type")
    val streamType: String = "",

    @SerialName("level")
    val level: Double = 0.0
) {
    fun mapTo(): List<OrderBookModel> {
        val askList = ArrayList<OrderBookModel>()
        val bidList = ArrayList<OrderBookModel>()
        orderbook_units.forEach { orderBookUnit ->
            askList.add(
                OrderBookModel(
                    price = orderBookUnit.askPrice.newBigDecimal(
                        rootExchange = ROOT_EXCHANGE_UPBIT,
                        market = code
                    ),
                    size = orderBookUnit.askSize,
                    kind = OrderBookKind.ASK
                )
            )
            bidList.add(
                OrderBookModel(
                    price = orderBookUnit.bidPrice.newBigDecimal(
                        rootExchange = ROOT_EXCHANGE_UPBIT,
                        market = code
                    ),
                    size = orderBookUnit.bidSize,
                    kind = OrderBookKind.BID
                )
            )
        }
        askList.reverse()
        return (askList + bidList).toList()
    }
}

@Keep
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