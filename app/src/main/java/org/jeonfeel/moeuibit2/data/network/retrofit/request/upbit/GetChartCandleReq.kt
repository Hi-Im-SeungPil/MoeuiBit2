package org.jeonfeel.moeuibit2.data.network.retrofit.request.upbit

import androidx.annotation.Keep

@Keep
data class GetChartCandleReq(
    val candleType: String,
    val market: String,
    val to: String = "",
    val count: String = "200"
)
