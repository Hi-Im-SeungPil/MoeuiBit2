package org.jeonfeel.moeuibit2.data.network.retrofit.request.upbit

data class GetChartCandleReq(
    val market: String,
    val to: String,
    val count: Int
)
