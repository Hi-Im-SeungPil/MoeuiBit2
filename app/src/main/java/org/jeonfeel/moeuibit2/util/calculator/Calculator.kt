package org.jeonfeel.moeuibit2.util.calculator

import org.jeonfeel.moeuibit2.constant.SELECTED_KRW_MARKET
import org.jeonfeel.moeuibit2.util.eighthDecimal
import org.jeonfeel.moeuibit2.util.forthDecimal
import org.jeonfeel.moeuibit2.util.secondDecimal
import java.text.DecimalFormat
import kotlin.math.abs
import kotlin.math.floor
import kotlin.math.round

object Calculator {

    private val decimalFormat = DecimalFormat("###,###")
    private val decimalDecimalFormat = DecimalFormat("#.########")

    fun tradePriceCalculator(tradePrice: Double): String {
        return if (tradePrice >= 100) {
            round(tradePrice).toLong().toString()
        } else if (tradePrice < 100 && tradePrice >= 1) {
            tradePrice.secondDecimal()
        } else {
            tradePrice.forthDecimal()
        }
    }

    fun tradePriceCalculatorForChart(tradePrice: Double): String {
        return if (tradePrice >= 100) {
            decimalFormat.format(round(tradePrice))
        } else if (tradePrice < 100 && tradePrice >= 1) {
            tradePrice.secondDecimal()
        } else {
            tradePrice.forthDecimal()
        }
    }

    fun tradePriceCalculatorForChart(tradePrice: Float): String {
        return if (tradePrice >= 100) {
            decimalFormat.format(round(tradePrice))
        } else if (tradePrice < 100 && tradePrice >= 1) {
            tradePrice.secondDecimal()
        } else {
            tradePrice.forthDecimal()
        }
    }

    fun signedChangeRateCalculator(signedChangeRate: Double): String {
        return (signedChangeRate * 100).secondDecimal()
    }

    fun accTradePrice24hCalculatorForChart(accTradePrice24h: Double): String {
        val decimalFormatResult = decimalFormat.format(round(accTradePrice24h * 0.000001))
        return if (decimalFormatResult != "0") {
            decimalFormatResult
        } else {
            (accTradePrice24h * 0.000001).secondDecimal()
        }
    }

    fun changePriceCalculator(changePrice: Double, marketState: Int): String {
        val absChangePrice = abs(changePrice)

        val result: String = if (marketState == SELECTED_KRW_MARKET) {
            if (absChangePrice >= 100) {
                decimalFormat.format(round(changePrice).toInt())
            } else if (absChangePrice < 100 && absChangePrice >= 1) {
                changePrice.secondDecimal()
            } else {
                changePrice.forthDecimal()
            }
        } else {
            changePrice.eighthDecimal()
        }

        if (changePrice > 0.0) {
            return "+".plus(result)
        }

        return result
    }

    fun orderBookPriceCalculator(orderBookPrice: Double): String {
        return if (orderBookPrice >= 100) {
            decimalFormat.format(round(orderBookPrice).toInt())
        } else if (orderBookPrice < 100 && orderBookPrice >= 1) {
            String.format("%.2f", orderBookPrice)
        } else {
            String.format("%.4f", orderBookPrice)
        }
    }

    fun orderBookRateCalculator(preClosingPrice: Double, orderBookPrice: Double): Double {
        return ((orderBookPrice - preClosingPrice) / preClosingPrice * 100)
    }

    fun markerViewRateCalculator(preClosingPrice: Float, orderBookPrice: Float): Float {
        return (orderBookPrice - preClosingPrice) / preClosingPrice * 100
    }

    fun getDecimalFormat(): DecimalFormat {
        return decimalFormat
    }

    fun getDecimalDecimalFormat(): DecimalFormat {
        return decimalDecimalFormat
    }

    fun orderScreenTotalPriceCalculator(
        quantity: Double,
        tradePrice: Double,
        marketState: Int,
    ): String {
        val result = floor(quantity * tradePrice)
        return if (marketState == SELECTED_KRW_MARKET) {
            if (result >= 100) {
                decimalFormat.format(result.toLong())
            } else if (result < 100 && result >= 1) {
                result.secondDecimal()
            } else {
                result.forthDecimal()
            }
        } else {
            (quantity * tradePrice).eighthDecimal()
        }
    }

    fun orderScreenBidTotalPriceCalculator(
        quantity: Double,
        tradePrice: Double,
        marketState: Int,
    ): Double {
        return if (marketState == SELECTED_KRW_MARKET) {
            if (quantity == 0.0 || tradePrice == 0.0) {
                0.0
            } else {
                floor(quantity * tradePrice)
            }
        } else {
            if (quantity == 0.0 || tradePrice == 0.0) {
                0.0
            } else {
                floor(tradePrice * quantity * 100000000) * 0.00000001
            }
        }
    }

    fun orderScreenSpinnerBidValueCalculator(
        label: String,
        seedMoney: Long,
        tradePrice: Double,
        fee: Float
    ): String {
        return when (label) {
                "최대" -> {
                    val first = (seedMoney / (tradePrice + floor(tradePrice * ((fee) * 0.01) * 100000000) * 0.00000001))
                    decimalDecimalFormat.format((first))
                }
                "50%" -> {
                    decimalDecimalFormat.format((seedMoney * 0.5) / tradePrice)
                }
                "25%" -> {
                    decimalDecimalFormat.format((seedMoney * 0.25) / tradePrice)
                }
                "10%" -> {
                    decimalDecimalFormat.format((seedMoney * 0.1) / tradePrice)
                }
                else -> "0"
            }

    }

    fun orderScreenSpinnerBidValueCalculatorForBTC(
        label: String,
        BTCQuantity: Double,
        tradePrice: Double,
        fee: Float
    ): String {
        return when (label) {
            "최대" -> {
                val first = (BTCQuantity / (tradePrice + (tradePrice * ((fee + 0.00001) * 0.01))))
                decimalDecimalFormat.format(first)
            }
            "50%" -> {
                decimalDecimalFormat.format((BTCQuantity * 0.5) / tradePrice)
            }
            "25%" -> {
                decimalDecimalFormat.format((BTCQuantity * 0.25) / tradePrice)
            }
            "10%" -> {
                decimalDecimalFormat.format((BTCQuantity * 0.1) / tradePrice)
            }
            else -> "0"
        }
    }

    fun orderScreenSpinnerAskValueCalculator(
        label: String,
        userCoinQuantity: Double,
    ): String {
        return try {
            when (label) {
                "최대" -> {
                    decimalDecimalFormat.format(userCoinQuantity)
                }
                "50%" -> {
                    decimalDecimalFormat.format(userCoinQuantity * 0.5)
                }
                "25%" -> {
                    decimalDecimalFormat.format(userCoinQuantity * 0.25)
                }
                "10%" -> {
                    decimalDecimalFormat.format(userCoinQuantity * 0.1)
                }
                else -> "0"
            }
        } catch (e: Exception) {
            "0"
        }
    }

    fun averagePurchasePriceCalculator(
        currentPrice: Double,
        currentQuantity: Double,
        preAveragePurchasePrice: Double,
        preCoinQuantity: Double,
        marketState: Int
    ): Double {
        val result =
            ((preAveragePurchasePrice * preCoinQuantity) + (currentPrice * currentQuantity)) / (currentQuantity + preCoinQuantity)
        return if(marketState == SELECTED_KRW_MARKET) {
            if (result >= 100) {
                round(result)
            } else if (result < 100 && result >= 1) {
                result.secondDecimal().toDouble()
            } else {
                result.forthDecimal().toDouble()
            }
        } else {
            result.eighthDecimal() .toDouble()
        }
    }

    fun valuationGainOrLossDecimal(purchaseAverage: Double): String {
        val absPrice = abs(purchaseAverage)
        return if (absPrice >= 100) {
            decimalFormat.format(floor(purchaseAverage))
        } else if (absPrice < 100 && absPrice >= 1) {
            purchaseAverage.secondDecimal()
        } else if (absPrice == 0.0) {
            "0"
        } else {
            purchaseAverage.forthDecimal()
        }
    }
}

//        증가액 / 전년도 연봉 * 100
//        val open = round(openingPrice*100) / 100
//        val orderBook = round(orderBookPrice*100) / 100