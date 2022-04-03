package org.jeonfeel.moeuibit2.data.remote.websocket.model

data class CoinDetailOrderBookModel(
    val price: Double,
    val size: Double,
    val state: Int
)