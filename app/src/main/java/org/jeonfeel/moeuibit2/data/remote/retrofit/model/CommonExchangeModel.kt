package org.jeonfeel.moeuibit2.data.remote.retrofit.model

import androidx.annotation.Keep
import androidx.compose.runtime.Stable

@Keep
data class CommonExchangeModel(
    @Stable
    val koreanName: String,
    @Stable
    val englishName: String,
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