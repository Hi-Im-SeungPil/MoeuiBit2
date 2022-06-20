package org.jeonfeel.moeuibit2.data.remote.websocket.model

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class CoinDetailOrderBookAskModel(
    @SerializedName("ap")
    val ask_price: Double,
    @SerializedName("as")
    val ask_size: Double
)