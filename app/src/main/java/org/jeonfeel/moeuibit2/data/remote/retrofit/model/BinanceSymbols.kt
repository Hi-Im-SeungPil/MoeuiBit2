package org.jeonfeel.moeuibit2.data.remote.retrofit.model

/**
 * 바이낸스는 symbols json array안에 마켓 코인 정보가 있음
 */
data class BinanceSymbols (
    val symbols: List<SymbolsEntity>
    )

data class SymbolsEntity(
    val baseAsset: String,
    val quoteAsset: String
)