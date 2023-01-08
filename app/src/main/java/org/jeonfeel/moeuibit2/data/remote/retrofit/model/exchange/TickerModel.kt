package org.jeonfeel.moeuibit2.data.remote.retrofit.model.exchange

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
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