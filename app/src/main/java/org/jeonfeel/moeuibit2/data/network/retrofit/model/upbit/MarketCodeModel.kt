package org.jeonfeel.moeuibit2.data.network.retrofit.model.upbit

import androidx.annotation.Keep

@Keep
data class MarketCodeModel(
    val market: String,
    val korean_name: String,
    val english_name: String,
    val market_warning: String
)