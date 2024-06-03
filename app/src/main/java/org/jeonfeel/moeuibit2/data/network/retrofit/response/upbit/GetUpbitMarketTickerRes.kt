package org.jeonfeel.moeuibit2.data.network.retrofit.response.upbit

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Serializer
import org.jeonfeel.moeuibit2.data.network.retrofit.model.upbit.CommonExchangeModel
import org.jeonfeel.moeuibit2.utils.BigDecimalMapper.newBigDecimal

@Serializable
data class GetUpbitMarketTickerRes(
    val market: String = "",
    val change: String = "",
    val timestamp: Long = 0L,
    @SerialName("acc_trade_price")
    val accTradePrice: Double,
    @SerialName("acc_trade_price_24h")
    val accTradePrice24h: Double,
    @SerialName("acc_trade_volume")
    val accTradeVolume: Double,
    @SerialName("acc_trade_volume_24h")
    val accTradeVolume24h: Double,
    @SerialName("change_price")
    val changePrice: Double,
    @SerialName("change_rate")
    val changeRate: Double,
    @SerialName("high_price")
    val highPrice: Double,
    @SerialName("highest_52_week_date")
    val highest52WeekDate: String,
    @SerialName("highest_52_week_price")
    val highest52WeekPrice: Double,
    @SerialName("low_price")
    val lowPrice: Double,
    @SerialName("lowest_52_week_date")
    val lowest52WeekDate: String,
    @SerialName("lowest_52_week_price")
    val lowest52WeekPrice: Double,
    @SerialName("opening_price")
    val openingPrice: Double,
    @SerialName("prev_closing_price")
    val prevClosingPrice: Double,
    @SerialName("signed_change_price")
    val signedChangePrice: Double,
    @SerialName("signed_change_rate")
    val signedChangeRate: Double,
    @SerialName("trade_date")
    val tradeDate: String,
    @SerialName("trade_date_kst")
    val tradeDateKst: String = "",
    @SerialName("trade_price")
    val tradePrice: Double,
    @SerialName("trade_time")
    val tradeTime: String,
    @SerialName("trade_time_kst")
    val tradeTimeKst: String = "",
    @SerialName("trade_timestamp")
    val tradeTimestamp: Long,
    @SerialName("trade_volume")
    val tradeVolume: Double
) {
    fun mapTo(getUpbitMarketCodeRes: UpbitMarketCodeRes): CommonExchangeModel {
        return CommonExchangeModel(
            market = market,
            tradePrice = tradePrice.newBigDecimal(),
            tradeVolume = tradeVolume,
            changePrice = changePrice,
            changeRate = changeRate * 100,
            openingPrice = openingPrice,
            signedChangePrice = signedChangePrice,
            signedChangeRate = signedChangeRate * 100,
            highPrice = highPrice,
            lowPrice = lowPrice,
            accTradePrice24h = accTradePrice24h.newBigDecimal(),
            tradeDate = tradeDate,
            tradeTime = tradeTime,
            timestamp = timestamp,
            change = change,
            warning = getUpbitMarketCodeRes.marketEvent.warning,
            englishName = getUpbitMarketCodeRes.englishName,
            symbol = market.substring(4),
            koreanName = getUpbitMarketCodeRes.koreanName
        )
    }
}