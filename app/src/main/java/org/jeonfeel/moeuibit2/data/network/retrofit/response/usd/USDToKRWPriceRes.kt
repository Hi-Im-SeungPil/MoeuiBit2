package org.jeonfeel.moeuibit2.data.network.retrofit.response.usd

data class USDToKRWPriceRes(
    val date: String,
    val usd: USD,
) {
    data class USD(
        val krw: Double,
    )
}