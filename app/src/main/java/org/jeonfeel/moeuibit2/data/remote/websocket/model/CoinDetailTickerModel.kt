package org.jeonfeel.moeuibit2.data.remote.websocket.model

import androidx.compose.runtime.Stable
import com.google.gson.annotations.SerializedName

data class CoinDetailTickerModel (
    @Stable
    @SerializedName("cd")
    val code: String,
    @SerializedName("tp")
    val tradePrice: Double,
    @SerializedName("scr")
    val signedChangeRate: Double,
    @SerializedName("scp")
    val signed_change_price: Double
)