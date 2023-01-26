package org.jeonfeel.moeuibit2.ui.main.portfolio

import android.content.Intent
import androidx.activity.result.ActivityResultLauncher
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import org.jeonfeel.moeuibit2.ui.viewmodels.MainViewModel
import org.jeonfeel.moeuibit2.constants.SELECTED_KRW_MARKET
import org.jeonfeel.moeuibit2.constants.SYMBOL_BTC
import org.jeonfeel.moeuibit2.data.local.room.entity.MyCoin
import org.jeonfeel.moeuibit2.ui.main.portfolio.dto.UserHoldCoinDTO
import org.jeonfeel.moeuibit2.ui.theme.decrease_color
import org.jeonfeel.moeuibit2.ui.theme.increase_color
import org.jeonfeel.moeuibit2.utils.Utils
import org.jeonfeel.moeuibit2.utils.calculator.Calculator
import org.jeonfeel.moeuibit2.utils.calculator.CurrentCalculator
import org.jeonfeel.moeuibit2.utils.eighthDecimal
import org.jeonfeel.moeuibit2.utils.secondDecimal

@Composable
fun UserHoldCoinLazyColumn(
    startForActivityResult: ActivityResultLauncher<Intent>,
    columnItemDialogState: MutableState<Boolean>,
    portfolioOrderState: MutableState<Int>,
    totalValuedAssets: MutableState<Double>,
    totalPurchase: MutableState<Double>,
    userSeedMoney: MutableState<Long>,
    adDialogState: MutableState<Boolean>,
    pieChartState: MutableState<Boolean>,
    userHoldCoinList: List<MyCoin?>,
    earnReward: () -> Unit,
    userHoldCoinDTOList: SnapshotStateList<UserHoldCoinDTO>,
    selectedCoinKoreanName: MutableState<String>,
    btcTradePrice: MutableState<Double>
) {
    LazyColumn(modifier = Modifier.fillMaxSize()) {
        item {
            PortfolioMain(
                portfolioOrderState = portfolioOrderState,
                totalValuedAssets = totalValuedAssets,
                totalPurchase = totalPurchase,
                userSeedMoney = userSeedMoney,
                orderByNameTextInfo = getTextColors(buttonNum = 1, textState = portfolioOrderState.value),
                orderByRateTextInfo =  getTextColors(buttonNum = 2, textState = portfolioOrderState.value),
                adDialogState = adDialogState,
                pieChartState = pieChartState,
                userHoldCoinList = userHoldCoinList,
                earnReward = earnReward
            )
        }
        itemsIndexed(items = userHoldCoinDTOList) { _, item ->
            if (userHoldCoinDTOList.isNotEmpty()) {
                val marketState = Utils.getSelectedMarket(item.market)
                val currentPrice = item.currentPrice
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
                val purchaseAverage = item.myCoinsBuyingAverage
                val purchaseAverageBtcPrice = item.purchaseAverageBtcPrice
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

                val valuationGainOrLoss = if (marketState == SELECTED_KRW_MARKET) {
                    evaluationAmount - purchaseAmount
                } else {
                    (evaluationAmount * btcTradePrice.value) - (item.myCoinsQuantity * purchaseAverage * purchaseAverageBtcPrice)
                }

                val evaluationAmountFormat = if (marketState == SELECTED_KRW_MARKET) {
                    Calculator.getDecimalFormat().format(evaluationAmount)
                } else {
                    Calculator.getDecimalFormat().format(evaluationAmount * btcTradePrice.value)
                }

                val purchasePrice = if (marketState == SELECTED_KRW_MARKET) {
                    CurrentCalculator.tradePriceCalculator(purchaseAverage, SELECTED_KRW_MARKET)
                } else {
                    CurrentCalculator.tradePriceCalculator(
                        purchaseAverage * purchaseAverageBtcPrice,
                        SELECTED_KRW_MARKET
                    )
                }

                val purchaseAmountResult = if (marketState == SELECTED_KRW_MARKET) {
                    CurrentCalculator.tradePriceCalculator(purchaseAmount, SELECTED_KRW_MARKET)
                } else {
                    CurrentCalculator.tradePriceCalculator(
                        item.myCoinsQuantity * purchaseAverage * purchaseAverageBtcPrice,
                        SELECTED_KRW_MARKET
                    )
                }

                UserHoldCoinLazyColumnItem(
                    coinKoreanName = Utils.getPortfolioName(
                        marketState = marketState,
                        name = item.myCoinsKoreanName
                    ),
                    coinEngName = Utils.getPortfolioName(
                        marketState = marketState,
                        name = item.myCoinsEngName
                    ),
                    symbol = item.myCoinsSymbol,
                    valuationGainOrLoss = Calculator.valuationGainOrLossDecimal(
                        purchaseAverage = valuationGainOrLoss
                    ),
                    aReturn = aReturn.toString().plus("%"),
                    coinQuantity = item.myCoinsQuantity.eighthDecimal(),
                    purchaseAverage = purchasePrice,
                    purchaseAmount = purchaseAmountResult,
                    evaluationAmount = evaluationAmountFormat,
                    color = Utils.getIncreaseOrDecreaseColor(
                        value = valuationGainOrLoss.toFloat()
                    ),
                    openingPrice = item.openingPrice,
                    warning = item.warning,
                    isFavorite = item.isFavorite,
                    currentPrice = item.currentPrice,
                    marketState = marketState,
                    startForActivityResult = startForActivityResult,
                    selectedCoinKoreanName = selectedCoinKoreanName,
                    dialogState = columnItemDialogState
                )
            }
        }
    }
}