package org.jeonfeel.moeuibit2.data.network.retrofit.response.upbit

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GetChartCandleRes(
    @SerialName("candle_acc_trade_price")
    val candleAccTradePrice: Double,

    @SerialName("candle_acc_trade_volume")
    val candleAccTradeVolume: Double,

    @SerialName("candle_date_time_kst")
    val candleDateTimeKst: String,

    @SerialName("candle_date_time_utc")
    val candleDateTimeUtc: String,

    @SerialName("high_price")
    val highPrice: Int,

    @SerialName("low_price")
    val lowPrice: Int,

    @SerialName("market")
    val market: String,

    @SerialName("opening_price")
    val openingPrice: Int,

    @SerialName("timestamp")
    val timestamp: Long,

    @SerialName("trade_price")
    val tradePrice: Int,

    @SerialName("unit")
    val unit: Int
)