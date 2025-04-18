package org.jeonfeel.moeuibit2.ui.coindetail.order

import org.jeonfeel.moeuibit2.utils.BigDecimalMapper.formattedString
import org.jeonfeel.moeuibit2.utils.secondDecimal
import java.math.BigDecimal
import java.math.RoundingMode

object OrderCalculator {
    fun calculateTotalAmount(quantity: Double, price: BigDecimal?): String {
        val quantityBigDecimal = BigDecimal.valueOf(quantity)
        return price?.multiply(quantityBigDecimal)?.formattedString() ?: BigDecimal.ZERO.toString()
    }

    fun calculateProfitPercentage(purchaseAverage: BigDecimal, currentPrice: BigDecimal): String {
        val difference = currentPrice.subtract(purchaseAverage)
        val percentage = difference.divide(purchaseAverage, 4, RoundingMode.HALF_UP)
            .multiply(BigDecimal(100))

        return percentage.toDouble().secondDecimal()  // 네가 만든 확장함수 사용
    }

    fun calculatePNL(quantity: Double, purchaseAverage: Double, currentPrice: BigDecimal): String {
        val quantityBigDecimal = BigDecimal.valueOf(quantity)
        val purchaseAverageBigDecimal = BigDecimal.valueOf(purchaseAverage)
        val currentAmountBigDecimal = quantityBigDecimal.multiply(currentPrice)
        val amountBigDecimal = purchaseAverageBigDecimal.multiply(quantityBigDecimal)
        val pnl = currentAmountBigDecimal.minus(amountBigDecimal)
        return pnl.formattedString()
    }
}