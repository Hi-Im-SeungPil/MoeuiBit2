package org.jeonfeel.moeuibit2.data.network.retrofit.response.upbit

import com.google.gson.annotations.SerializedName
import kotlinx.serialization.Serializable
import org.jeonfeel.moeuibit2.data.network.retrofit.model.upbit.UpbitOrderBookModel
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
    fun mapTo(): List<UpbitOrderBookModel> {
        val askList = ArrayList<UpbitOrderBookModel>()
        val bidList = ArrayList<UpbitOrderBookModel>()
        orderbookUnits.forEach { orderBookUnit ->
            askList.add(
                UpbitOrderBookModel(
                    price = orderBookUnit.askPrice.newBigDecimal(
                        rootExchange = ROOT_EXCHANGE_UPBIT,
                        market = market
                    ),
                    size = orderBookUnit.askSize,
                    kind = OrderBookKind.ASK
                )
            )
            bidList.add(
                UpbitOrderBookModel(
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
        return (askList + bidList).toList()
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