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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.view.get
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.github.mikephil.charting.charts.CombinedChart
import com.github.mikephil.charting.data.CandleEntry
import org.jeonfeel.moeuibit2.R
import org.jeonfeel.moeuibit2.activity.coindetail.viewmodel.CoinDetailViewModel
import org.jeonfeel.moeuibit2.ui.custom.AutoSizeText
import org.jeonfeel.moeuibit2.util.EtcUtils
import org.jeonfeel.moeuibit2.util.OnLifecycleEvent
import org.jeonfeel.moeuibit2.util.addAccAmountLimitLine
import org.jeonfeel.moeuibit2.util.calculator.CurrentCalculator
import org.jeonfeel.moeuibit2.util.initCombinedChart

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
        CombinedChart(context)
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
                        combinedChart.xAxis.axisMaximum = combinedChart.xAxis.axisMaximum + 1f
                        combinedChart.invalidate()
                    } else {
                        if (combinedChart.candleData != null) {
                            if (combinedChart.data.candleData.xMax <= combinedChart.highestVisibleX && !coinDetailViewModel.candleEntriesIsEmpty) {
                                val marketState = EtcUtils.getSelectedMarket(coinDetailViewModel.market)
                                val canvas = (combinedChart[0] as ChartCanvas)
                                val lastCandle = coinDetailViewModel.candleEntryLast
                                val tradePrice = lastCandle.close
                                val openPrice = lastCandle.open
                                val color = if (tradePrice - openPrice >= 0.0) {
                                    android.graphics.Color.RED
                                } else {
                                    android.graphics.Color.BLUE
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
                                    CurrentCalculator.tradePriceCalculator(tradePrice,marketState),
                                    color
                                )
                                if (it == CHART_SET_ALL) {
                                    val lastBar =
                                        if (combinedChart.barData.dataSets[0].getEntriesForXValue(
                                                lastCandle.x
                                            ).isEmpty()
                                        ) {
                                            try{
                                                combinedChart.barData.dataSets[1].getEntriesForXValue(
                                                    lastCandle.x
                                                ).first()
                                            }catch (e: Exception) {
                                                CandleEntry(1f,1f,1f,1f,1f)
                                            }
                                        } else {
                                            try {
                                                combinedChart.barData.dataSets[0].getEntriesForXValue(
                                                    lastCandle.x
                                                ).first()

                                            }catch(e: Exception){
                                                CandleEntry(1f,1f,1f,1f,1f)
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

            TextButton(onClick = {
                coinDetailViewModel.minuteVisible = !coinDetailViewModel.minuteVisible
            }, modifier = if (coinDetailViewModel.selectedButton == MINUTE_SELECT)
                buttonModifier
                    .border(1.dp, colorResource(id = R.color.C0F0F5C))
                    .fillMaxHeight()
            else buttonModifier) {
                Text(text = coinDetailViewModel.minuteText,
                    style = TextStyle(color = btnColor)
                )
            }
            if (coinDetailViewModel.selectedButton == DAY_SELECT) {
                PeriodButton(coinDetailViewModel,
                    combinedChart,
                    buttonModifier
                        .border(1.dp,
                            colorResource(id = R.color.C0F0F5C))
                        .fillMaxHeight(),
                    "days",
                    "일", DAY_SELECT)
            } else {
                PeriodButton(coinDetailViewModel,
                    combinedChart,
                    buttonModifier,
                    "days",
                    "일", DAY_SELECT)
            }

            if (coinDetailViewModel.selectedButton == WEEK_SELECT) {
                PeriodButton(coinDetailViewModel,
                    combinedChart,
                    buttonModifier
                        .border(1.dp,
                            colorResource(id = R.color.C0F0F5C))
                        .fillMaxHeight(),
                    "weeks",
                    "주", WEEK_SELECT)
            } else {
                PeriodButton(coinDetailViewModel,
                    combinedChart,
                    buttonModifier,
                    "weeks",
                    "주", WEEK_SELECT)
            }

            if (coinDetailViewModel.selectedButton == MONTH_SELECT) {
                PeriodButton(coinDetailViewModel,
                    combinedChart,
                    buttonModifier
                        .border(1.dp,
                            colorResource(id = R.color.C0F0F5C))
                        .fillMaxHeight(),
                    "months",
                    "월", MONTH_SELECT)
            } else {
                PeriodButton(coinDetailViewModel,
                    combinedChart,
                    buttonModifier,
                    "months",
                    "월", MONTH_SELECT)
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
                    val modifier = Modifier
                        .weight(1f)
                        .background(Color.White)
                        .border(0.5.dp, colorResource(id = R.color.C0F0F5C))
                        .fillMaxHeight()
                    MinuteButton(coinDetailViewModel,
                        combinedChart,
                        modifier,
                        "1",
                        "1분",
                        false)
                    MinuteButton(coinDetailViewModel,
                        combinedChart,
                        modifier,
                        "3",
                        "3분",
                        false)
                    MinuteButton(coinDetailViewModel,
                        combinedChart,
                        modifier,
                        "5",
                        "5분",
                        false)
                    MinuteButton(coinDetailViewModel,
                        combinedChart,
                        modifier,
                        "10",
                        "10분",
                        true)
                    MinuteButton(coinDetailViewModel,
                        combinedChart,
                        modifier,
                        "15",
                        "15분",
                        true)
                    MinuteButton(coinDetailViewModel,
                        combinedChart,
                        modifier,
                        "30",
                        "30분",
                        true)
                    MinuteButton(coinDetailViewModel,
                        combinedChart,
                        modifier,
                        "60",
                        "60분",
                        true)
                    MinuteButton(coinDetailViewModel,
                        combinedChart,
                        modifier,
                        "240",
                        "240분",
                        true)
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
        coinDetailViewModel.minuteText = "분"
        coinDetailViewModel.requestChartData(combinedChart)
        coinDetailViewModel.selectedButton = period
    }, modifier = modifier) {
        Text(text = buttonText,
            style = TextStyle(color = buttonColor))
    }
}

@Composable
fun MinuteButton(
    coinDetailViewModel: CoinDetailViewModel = viewModel(),
    combinedChart: CombinedChart,
    modifier: Modifier,
    candleType: String,
    minuteTextValue: String,
    autoSizeText: Boolean,
) {
    TextButton(onClick = {
        coinDetailViewModel.chartLastData = false
        coinDetailViewModel.candleType = candleType
        coinDetailViewModel.requestChartData(combinedChart)
        coinDetailViewModel.minuteVisible = false
        coinDetailViewModel.minuteText = minuteTextValue
        coinDetailViewModel.selectedButton = MINUTE_SELECT
    }, modifier = modifier
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
            Text(text = minuteTextValue, style = TextStyle(color = Color.Black, fontSize = 14.sp))
        }
    }
}