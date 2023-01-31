package org.jeonfeel.moeuibit2.ui.coindetail.chart

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.view.get
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.github.mikephil.charting.charts.CombinedChart
import com.github.mikephil.charting.data.CandleEntry
import org.jeonfeel.moeuibit2.MoeuiBitDataStore.isKor
import org.jeonfeel.moeuibit2.R
import org.jeonfeel.moeuibit2.constants.chartMinuteArray
import org.jeonfeel.moeuibit2.constants.chartMinuteStrArray
import org.jeonfeel.moeuibit2.ui.viewmodels.CoinDetailViewModel
import org.jeonfeel.moeuibit2.ui.custom.AutoSizeText
import org.jeonfeel.moeuibit2.ui.custom.DpToSp
import org.jeonfeel.moeuibit2.ui.theme.decrease_candle_color
import org.jeonfeel.moeuibit2.ui.theme.increase_candle_color
import org.jeonfeel.moeuibit2.utils.Utils
import org.jeonfeel.moeuibit2.utils.OnLifecycleEvent
import org.jeonfeel.moeuibit2.utils.addAccAmountLimitLine
import org.jeonfeel.moeuibit2.utils.calculator.CurrentCalculator
import org.jeonfeel.moeuibit2.utils.initCombinedChart

const val MINUTE_SELECT = 1
const val DAY_SELECT = 2
const val WEEK_SELECT = 3
const val MONTH_SELECT = 4

const val CHART_ADD = 1
const val CHART_SET_CANDLE = 2
const val CHART_SET_ALL = 3

@Composable
fun ChartScreen(coinDetailViewModel: CoinDetailViewModel = viewModel()) {

    val context = LocalContext.current
    val applicationContext = context.applicationContext
    val combinedChart = remember {
        MBitCombinedChart(context,null)
    }

//    CommonLoadingDialog(dialogState = coinDetailViewModel.dialogState, text = stringResource(id = R.string.loading_chart))
    OnLifecycleEvent { lifeCycleOwner, event ->
        when (event) {
            Lifecycle.Event.ON_STOP -> {
                coinDetailViewModel.candlePosition = 0f
                coinDetailViewModel.isUpdateChart = false
                combinedChart.xAxis.valueFormatter = null
            }

            Lifecycle.Event.ON_START -> {
                combinedChart.initCombinedChart(applicationContext, coinDetailViewModel)
                coinDetailViewModel.requestChartData(combinedChart)
                coinDetailViewModel.isUpdateChart = true
                coinDetailViewModel.candleUpdateLiveData.observe(lifeCycleOwner) {
                    if (it == CHART_ADD) {
//                        combinedChart.addCandle()
                    } else {
                        if (combinedChart.candleData != null) {
                            if (combinedChart.data.candleData.xMax <= combinedChart.highestVisibleX && !coinDetailViewModel.candleEntriesIsEmpty) {
                                val marketState =
                                    Utils.getSelectedMarket(coinDetailViewModel.market)
                                val canvas = (combinedChart[0] as ChartCanvas)
                                val lastCandle = coinDetailViewModel.candleEntryLast
                                val tradePrice = lastCandle.close
                                val openPrice = lastCandle.open
                                val color = if (tradePrice - openPrice >= 0.0) {
                                    increase_candle_color
                                } else {
                                    decrease_candle_color
                                }
                                val yp = combinedChart.getPosition(
                                    CandleEntry(
                                        tradePrice,
                                        tradePrice,
                                        tradePrice,
                                        tradePrice,
                                        tradePrice
                                    ), combinedChart.axisRight.axisDependency
                                ).y
                                canvas.realTimeLastCandleClose(
                                    yp,
                                    CurrentCalculator.tradePriceCalculator(tradePrice, marketState),
                                    color
                                )
                                if (it == CHART_SET_ALL) {
                                    val lastBar =
                                        if (combinedChart.barData.dataSets[0].getEntriesForXValue(
                                                lastCandle.x
                                            ).isEmpty()
                                        ) {
                                            try {
                                                combinedChart.barData.dataSets[1].getEntriesForXValue(
                                                    lastCandle.x
                                                ).first()
                                            } catch (e: Exception) {
                                                CandleEntry(1f, 1f, 1f, 1f, 1f)
                                            }
                                        } else {
                                            try {
                                                combinedChart.barData.dataSets[0].getEntriesForXValue(
                                                    lastCandle.x
                                                ).first()

                                            } catch (e: Exception) {
                                                CandleEntry(1f, 1f, 1f, 1f, 1f)
                                            }
                                        }
                                    combinedChart.addAccAmountLimitLine(
                                        lastBar.x,
                                        coinDetailViewModel,
                                        color,
                                        marketState
                                    )
                                }
                            }
                            combinedChart.lineData.notifyDataChanged()
                            combinedChart.invalidate()
                        }
                    }
                }
            }
            else -> {}
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(35.dp)
        ) {
            val buttonModifier = remember {
                Modifier
                    .weight(1f)
                    .fillMaxHeight()
            }
            val btnColor = if (coinDetailViewModel.selectedButton == MINUTE_SELECT) {
                colorResource(id = R.color.C0F0F5C)
            } else {
                Color.LightGray
            }

            TextButton(
                onClick = {
                    coinDetailViewModel.minuteVisible = !coinDetailViewModel.minuteVisible
                }, modifier = if (coinDetailViewModel.selectedButton == MINUTE_SELECT)
                    buttonModifier
                        .border(1.dp, colorResource(id = R.color.C0F0F5C))
                        .fillMaxHeight()
                else buttonModifier
            ) {
                Text(
                    text = coinDetailViewModel.minuteText,
                    style = TextStyle(color = btnColor)
                )
            }
            if (coinDetailViewModel.selectedButton == DAY_SELECT) {


                PeriodButton(
                    coinDetailViewModel,
                    combinedChart,
                    buttonModifier
                        .border(
                            1.dp,
                            colorResource(id = R.color.C0F0F5C)
                        )
                        .fillMaxHeight(),
                    "days",
                    stringResource(id = R.string.day), DAY_SELECT
                )
            } else {
                PeriodButton(
                    coinDetailViewModel,
                    combinedChart,
                    buttonModifier,
                    "days",
                    stringResource(id = R.string.day), DAY_SELECT
                )
            }

            if (coinDetailViewModel.selectedButton == WEEK_SELECT) {
                PeriodButton(
                    coinDetailViewModel,
                    combinedChart,
                    buttonModifier
                        .border(
                            1.dp,
                            colorResource(id = R.color.C0F0F5C)
                        )
                        .fillMaxHeight(),
                    "weeks",
                    stringResource(id = R.string.week), WEEK_SELECT
                )
            } else {
                PeriodButton(
                    coinDetailViewModel,
                    combinedChart,
                    buttonModifier,
                    "weeks",
                    stringResource(id = R.string.week), WEEK_SELECT
                )
            }

            if (coinDetailViewModel.selectedButton == MONTH_SELECT) {
                PeriodButton(
                    coinDetailViewModel,
                    combinedChart,
                    buttonModifier
                        .border(
                            1.dp,
                            colorResource(id = R.color.C0F0F5C)
                        )
                        .fillMaxHeight(),
                    "months",
                    stringResource(id = R.string.month), MONTH_SELECT
                )
            } else {
                PeriodButton(
                    coinDetailViewModel,
                    combinedChart,
                    buttonModifier,
                    "months",
                    stringResource(id = R.string.month), MONTH_SELECT
                )
            }
        }

        Box(modifier = Modifier.fillMaxSize()) {
            AndroidView(
                factory = {
                    combinedChart
                },
                modifier = Modifier.fillMaxSize()
            )
            if (coinDetailViewModel.minuteVisible) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(35.dp)
                        .align(Alignment.TopCenter)
                ) {
                    for (i in chartMinuteArray.indices) {
                        MinuteButton(
                            coinDetailViewModel = coinDetailViewModel,
                            combinedChart = combinedChart,
                            candleType = chartMinuteArray[i],
                            minuteTextValue = chartMinuteStrArray[i],
                            autoSizeText = false
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun PeriodButton(
    coinDetailViewModel: CoinDetailViewModel = viewModel(),
    combinedChart: CombinedChart,
    modifier: Modifier,
    candleType: String,
    buttonText: String,
    period: Int,
) {
    val buttonColor = if (coinDetailViewModel.selectedButton == period) {
        colorResource(id = R.color.C0F0F5C)
    } else {
        Color.LightGray
    }

    TextButton(onClick = {
        coinDetailViewModel.chartLastData = false
        coinDetailViewModel.candleType = candleType
        coinDetailViewModel.minuteVisible = false
        coinDetailViewModel.minuteText = if (isKor) "분" else "m,h"
        coinDetailViewModel.requestChartData(combinedChart)
        coinDetailViewModel.selectedButton = period
    }, modifier = modifier) {
        Text(
            text = buttonText,
            style = TextStyle(color = buttonColor)
        )
    }
}

@Composable
fun RowScope.MinuteButton(
    coinDetailViewModel: CoinDetailViewModel = viewModel(),
    combinedChart: CombinedChart,
    candleType: String,
    minuteTextValue: String,
    autoSizeText: Boolean,
) {
    TextButton(
        onClick = {
            coinDetailViewModel.chartLastData = false
            coinDetailViewModel.candleType = candleType
            coinDetailViewModel.requestChartData(combinedChart)
            coinDetailViewModel.minuteVisible = false
            coinDetailViewModel.minuteText = minuteTextValue
            coinDetailViewModel.selectedButton = MINUTE_SELECT
        }, modifier = Modifier
            .weight(1f)
            .background(Color.White)
            .border(0.5.dp, colorResource(id = R.color.C0F0F5C))
            .fillMaxHeight()
    ) {
        if (autoSizeText) {
            AutoSizeText(
                text = minuteTextValue,
                textStyle = MaterialTheme.typography.body1.copy(color = Color.Black),
                modifier = Modifier
                    .fillMaxHeight()
                    .wrapContentHeight()
                    .weight(1f)
            )
        } else {
            Text(
                text = minuteTextValue,
                style = TextStyle(color = Color.Black, fontSize = DpToSp(14.dp))
            )
        }
    }
}