package org.jeonfeel.moeuibit2.data.network.retrofit.model.upbit

import androidx.annotation.Keep
import java.math.BigDecimal

@Keep
data class CommonExchangeModel(
    val koreanName: String,
    val englishName: String,
    val market: String,
    val initialConstant: String,
    val symbol: String,
    val openingPrice: Double,
    val tradePrice: BigDecimal,
    val signedChangeRate: Double,
    val accTradePrice24h: BigDecimal,
    val tradeDate: String,
    val tradeTime: String,
    val tradeVolume: Double,
    val change: String,
    val changePrice: Double,
    val changeRate: Double,
    val highPrice: Double,
    val lowPrice: Double,
    val signedChangePrice: Double,
    val timestamp: Long,
    val warning: Boolean,
    val askBid: String? = null,
    val prevClosingPrice: Double = 0.0,
)