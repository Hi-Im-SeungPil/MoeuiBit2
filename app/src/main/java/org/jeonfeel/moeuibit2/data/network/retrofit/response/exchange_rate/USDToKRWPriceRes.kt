package org.jeonfeel.moeuibit2.data.network.retrofit.response.exchange_rate

import androidx.annotation.Keep
import kotlinx.serialization.Serializable

@Keep
@Serializable
data class USDToKRWPriceRes(
    val date: String,
    val usd: USD,
) {
    @Keep
    @Serializable
    data class USD(
        val krw: Double,
    )
}