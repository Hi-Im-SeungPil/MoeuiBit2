package org.jeonfeel.moeuibit2.ui.coindetail

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.Observer
import androidx.lifecycle.viewmodel.compose.viewModel
import com.github.mikephil.charting.charts.CombinedChart
import org.jeonfeel.moeuibit2.R
import org.jeonfeel.moeuibit2.util.OnLifecycleEvent
import org.jeonfeel.moeuibit2.util.initCombinedChart
import org.jeonfeel.moeuibit2.viewmodel.CoinDetailViewModel

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
    val minuteVisible = coinDetailViewModel.minuteVisible

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
                coinDetailViewModel.requestChartData(
                    coinDetailViewModel.candleType.value,
                    combinedChart
                )
                coinDetailViewModel.isUpdateChart = true
                coinDetailViewModel.firstCandleDataSetLiveData.observe(lifeCycleOwner, Observer {
                    if (it == "add") {
                        combinedChart.xAxis.axisMaximum = combinedChart.xAxis.axisMaximum + 1f
                        combinedChart.data.notifyDataChanged()
                        combinedChart.notifyDataSetChanged()
                        combinedChart.invalidate()
                    } else {
                        if (combinedChart.candleData != null) {
                            coinDetailViewModel.setBar()
                            combinedChart.data.notifyDataChanged()
                            combinedChart.notifyDataSetChanged()
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
                minuteVisible.value = !minuteVisible.value
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
                    minuteVisible,
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
                    minuteVisible,
                    buttonModifier,
                    "days",
                    "일", selectedButton, DAY_SELECT)
            }

            if (selectedButton.value == WEEK_SELECT) {
                PeriodButton(coinDetailViewModel,
                    combinedChart,
                    minuteText,
                    minuteVisible,
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
                    minuteVisible,
                    buttonModifier,
                    "weeks",
                    "주", selectedButton, WEEK_SELECT)
            }

            if (selectedButton.value == MONTH_SELECT) {
                PeriodButton(coinDetailViewModel,
                    combinedChart,
                    minuteText,
                    minuteVisible,
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
                    minuteVisible,
                    buttonModifier,
                    "months",
                    "월", selectedButton, MONTH_SELECT)
            }
        }

        Box(modifier = Modifier.fillMaxSize()) {
            AndroidView(
                factory = { context ->
                    combinedChart
                },
                modifier = Modifier.fillMaxSize()
            )
            if (minuteVisible.value) {
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
                        minuteVisible,
                        minuteText,
                        modifier,
                        "1",
                        "1분",
                        selectedButton,
                        false)
                    MinuteButton(coinDetailViewModel,
                        combinedChart,
                        minuteVisible,
                        minuteText,
                        modifier,
                        "3",
                        "3분",
                        selectedButton,
                        false)
                    MinuteButton(coinDetailViewModel,
                        combinedChart,
                        minuteVisible,
                        minuteText,
                        modifier,
                        "5",
                        "5분",
                        selectedButton,
                        false)
                    MinuteButton(coinDetailViewModel,
                        combinedChart,
                        minuteVisible,
                        minuteText,
                        modifier,
                        "10",
                        "10분",
                        selectedButton,
                        true)
                    MinuteButton(coinDetailViewModel,
                        combinedChart,
                        minuteVisible,
                        minuteText,
                        modifier,
                        "15",
                        "15분",
                        selectedButton,
                        true)
                    MinuteButton(coinDetailViewModel,
                        combinedChart,
                        minuteVisible,
                        minuteText,
                        modifier,
                        "30",
                        "30분",
                        selectedButton,
                        true)
                    MinuteButton(coinDetailViewModel,
                        combinedChart,
                        minuteVisible,
                        minuteText,
                        modifier,
                        "60",
                        "60분",
                        selectedButton,
                        true)
                    MinuteButton(coinDetailViewModel,
                        combinedChart,
                        minuteVisible,
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
    minuteVisible: MutableState<Boolean>,
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
        coinDetailViewModel.candleType.value = candleType
        coinDetailViewModel.requestChartData(
            coinDetailViewModel.candleType.value,
            combinedChart
        )
        minuteText.value = "분"
        minuteVisible.value = false
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
    minuteVisible: MutableState<Boolean>,
    minuteText: MutableState<String>,
    modifier: Modifier,
    candleType: String,
    minuteTextValue: String,
    buttonPeriod: MutableState<Int>,
    autoSizeText: Boolean,
) {
    TextButton(onClick = {
        coinDetailViewModel.candleType.value = candleType
        coinDetailViewModel.requestChartData(
            coinDetailViewModel.candleType.value,
            combinedChart
        )
        minuteVisible.value = false
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
    if (coinDetailViewModel.dialogState.value) {
        Dialog(onDismissRequest = { coinDetailViewModel.dialogState.value = false },
            DialogProperties(dismissOnBackPress = false, dismissOnClickOutside = false)) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .background(colorResource(id = R.color.design_default_color_background), shape = RoundedCornerShape(12.dp))
            ) {
                Column {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally).padding(0.dp,20.dp,0.dp,0.dp))
                    Text(text = "데이터 불러오는 중...", Modifier.padding(20.dp, 8.dp, 20.dp, 15.dp))
                }
            }
        }
    }
}