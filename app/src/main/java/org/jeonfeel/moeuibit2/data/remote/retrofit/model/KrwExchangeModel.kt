package org.jeonfeel.moeuibit2.data.remote.retrofit.model

import androidx.compose.runtime.Stable

data class KrwExchangeModel(
    @Stable
    val koreanName: String,
    @Stable
    val EnglishName: String,
    @Stable
    val market: String,
    @Stable
    val symbol: String,
    @Stable
    val opening_price: Double,
    var tradePrice: Double,
    var signedChangeRate: Double,
    var accTradePrice24h: Double,
    var warning: String
)