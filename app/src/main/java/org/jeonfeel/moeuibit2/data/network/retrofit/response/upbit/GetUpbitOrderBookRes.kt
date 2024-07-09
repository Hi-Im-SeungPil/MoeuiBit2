package org.jeonfeel.moeuibit2.data.network.retrofit.response.upbit

import com.google.gson.annotations.SerializedName
import kotlinx.serialization.Serializable
import org.jeonfeel.moeuibit2.data.network.retrofit.model.upbit.OrderBookModel
import org.jeonfeel.moeuibit2.data.usecase.OrderBookKind
import org.jeonfeel.moeuibit2.ui.main.exchange.ExchangeViewModel.Companion.ROOT_EXCHANGE_UPBIT
import org.jeonfeel.moeuibit2.utils.BigDecimalMapper.newBigDecimal

@Serializable
data class GetUpbitOrderBookRes(
    val level: Int,
    val market: String,
    val orderbookUnits: List<OrderbookUnit>,
    val timestamp: Long,
    @SerializedName("total_ask_size")
    val totalAskSize: Double,
    @SerializedName("total_bid_size")
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
        bidList.reverse()
        return askList + bidList
    }
}

@Serializable
data class OrderbookUnit(
    @SerializedName("ask_price")
    val askPrice: Double,
    @SerializedName("ask_size")
    val askSize: Double,
    @SerializedName("bid_price")
    val bidPrice: Double,
    @SerializedName("bid_size")
    val bidSize: Double
)