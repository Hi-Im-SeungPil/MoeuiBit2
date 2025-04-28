package org.jeonfeel.moeuibit2.data.network.retrofit.response.bitthumb

import androidx.annotation.Keep
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Keep
@Serializable
data class BitThumbMarketCodeRes(
    val market: String,
    @SerialName("english_name")
    val englishName: String,
    @SerialName("korean_name")
    val koreanName: String,
    @SerialName("market_warning")
    val marketWarning: String,
)