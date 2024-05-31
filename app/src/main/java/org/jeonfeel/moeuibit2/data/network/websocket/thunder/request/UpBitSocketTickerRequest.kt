package org.jeonfeel.moeuibit2.data.network.websocket.thunder.request

sealed interface UpBitSocketTickerRequest

data class RequestTicketField(
    val ticket: String
): UpBitSocketTickerRequest

data class RequestTypeField(
    val type: String,
    val codes: List<String>
): UpBitSocketTickerRequest

data class RequestFormatField(
    val format: String = "DEFAULT"
): UpBitSocketTickerRequest