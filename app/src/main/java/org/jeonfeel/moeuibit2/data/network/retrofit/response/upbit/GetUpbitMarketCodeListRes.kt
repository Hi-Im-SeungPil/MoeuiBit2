package org.jeonfeel.moeuibit2.data.network.retrofit.response.upbit

import androidx.annotation.Keep
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Serializer

@Keep
@Serializable
data class UpbitMarketCodeRes(
    @SerialName("english_name")
    val englishName: String,
    @SerialName("korean_name")
    val koreanName: String,
    val market: String,
    @SerialName("market_event")
    val marketEvent: MarketEvent,
)

@Keep
@Serializable
data class MarketEvent(
    val caution: Caution,
    val warning: Boolean
)

@Keep
@Serializable
data class Caution(
    @SerialName("CONCENTRATION_OF_SMALL_ACCOUNTS")
    val concentrationOfSmallAccounts: Boolean = false,

    @SerialName("DEPOSIT_AMOUNT_SOARING")
    val depositAmountSoaring: Boolean = false,

    @SerialName("GLOBAL_PRICE_DIFFERENCES")
    val globalPriceDifferences: Boolean = false,

    @SerialName("PRICE_FLUCTUATIONS")
    val priceFluctuations: Boolean = false,

    @SerialName("TRADING_VOLUME_SOARING")
    val tradingVolumeSoaring: Boolean = false
)