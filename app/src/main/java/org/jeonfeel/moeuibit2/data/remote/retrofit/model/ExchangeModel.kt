package org.jeonfeel.moeuibit2.data.remote.retrofit.model

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class ExchangeModel(
    val market: String,
    @SerializedName("trade_price")
    val tradePrice: Double,
    @SerializedName("signed_change_rate")
    val signedChangePrice: Double,
    @SerializedName("acc_trade_price_24h")
    val accTradePrice24h: Double,
    @SerializedName("prev_closing_price")
    val preClosingPrice: Double
)