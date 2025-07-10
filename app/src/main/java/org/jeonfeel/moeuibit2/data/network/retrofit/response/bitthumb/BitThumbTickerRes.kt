package org.jeonfeel.moeuibit2.data.network.retrofit.response.bitthumb

import androidx.annotation.Keep
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.jeonfeel.moeuibit2.constants.EXCHANGE_BITTHUMB
import org.jeonfeel.moeuibit2.data.network.retrofit.model.upbit.CommonExchangeModel
import org.jeonfeel.moeuibit2.data.network.retrofit.response.upbit.Caution
import org.jeonfeel.moeuibit2.utils.BigDecimalMapper.accBigDecimal
import org.jeonfeel.moeuibit2.utils.BigDecimalMapper.newBigDecimal
import org.jeonfeel.moeuibit2.utils.Utils

@Keep
@Serializable
data class BitThumbTickerRes(
    val market: String = "",
    val change: String = "",
    val timestamp: Long = 0L,
    @SerialName("acc_trade_price")
    val accTradePrice: Double = 0.0,
    @SerialName("acc_trade_price_24h")
    val accTradePrice24h: Double = 0.0,
    @SerialName("acc_trade_volume")
    val accTradeVolume: Double = 0.0,
    @SerialName("acc_trade_volume_24h")
    val accTradeVolume24h: Double = 0.0,
    @SerialName("change_price")
    val changePrice: Double = 0.0,
    @SerialName("change_rate")
    val changeRate: Double = 0.0,
    @SerialName("high_price")
    val highPrice: Double = 0.0,
    @SerialName("highest_52_week_date")
    val highest52WeekDate: String = "",
    @SerialName("highest_52_week_price")
    val highest52WeekPrice: Double = 0.0,
    @SerialName("low_price")
    val lowPrice: Double = 0.0,
    @SerialName("lowest_52_week_date")
    val lowest52WeekDate: String = "",
    @SerialName("lowest_52_week_price")
    val lowest52WeekPrice: Double = 0.0,
    @SerialName("opening_price")
    val openingPrice: Double = 0.0,
    @SerialName("prev_closing_price")
    val prevClosingPrice: Double = 0.0,
    @SerialName("signed_change_price")
    val signedChangePrice: Double = 0.0,
    @SerialName("signed_change_rate")
    val signedChangeRate: Double = 0.0,
    @SerialName("trade_date")
    val tradeDate: String = "",
    @SerialName("trade_date_kst")
    val tradeDateKst: String = "",
    @SerialName("trade_price")
    val tradePrice: Double = 0.0,
    @SerialName("trade_time")
    val tradeTime: String = "",
    @SerialName("trade_time_kst")
    val tradeTimeKst: String = "",
    @SerialName("trade_timestamp")
    val tradeTimestamp: Long = 0L,
    @SerialName("trade_volume")
    val tradeVolume: Double = 0.0,
) {
    fun mapToCommonExchangeModel(
        bitThumbMarketCodeRes: BitThumbMarketCodeRes? = null,
        biThumbWarningList: List<String> = emptyList(),
    ): CommonExchangeModel {
        val koreanName = bitThumbMarketCodeRes?.koreanName ?: ""
        val engName = bitThumbMarketCodeRes?.englishName ?: ""
        val initialConstant = Utils.extractInitials(koreanName)
        val caution = bitThumbMarketCodeRes?.marketWarning == "CAUTION"

        return CommonExchangeModel(
            koreanName = koreanName,
            englishName = engName,
            market = market,
            initialConstant = initialConstant,
            symbol = market.substring(4),
            openingPrice = openingPrice,
            tradePrice = tradePrice.newBigDecimal(
                EXCHANGE_BITTHUMB,
                market = market
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
            warning = caution,
            caution = mapToCaution(biThumbWarningList),
            askBid = "NONE",
            prevClosingPrice = prevClosingPrice
        )
    }

    private fun mapToCaution(warningTypes: List<String>): Caution? {
        if (warningTypes.isEmpty()) return Caution()

        var caution = Caution() // 기본값은 모든 필드가 false

        warningTypes.forEach { warningType ->
            caution = when (warningType) {
                "PRICE_SUDDEN_FLUCTUATION" -> caution.copy(priceFluctuations = true)
                "TRADING_VOLUME_SUDDEN_FLUCTUATION" -> caution.copy(tradingVolumeSoaring = true)
                "DEPOSIT_AMOUNT_SUDDEN_FLUCTUATION" -> caution.copy(depositAmountSoaring = true)
                "SPECIFIC_ACCOUNT_HIGH_TRANSACTION" -> caution.copy(concentrationOfSmallAccounts = true)
                "EXCHANGE_TRADING_CONCENTRATION" -> caution.copy(globalPriceDifferences = true)
                else -> caution // unknown type, no change
            }
        }

        return caution
    }
}