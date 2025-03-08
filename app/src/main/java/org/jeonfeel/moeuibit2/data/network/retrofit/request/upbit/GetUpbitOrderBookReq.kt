package org.jeonfeel.moeuibit2.data.network.retrofit.request.upbit

import androidx.annotation.Keep

@Keep
data class GetUpbitOrderBookReq(
    val market: String
)
