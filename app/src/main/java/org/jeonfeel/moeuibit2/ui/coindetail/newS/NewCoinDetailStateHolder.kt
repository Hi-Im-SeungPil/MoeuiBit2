package org.jeonfeel.moeuibit2.ui.coindetail.newS

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import com.orhanobut.logger.Logger
import org.jeonfeel.moeuibit2.utils.BigDecimalMapper.formattedString
import org.jeonfeel.moeuibit2.utils.BigDecimalMapper.newBigDecimal
import org.jeonfeel.moeuibit2.utils.EvenColor
import org.jeonfeel.moeuibit2.utils.FallColor
import org.jeonfeel.moeuibit2.utils.RiseColor
import org.jeonfeel.moeuibit2.utils.commaFormat
import org.jeonfeel.moeuibit2.utils.decimalPoint
import org.jeonfeel.moeuibit2.utils.getCoinDetailTitle
import org.jeonfeel.moeuibit2.utils.secondDecimal
import kotlin.math.abs

class CoinDetailStateHolder(
    private val context: Context
) {
    fun getCoinDetailTitle(
        koreanCoinName: String,
        warning: Boolean
    ) {
        return getCoinDetailTitle(warning, context, koreanCoinName)
    }

    fun getCoinDetailPrice(price: Double, rootExchange: String, market: String): String {
        return price.newBigDecimal(rootExchange, market).formattedString()
    }

    fun getCoinDetailPriceTextColor(changeRate: Double): Color {
        return when {
            changeRate > 0.0 -> RiseColor
            changeRate < 0.0 -> FallColor
            else -> EvenColor
        }
    }

    fun getFluctuateRate(fluctuateRate: Double): String {
        return if(fluctuateRate > 0.0) {
            "+".plus(fluctuateRate.secondDecimal().plus("%"))
        } else {
            fluctuateRate.secondDecimal().plus("%")
        }
    }

    fun getFluctuatePrice(fluctuatePrice: Double): String {
        val absValue = abs(fluctuatePrice)
        return if (absValue >= 1000) {
            if (fluctuatePrice > 0) {
                "+${fluctuatePrice.commaFormat()}"
            } else {
                fluctuatePrice.commaFormat()
            }
        } else if (absValue >= 1.0) {
            if (fluctuatePrice > 0) {
                "+${fluctuatePrice}"
            } else {
                fluctuatePrice.toInt().toString()
            }
        } else {
            if (fluctuatePrice > 0) {
                "+${fluctuatePrice.decimalPoint()}"
            } else {
                fluctuatePrice.decimalPoint()
            }
        }
    }
}

@Composable
fun rememberCoinDetailStateHolder(
    context: Context
) = remember {
    CoinDetailStateHolder(
        context = context
    )
}