package org.jeonfeel.moeuibit2.data.network.retrofit.model.upbit

import android.content.Context
import androidx.annotation.Keep
import androidx.datastore.preferences.preferencesDataStore
import com.google.gson.annotations.SerializedName
@Keep
data class ChartModel(
    @SerializedName("candle_date_time_kst")
    val candleDateTimeKst: String,
    @SerializedName("candle_date_time_utc")
    val candleDateTimeUtc: String,
    @SerializedName("opening_price")
    val openingPrice: Double,
    @SerializedName("high_price")
    val highPrice: Double,
    @SerializedName("low_price")
    val lowPrice: Double,
    @SerializedName("trade_price")
    val tradePrice: Double,
    @SerializedName("candle_acc_trade_price")
    val candleAccTradePrice: Double,
    val timestamp: Long
)