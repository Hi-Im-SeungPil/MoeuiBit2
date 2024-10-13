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
    val highPrice: Double,

    @SerialName("low_price")
    val lowPrice: Double,

    @SerialName("market")
    val market: String,

    @SerialName("opening_price")
    val openingPrice: Double,

    @SerialName("timestamp")
    val timestamp: Long,

    @SerialName("trade_price")
    val tradePrice: Double,

    @SerialName("unit")
    val unit: Int = 0,

    @SerialName("prev_closing_price")
    val prevClosingPrice: Double? = 0.0,

    @SerialName("change_price")
    val changePrice: Double? = 0.0,

    @SerialName("change_rate")
    val changeRate: Double? = 0.0,

    @SerialName("first_day_of_period")
    val firstDayOfPeriod: String = "",
)