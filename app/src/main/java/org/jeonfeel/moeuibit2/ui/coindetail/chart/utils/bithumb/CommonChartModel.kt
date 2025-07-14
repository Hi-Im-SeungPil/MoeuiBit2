package org.jeonfeel.moeuibit2.ui.coindetail.chart.utils.bithumb

data class CommonChartModel(
    val highPrice: Double,
    val lowPrice: Double,
    val openingPrice: Double,
    val tradePrice: Double,
    val candleAccTradePrice: Double,
    val candleDateTimeUtc: String,
    val candleDateTimeKst: String
)
