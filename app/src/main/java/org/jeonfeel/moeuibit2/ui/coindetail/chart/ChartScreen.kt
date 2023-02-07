package org.jeonfeel.moeuibit2.ui.coindetail.chart

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.github.mikephil.charting.charts.CombinedChart
import org.jeonfeel.moeuibit2.MoeuiBitDataStore.isKor
import org.jeonfeel.moeuibit2.R
import org.jeonfeel.moeuibit2.ui.common.CommonLoadingDialog
import org.jeonfeel.moeuibit2.ui.viewmodels.CoinDetailViewModel
import org.jeonfeel.moeuibit2.ui.custom.AutoSizeText
import org.jeonfeel.moeuibit2.ui.custom.DpToSp
import org.jeonfeel.moeuibit2.utils.Utils
import org.jeonfeel.moeuibit2.utils.OnLifecycleEvent
import org.jeonfeel.moeuibit2.utils.XAxisValueFormatter
import org.jeonfeel.moeuibit2.utils.chartRefreshLoadMoreData

const val MINUTE_SELECT = 1
const val DAY_SELECT = 2
const val WEEK_SELECT = 3
const val MONTH_SELECT = 4

const val CHART_ADD = 1
const val CHART_SET_CANDLE = 2
const val CHART_SET_ALL = 3
const val CHART_INIT = 4
const val CHART_OLD_DATA = 5

const val POSITIVE_BAR = 0
const val NEGATIVE_BAR = 1

@Composable
fun ChartScreen(coinDetailViewModel: CoinDetailViewModel = viewModel()) {

    val context = LocalContext.current
    val combinedChart = remember { MBitCombinedChart(context) }
    val candleUpdateLiveData = coinDetailViewModel.chart.chartUpdateLiveData.observeAsState(-99)

    CommonLoadingDialog(
        dialogState = coinDetailViewModel.chart.state.loadingDialogState,
        text = stringResource(id = R.string.loading_chart)
    )

    OnLifecycleEvent { _, event ->
        when (event) {
            Lifecycle.Event.ON_STOP -> {
                coinDetailViewModel.chart.candlePosition = 0f
                coinDetailViewModel.chart.state.isUpdateChart.value = false
                combinedChart.xAxis.valueFormatter = null
            }
            Lifecycle.Event.ON_START -> {
                combinedChart.initChart(
                    coinDetailViewModel::requestOldData,
                    marketState = Utils.getSelectedMarket(coinDetailViewModel.market),
                    isLoadingMoreData = coinDetailViewModel.chart.loadingMoreChartData,
                    minuteVisibility = coinDetailViewModel.chart.state.minuteVisible,
                    accData = coinDetailViewModel.chart.accData,
                    kstDateHashMap = coinDetailViewModel.chart.kstDateHashMap
                )
                combinedChart.axisRight.removeAllLimitLines()
                combinedChart.xAxis.removeAllLimitLines()
                coinDetailViewModel.requestChartData()
                coinDetailViewModel.chart.state.isUpdateChart.value = true
            }
            else -> {}
        }
    }

    when (candleUpdateLiveData.value) {
        CHART_INIT -> {
            combinedChart.getChartXValueFormatter()?.let {
//                (it as XAxisValueFormatter).setItem(
//                    newDateHashMap = coinDetailViewModel.chart.kstDateHashMap
//                )
            }
            combinedChart.chartInit(
                candleEntries = coinDetailViewModel.chart.candleEntries,
                candleDataSet = coinDetailViewModel.chart.candleDataSet,
                positiveBarDataSet = coinDetailViewModel.chart.positiveBarDataSet,
                negativeBarDataSet = coinDetailViewModel.chart.negativeBarDataSet,
                lineData = coinDetailViewModel.chart.createLineData(),
                purchaseAveragePrice = coinDetailViewModel.chart.purchaseAveragePrice
            )
            combinedChart.initCanvas()
        }
        CHART_ADD -> {
            combinedChart.getChartXValueFormatter()?.let {
                val candlePosition = coinDetailViewModel.chart.candlePosition.toInt()
//                (it as XAxisValueFormatter).addItem(
//                    newDateString = coinDetailViewModel.chart.kstDateHashMap[candlePosition] ?: "",
//                    position = candlePosition
//                )
            }
            combinedChart.chartAdd(
                model = coinDetailViewModel.chart.addModel,
                candlePosition = coinDetailViewModel.chart.candlePosition,
                addLineData = coinDetailViewModel.chart::addLineData
            )
        }
        CHART_OLD_DATA -> {
//            combinedChart.getChartXValueFormatter()?.let {
//                (it as XAxisValueFormatter).setItem(coinDetailViewModel.chart.kstDateHashMap)
//            }
            combinedChart.chartRefreshLoadMoreData(
                candleDataSet = coinDetailViewModel.chart.candleDataSet,
                positiveBarDataSet = coinDetailViewModel.chart.positiveBarDataSet,
                negativeBarDataSet = coinDetailViewModel.chart.negativeBarDataSet,
                lineData = coinDetailViewModel.chart.createLineData(),
                startPosition = combinedChart.lowestVisibleX,
                currentVisible = combinedChart.visibleXRange
            )
        }
        else -> {
            if (candleUpdateLiveData.value != -99 && !coinDetailViewModel.chart.isCandleEntryEmpty()) {
                combinedChart.chartSet(
                    marketState = Utils.getSelectedMarket(coinDetailViewModel.market),
                    lastCandleEntry = coinDetailViewModel.chart.getLastCandleEntry(),
                    candleEntriesIsEmpty = coinDetailViewModel.chart.isCandleEntryEmpty(),
                    candleUpdateLiveDataValue = candleUpdateLiveData.value,
                    isUpdateChart = coinDetailViewModel.chart.state.isUpdateChart,
                    accData = coinDetailViewModel.chart.accData,
                    candlePosition = coinDetailViewModel.chart.candlePosition,
                )
            }
        }
    }

    AndroidView(factory = {
        combinedChart
    }, modifier = Modifier.fillMaxSize())
    buttons()
}

@Composable
private fun buttons() {
//    Column(modifier = Modifier.fillMaxSize()) {
//        Row(
//            modifier = Modifier
//                .fillMaxWidth()
//                .height(35.dp)
//        ) {
//            val buttonModifier = remember {
//                Modifier
//                    .weight(1f)
//                    .fillMaxHeight()
//            }
//            val btnColor = if (coinDetailViewModel.selectedButton == MINUTE_SELECT) {
//                colorResource(id = R.color.C0F0F5C)
//            } else {
//                Color.LightGray
//            }
//
//            TextButton(
//                onClick = {
//                    coinDetailViewModel.minuteVisible = !coinDetailViewModel.minuteVisible
//                }, modifier = if (coinDetailViewModel.selectedButton == MINUTE_SELECT)
//                    buttonModifier
//                        .border(1.dp, colorResource(id = R.color.C0F0F5C))
//                        .fillMaxHeight()
//                else buttonModifier
//            ) {
//                Text(
//                    text = coinDetailViewModel.minuteText,
//                    style = TextStyle(color = btnColor)
//                )
//            }
//            if (coinDetailViewModel.selectedButton == DAY_SELECT) {
//
//
//                PeriodButton(
//                    coinDetailViewModel,
//                    combinedChart,
//                    buttonModifier
//                        .border(
//                            1.dp,
//                            colorResource(id = R.color.C0F0F5C)
//                        )
//                        .fillMaxHeight(),
//                    "days",
//                    stringResource(id = R.string.day), DAY_SELECT
//                )
//            } else {
//                PeriodButton(
//                    coinDetailViewModel,
//                    combinedChart,
//                    buttonModifier,
//                    "days",
//                    stringResource(id = R.string.day), DAY_SELECT
//                )
//            }
//
//            if (coinDetailViewModel.selectedButton == WEEK_SELECT) {
//                PeriodButton(
//                    coinDetailViewModel,
//                    combinedChart,
//                    buttonModifier
//                        .border(
//                            1.dp,
//                            colorResource(id = R.color.C0F0F5C)
//                        )
//                        .fillMaxHeight(),
//                    "weeks",
//                    stringResource(id = R.string.week), WEEK_SELECT
//                )
//            } else {
//                PeriodButton(
//                    coinDetailViewModel,
//                    combinedChart,
//                    buttonModifier,
//                    "weeks",
//                    stringResource(id = R.string.week), WEEK_SELECT
//                )
//            }
//
//            if (coinDetailViewModel.selectedButton == MONTH_SELECT) {
//                PeriodButton(
//                    coinDetailViewModel,
//                    combinedChart,
//                    buttonModifier
//                        .border(
//                            1.dp,
//                            colorResource(id = R.color.C0F0F5C)
//                        )
//                        .fillMaxHeight(),
//                    "months",
//                    stringResource(id = R.string.month), MONTH_SELECT
//                )
//            } else {
//                PeriodButton(
//                    coinDetailViewModel,
//                    combinedChart,
//                    buttonModifier,
//                    "months",
//                    stringResource(id = R.string.month), MONTH_SELECT
//                )
//            }
//        }
//
//        Box(modifier = Modifier.fillMaxSize()) {
//            AndroidView(
//                factory = {
//                    combinedChart
//                },
//                modifier = Modifier.fillMaxSize()
//            )
//            if (coinDetailViewModel.minuteVisible) {
//                Row(
//                    modifier = Modifier
//                        .fillMaxWidth()
//                        .height(35.dp)
//                        .align(Alignment.TopCenter)
//                ) {
//                    for (i in chartMinuteArray.indices) {
//                        MinuteButton(
//                            coinDetailViewModel = coinDetailViewModel,
//                            combinedChart = combinedChart,
//                            candleType = chartMinuteArray[i],
//                            minuteTextValue = chartMinuteStrArray[i],
//                            autoSizeText = false
//                        )
//                    }
//                }
//            }
//        }
//    }
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
//    val buttonColor = if (coinDetailViewModel.selectedButton == period) {
//        colorResource(id = R.color.C0F0F5C)
//    } else {
//        Color.LightGray
//    }
//
//    TextButton(onClick = {
//        coinDetailViewModel.chartLastData = false
//        coinDetailViewModel.candleType = candleType
//        coinDetailViewModel.minuteVisible = false
//        coinDetailViewModel.minuteText = if (isKor) "분" else "m,h"
//        coinDetailViewModel.requestChartData(combinedChart)
//        coinDetailViewModel.selectedButton = period
//    }, modifier = modifier) {
//        Text(
//            text = buttonText,
//            style = TextStyle(color = buttonColor)
//        )
//    }
//}
//
//@Composable
//fun RowScope.MinuteButton(
//    coinDetailViewModel: CoinDetailViewModel = viewModel(),
//    combinedChart: CombinedChart,
//    candleType: String,
//    minuteTextValue: String,
//    autoSizeText: Boolean,
//) {
//    TextButton(
//        onClick = {
//            coinDetailViewModel.chartLastData = false
//            coinDetailViewModel.candleType = candleType
//            coinDetailViewModel.requestChartData(combinedChart)
//            coinDetailViewModel.minuteVisible = false
//            coinDetailViewModel.minuteText = minuteTextValue
//            coinDetailViewModel.selectedButton = MINUTE_SELECT
//        }, modifier = Modifier
//            .weight(1f)
//            .background(Color.White)
//            .border(0.5.dp, colorResource(id = R.color.C0F0F5C))
//            .fillMaxHeight()
//    ) {
//        if (autoSizeText) {
//            AutoSizeText(
//                text = minuteTextValue,
//                textStyle = MaterialTheme.typography.body1.copy(color = Color.Black),
//                modifier = Modifier
//                    .fillMaxHeight()
//                    .wrapContentHeight()
//                    .weight(1f)
//            )
//        } else {
//            Text(
//                text = minuteTextValue,
//                style = TextStyle(color = Color.Black, fontSize = DpToSp(14.dp))
//            )
//        }
//    }
}