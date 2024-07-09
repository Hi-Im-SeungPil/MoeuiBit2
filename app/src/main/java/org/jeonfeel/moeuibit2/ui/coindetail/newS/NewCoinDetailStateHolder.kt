package org.jeonfeel.moeuibit2.ui.coindetail.newS

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import org.jeonfeel.moeuibit2.utils.BigDecimalMapper.newBigDecimal
import org.jeonfeel.moeuibit2.utils.EvenColor
import org.jeonfeel.moeuibit2.utils.FallColor
import org.jeonfeel.moeuibit2.utils.RiseColor
import org.jeonfeel.moeuibit2.utils.getCoinDetailTitle

class CoinDetailStateHolder(
    private val context: Context
) {
    fun getCoinDetailTitle(
        koreanCoinName: String,
        warning: String
    ): String {
        return getCoinDetailTitle(warning, context, koreanCoinName)
    }

    fun getCoinDetailPrice(price: Double, rootExchange: String, market: String): String {
        return price.newBigDecimal(rootExchange, market).toPlainString()
    }

    fun getCoinDetailPriceTextColor(changeRate: Double): Color {
        return when {
            changeRate > 0.0 -> RiseColor
            changeRate < 0.0 -> FallColor
            else -> EvenColor
        }
    }

    fun getFluctuateRate(): String {
        return ""
    }

    fun getFluctuatePrice(): String {
        return ""
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