package org.jeonfeel.moeuibit2.data.remote.websocket.model.bitthumb

data class BitthumbCoinDetailOrderBookModel(
    val content: Content,
    val type: String
)

data class Content(
    val asks: List<List<String>>,
    val bids: List<List<String>>,
    val datetime: String,
    val symbol: String
)