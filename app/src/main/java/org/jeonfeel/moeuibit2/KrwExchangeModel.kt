package org.jeonfeel.moeuibit2

import androidx.compose.runtime.Stable

data class KrwExchangeModel(
    @Stable
    val koreanName: String,
    @Stable
    val market: String,
    var tradePrice: Double,
    var signedChangeRate: Double,
    var accTradePrice24h: Double,
)