package org.jeonfeel.moeuibit2.ui.mainactivity.portfolio

import android.content.Intent
import android.util.Log
import androidx.activity.result.ActivityResultLauncher
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import org.jeonfeel.moeuibit2.activity.main.viewmodel.MainViewModel
import org.jeonfeel.moeuibit2.constant.SELECTED_KRW_MARKET
import org.jeonfeel.moeuibit2.constant.SYMBOL_BTC
import org.jeonfeel.moeuibit2.util.EtcUtils
import org.jeonfeel.moeuibit2.util.calculator.Calculator
import org.jeonfeel.moeuibit2.util.calculator.CurrentCalculator
import org.jeonfeel.moeuibit2.util.eighthDecimal
import org.jeonfeel.moeuibit2.util.secondDecimal

@Composable
fun UserHoldCoinLazyColumn(
    mainViewModel: MainViewModel,
    startForActivityResult: ActivityResultLauncher<Intent>,
    dialogState: MutableState<Boolean>,
) {
    val portfolioOrderState = remember {
        mutableStateOf(-1)
    }
    val selectedCoinKoreanName = remember {
        mutableStateOf("")
    }
    val pieChartState = remember {
        mutableStateOf(false)
    }
    val orderByNameTextInfo = getTextColors(buttonNum = 1, textState = portfolioOrderState.value)
    val orderByRateTextInfo = getTextColors(buttonNum = 2, textState = portfolioOrderState.value)

    LazyColumn(modifier = Modifier.fillMaxSize()) {
        val userHoldCoinDtoList = mainViewModel.userHoldCoinDtoList
        item {
            PortfolioMain(
                mainViewModel,
                portfolioOrderState,
                orderByNameTextInfo,
                orderByRateTextInfo,
                pieChartState
            )
        }
        itemsIndexed(items = userHoldCoinDtoList) { _, item ->
            if (userHoldCoinDtoList.isNotEmpty()) {
                val marketState = EtcUtils.getSelectedMarket(item.market)
                val openingPrice = item.openingPrice
                val warning = item.warning
                val isFavorite = item.isFavorite
                val symbol = item.myCoinsSymbol
                val currentPrice = item.currentPrice
                val purchaseAmount =
                    CurrentCalculator.tradePriceCalculatorNoStringFormat(item.myCoinsQuantity * item.myCoinsBuyingAverage,
                        marketState)
                val evaluationAmount =
                    CurrentCalculator.tradePriceCalculatorNoStringFormat(item.myCoinsQuantity * currentPrice,
                        marketState)
                val purchaseAverage = item.myCoinsBuyingAverage
                val coinQuantity = item.myCoinsQuantity.eighthDecimal()
                val btcPrice = mainViewModel.btcTradePrice.value
                val purchaseAverageBtcPrice = item.purchaseAverageBtcPrice
                val koreanName = if(marketState == SELECTED_KRW_MARKET) {
                    item.myCoinsKoreanName
                } else {
                    "[$SYMBOL_BTC] ".plus(item.myCoinsKoreanName)
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
                        val localBtcPrice = mainViewModel.btcTradePrice.value
                        val currentCoinPrice = currentPrice * localBtcPrice
                        val preCoinPrice = item.myCoinsBuyingAverage * item.purchaseAverageBtcPrice
                        val tempAReturn =
                            ((currentCoinPrice - preCoinPrice) / preCoinPrice * 100)
                        Log.e("coin", "current => $currentCoinPrice, preCoin => $preCoinPrice, result => $tempAReturn")
                        if (tempAReturn.isNaN()) {
                            0
                        } else {
                            tempAReturn.secondDecimal()
                        }
                    }

                val valuationGainOrLoss = if (marketState == SELECTED_KRW_MARKET) {
                    evaluationAmount - purchaseAmount
                } else {
                    (evaluationAmount * btcPrice) - (item.myCoinsQuantity * purchaseAverage * purchaseAverageBtcPrice)
                }

                val color = if (valuationGainOrLoss > 0) {
                    Color.Red
                } else if (valuationGainOrLoss < 0) {
                    Color.Blue
                } else {
                    Color.Black

                }
                val evaluationAmountFormat = if (marketState == SELECTED_KRW_MARKET) {
                    Calculator.getDecimalFormat().format(evaluationAmount)
                } else {
                    Calculator.getDecimalFormat().format(evaluationAmount * btcPrice)
                }

                val purchasePrice = if (marketState == SELECTED_KRW_MARKET) {
                    CurrentCalculator.tradePriceCalculator(purchaseAverage, SELECTED_KRW_MARKET)
                } else {
                    CurrentCalculator.tradePriceCalculator(purchaseAverage * purchaseAverageBtcPrice,
                        SELECTED_KRW_MARKET)
                }

                val purchaseAmountResult = if (marketState == SELECTED_KRW_MARKET) {
                    CurrentCalculator.tradePriceCalculator(purchaseAmount, SELECTED_KRW_MARKET)
                } else {
                    CurrentCalculator.tradePriceCalculator(item.myCoinsQuantity * purchaseAverage * purchaseAverageBtcPrice,
                        SELECTED_KRW_MARKET)
                }

                val valuationGainOrLossResult =
                    Calculator.valuationGainOrLossDecimal(valuationGainOrLoss)

                UserHoldCoinLazyColumnItem(
                    koreanName,
                    symbol,
                    valuationGainOrLossResult,
                    aReturn.toString().plus("%"),
                    coinQuantity,
                    purchasePrice,
                    purchaseAmountResult,
                    evaluationAmountFormat,
                    color,
                    openingPrice,
                    warning,
                    isFavorite,
                    currentPrice,
                    marketState,
                    startForActivityResult,
                    selectedCoinKoreanName,
                    dialogState
                )
            }
        }
    }
}