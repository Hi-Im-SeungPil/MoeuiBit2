package org.jeonfeel.moeuibit2.data.network.websocket.thunder.request

sealed interface UpBitSocketTickerReq

data class RequestTicketField(
    val ticket: String
): UpBitSocketTickerReq

data class RequestTypeField(
    val type: String,
    val codes: List<String>
): UpBitSocketTickerReq

data class RequestFormatField(
    val format: String = "DEFAULT"
): UpBitSocketTickerReq