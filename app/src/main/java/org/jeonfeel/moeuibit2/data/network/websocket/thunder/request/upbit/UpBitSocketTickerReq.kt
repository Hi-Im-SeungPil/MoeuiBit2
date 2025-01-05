package org.jeonfeel.moeuibit2.data.network.websocket.thunder.request.upbit

sealed interface UpBitSocketTickerReq

data class RequestTicketField(
    val ticket: String
) : UpBitSocketTickerReq

data class RequestTypeField(
    val type: String,
    val codes: List<String>,
) : UpBitSocketTickerReq

data class RequestFormatField(
    val format: String = "DEFAULT"
) : UpBitSocketTickerReq

//data class RequestSnapShot(
//    val is_only_snapshot: Boolean = true
//): UpBitSocketTickerReq