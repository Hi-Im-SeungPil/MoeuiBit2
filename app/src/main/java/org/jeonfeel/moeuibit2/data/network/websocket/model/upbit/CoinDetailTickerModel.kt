package org.jeonfeel.moeuibit2.data.network.websocket.model.upbit

import androidx.annotation.Keep
import androidx.compose.runtime.Stable
import com.google.gson.annotations.SerializedName

@Keep
data class CoinDetailTickerModel (
    @Stable
    @SerializedName("cd")
    val code: String,
    @SerializedName("tp")
    val tradePrice: Double,
    @SerializedName("scr")
    val signedChangeRate: Double,
    @SerializedName("scp")
    val signedChangePrice: Double
)