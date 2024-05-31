package org.jeonfeel.moeuibit2.data.network.retrofit.response.upbit

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Serializer

@Serializable
data class UpbitMarketCodeRes(
    @SerialName("english_name")
    val englishName: String,
    @SerialName("korean_name")
    val koreanName: String,
    val market: String,
    @SerialName("market_event")
    val marketEvent: MarketEvent,
    @SerialName("market_warning")
    val marketWarning: String
)

@Serializable
data class MarketEvent(
    val caution: Caution,
    val warning: Boolean
)

@Serializable
data class Caution(
    @SerialName("CONCENTRATION_OF_SMALL_ACCOUNTS")
    val concentrationOfSmallAccounts: Boolean,
    @SerialName("DEPOSIT_AMOUNT_SOARING")
    val depositAmountSoaring: Boolean,
    @SerialName("GLOBAL_PRICE_DIFFERENCES")
    val globalPriceDifferences: Boolean,
    @SerialName("PRICE_FLUCTUATIONS")
    val priceFluctuations: Boolean,
    @SerialName("TRADING_VOLUME_SOARING")
    val tradingVolumeSoaring: Boolean
)