package org.jeonfeel.moeuibit2.data.network.retrofit.request.bithumb

data class BiThumbWeekCandleReq(
    val market: String,
    val to: String = "",
    val count: String,
)
