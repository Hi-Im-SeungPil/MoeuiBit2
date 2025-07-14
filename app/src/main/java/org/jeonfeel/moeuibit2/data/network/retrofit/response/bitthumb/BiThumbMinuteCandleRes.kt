package org.jeonfeel.moeuibit2.data.network.retrofit.response.bitthumb

import androidx.annotation.Keep
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Keep
@Serializable
data class BiThumbMinuteCandleRes(
    val market: String, // 마켓명

    @SerialName("candle_date_time_utc")
    val candleDateTimeUtc: String, // 캔들 기준 시각(UTC), yyyy-MM-dd'T'HH:mm:ss

    @SerialName("candle_date_time_kst")
    val candleDateTimeKst: String, // 캔들 기준 시각(KST), yyyy-MM-dd'T'HH:mm:ss

    @SerialName("opening_price")
    val openingPrice: Double, // 시가

    @SerialName("high_price")
    val highPrice: Double, // 고가

    @SerialName("low_price")
    val lowPrice: Double, // 저가

    @SerialName("trade_price")
    val tradePrice: Double, // 종가

    val timestamp: Long, // 캔들 종료 시각(KST 기준)

    @SerialName("candle_acc_trade_price")
    val candleAccTradePrice: Double, // 누적 거래 금액

    @SerialName("candle_acc_trade_volume")
    val candleAccTradeVolume: Double, // 누적 거래량

    val unit: Int // 분 단위(유닛)
)