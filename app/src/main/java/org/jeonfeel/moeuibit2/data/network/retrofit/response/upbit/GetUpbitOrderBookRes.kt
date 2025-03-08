package org.jeonfeel.moeuibit2.data.network.retrofit.response.upbit

import androidx.annotation.Keep
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.jeonfeel.moeuibit2.data.network.retrofit.model.upbit.OrderBookModel
import org.jeonfeel.moeuibit2.data.usecase.OrderBookKind
import org.jeonfeel.moeuibit2.ui.main.exchange.ExchangeViewModel.Companion.ROOT_EXCHANGE_UPBIT
import org.jeonfeel.moeuibit2.utils.BigDecimalMapper.newBigDecimal

@Keep
@Serializable
data class GetUpbitOrderBookRes(
    val level: Int,
    val market: String,
    @SerialName("orderbook_units")
    val orderbookUnits: List<OrderbookUnit>,
    val timestamp: Long,
    @SerialName("total_ask_size")
    val totalAskSize: Double,
    @SerialName("total_bid_size")
    val totalBidSize: Double
) {
    fun mapTo(): List<OrderBookModel> {
        val askList = ArrayList<OrderBookModel>()
        val bidList = ArrayList<OrderBookModel>()
        orderbookUnits.forEach { orderBookUnit ->
            askList.add(
                OrderBookModel(
                    price = orderBookUnit.askPrice.newBigDecimal(
                        rootExchange = ROOT_EXCHANGE_UPBIT,
                        market = market
                    ),
                    size = orderBookUnit.askSize,
                    kind = OrderBookKind.ASK
                )
            )
            bidList.add(
                OrderBookModel(
                    price = orderBookUnit.bidPrice.newBigDecimal(
                        rootExchange = ROOT_EXCHANGE_UPBIT,
                        market = market
                    ),
                    size = orderBookUnit.bidSize,
                    kind = OrderBookKind.BID
                )
            )
        }
        askList.reverse()
        return askList + bidList
    }
}

@Keep
@Serializable
data class OrderbookUnit(
    @SerialName("ask_price")
    val askPrice: Double,
    @SerialName("ask_size")
    val askSize: Double,
    @SerialName("bid_price")
    val bidPrice: Double,
    @SerialName("bid_size")
    val bidSize: Double
)