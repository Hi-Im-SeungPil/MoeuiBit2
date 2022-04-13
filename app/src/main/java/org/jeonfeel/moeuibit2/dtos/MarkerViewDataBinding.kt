package org.jeonfeel.moeuibit2.dtos

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