package org.jeonfeel.moeuibit2.data.remote.retrofit.model.bitthumb

data class BitthumbTickerContent (
    val content: BitthumbTickerModel
)

data class BitthumbTickerModel(
    val buyVolume: String,
    val chgAmt: String,
    val chgRate: String,
    val closePrice: String,
    val date: String,
    val highPrice: String,
    val lowPrice: String,
    val openPrice: String,
    val prevClosePrice: String,
    val sellVolume: String,
    val symbol: String,
    val tickType: String,
    val time: String,
    val value: String,
    val volume: String,
    val volumePower: String
)