package org.jeonfeel.moeuibit2.data.network.retrofit.response.bitthumb

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class BiThumbMonthCandleRes(
    val market: String,

    @SerialName("candle_date_time_utc")
    val candleDateTimeUtc: String,

    @SerialName("candle_date_time_kst")
    val candleDateTimeKst: String,

    @SerialName("opening_price")
    val openingPrice: Double,

    @SerialName("high_price")
    val highPrice: Double,

    @SerialName("low_price")
    val lowPrice: Double,

    @SerialName("trade_price")
    val tradePrice: Double,

    val timestamp: Long,

    @SerialName("candle_acc_trade_price")
    val candleAccTradePrice: Double,

    @SerialName("candle_acc_trade_volume")
    val candleAccTradeVolume: Double,

    @SerialName("first_day_of_period")
    val firstDayOfPeriod: String
)
