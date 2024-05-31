package org.jeonfeel.moeuibit2.data.network.retrofit.model.upbit

import androidx.annotation.Keep

@Keep
data class CoinDetailOrderBookBidRetrofitModel(
    val bid_price: Double,
    val bid_size: Double,
)