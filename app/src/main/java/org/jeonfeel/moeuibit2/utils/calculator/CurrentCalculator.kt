package org.jeonfeel.moeuibit2.utils.calculator

import com.orhanobut.logger.Logger
import org.jeonfeel.moeuibit2.constants.KRW_COMMISSION_FEE
import org.jeonfeel.moeuibit2.constants.SELECTED_BTC_MARKET
import org.jeonfeel.moeuibit2.constants.SELECTED_KRW_MARKET
import org.jeonfeel.moeuibit2.utils.*
import java.math.BigDecimal
import java.math.RoundingMode
import kotlin.math.abs
import kotlin.math.round

object CurrentCalculator {

    fun tradePriceCalculator(tradePrice: Double, marketState: Int): String {
        return if (marketState == SELECTED_KRW_MARKET) {
            if (tradePrice == 0.0 || tradePrice < 0.00000001) {
                "0"
            } else {
                if (tradePrice >= 1_000) {
                    round(tradePrice).commaFormat()
                } else if (tradePrice >= 100 && tradePrice < 1_000) {
                    tradePrice.firstDecimal()
                } else if (tradePrice >= 10 && tradePrice < 100) {
                    tradePrice.secondDecimal()
                } else if (tradePrice >= 1 && tradePrice < 10) {
                    tradePrice.thirdDecimal()
                } else if (tradePrice >= 0.1 && tradePrice < 1) {
                    tradePrice.forthDecimal()
                } else if (tradePrice >= 0.01 && tradePrice < 0.1) {
                    tradePrice.fiveDecimal()
                } else if (tradePrice >= 0.001 && tradePrice < 0.01) {
                    tradePrice.sixthDecimal()
                } else if (tradePrice >= 0.0001 && tradePrice < 0.001) {
                    tradePrice.sevenDecimal()
                } else {
                    tradePrice.eighthDecimal()
                }
            }
        } else if (marketState == SELECTED_BTC_MARKET) {
            tradePrice.eighthDecimal()
        } else {
            ""
        }
    }

    fun tradePriceCalculator(tradePrice: Float, marketState: Int): String {
        return if (marketState == SELECTED_KRW_MARKET) {
            if (tradePrice == 0.0f || tradePrice < 0.00000001) {
                "0"
            } else {
                if (tradePrice >= 1000) {
                    round(tradePrice).toLong().commaFormat()
                } else if (tradePrice >= 100 && tradePrice < 1000) {
                    tradePrice.firstDecimal()
                } else if (tradePrice >= 10 && tradePrice < 100) {
                    tradePrice.secondDecimal()
                } else if (tradePrice >= 1 && tradePrice < 10) {
                    tradePrice.thirdDecimal()
                } else if (tradePrice >= 0.1 && tradePrice < 1) {
                    tradePrice.forthDecimal()
                } else if (tradePrice >= 0.01 && tradePrice < 0.1) {
                    tradePrice.fiveDecimal()
                } else if (tradePrice >= 0.001 && tradePrice < 0.01) {
                    tradePrice.sixthDecimal()
                } else if (tradePrice >= 0.0001 && tradePrice < 0.001) {
                    tradePrice.sevenDecimal()
                } else {
                    tradePrice.eighthDecimal()
                }
            }
        } else if (marketState == SELECTED_BTC_MARKET) {
            tradePrice.eighthDecimal()
        } else {
            ""
        }
    }

    fun tradePriceCalculatorNoStringFormat(tradePrice: Double, marketState: Int): Double {
        return if (marketState == SELECTED_KRW_MARKET) {
            if (tradePrice == 0.0 || tradePrice < 0.00000001) {
                0.0
            } else {
                if (tradePrice >= 100) {
                    round(tradePrice)
                } else if (tradePrice < 100 && tradePrice >= 1) {
                    tradePrice.secondDecimal().toDouble()
                } else {
                    tradePrice.forthDecimal().toDouble()
                }
            }
        } else if (marketState == SELECTED_BTC_MARKET) {
            tradePrice.eighthDecimal().toDouble()
        } else {
            -1.0
        }
    }

    fun signedChangeRateCalculator(signedChangeRate: Double): String {
        return (signedChangeRate * 100).secondDecimal()
    }

    fun accTradePrice24hCalculator(accTradePrice24h: Double, marketState: Int): String {
        return when (marketState) {
            SELECTED_KRW_MARKET -> {
                round(accTradePrice24h * 0.000001).commaFormat()
            }

            SELECTED_BTC_MARKET -> {
                accTradePrice24h.thirdDecimal()
            }

            else -> {
                ""
            }
        }
    }

    fun btcToKrw(price: Double, marketState: Int): String {
        return if (marketState == SELECTED_BTC_MARKET) {
            tradePriceCalculator(price, SELECTED_KRW_MARKET)
        } else {
            ""
        }
    }

    fun total(
        isKrw: Boolean,
        currentPrice: BigDecimal,
        designatePrice: Double
    ): Double {
        return if (isKrw) {
            ((designatePrice * 0.9995) / currentPrice.toDouble()).eighthDecimal().toDouble()
        } else {
            ((designatePrice * 0.9975) / currentPrice.toDouble()).eighthDecimal().toDouble()
        }
    }

    fun getUserCoinValue(
        userCoinQuantity: Double,
        currentPrice: BigDecimal,
        btcPrice: BigDecimal?
    ): String {
        if (userCoinQuantity == 0.0 || currentPrice == BigDecimal.ZERO) {
            return "0"
        }

        val userCoinValue = if (btcPrice == BigDecimal.ZERO) {
            userCoinQuantity.toBigDecimal().multiply(currentPrice).toDouble()
        } else {
            userCoinQuantity.toBigDecimal().multiply(currentPrice).multiply(btcPrice).toDouble()
        }
        return tradePriceCalculator(userCoinValue, SELECTED_KRW_MARKET)
    }
}