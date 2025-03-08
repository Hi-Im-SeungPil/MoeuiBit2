package org.jeonfeel.moeuibit2.data.network.websocket.model.upbit

import androidx.annotation.Keep
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.jeonfeel.moeuibit2.data.network.retrofit.model.upbit.CommonExchangeModel
import org.jeonfeel.moeuibit2.data.network.retrofit.response.upbit.UpbitMarketCodeRes
import org.jeonfeel.moeuibit2.ui.main.exchange.ExchangeViewModel
import org.jeonfeel.moeuibit2.utils.BigDecimalMapper.accBigDecimal
import org.jeonfeel.moeuibit2.utils.BigDecimalMapper.newBigDecimal
import org.jeonfeel.moeuibit2.utils.Utils
import org.jeonfeel.moeuibit2.utils.calculator.Calculator
import org.jeonfeel.moeuibit2.utils.commaFormat
import org.jeonfeel.moeuibit2.utils.decimalPoint
import org.jeonfeel.moeuibit2.utils.eighthDecimal
import org.jeonfeel.moeuibit2.utils.isTradeCurrencyKrw
import kotlin.math.abs

@Keep
@Serializable
data class UpbitSocketTradeRes(

    @SerialName("ask_bid")
    val askBid: String,

    @SerialName("best_ask_price")
    val bestAskPrice: Double,

    @SerialName("best_ask_size")
    val bestAskSize: Double,

    @SerialName("best_bid_price")
    val bestBidPrice: Double,

    @SerialName("best_bid_size")
    val bestBidSize: Double,

    @SerialName("change")
    val change: String,

    @SerialName("change_price")
    val changePrice: Double,

    @SerialName("code")
    val code: String,

    @SerialName("prev_closing_price")
    val prevClosingPrice: Double,

    @SerialName("sequential_id")
    val sequentialId: Long,

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
    fun mapTo(tempCoinTicker: CommonExchangeModel): CommonExchangeModel {
        return CommonExchangeModel(
            koreanName = "",
            englishName = "",
            market = code,
            initialConstant = "",
            symbol = code.substring(4),
            openingPrice = prevClosingPrice,
            tradePrice = tradePrice.newBigDecimal(
                ExchangeViewModel.ROOT_EXCHANGE_UPBIT,
                market = code
            ),
            signedChangeRate = Calculator.orderBookRateCalculator(
                prevClosingPrice,
                tradePrice
            ), // 계산
            accTradePrice24h = tempCoinTicker.accTradePrice24h,
            tradeDate = tradeDate,
            tradeTime = tradeTime,
            tradeVolume = tradeVolume,
            change = change,
            changePrice = changePrice,
            changeRate = tempCoinTicker.changeRate,
            highPrice = tempCoinTicker.highPrice,
            lowPrice = tempCoinTicker.lowPrice,
            signedChangePrice = tradePrice - prevClosingPrice, // 계산
            timestamp = timestamp,
            warning = false,
            askBid = "NONE",
            prevClosingPrice = prevClosingPrice
        )
    }
}