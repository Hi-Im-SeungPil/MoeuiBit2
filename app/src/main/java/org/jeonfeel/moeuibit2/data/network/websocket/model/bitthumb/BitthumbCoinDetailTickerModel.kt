package org.jeonfeel.moeuibit2.data.network.websocket.model.bitthumb

import androidx.annotation.Keep
import androidx.compose.runtime.Stable
import com.google.gson.annotations.SerializedName

@Keep
data class BitthumbCoinDetailTickerModel(
    val content: BitthumbCoinDetailTickerModelContent,
    val type: String
)

@Keep
data class BitthumbCoinDetailTickerModelContent(
    @Stable
    @SerializedName("symbol")
    val code: String,
    @SerializedName("closePrice")
    val tradePrice: String,
    @SerializedName("chgRate")
    val signedChangeRate: String,
    @SerializedName("chgAmt")
    val signedChangePrice: String
)