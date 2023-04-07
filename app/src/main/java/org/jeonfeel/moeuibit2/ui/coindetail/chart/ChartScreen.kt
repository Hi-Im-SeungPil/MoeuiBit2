package org.jeonfeel.moeuibit2.ui.coindetail.chart

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import org.jeonfeel.moeuibit2.MoeuiBitDataStore.isKor
import org.jeonfeel.moeuibit2.R
import org.jeonfeel.moeuibit2.constants.chartMinuteArray
import org.jeonfeel.moeuibit2.constants.chartMinuteStrArray
import org.jeonfeel.moeuibit2.ui.coindetail.chart.ui.view.MBitCombinedChart
import org.jeonfeel.moeuibit2.ui.coindetail.chart.utils.chartRefreshLoadMoreData
import org.jeonfeel.moeuibit2.ui.common.CommonLoadingDialog
import org.jeonfeel.moeuibit2.ui.custom.AutoSizeText
import org.jeonfeel.moeuibit2.ui.custom.DpToSp
import org.jeonfeel.moeuibit2.ui.viewmodels.CoinDetailViewModel
import org.jeonfeel.moeuibit2.utils.OnLifecycleEvent
import org.jeonfeel.moeuibit2.utils.Utils

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
    val lifecycleOwner = LocalLifecycleOwner.current

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
                combinedChart.axisRight.removeAllLimitLines()
                combinedChart.xAxis.removeAllLimitLines()
                coinDetailViewModel.requestChartData()
                coinDetailViewModel.chart.state.isUpdateChart.value = true
            }
            else -> {}
        }
    }

    // 차트 추가인지 셋인지
    LaunchedEffect(true) {
        coinDetailViewModel.chart.chartUpdateLiveData.observe(
            lifecycleOwner
        ) {
            when (it) {
                // 차트 초기화
                CHART_INIT -> {
                    combinedChart.getChartXValueFormatter()?.setItem(
                        newDateHashMap = coinDetailViewModel.chart.kstDateHashMap
                    )
                    combinedChart.initChart(
                        coinDetailViewModel::requestOldData,
                        marketState = Utils.getSelectedMarket(coinDetailViewModel.market),
                        loadingOldData = coinDetailViewModel.chart.state.loadingOldData,
                        isChartLastData = coinDetailViewModel.chart.state.isLastData,
                        minuteVisibility = coinDetailViewModel.chart.state.minuteVisible,
                        accData = coinDetailViewModel.chart.accData,
                        kstDateHashMap = coinDetailViewModel.chart.kstDateHashMap
                    )
                    combinedChart.chartDataInit(
                        candleEntries = coinDetailViewModel.chart.candleEntries,
                        candleDataSet = coinDetailViewModel.chart.candleDataSet,
                        positiveBarDataSet = coinDetailViewModel.chart.positiveBarDataSet,
                        negativeBarDataSet = coinDetailViewModel.chart.negativeBarDataSet,
                        lineData = coinDetailViewModel.chart.createLineData(),
                        purchaseAveragePrice = coinDetailViewModel.chart.purchaseAveragePrice
                    )
                    combinedChart.initCanvas()
                }
                // 차트 컴포넌트 추가되었을 떄
                CHART_ADD -> {
                    combinedChart.getChartXValueFormatter()?.let {
                        val candlePosition = coinDetailViewModel.chart.candlePosition.toInt()
                        it.addItem(
                            newDateString = coinDetailViewModel.chart.kstDateHashMap[candlePosition]
                                ?: "",
                            position = candlePosition
                        )
                    }
                    combinedChart.chartAdd(
                        model = coinDetailViewModel.chart.addModel,
                        candlePosition = coinDetailViewModel.chart.candlePosition,
                        addLineData = coinDetailViewModel.chart::addLineData
                    )
                }
                // 예전 데이터 불러오기
                CHART_OLD_DATA -> {
                    combinedChart.getChartXValueFormatter()
                        ?.setItem(coinDetailViewModel.chart.kstDateHashMap)
                    combinedChart.chartRefreshLoadMoreData(
                        candleDataSet = coinDetailViewModel.chart.candleDataSet,
                        positiveBarDataSet = coinDetailViewModel.chart.positiveBarDataSet,
                        negativeBarDataSet = coinDetailViewModel.chart.negativeBarDataSet,
                        lineData = coinDetailViewModel.chart.createLineData(),
                        startPosition = combinedChart.lowestVisibleX,
                        currentVisible = combinedChart.visibleXRange,
                        loadingOldData = coinDetailViewModel.chart.state.loadingOldData
                    )
                }
                // 마지막 차트 컴포넌트 최신화
                else -> {
                    if (!coinDetailViewModel.chart.isCandleEntryEmpty()) {
                        combinedChart.chartSet(
                            marketState = Utils.getSelectedMarket(coinDetailViewModel.market),
                            lastCandleEntry = coinDetailViewModel.chart.getLastCandleEntry(),
                            candleEntriesIsEmpty = coinDetailViewModel.chart.isCandleEntryEmpty(),
                            candleUpdateLiveDataValue = it,
                            isUpdateChart = coinDetailViewModel.chart.state.isUpdateChart,
                            accData = coinDetailViewModel.chart.accData,
                            candlePosition = coinDetailViewModel.chart.candlePosition,
                        )
                    }
                }
            }
        }
    }
    // 버튼들과 차트.
    Column(modifier = Modifier.fillMaxSize()) {
        PeriodButtons(
            selectedButton = coinDetailViewModel.chart.state.selectedButton,
            minuteVisibility = coinDetailViewModel.chart.state.minuteVisible,
            minuteText = coinDetailViewModel.chart.state.minuteText,
            candleType = coinDetailViewModel.chart.state.candleType,
            isChartLastData = coinDetailViewModel.chart.state.isLastData,
            requestChartData = coinDetailViewModel::requestChartData
        )
        Box(modifier = Modifier.fillMaxSize()) {
            AndroidView(
                factory = {
                    combinedChart
                },
                modifier = Modifier.fillMaxSize()
            )
            if (coinDetailViewModel.chart.state.minuteVisible.value) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(35.dp)
                        .align(Alignment.TopCenter)
                ) {
                    for (i in chartMinuteArray.indices) {
                        MinuteButton(
                            isChartLastData = coinDetailViewModel.chart.state.isLastData,
                            minuteVisibility = coinDetailViewModel.chart.state.minuteVisible,
                            minuteText = coinDetailViewModel.chart.state.minuteText,
                            minuteTextValue = chartMinuteStrArray[i],
                            candleType = coinDetailViewModel.chart.state.candleType,
                            candleTypeValue = chartMinuteArray[i],
                            autoSizeText = true,
                            selectedButton = coinDetailViewModel.chart.state.selectedButton,
                            requestChartData = coinDetailViewModel::requestChartData
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun PeriodButtons(
    selectedButton: MutableState<Int>,
    minuteVisibility: MutableState<Boolean>,
    minuteText: MutableState<String>,
    candleType: MutableState<String>,
    isChartLastData: MutableState<Boolean>,
    requestChartData: () -> Unit
) {
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
            androidx.compose.material3.MaterialTheme.colorScheme.primary
        } else {
            Color.LightGray
        }

        TextButton(
            onClick = {
                minuteVisibility.value = !minuteVisibility.value
            }, modifier = if (selectedButton.value == MINUTE_SELECT)
                buttonModifier
                    .border(1.dp, androidx.compose.material3.MaterialTheme.colorScheme.primary)
                    .fillMaxHeight()
            else buttonModifier
        ) {
            Text(
                text = minuteText.value,
                style = TextStyle(color = btnColor)
            )
        }

        PeriodButton(
            modifier = buttonModifier,
            selectedButton = selectedButton,
            candleType = candleType,
            candleTypeValue = "days",
            minuteVisibility,
            minuteText,
            buttonText = stringResource(id = R.string.day),
            isChartLastData = isChartLastData,
            period = DAY_SELECT,
            requestChartData = requestChartData
        )
        PeriodButton(
            modifier = buttonModifier,
            selectedButton = selectedButton,
            candleType = candleType,
            candleTypeValue = "weeks",
            minuteVisibility,
            minuteText,
            buttonText = stringResource(id = R.string.week),
            isChartLastData = isChartLastData,
            period = WEEK_SELECT,
            requestChartData = requestChartData
        )
        PeriodButton(
            modifier = buttonModifier,
            selectedButton = selectedButton,
            candleType = candleType,
            candleTypeValue = "months",
            minuteVisibility,
            minuteText,
            buttonText = stringResource(id = R.string.month),
            isChartLastData = isChartLastData,
            period = MONTH_SELECT,
            requestChartData = requestChartData
        )
    }
}

@Composable
fun PeriodButton(
    modifier: Modifier,
    selectedButton: MutableState<Int>,
    candleType: MutableState<String>,
    candleTypeValue: String,
    minuteVisibility: MutableState<Boolean>,
    minuteText: MutableState<String>,
    buttonText: String,
    isChartLastData: MutableState<Boolean>,
    period: Int,
    requestChartData: () -> Unit
) {
    val buttonColor = if (selectedButton.value == period) {
        androidx.compose.material3.MaterialTheme.colorScheme.primary
    } else {
        Color.LightGray
    }

    val modifierResult = if (selectedButton.value == period) {
        modifier
            .border(1.dp, androidx.compose.material3.MaterialTheme.colorScheme.primary)
            .fillMaxHeight()
    } else {
        modifier
    }

    TextButton(onClick = {
        isChartLastData.value = false
        candleType.value = candleTypeValue
        minuteVisibility.value = false
        minuteText.value = if (isKor) "분" else "m,h"
        selectedButton.value = period
        requestChartData()
    }, modifier = modifierResult) {
        Text(
            text = buttonText,
            style = TextStyle(color = buttonColor)
        )
    }
}

@Composable
fun RowScope.MinuteButton(
    isChartLastData: MutableState<Boolean>,
    minuteVisibility: MutableState<Boolean>,
    minuteText: MutableState<String>,
    selectedButton: MutableState<Int>,
    candleType: MutableState<String>,
    candleTypeValue: String,
    minuteTextValue: String,
    autoSizeText: Boolean,
    requestChartData: () -> Unit
) {
    TextButton(
        onClick = {
            isChartLastData.value = false
            candleType.value = candleTypeValue
            minuteVisibility.value = false
            minuteText.value = minuteTextValue
            selectedButton.value = MINUTE_SELECT
            requestChartData()
        }, modifier = Modifier
            .weight(1f)
            .background(androidx.compose.material3.MaterialTheme.colorScheme.background)
            .border(0.5.dp, androidx.compose.material3.MaterialTheme.colorScheme.primary)
            .fillMaxHeight()
    ) {
        if (autoSizeText) {
            AutoSizeText(
                text = minuteTextValue,
                textStyle = MaterialTheme.typography.body1.copy(color = androidx.compose.material3.MaterialTheme.colorScheme.onBackground),
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