package org.jeonfeel.moeuibit2.ui.coindetail.order

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.tradingview.lightweightcharts.Logger
import org.jeonfeel.moeuibit2.ui.theme.newtheme.commonFallColor
import org.jeonfeel.moeuibit2.ui.theme.newtheme.commonRiseColor
import org.jeonfeel.moeuibit2.ui.theme.newtheme.commonTextColor
import org.jeonfeel.moeuibit2.utils.BigDecimalMapper.formattedString
import org.jeonfeel.moeuibit2.utils.BigDecimalMapper.formattedStringMustFormat
import org.jeonfeel.moeuibit2.utils.secondDecimal
import java.math.BigDecimal
import java.math.RoundingMode

object OrderCalculator {
    fun calculateTotalAmount(quantity: Double, price: BigDecimal?): String {
        val quantityBigDecimal = BigDecimal.valueOf(quantity)
        return price?.multiply(quantityBigDecimal)?.formattedString() ?: BigDecimal.ZERO.toString()
    }

    @Composable
    fun getTextColor(purchaseAverage: BigDecimal, currentPrice: BigDecimal): Color {
        val difference = currentPrice.subtract(purchaseAverage)
        val percentage = difference.divide(purchaseAverage, 4, RoundingMode.HALF_UP)
            .multiply(BigDecimal(100))

        Logger.e(percentage.toString())

        val color = if (percentage.toDouble() > 0.0) {
            Logger.e("red")
            commonRiseColor()
        } else if(percentage.toDouble() == 0.0){
            Logger.e("black")
            commonTextColor()
        } else {
            Logger.e("blue")
            commonFallColor()
        }

        return color
    }

    fun calculateProfitPercentage(purchaseAverage: BigDecimal, currentPrice: BigDecimal): String {
        val difference = currentPrice.subtract(purchaseAverage)
        val percentage = difference.divide(purchaseAverage, 4, RoundingMode.HALF_UP)
            .multiply(BigDecimal(100))

        return percentage.toDouble().secondDecimal().plus("%")
    }

    fun calculatePNL(quantity: Double, purchaseAverage: Double, currentPrice: BigDecimal): String {
        val quantityBigDecimal = BigDecimal.valueOf(quantity)
        val purchaseAverageBigDecimal = BigDecimal.valueOf(purchaseAverage)
        val currentAmountBigDecimal = quantityBigDecimal.multiply(currentPrice)
        val amountBigDecimal = purchaseAverageBigDecimal.multiply(quantityBigDecimal)
        val pnl = currentAmountBigDecimal.minus(amountBigDecimal)
        return pnl.formattedStringMustFormat()
    }
}