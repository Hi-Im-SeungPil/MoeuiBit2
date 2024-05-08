package org.jeonfeel.moeuibit2.ui.main.portfolio

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.result.ActivityResult
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableDoubleState
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import org.jeonfeel.moeuibit2.R
import org.jeonfeel.moeuibit2.constants.SELECTED_KRW_MARKET
import org.jeonfeel.moeuibit2.ui.main.portfolio.dto.UserHoldCoinDTO
import org.jeonfeel.moeuibit2.utils.Utils
import org.jeonfeel.moeuibit2.utils.calculator.Calculator
import org.jeonfeel.moeuibit2.utils.calculator.CurrentCalculator
import org.jeonfeel.moeuibit2.utils.manager.AdMobManager
import org.jeonfeel.moeuibit2.utils.secondDecimal
import org.jeonfeel.moeuibit2.utils.showToast
import kotlin.math.round

class PortfolioScreenStateHolder(
    val context: Context,
    val resultLauncher: ManagedActivityResultLauncher<Intent, ActivityResult>,
    val adMobManager: AdMobManager,
    val errorReward: () -> Unit,
    val earnReward: () -> Unit,
    val btcTradePrice: MutableDoubleState
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
        val valuationGainOrLoss = if (marketState == SELECTED_KRW_MARKET) {
            evaluationAmount - purchaseAmount
        } else {
            (evaluationAmount * btcTradePrice.value) - (item.myCoinsQuantity * purchaseAverage * purchaseAverageBtcPrice)
        }

        val tradePrice = if (marketState == SELECTED_KRW_MARKET) {
            CurrentCalculator.tradePriceCalculator(purchaseAmount, SELECTED_KRW_MARKET)
        } else {
            CurrentCalculator.tradePriceCalculator(
                (item.myCoinsQuantity * purchaseAverage * purchaseAverageBtcPrice),
                SELECTED_KRW_MARKET
            )
        }
        val purchasePrice = if (marketState == SELECTED_KRW_MARKET) {
            CurrentCalculator.tradePriceCalculator(purchaseAverage, SELECTED_KRW_MARKET)
        } else {
            CurrentCalculator.tradePriceCalculator(
                purchaseAverage * purchaseAverageBtcPrice,
                SELECTED_KRW_MARKET
            )
        }
        val evaluationAmountFormat = if (marketState == SELECTED_KRW_MARKET) {
            Calculator.getDecimalFormat().format(evaluationAmount)
        } else {
            Calculator.getDecimalFormat().format(evaluationAmount * btcTradePrice.value)
        }
        val aReturn =
            if (marketState == SELECTED_KRW_MARKET) {
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
        val valuationGainOrLossResult = Calculator.valuationGainOrLossDecimal(
            purchaseAverage = valuationGainOrLoss
        )
        val coinKoreanName = Utils.getPortfolioName(
            marketState = marketState,
            name = item.myCoinsKoreanName
        )
        val coinEngName = Utils.getPortfolioName(
            marketState = marketState,
            name = item.myCoinsEngName
        )
        val purchaseAmountResult = if (marketState == SELECTED_KRW_MARKET) {
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

    fun getPortfolioMainInfoMap(
        totalValuedAssets: MutableState<Double>,
        totalPurchase: MutableState<Double>,
        userSeedMoney: MutableState<Long>,
    ): Map<String, String> {
        val calcTotalValuedAssets = Calculator.getDecimalFormat()
            .format(round(totalValuedAssets.value).toLong())
        val totalPurchaseValue =
            Calculator.getDecimalFormat().format(round(totalPurchase.value).toLong())
        val calcUserSeedMoney = Calculator.getDecimalFormat().format(userSeedMoney.value)
        val totalHoldings = Calculator.getDecimalFormat()
            .format(round(userSeedMoney.value + totalValuedAssets.value).toLong())
        val valuationGainOrLoss = Calculator.getDecimalFormat()
            .format(round(totalValuedAssets.value - totalPurchase.value).toLong())
        val aReturn = if (totalValuedAssets.value == 0.0) {
            "0"
        } else {
            ((totalValuedAssets.value - totalPurchase.value) / totalPurchase.value * 100).secondDecimal()
        }
        val colorStandard = round(totalValuedAssets.value - totalPurchase.value).toLong()

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
    resultLauncher: ManagedActivityResultLauncher<Intent, ActivityResult>,
    adMobManager: AdMobManager,
    errorReward: () -> Unit,
    earnReward: () -> Unit,
    btcTradePrice: MutableDoubleState
) = remember() {
    PortfolioScreenStateHolder(
        context = context,
        resultLauncher = resultLauncher,
        adMobManager = adMobManager,
        errorReward = errorReward,
        earnReward = earnReward,
        btcTradePrice = btcTradePrice
    )
}