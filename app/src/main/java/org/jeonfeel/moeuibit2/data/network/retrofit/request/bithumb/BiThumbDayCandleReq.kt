package org.jeonfeel.moeuibit2.data.network.retrofit.request.bithumb

data class BiThumbDayCandleReq (
    val market: String,
    val to: String = "",
    val count: String,
    val convertingPriceUnit: String = "KRW"
)