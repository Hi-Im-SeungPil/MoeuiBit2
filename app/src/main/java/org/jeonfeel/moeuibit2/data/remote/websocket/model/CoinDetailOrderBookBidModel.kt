package org.jeonfeel.moeuibit2.data.remote.websocket.model

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class CoinDetailOrderBookBidModel(
    @SerializedName("bp")
    val bid_price: Double,
    @SerializedName("bs")
    val bid_size: Double,
)