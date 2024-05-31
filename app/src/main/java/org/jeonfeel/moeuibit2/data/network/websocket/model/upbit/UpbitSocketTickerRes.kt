package org.jeonfeel.moeuibit2.data.network.websocket.model.upbit

import com.google.gson.annotations.SerializedName
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.jeonfeel.moeuibit2.data.network.retrofit.model.upbit.CommonExchangeModel
import org.jeonfeel.moeuibit2.data.network.retrofit.response.upbit.UpbitMarketCodeRes
import org.jeonfeel.moeuibit2.utils.BigDecimalMapper.newBigDecimal
import java.util.Date

@Serializable
data class UpbitSocketTickerRes(
    @SerialName("acc_ask_volume")
    val accAskVolume: Double,
    @SerialName("acc_bid_volume")
    val accBidVolume: Double,
    @SerialName("acc_trade_price")
    val accTradePrice: Double,
    @SerialName("acc_trade_price_24h")
    val accTradePrice24h: Double,
    @SerialName("acc_trade_volume")
    val accTradeVolume: Double,
    @SerialName("acc_trade_volume_24h")
    val accTradeVolume24h: Double,
    @SerialName("ask_bid")
    val askBid: String,
    @SerialName("change")
    val change: String,
    @SerialName("change_price")
    val changePrice: Double,
    @SerialName("change_rate")
    val changeRate: Double,
    @SerialName("code")
    val code: String,
    @SerialName("high_price")
    val highPrice: Double,
    @SerialName("highest_52_week_date")
    val highest52WeekDate: String,
    @SerialName("highest_52_week_price")
    val highest52WeekPrice: Double,
    @SerialName("is_trading_suspended")
    val isTradingSuspended: Boolean,
    @SerialName("low_price")
    val lowPrice: Double,
    @SerialName("lowest_52_week_date")
    val lowest52WeekDate: String,
    @SerialName("lowest_52_week_price")
    val lowest52WeekPrice: Double,
    @SerialName("market_state")
    val marketState: String,
    @SerialName("market_warning")
    val marketWarning: String,
    @SerialName("opening_price")
    val openingPrice: Double,
    @SerialName("prev_closing_price")
    val prevClosingPrice: Double,
    @SerialName("signed_change_price")
    val signedChangePrice: Double,
    @SerialName("signed_change_rate")
    val signedChangeRate: Double,
    @SerialName("stream_type")
    val streamType: String,
    @SerialName("timestamp")
    val timestamp: Long,
    @SerialName("trade_date")
    val tradeDate: String,
    @SerialName("trade_price")
    val tradePrice: Double,
    @SerialName("trade_time")
    val tradeTime: String,
    @SerialName("trade_timestamp")
    val tradeTimestamp: Long,
    @SerialName("trade_volume")
    val tradeVolume: Double,
    @SerialName("type")
    val type: String
) {
    fun mapTo(getUpbitMarketCodeRes: UpbitMarketCodeRes): CommonExchangeModel {
        return CommonExchangeModel(
            market = code,
            tradePrice = tradePrice.newBigDecimal(),
            tradeVolume = tradeVolume,
            changePrice = changePrice,
            changeRate = changeRate,
            openingPrice = openingPrice,
            signedChangePrice = signedChangePrice,
            signedChangeRate = signedChangeRate,
            highPrice = highPrice,
            lowPrice = lowPrice,
            accTradePrice24h = accTradePrice24h.newBigDecimal(),
            tradeDate = tradeDate,
            tradeTime = tradeTime,
            timestamp = timestamp,
            change = change,
            warning = getUpbitMarketCodeRes.marketEvent.warning,
            englishName = getUpbitMarketCodeRes.englishName,
            symbol = code.substring(4),
            koreanName = getUpbitMarketCodeRes.koreanName
        )
    }
}