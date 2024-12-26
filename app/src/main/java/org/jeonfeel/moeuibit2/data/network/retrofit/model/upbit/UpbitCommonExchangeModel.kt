package org.jeonfeel.moeuibit2.data.network.retrofit.model.upbit

import androidx.annotation.Keep
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import org.jeonfeel.moeuibit2.data.network.retrofit.response.upbit.Caution
import org.jeonfeel.moeuibit2.ui.main.exchange.TickerAskBidState
import java.math.BigDecimal

@Keep
data class CommonExchangeModel(
    val koreanName: String = "",
    val englishName: String = "",
    val market: String = "",
    val initialConstant: String = "",
    val symbol: String = "",
    val openingPrice: Double = 0.0,
    val tradePrice: BigDecimal = BigDecimal.ZERO,
    val signedChangeRate: Double = 0.0,
    val accTradePrice24h: BigDecimal = BigDecimal.ZERO,
    val tradeDate: String = "",
    val tradeTime: String = "",
    val tradeVolume: Double = 0.0,
    val change: String = "",
    val changePrice: Double = 0.0,
    val changeRate: Double = 0.0,
    val highPrice: Double = 0.0,
    val lowPrice: Double = 0.0,
    val signedChangePrice: Double = 0.0,
    val timestamp: Long = 0L,
    val askBid: String? = null,
    val prevClosingPrice: Double = 0.0,
    val needAnimation: MutableState<String> = mutableStateOf(TickerAskBidState.NONE.name),
    val warning: Boolean = false,
    val caution: Caution? = null
)