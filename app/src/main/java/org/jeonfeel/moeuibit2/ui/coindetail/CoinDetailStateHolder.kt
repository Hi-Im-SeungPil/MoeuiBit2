package org.jeonfeel.moeuibit2.ui.coindetail

import android.content.Context
import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.orhanobut.logger.Logger
import org.jeonfeel.moeuibit2.data.network.retrofit.response.upbit.Caution
import org.jeonfeel.moeuibit2.utils.BigDecimalMapper.formattedString
import org.jeonfeel.moeuibit2.utils.BigDecimalMapper.newBigDecimal
import org.jeonfeel.moeuibit2.utils.ext.EvenColor
import org.jeonfeel.moeuibit2.utils.ext.FallColor
import org.jeonfeel.moeuibit2.utils.ext.RiseColor
import org.jeonfeel.moeuibit2.utils.commaFormat
import org.jeonfeel.moeuibit2.utils.decimalPoint
import org.jeonfeel.moeuibit2.utils.eighthDecimal
import org.jeonfeel.moeuibit2.utils.isTradeCurrencyKrw
import org.jeonfeel.moeuibit2.utils.secondDecimal
import java.util.ArrayList
import kotlin.math.abs

class CoinDetailStateHolder(
    private val context: Context,
    val navController: NavHostController,
    private val caution: Caution?
) {
    private var toast: Toast? = null

    fun getCoinDetailPrice(price: Double, rootExchange: String, market: String): String {
        if (price == 0.0) return "0"

        return price.newBigDecimal(rootExchange, market).formattedString()
    }

    fun getCoinDetailPriceTextColor(changeRate: Double): Color {
        return when {
            changeRate > 0.0 -> RiseColor
            changeRate < 0.0 -> FallColor
            else -> EvenColor
        }
    }

    fun showToast(message: String) {
        toast?.cancel()
        toast = Toast.makeText(context, message, Toast.LENGTH_SHORT).also { it.show() }
    }

    fun getFluctuateRate(fluctuateRate: Double): String {
        return if (fluctuateRate > 0.0) {
            "+".plus(fluctuateRate.secondDecimal().plus("%"))
        } else {
            fluctuateRate.secondDecimal().plus("%")
        }
    }

    fun getFluctuatePrice(fluctuatePrice: Double, market: String): String {
        val absValue = abs(fluctuatePrice)
        return if (market.isTradeCurrencyKrw()) {
            if (absValue >= 1000) {
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
        } else {
            fluctuatePrice.eighthDecimal()
        }
    }

    fun getCautionMessageList(fluctuateRate: Double): List<String> {
        Logger.e(fluctuateRate.toString())
        Logger.e(caution.toString())

        if (caution == null) return emptyList()

        val list = ArrayList<String>()

        if (caution.priceFluctuations) {
            list.add("가격 급등락 발생")
        }

        if (caution.tradingVolumeSoaring) {
            list.add("거래량 급등 발생")
        }

        if (caution.depositAmountSoaring) {
            list.add("입금량 급등 발생")
        }

        if (caution.concentrationOfSmallAccounts) {
            list.add("소수 계정 거래 집중")
        }

        return list
    }
}

@Composable
fun rememberCoinDetailStateHolder(
    context: Context,
    navController: NavHostController = rememberNavController(),
    caution: Caution?
) = remember {
    CoinDetailStateHolder(
        context = context,
        navController = navController,
        caution = caution
    )
}