package org.jeonfeel.moeuibit2.data.network.websocket.thunder.request.upbit

sealed interface UpbitSocketOrderBookReq
data class OrderBookRequestTypeField(
    val type: String = "orderbook",
    val codes: List<String>
): UpbitSocketOrderBookReq

data class OrderBookIsOnlyRealTimeField(
    val isOnlyRealTime: Boolean = true
): UpbitSocketOrderBookReq

data class OrderBookTicketField(
    val ticket: String
): UpbitSocketOrderBookReq