package org.jeonfeel.moeuibit2.data.network.retrofit.response.bitthumb

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.jeonfeel.moeuibit2.ui.coindetail.chart.utils.bithumb.CommonChartModel

@Serializable
data class BiThumbWeekCandleRes(
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

    @SerialName("prev_closing_price")
    val prevClosingPrice: Double,

    @SerialName("change_price")
    val changePrice: Double,

    @SerialName("change_rate")
    val changeRate: Double,

    @SerialName("converted_trade_price")
    val convertedTradePrice: Double? = null,

    @SerialName("first_day_of_period")
    val firstDayOfPeriod: String
)

fun BiThumbMinuteCandleRes.toChartCandle() = CommonChartModel(
    highPrice = this.highPrice,
    lowPrice = this.lowPrice,
    openingPrice = this.openingPrice,
    tradePrice = this.tradePrice,
    candleAccTradePrice = this.candleAccTradePrice,
    candleDateTimeUtc = this.candleDateTimeUtc,
    candleDateTimeKst = this.candleDateTimeKst
)

fun BiThumbDayCandleRes.toChartCandle() = CommonChartModel(
    highPrice = this.highPrice,
    lowPrice = this.lowPrice,
    openingPrice = this.openingPrice,
    tradePrice = this.tradePrice,
    candleAccTradePrice = this.candleAccTradePrice,
    candleDateTimeUtc = this.candleDateTimeUtc,
    candleDateTimeKst = this.candleDateTimeKst
)

fun BiThumbWeekCandleRes.toChartCandle() = CommonChartModel(
    highPrice = this.highPrice,
    lowPrice = this.lowPrice,
    openingPrice = this.openingPrice,
    tradePrice = this.tradePrice,
    candleAccTradePrice = this.candleAccTradePrice,
    candleDateTimeUtc = this.candleDateTimeUtc,
    candleDateTimeKst = this.candleDateTimeKst
)

fun BiThumbMonthCandleRes.toChartCandle() = CommonChartModel(
    highPrice = this.highPrice,
    lowPrice = this.lowPrice,
    openingPrice = this.openingPrice,
    tradePrice = this.tradePrice,
    candleAccTradePrice = this.candleAccTradePrice,
    candleDateTimeUtc = this.candleDateTimeUtc,
    candleDateTimeKst = this.candleDateTimeKst
)
