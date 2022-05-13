package org.jeonfeel.moeuibit2.ui.coindetail.chart

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
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
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.core.view.get
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.Observer
import androidx.lifecycle.viewmodel.compose.viewModel
import com.github.mikephil.charting.charts.CombinedChart
import com.github.mikephil.charting.components.LimitLine
import com.github.mikephil.charting.data.CandleEntry
import org.jeonfeel.moeuibit2.R
import org.jeonfeel.moeuibit2.ui.coindetail.AutoSizeText
import org.jeonfeel.moeuibit2.util.Calculator
import org.jeonfeel.moeuibit2.util.OnLifecycleEvent
import org.jeonfeel.moeuibit2.util.initCombinedChart
import org.jeonfeel.moeuibit2.viewmodel.coindetail.CoinDetailViewModel

const val MINUTE_SELECT = 1
const val DAY_SELECT = 2
const val WEEK_SELECT = 3
const val MONTH_SELECT = 4

@Composable
fun ChartScreen(coinDetailViewModel: CoinDetailViewModel = viewModel()) {

    val context = LocalContext.current
    val applicationContext = context.applicationContext
    val combinedChart = remember {
        CombinedChart(context)
    }
    val minuteText = remember {
        mutableStateOf("1분")
    }
    val selectedButton = remember {
        mutableStateOf(MINUTE_SELECT)
    }

    ProgressDialog(coinDetailViewModel)
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

                coinDetailViewModel.candleUpdateLiveData.observe(lifeCycleOwner, Observer {
                    if (it == "add") {
                        combinedChart.xAxis.axisMaximum = combinedChart.xAxis.axisMaximum + 1f
                        combinedChart.invalidate()
                    } else if (it == "set") {
                        if (combinedChart.candleData != null) {
                            if (combinedChart.data.candleData.xMax <= combinedChart.highestVisibleX && !coinDetailViewModel.candleEntriesIsEmpty) {
                                val canvas = (combinedChart[0] as DrawPractice)
                                val lastCandle = coinDetailViewModel.candleEntryLast
                                val tradePrice = lastCandle.close
                                val openPrice = lastCandle.open
                                val color = if (tradePrice - openPrice >= 0.0) {
                                    android.graphics.Color.RED
                                } else {
                                    android.graphics.Color.BLUE
                                }
                                val yp = combinedChart.getPosition(CandleEntry(tradePrice,
                                    tradePrice,
                                    tradePrice,
                                    tradePrice,
                                    tradePrice), combinedChart.axisRight.axisDependency).y
                                canvas.realTimeLastCandleClose(yp,
                                    Calculator.tradePriceCalculatorForChart(tradePrice),
                                    color)
                            }
                            combinedChart.invalidate()
                        }
                    } else {
                        if (combinedChart.candleData != null) {
                            if (combinedChart.data.candleData.xMax <= combinedChart.highestVisibleX && !coinDetailViewModel.candleEntriesIsEmpty) {
                                val canvas = (combinedChart[0] as DrawPractice)
                                val lastCandle = coinDetailViewModel.candleEntryLast
                                val tradePrice = lastCandle.close
                                val openPrice = lastCandle.open
                                val lastX = lastCandle.x
                                val color = if (tradePrice - openPrice >= 0.0) {
                                    android.graphics.Color.RED
                                } else {
                                    android.graphics.Color.BLUE
                                }

                                val lastBar =
                                    if (combinedChart.barData.dataSets[0].getEntriesForXValue(lastX)
                                            .isEmpty()
                                    ) {
                                        combinedChart.barData.dataSets[1].getEntriesForXValue(lastX)
                                            .first()
                                    } else {
                                        combinedChart.barData.dataSets[0].getEntriesForXValue(lastX)
                                            .first()
                                    }

                                val barPrice = lastBar.y
                                val yp = combinedChart.getPosition(CandleEntry(tradePrice,
                                    tradePrice,
                                    tradePrice,
                                    tradePrice,
                                    tradePrice), combinedChart.axisRight.axisDependency).y

                                if (combinedChart.axisLeft.limitLines.isNotEmpty()) {
                                    combinedChart.axisLeft.removeAllLimitLines()
                                }
                                val lastBarLimitLine = LimitLine(barPrice,
                                    Calculator.accTradePrice24hCalculator(coinDetailViewModel.accData[lastX.toInt()]!!))
                                lastBarLimitLine.lineColor = color
                                lastBarLimitLine.textColor = color
                                lastBarLimitLine.lineWidth = 0f
                                lastBarLimitLine.textSize = 11f
                                lastBarLimitLine.labelPosition =
                                    LimitLine.LimitLabelPosition.RIGHT_TOP
                                combinedChart.axisLeft.addLimitLine(lastBarLimitLine)
                                canvas.realTimeLastCandleClose(yp,
                                    Calculator.tradePriceCalculatorForChart(tradePrice),
                                    color)
                            }
                            combinedChart.invalidate()
                        }
                    }
                })
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
            val btnColor = if (selectedButton.value == MINUTE_SELECT) {
                colorResource(id = R.color.C0F0F5C)
            } else {
                Color.LightGray
            }

            TextButton(onClick = {
                coinDetailViewModel.minuteVisible = !coinDetailViewModel.minuteVisible
            }, modifier = if (selectedButton.value == MINUTE_SELECT)
                buttonModifier
                    .border(1.dp, colorResource(id = R.color.C0F0F5C))
                    .fillMaxHeight()
            else buttonModifier) {
                Text(text = minuteText.value,
                    style = TextStyle(color = btnColor)
                )
            }
            if (selectedButton.value == DAY_SELECT) {
                PeriodButton(coinDetailViewModel,
                    combinedChart,
                    minuteText,
                    buttonModifier
                        .border(1.dp,
                            colorResource(id = R.color.C0F0F5C))
                        .fillMaxHeight(),
                    "days",
                    "일", selectedButton, DAY_SELECT)
            } else {
                PeriodButton(coinDetailViewModel,
                    combinedChart,
                    minuteText,
                    buttonModifier,
                    "days",
                    "일", selectedButton, DAY_SELECT)
            }

            if (selectedButton.value == WEEK_SELECT) {
                PeriodButton(coinDetailViewModel,
                    combinedChart,
                    minuteText,
                    buttonModifier
                        .border(1.dp,
                            colorResource(id = R.color.C0F0F5C))
                        .fillMaxHeight(),
                    "weeks",
                    "주", selectedButton, WEEK_SELECT)
            } else {
                PeriodButton(coinDetailViewModel,
                    combinedChart,
                    minuteText,
                    buttonModifier,
                    "weeks",
                    "주", selectedButton, WEEK_SELECT)
            }

            if (selectedButton.value == MONTH_SELECT) {
                PeriodButton(coinDetailViewModel,
                    combinedChart,
                    minuteText,
                    buttonModifier
                        .border(1.dp,
                            colorResource(id = R.color.C0F0F5C))
                        .fillMaxHeight(),
                    "months",
                    "월", selectedButton, MONTH_SELECT)
            } else {
                PeriodButton(coinDetailViewModel,
                    combinedChart,
                    minuteText,
                    buttonModifier,
                    "months",
                    "월", selectedButton, MONTH_SELECT)
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
                        minuteText,
                        modifier,
                        "1",
                        "1분",
                        selectedButton,
                        false)
                    MinuteButton(coinDetailViewModel,
                        combinedChart,
                        minuteText,
                        modifier,
                        "3",
                        "3분",
                        selectedButton,
                        false)
                    MinuteButton(coinDetailViewModel,
                        combinedChart,
                        minuteText,
                        modifier,
                        "5",
                        "5분",
                        selectedButton,
                        false)
                    MinuteButton(coinDetailViewModel,
                        combinedChart,
                        minuteText,
                        modifier,
                        "10",
                        "10분",
                        selectedButton,
                        true)
                    MinuteButton(coinDetailViewModel,
                        combinedChart,
                        minuteText,
                        modifier,
                        "15",
                        "15분",
                        selectedButton,
                        true)
                    MinuteButton(coinDetailViewModel,
                        combinedChart,
                        minuteText,
                        modifier,
                        "30",
                        "30분",
                        selectedButton,
                        true)
                    MinuteButton(coinDetailViewModel,
                        combinedChart,
                        minuteText,
                        modifier,
                        "60",
                        "60분",
                        selectedButton,
                        true)
                    MinuteButton(coinDetailViewModel,
                        combinedChart,
                        minuteText,
                        modifier,
                        "240",
                        "240분",
                        selectedButton,
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
    minuteText: MutableState<String>,
    modifier: Modifier,
    candleType: String,
    buttonText: String,
    buttonPeriod: MutableState<Int>,
    period: Int,
) {
    val buttonColor = if (buttonPeriod.value == period) {
        colorResource(id = R.color.C0F0F5C)
    } else {
        Color.LightGray
    }

    TextButton(onClick = {
        coinDetailViewModel.chartLastData = false
        coinDetailViewModel.candleType = candleType
        coinDetailViewModel.requestChartData(combinedChart)
        coinDetailViewModel.minuteVisible = false
        minuteText.value = "분"
        buttonPeriod.value = period
    }, modifier = modifier) {
        Text(text = buttonText,
            style = TextStyle(color = buttonColor))
    }
}

@Composable
fun MinuteButton(
    coinDetailViewModel: CoinDetailViewModel = viewModel(),
    combinedChart: CombinedChart,
    minuteText: MutableState<String>,
    modifier: Modifier,
    candleType: String,
    minuteTextValue: String,
    buttonPeriod: MutableState<Int>,
    autoSizeText: Boolean,
) {
    TextButton(onClick = {
        coinDetailViewModel.chartLastData = false
        coinDetailViewModel.candleType = candleType
        coinDetailViewModel.requestChartData(combinedChart)
        coinDetailViewModel.minuteVisible = false
        minuteText.value = minuteTextValue
        buttonPeriod.value = MINUTE_SELECT
    }, modifier = modifier
    ) {
        if (autoSizeText) {
            val style = MaterialTheme.typography.body2.copy(color = Color.Black)
            val textStyle = remember { mutableStateOf(style) }
            AutoSizeText(
                text = minuteTextValue, textStyle = textStyle.value,
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

@Composable
fun ProgressDialog(coinDetailViewModel: CoinDetailViewModel = viewModel()) {
    if (coinDetailViewModel.dialogState) {
        Dialog(onDismissRequest = { coinDetailViewModel.dialogState = false },
            DialogProperties(dismissOnBackPress = true, dismissOnClickOutside = false)) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .background(colorResource(id = R.color.design_default_color_background))
            ) {
                Column {
                    CircularProgressIndicator(modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .padding(0.dp, 20.dp, 0.dp, 0.dp),
                        color = colorResource(id = R.color.C0F0F5C))
                    Text(text = "데이터 불러오는 중...", Modifier.padding(20.dp, 8.dp, 20.dp, 15.dp))
                }
            }
        }
    }
}