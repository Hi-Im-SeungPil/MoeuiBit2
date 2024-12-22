package org.jeonfeel.moeuibit2.ui.main.portfolio

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.result.ActivityResult
import androidx.compose.material.ModalBottomSheetState
import androidx.compose.material.rememberBottomSheetState
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.material3.SheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import com.orhanobut.logger.Logger
import org.jeonfeel.moeuibit2.R
import org.jeonfeel.moeuibit2.constants.SELECTED_KRW_MARKET
import org.jeonfeel.moeuibit2.ui.main.portfolio.dto.UserHoldCoinDTO
import org.jeonfeel.moeuibit2.utils.BigDecimalMapper.formattedStringTo1000
import org.jeonfeel.moeuibit2.utils.Utils
import org.jeonfeel.moeuibit2.utils.calculator.Calculator
import org.jeonfeel.moeuibit2.utils.calculator.CurrentCalculator
import org.jeonfeel.moeuibit2.utils.isTradeCurrencyKrw
import org.jeonfeel.moeuibit2.utils.manager.AdMobManager
import org.jeonfeel.moeuibit2.utils.secondDecimal
import org.jeonfeel.moeuibit2.utils.showToast
import java.math.BigDecimal
import java.math.RoundingMode

class PortfolioScreenStateHolder(
    val context: Context,
    val adMobManager: AdMobManager,
    val errorReward: () -> Unit,
    val earnReward: () -> Unit,
    val btcTradePrice: State<Double>,
    val portfolioSearchTextState: MutableState<String> = mutableStateOf(""),
    val userHoldCoinDtoList: List<UserHoldCoinDTO>,
) {
    val adLoadingDialogState = mutableStateOf(false)
    val adConfirmDialogState = mutableStateOf(false)
    val columnItemDialogState = mutableStateOf(false)
    val editHoldCoinDialogState = mutableStateOf(false)
    val selectedCoinKoreanName = mutableStateOf("")
    val pieChartState = mutableStateOf(false)

    fun getUserCoinResultMap(
        item: UserHoldCoinDTO,
    ): Map<String, String> {
//        Logger.e("myCoin -> $item")
        val marketState = Utils.getSelectedMarket(item.market)
        val currentPrice = item.currentPrice
        val purchaseAverage = item.myCoinsBuyingAverage
        val purchaseAverageBtcPrice = item.purchaseAverageBtcPrice

        val purchaseAmount =
            CurrentCalculator.tradePriceCalculatorNoStringFormat(
                item.myCoinsQuantity * item.myCoinsBuyingAverage,
                marketState
            )

        val evaluationAmount =
            CurrentCalculator.tradePriceCalculatorNoStringFormat(
                item.myCoinsQuantity * currentPrice,
                marketState
            )

        val valuationGainOrLoss = if (item.market.isTradeCurrencyKrw()) {
            evaluationAmount - purchaseAmount
        } else {
            (evaluationAmount * btcTradePrice.value) - (item.myCoinsQuantity * purchaseAverage * purchaseAverageBtcPrice)
        }

        val tradePrice = if (item.market.isTradeCurrencyKrw()) {
            CurrentCalculator.tradePriceCalculator(purchaseAmount, SELECTED_KRW_MARKET)
        } else {
            CurrentCalculator.tradePriceCalculator(
                (item.myCoinsQuantity * purchaseAverage * purchaseAverageBtcPrice),
                SELECTED_KRW_MARKET
            )
        }

        val purchasePrice = if (item.market.isTradeCurrencyKrw()) {
            CurrentCalculator.tradePriceCalculator(purchaseAverage, SELECTED_KRW_MARKET)
        } else {
            CurrentCalculator.tradePriceCalculator(
                purchaseAverage * purchaseAverageBtcPrice,
                SELECTED_KRW_MARKET
            )
        }

        val evaluationAmountFormat = if (item.market.isTradeCurrencyKrw()) {
            Calculator.getDecimalFormat().format(evaluationAmount)
        } else {
            Calculator.getDecimalFormat().format(evaluationAmount * btcTradePrice.value)
        }

        val aReturn =
            if (item.market.isTradeCurrencyKrw()) {
                val tempAReturn =
                    ((currentPrice - item.myCoinsBuyingAverage) / item.myCoinsBuyingAverage * 100)
                if (tempAReturn.isNaN()) {
                    0
                } else {
                    tempAReturn.secondDecimal()
                }
            } else {
                val currentCoinPrice = currentPrice * btcTradePrice.value
                val preCoinPrice = item.myCoinsBuyingAverage * item.purchaseAverageBtcPrice
                val tempAReturn =
                    ((currentCoinPrice - preCoinPrice) / preCoinPrice * 100)
                if (tempAReturn.isNaN()) {
                    0
                } else {
                    tempAReturn.secondDecimal()
                }
            }

        val valuationGainOrLossResult = valuationGainOrLoss.toBigDecimal().formattedStringTo1000()

        val coinKoreanName = Utils.getPortfolioName(
            marketState = marketState,
            name = item.myCoinKoreanName
        )

        val coinEngName = Utils.getPortfolioName(
            marketState = marketState,
            name = item.myCoinEngName
        )

        val purchaseAmountResult = if (item.market.isTradeCurrencyKrw()) {
            CurrentCalculator.tradePriceCalculator(purchaseAmount, SELECTED_KRW_MARKET)
        } else {
            CurrentCalculator.tradePriceCalculator(
                item.myCoinsQuantity * purchaseAverage * purchaseAverageBtcPrice,
                SELECTED_KRW_MARKET
            )
        }

        return mapOf<String, String>(
            USER_COIN_RESULT_KEY_A_RETURN to aReturn.toString().plus("%"),
            USER_COIN_RESULT_KEY_VALUATION_GAIN_OR_LOSE_RESULT to valuationGainOrLossResult,
            USER_COIN_RESULT_KEY_VALUATION_GAIN_OR_LOSE to valuationGainOrLoss.toString(),
            USER_COIN_RESULT_KEY_EVALUATION_AMOUNT_FORMAT to evaluationAmountFormat,
            USER_COIN_RESULT_KEY_PURCHASE_PRICE to purchasePrice,
            USER_COIN_RESULT_KEY_TRADE_PRICE to tradePrice,
            USER_COIN_RESULT_KEY_COIN_ENG_NAME to coinEngName,
            USER_COIN_RESULT_KEY_COIN_KOREAN_NAME to coinKoreanName,
            USER_COIN_RESULT_KEY_MARKET_STATE to marketState.toString(),
            USER_COIN_RESULT_KEY_PURCHASE_AMOUNT to purchaseAmount.toString(),
            USER_COIN_RESULT_KEY_PURCHASE_AMOUNT_RESULT to purchaseAmountResult
        )
    }

    fun getList(): List<UserHoldCoinDTO> {
        val strResult = portfolioSearchTextState.value.uppercase()
        return when {
            strResult.isEmpty() -> userHoldCoinDtoList
            else -> {
                userHoldCoinDtoList.filter {
                    it.myCoinsSymbol.uppercase().contains(strResult)
                            || it.myCoinKoreanName.uppercase().contains(strResult)
                            || it.myCoinEngName.uppercase().contains(strResult)
                            || it.initialConstant.uppercase().contains(strResult)
                }
            }
        }
    }

    fun getPortfolioMainInfoMap(
        totalValuedAssets: State<BigDecimal>,
        totalPurchase: State<BigDecimal>,
        userSeedMoney: State<Long>,
    ): Map<String, String> {
        val calcTotalValuedAssets = totalValuedAssets.value.formattedStringTo1000()
        val totalPurchaseValue = totalPurchase.value.formattedStringTo1000()
        val calcUserSeedMoney = Calculator.getDecimalFormat().format(userSeedMoney.value)
        val totalHoldings =
            totalValuedAssets.value.plus(userSeedMoney.value.toBigDecimal()).formattedStringTo1000()
        val valuationGainOrLoss =
            totalValuedAssets.value.minus(totalPurchase.value).formattedStringTo1000()
        val aReturn = if (totalValuedAssets.value.toDouble() == 0.0) {
            "0"
        } else {
            (totalValuedAssets.value
                .minus(totalPurchase.value))
                .divide(totalPurchase.value, 10, RoundingMode.HALF_UP)
                .multiply(BigDecimal(100))
                .setScale(2, RoundingMode.HALF_UP)
                .toString()
        }
        val colorStandard =
            totalValuedAssets.value.minus(totalPurchase.value)
                .setScale(0, RoundingMode.HALF_UP)

        return mapOf(
            PORTFOLIO_MAIN_KEY_CALC_TOTAL_VALUED_ASSETS to calcTotalValuedAssets,
            PORTFOLIO_MAIN_KEY_TOTAL_PURCHASE_VALUE to totalPurchaseValue,
            PORTFOLIO_MAIN_KEY_CALC_USER_SEED_MONEY to calcUserSeedMoney,
            PORTFOLIO_MAIN_KEY_TOTAL_HOLDINGS to totalHoldings,
            PORTFOLIO_MAIN_KEY_VALUATION_GAIN_OR_LOSE to valuationGainOrLoss,
            PORTFOLIO_MAIN_KEY_A_RETURN to aReturn.plus("%"),
            PORTFOLIO_MAIN_KEY_COLOR_STANDARD to colorStandard.toString()
        )
    }

    fun showAd() {
        adMobManager.loadRewardVideoAd(
            activity = context as Activity,
            onAdLoaded = {
                adLoadingDialogState.value = false
            },
            onAdFailedToLoad = {
                context.showToast(context.getString(R.string.NO_INTERNET_CONNECTION))
                adLoadingDialogState.value = false
            },
            fullScreenOnAdLoad = {
                adLoadingDialogState.value = false
            },
            fullScreenOnAdFailedToLoad = {
                context.showToast(context.getString(R.string.adLoadError))
                errorReward()
                adLoadingDialogState.value = false
            },
            rewardListener = {
                earnReward()
            }
        )
    }

    companion object {
        const val USER_COIN_RESULT_KEY_A_RETURN = "aReturn"
        const val USER_COIN_RESULT_KEY_VALUATION_GAIN_OR_LOSE = "valuationGainOrLose"
        const val USER_COIN_RESULT_KEY_VALUATION_GAIN_OR_LOSE_RESULT = "valuationGainOrLoseResult"
        const val USER_COIN_RESULT_KEY_EVALUATION_AMOUNT_FORMAT = "evaluationAmountFormat"
        const val USER_COIN_RESULT_KEY_PURCHASE_PRICE = "purchasePrice"
        const val USER_COIN_RESULT_KEY_TRADE_PRICE = "tradePrice"
        const val USER_COIN_RESULT_KEY_COIN_KOREAN_NAME = "coinKoreanName"
        const val USER_COIN_RESULT_KEY_COIN_ENG_NAME = "coinEngName"
        const val USER_COIN_RESULT_KEY_MARKET_STATE = "marketState"
        const val USER_COIN_RESULT_KEY_PURCHASE_AMOUNT = "purchaseAmount"
        const val USER_COIN_RESULT_KEY_PURCHASE_AMOUNT_RESULT = "purchaseAmountResult"

        const val PORTFOLIO_MAIN_KEY_CALC_TOTAL_VALUED_ASSETS = "calcTotalValuedAssets"
        const val PORTFOLIO_MAIN_KEY_TOTAL_PURCHASE_VALUE = "totalPurchaseValue"
        const val PORTFOLIO_MAIN_KEY_CALC_USER_SEED_MONEY = "calcUserSeedMoney"
        const val PORTFOLIO_MAIN_KEY_TOTAL_HOLDINGS = "totalHoldings"
        const val PORTFOLIO_MAIN_KEY_VALUATION_GAIN_OR_LOSE = "valuationGainOrLose"
        const val PORTFOLIO_MAIN_KEY_A_RETURN = "aReturn"
        const val PORTFOLIO_MAIN_KEY_COLOR_STANDARD = "colorStandard"
    }
}

@Composable
fun rememberPortfolioScreenStateHolder(
    context: Context,
    adMobManager: AdMobManager,
    errorReward: () -> Unit,
    earnReward: () -> Unit,
    btcTradePrice: State<Double>,
    userHoldCoinDtoList: List<UserHoldCoinDTO>,
    portfolioSearchTextState: MutableState<String>
) = remember() {
    PortfolioScreenStateHolder(
        context = context,
        adMobManager = adMobManager,
        errorReward = errorReward,
        earnReward = earnReward,
        btcTradePrice = btcTradePrice,
        userHoldCoinDtoList = userHoldCoinDtoList,
        portfolioSearchTextState = portfolioSearchTextState
    )
}