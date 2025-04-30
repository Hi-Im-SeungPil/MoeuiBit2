package org.jeonfeel.moeuibit2.data.network.websocket.model.bitthumb

import androidx.annotation.Keep
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.jeonfeel.moeuibit2.constants.EXCHANGE_UPBIT
import org.jeonfeel.moeuibit2.data.network.retrofit.model.upbit.CommonExchangeModel
import org.jeonfeel.moeuibit2.data.network.retrofit.response.bitthumb.BitThumbMarketCodeRes
import org.jeonfeel.moeuibit2.data.network.websocket.model.upbit.UpbitSocketTickerRes.DeListingDate
import org.jeonfeel.moeuibit2.utils.BigDecimalMapper.accBigDecimal
import org.jeonfeel.moeuibit2.utils.BigDecimalMapper.newBigDecimal
import org.jeonfeel.moeuibit2.utils.Utils

@Keep
@Serializable
data class BithumbSocketTickerRes(
    @SerialName("acc_ask_volume")
    val accAskVolume: Double = 0.0,

    @SerialName("acc_bid_volume")
    val accBidVolume: Double = 0.0,

    @SerialName("acc_trade_price")
    val accTradePrice: Double = 0.0,

    @SerialName("acc_trade_price_24h")
    val accTradePrice24h: Double = 0.0,

    @SerialName("acc_trade_volume")
    val accTradeVolume: Double = 0.0,

    @SerialName("acc_trade_volume_24h")
    val accTradeVolume24h: Double = 0.0,

    @SerialName("ask_bid")
    val askBid: String = "",

    @SerialName("change")
    val change: String = "",

    @SerialName("change_price")
    val changePrice: Double = 0.0,

    @SerialName("change_rate")
    val changeRate: Double = 0.0,

    @SerialName("code")
    val code: String = "",

    @SerialName("high_price")
    val highPrice: Double = 0.0,

    @SerialName("highest_52_week_date")
    val highest52WeekDate: String = "",

    @SerialName("highest_52_week_price")
    val highest52WeekPrice: Double = 0.0,

    @SerialName("is_trading_suspended")
    val isTradingSuspended: Boolean = false,

    @SerialName("low_price")
    val lowPrice: Double = 0.0,

    @SerialName("lowest_52_week_date")
    val lowest52WeekDate: String = "",

    @SerialName("lowest_52_week_price")
    val lowest52WeekPrice: Double = 0.0,

    @SerialName("market_state")
    val marketState: String = "",

    @SerialName("opening_price")
    val openingPrice: Double = 0.0,

    @SerialName("prev_closing_price")
    val prevClosingPrice: Double = 0.0,

    @SerialName("signed_change_price")
    val signedChangePrice: Double = 0.0,

    @SerialName("signed_change_rate")
    val signedChangeRate: Double = 0.0,

    @SerialName("stream_type")
    val streamType: String = "",

    @SerialName("timestamp")
    val timestamp: Long = 0L,

    @SerialName("trade_date")
    val tradeDate: String = "",

    @SerialName("trade_price")
    val tradePrice: Double = 0.0,

    @SerialName("trade_time")
    val tradeTime: String = "",

    @SerialName("trade_timestamp")
    val tradeTimestamp: Long = 0L,

    @SerialName("trade_volume")
    val tradeVolume: Double = 0.0,

    @SerialName("type")
    val type: String = "",

    @SerialName("market_warning")
    val marketWarning: String = "NONE",

    @SerialName("delisting_date")
    val delistingDate: DeListingDate? = null,
) {
    @Keep
    @Serializable
    data class DeListingDate(
        val year: Int,
        val month: Int,
        val day: Int,
    )

    fun mapToCommonExchangeModel(bitThumbMarketCodeRes: BitThumbMarketCodeRes? = null): CommonExchangeModel {
        val koreanName = bitThumbMarketCodeRes?.koreanName ?: ""
        val engName = bitThumbMarketCodeRes?.englishName ?: ""
        val initialConstant = Utils.extractInitials(koreanName)
        val warning = bitThumbMarketCodeRes?.marketWarning == "WARNING"

        return CommonExchangeModel(
            koreanName = koreanName,
            englishName = engName,
            market = code,
            initialConstant = initialConstant,
            symbol = code.substring(4),
            openingPrice = openingPrice,
            tradePrice = tradePrice.newBigDecimal(
                EXCHANGE_UPBIT,
                market = code
            ),
            signedChangeRate = signedChangeRate * 100,
            accTradePrice24h = accTradePrice24h.accBigDecimal(),
            tradeDate = tradeDate,
            tradeTime = tradeTime,
            tradeVolume = tradeVolume,
            change = change,
            changePrice = changePrice,
            changeRate = changeRate * 100,
            highPrice = highPrice,
            lowPrice = lowPrice,
            signedChangePrice = signedChangePrice,
            timestamp = timestamp,
            warning = warning,
            caution = null,
            askBid = "NONE",
            prevClosingPrice = prevClosingPrice
        )
    }
}
