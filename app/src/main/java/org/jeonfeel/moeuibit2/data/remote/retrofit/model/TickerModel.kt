package org.jeonfeel.moeuibit2.data.remote.retrofit.model

import com.google.gson.annotations.SerializedName

data class TickerModel (
    @SerializedName("cd")
    val code: String,
    @SerializedName("tp")
    val tradePrice: Double,
    @SerializedName("scr")
    val signedChangeRate: Double,
    @SerializedName("atp24h")
    val accTradePrice24h: Double,
    @SerializedName("pcp")
    val preClosingPrice: Double,
    @SerializedName("mw")
    val marketWarning: String
)