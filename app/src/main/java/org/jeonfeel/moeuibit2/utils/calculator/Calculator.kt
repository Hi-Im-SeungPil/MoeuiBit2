package org.jeonfeel.moeuibit2.utils.calculator

import org.jeonfeel.moeuibit2.constants.SELECTED_KRW_MARKET
import org.jeonfeel.moeuibit2.data.local.room.entity.MyCoin
import org.jeonfeel.moeuibit2.utils.Utils
import java.math.BigDecimal
import java.math.RoundingMode
import java.text.DecimalFormat

object Calculator {

    private val decimalFormat = DecimalFormat("###,###")
    private val decimalDecimalFormat = DecimalFormat("#.########")

    fun orderBookRateCalculator(preClosingPrice: Double, orderBookPrice: Double): Double {
        return ((orderBookPrice - preClosingPrice) / preClosingPrice * 100)
    }

    fun markerViewRateCalculator(preClosingPrice: Float, orderBookPrice: Float): Float {
        return (orderBookPrice - preClosingPrice) / preClosingPrice * 100
    }

    fun getDecimalFormat(): DecimalFormat {
        return decimalFormat
    }

    fun averagePurchasePriceCalculator(
        currentPrice: Double,
        currentQuantity: Double,
        preAveragePurchasePrice: Double,
        preCoinQuantity: Double,
        marketState: Int,
    ): Double {
        val bdCurrentPrice = BigDecimal.valueOf(currentPrice)
        val bdCurrentQuantity = BigDecimal.valueOf(currentQuantity)
        val bdPreAveragePurchasePrice = BigDecimal.valueOf(preAveragePurchasePrice)
        val bdPreCoinQuantity = BigDecimal.valueOf(preCoinQuantity)

        val totalValue = bdPreAveragePurchasePrice.multiply(bdPreCoinQuantity)
            .add(bdCurrentPrice.multiply(bdCurrentQuantity))
        val totalQuantity = bdCurrentQuantity.add(bdPreCoinQuantity)
        val result = totalValue.divide(totalQuantity, 8, RoundingMode.HALF_UP)

        return if (marketState == SELECTED_KRW_MARKET) {
            when {
                result >= BigDecimal(100) -> result.setScale(0, RoundingMode.HALF_UP).toDouble()
                result >= BigDecimal(1) -> result.setScale(2, RoundingMode.HALF_UP).toDouble()
                else -> result.setScale(4, RoundingMode.HALF_UP).toDouble()
            }
        } else {
            result.setScale(8, RoundingMode.HALF_UP).toDouble()
        }
    }

    fun getTotalPurchase(myCoin: MyCoin): BigDecimal {
        return if (Utils.getSelectedMarket(myCoin.market) == SELECTED_KRW_MARKET) {
            (myCoin.quantity * myCoin.purchasePrice).toBigDecimal()
        } else {
            (myCoin.quantity * myCoin.purchasePrice * myCoin.purchaseAverageBtcPrice).toBigDecimal()
        }
    }
}