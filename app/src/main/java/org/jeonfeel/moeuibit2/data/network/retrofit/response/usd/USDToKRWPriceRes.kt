package org.jeonfeel.moeuibit2.data.network.retrofit.response.usd

import kotlinx.serialization.Serializable

@Serializable
data class USDToKRWPriceRes(
    val date: String,
    val usd: USD,
) {
    @Serializable
    data class USD(
        val krw: Double,
    )
}