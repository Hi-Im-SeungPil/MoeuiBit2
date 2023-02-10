package org.jeonfeel.moeuibit2.ui.coindetail.chart.ui.marker

import androidx.annotation.Keep

@Keep
data class MarkerViewDataBinding(
    val dateTime: String,
    val highPrice: String,
    val openPrice: String,
    val lowPrice: String,
    val closePrice: String,
    val highPriceRate: String,
    val openPriceRate: String,
    val lowPriceRate: String,
    val closePriceRate: String,
    val tradeAmount: String
)