package org.jeonfeel.moeuibit2.data.remote.websocket.model.upbit

import androidx.annotation.Keep

@Keep
data class CoinDetailOrderBookModel(
    val price: Double,
    val size: Double,
    val state: Int
)