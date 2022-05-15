package org.jeonfeel.moeuibit2.util

import java.text.DecimalFormat
import kotlin.math.abs
import kotlin.math.round

object Calculator {

    private val decimalFormat = DecimalFormat("###,###")

    fun tradePriceCalculator(tradePrice: Double): String {
        return if (tradePrice >= 100) {
            round(tradePrice).toInt().toString()
        } else if (tradePrice < 100 && tradePrice >= 1) {
            String.format("%.2f", tradePrice)
        } else {
            String.format("%.4f", tradePrice)
        }
    }

    fun tradePriceCalculatorForChart(tradePrice: Double): String {
        return if (tradePrice >= 100) {
            decimalFormat.format(round(tradePrice))
        } else if (tradePrice < 100 && tradePrice >= 1) {
            String.format("%.2f", tradePrice)
        } else {
            String.format("%.4f", tradePrice)
        }
    }

    fun tradePriceCalculatorForChart(tradePrice: Float): String {
        return if (tradePrice >= 100) {
            decimalFormat.format(round(tradePrice))
        } else if (tradePrice < 100 && tradePrice >= 1) {
            String.format("%.2f", tradePrice)
        } else {
            String.format("%.4f", tradePrice)
        }
    }

    fun signedChangeRateCalculator(signedChangeRate: Double): String {
        return String.format("%.2f", signedChangeRate * 100)
    }

    fun accTradePrice24hCalculator(accTradePrice24h: Double): String {
        return decimalFormat.format(round(accTradePrice24h * 0.000001))
    }

    fun accTradePrice24hCalculatorForChart(accTradePrice24h: Double): String {
        val decimalFormatResult = decimalFormat.format(round(accTradePrice24h * 0.000001))
        return if(decimalFormatResult != "0") {
            decimalFormatResult
        } else {
            String.format("%.2f",accTradePrice24h * 0.000001)
        }
    }


    fun changePriceCalculator(changePrice: Double): String {
        val absChangePrice = abs(changePrice)
        var result = ""
        result = if (absChangePrice >= 100) {
            decimalFormat.format(round(changePrice).toInt())
        } else if (absChangePrice < 100 && absChangePrice >= 1) {
            String.format("%.2f", changePrice)
        } else {
            String.format("%.4f", changePrice)
        }

        if(changePrice > 0.0) {
            return "+".plus(result)
        }
        return result
    }

    fun orderBookPriceCalculator(orderBookPrice: Double): String{
        var result = ""
        result = if (orderBookPrice >= 100) {
            decimalFormat.format(round(orderBookPrice).toInt())
        } else if (orderBookPrice < 100 && orderBookPrice >= 1) {
            String.format("%.2f", orderBookPrice)
        } else {
            String.format("%.4f", orderBookPrice)
        }
        return result
    }

    fun orderBookRateCalculator(preClosingPrice: Double, orderBookPrice: Double): Double {
        return ((orderBookPrice-preClosingPrice) / preClosingPrice * 100)
    }

    fun markerViewRateCalculator(preClosingPrice: Float, orderBookPrice: Float): Float {
        return (orderBookPrice-preClosingPrice) / preClosingPrice * 100
    }

    fun getDecimalFormat(): DecimalFormat {
        return decimalFormat
    }
}

//        증가액 / 전년도 연봉 * 100
//        val open = round(openingPrice*100) / 100
//        val orderBook = round(orderBookPrice*100) / 100