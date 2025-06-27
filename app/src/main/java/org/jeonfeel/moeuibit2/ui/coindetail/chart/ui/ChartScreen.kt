package org.jeonfeel.moeuibit2.ui.coindetail.chart.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import com.orhanobut.logger.Logger
import org.jeonfeel.moeuibit2.GlobalState
import org.jeonfeel.moeuibit2.R
import org.jeonfeel.moeuibit2.constants.EXCHANGE_BITTHUMB
import org.jeonfeel.moeuibit2.constants.EXCHANGE_UPBIT
import org.jeonfeel.moeuibit2.constants.bitthumbChartMinuteArray
import org.jeonfeel.moeuibit2.constants.bitthumbChartMinuteStrArray
import org.jeonfeel.moeuibit2.constants.chartMinuteArray
import org.jeonfeel.moeuibit2.constants.chartMinuteStrArray
import org.jeonfeel.moeuibit2.ui.coindetail.detail.NewCoinDetailViewModel
import org.jeonfeel.moeuibit2.ui.coindetail.chart.ui.view.MBitCombinedChart
import org.jeonfeel.moeuibit2.ui.coindetail.chart.utils.chartRefreshLoadMoreData
import org.jeonfeel.moeuibit2.ui.common.AutoSizeText
import org.jeonfeel.moeuibit2.ui.common.CommonLoadingDialog
import org.jeonfeel.moeuibit2.ui.common.DpToSp
import org.jeonfeel.moeuibit2.ui.theme.newtheme.commonBackground
import org.jeonfeel.moeuibit2.ui.theme.newtheme.commonHintTextColor
import org.jeonfeel.moeuibit2.ui.theme.newtheme.commonTextColor
import org.jeonfeel.moeuibit2.utils.NetworkConnectivityObserver
import org.jeonfeel.moeuibit2.utils.OnLifecycleEvent
import org.jeonfeel.moeuibit2.utils.Utils
import org.jeonfeel.moeuibit2.utils.ext.showToast
import kotlin.reflect.KFunction1

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
fun ChartScreen(viewModel: NewCoinDetailViewModel, market: String) {
    val context = LocalContext.current
    val combinedChart = remember { MBitCombinedChart(context) }
    val lifecycleOwner = LocalLifecycleOwner.current

    CommonLoadingDialog(
        dialogState = viewModel.chart.state.loadingDialogState,
        text = stringResource(id = R.string.loading_chart)
    )

    OnLifecycleEvent { _, event ->
        when (event) {
            Lifecycle.Event.ON_START -> {
                if (NetworkConnectivityObserver.isNetworkAvailable.value) {
                    if (!viewModel.isChartStarted.value) {
                        combinedChart.axisRight.removeAllLimitLines()
                        combinedChart.xAxis.removeAllLimitLines()
                        viewModel.requestChartData(market)
                        viewModel.chart.state
                        viewModel.chart.state.isUpdateChart.value = true
                    }
                }
            }

            Lifecycle.Event.ON_STOP -> {
                viewModel.stopRequestChartData()
                viewModel.chart.updateCandlePosition(0f)
                viewModel.chart.state.isUpdateChart.value = false
                combinedChart.xAxis.valueFormatter = null
            }

            else -> {}
        }
    }

    LaunchedEffect(NetworkConnectivityObserver.isNetworkAvailable.value) {
        if (NetworkConnectivityObserver.isNetworkAvailable.value) {
            if (!viewModel.isChartStarted.value) {
                combinedChart.axisRight.removeAllLimitLines()
                combinedChart.xAxis.removeAllLimitLines()
                viewModel.requestChartData(market)
                viewModel.chart.state.isUpdateChart.value = true
            }
        } else {
            viewModel.stopRequestChartData()
            viewModel.chart.updateCandlePosition(0f)
            viewModel.chart.state.isUpdateChart.value = false
            combinedChart.xAxis.valueFormatter = null
            context.showToast("인터넷 연결을 확인해주세요.")
        }
    }

    // 차트 추가인지 셋인지
    LaunchedEffect(true) {
        viewModel.chart.chartUpdateLiveData.observe(
            lifecycleOwner
        ) {
            try {
                when (it) {
                    // 차트 초기화
                    CHART_INIT -> {
                        combinedChart.getChartXValueFormatter()?.setItem(
                            newDateHashMap = viewModel.chart.kstDateHashMap
                        )
                        combinedChart.initChart(
                            viewModel::requestOldData,
                            marketState = Utils.getSelectedMarket(market),
                            loadingOldData = viewModel.chart.state.loadingOldData,
                            isChartLastData = viewModel.chart.state.isLastData,
                            minuteVisibility = viewModel.chart.state.minuteVisible,
                            accData = viewModel.chart.accData,
                            kstDateHashMap = viewModel.chart.kstDateHashMap,
                            market = market,
                        )
                        combinedChart.chartDataInit(
                            candleEntries = viewModel.chart.candleEntries,
                            candleDataSet = viewModel.chart.candleDataSet,
                            positiveBarDataSet = viewModel.chart.positiveBarDataSet,
                            negativeBarDataSet = viewModel.chart.negativeBarDataSet,
                            lineData = viewModel.chart.createLineData(),
                            purchaseAveragePrice = viewModel.chart.purchaseAveragePrice
                        )
                        combinedChart.initCanvas()
                    }
                    // 차트 컴포넌트 추가되었을 떄
                    CHART_ADD -> {
                        combinedChart.getChartXValueFormatter()?.let {
                            val candlePosition = viewModel.chart.candlePosition.toInt()
                            it.addItem(
                                newDateString = viewModel.chart.kstDateHashMap[candlePosition]
                                    ?: "",
                                position = candlePosition
                            )
                        }
                        combinedChart.chartAdd(
                            model = viewModel.chart.addModel,
                            candlePosition = viewModel.chart.candlePosition,
                            addLineData = viewModel.chart::addLineData
                        )
                    }
                    // 예전 데이터 불러오기
                    CHART_OLD_DATA -> {
                        combinedChart.getChartXValueFormatter()
                            ?.setItem(viewModel.chart.kstDateHashMap)
                        combinedChart.chartRefreshLoadMoreData(
                            candleDataSet = viewModel.chart.candleDataSet,
                            positiveBarDataSet = viewModel.chart.positiveBarDataSet,
                            negativeBarDataSet = viewModel.chart.negativeBarDataSet,
                            lineData = viewModel.chart.createLineData(),
                            startPosition = combinedChart.lowestVisibleX,
                            currentVisible = combinedChart.visibleXRange,
                            loadingOldData = viewModel.chart.state.loadingOldData
                        )
                    }
                    // 마지막 차트 컴포넌트 최신화
                    else -> {
                        if (!viewModel.chart.isCandleEntryEmpty()) {
                            combinedChart.chartSet(
                                marketState = Utils.getSelectedMarket(market),
                                lastCandleEntry = viewModel.chart.getLastCandleEntry(),
                                candleEntriesIsEmpty = viewModel.chart.isCandleEntryEmpty(),
                                candleUpdateLiveDataValue = it,
                                isUpdateChart = viewModel.chart.state.isUpdateChart,
                                accData = viewModel.chart.accData,
                                candlePosition = viewModel.chart.candlePosition,
                                rootExchange = EXCHANGE_UPBIT
                            )
                        }
                    }
                }
            } catch (e: Exception) {
                Logger.e(e.message.toString())
            }
        }
    }
    // 버튼들과 차트.
    Column(modifier = Modifier.fillMaxSize()) {
        PeriodButtons(
            selectedButton = viewModel.chart.state.selectedButton,
            minuteVisibility = viewModel.chart.state.minuteVisible,
            minuteText = viewModel.chart.state.minuteText,
            candleType = viewModel.chart.state.candleType,
            isChartLastData = viewModel.chart.state.isLastData,
            requestChartData = viewModel::requestChartData,
            rootExchange = EXCHANGE_UPBIT,
            market = market,
            setLastPeriod = viewModel::setLastPeriod
        )
        Box(modifier = Modifier.fillMaxSize()) {
            AndroidView(
                factory = {
                    combinedChart
                },
                modifier = Modifier
                    .fillMaxSize()
                    .background(commonBackground())
            )
            if (viewModel.chart.state.minuteVisible.value) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(35.dp)
                        .align(Alignment.TopCenter)
                        .border(0.5.dp, color = commonTextColor())
                ) {
                    if (GlobalState.globalExchangeState.value == EXCHANGE_UPBIT) {
                        for (i in chartMinuteArray.indices) {
                            MinuteButton(
                                isChartLastData = viewModel.chart.state.isLastData,
                                minuteVisibility = viewModel.chart.state.minuteVisible,
                                minuteText = viewModel.chart.state.minuteText,
                                minuteTextValue = chartMinuteStrArray[i],
                                candleType = viewModel.chart.state.candleType,
                                candleTypeValue = chartMinuteArray[i],
                                autoSizeText = true,
                                selectedButton = viewModel.chart.state.selectedButton,
                                requestChartData = viewModel::requestChartData,
                                market = market,
                                setLastPeriod = viewModel::setLastPeriod
                            )
                        }
                    } else if (GlobalState.globalExchangeState.value == EXCHANGE_BITTHUMB) {
                        for (i in chartMinuteArray.indices) {
                            MinuteButton(
                                isChartLastData = viewModel.chart.state.isLastData,
                                minuteVisibility = viewModel.chart.state.minuteVisible,
                                minuteText = viewModel.chart.state.minuteText,
                                minuteTextValue = bitthumbChartMinuteStrArray[i],
                                candleType = viewModel.chart.state.candleType,
                                candleTypeValue = bitthumbChartMinuteArray[i],
                                autoSizeText = true,
                                selectedButton = viewModel.chart.state.selectedButton,
                                requestChartData = viewModel::requestChartData,
                                market = market,
                                setLastPeriod = viewModel::setLastPeriod
                            )
                        }
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
    requestChartData: KFunction1<String, Unit>,
    rootExchange: String,
    market: String,
    setLastPeriod: (period: String) -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(35.dp)
            .background(commonBackground())
    ) {
        val buttonModifier = remember {
            Modifier
                .weight(1f)
                .fillMaxHeight()
        }
        val btnColor = if (selectedButton.value == MINUTE_SELECT) {
            commonTextColor()
        } else {
            commonHintTextColor()
        }

        TextButton(
            onClick = {
                minuteVisibility.value = !minuteVisibility.value
            }, modifier = if (selectedButton.value == MINUTE_SELECT) buttonModifier
                .border(1.dp, commonTextColor())
                .fillMaxHeight()
            else buttonModifier
        ) {
            Text(
                text = minuteText.value,
                style = TextStyle(color = btnColor, fontSize = DpToSp(dp = 13.dp))
            )
        }

        PeriodButton(
            modifier = buttonModifier,
            selectedButton = selectedButton,
            candleType = candleType,
            candleTypeValue = if (rootExchange == EXCHANGE_UPBIT) "days" else "24h",
            minuteVisibility,
            minuteText,
            buttonText = stringResource(id = R.string.day),
            isChartLastData = isChartLastData,
            period = DAY_SELECT,
            requestChartData = requestChartData,
            market = market,
            setLastPeriod
        )
        if (rootExchange != EXCHANGE_BITTHUMB) {
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
                requestChartData = requestChartData,
                market = market,
                setLastPeriod
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
                requestChartData = requestChartData,
                market = market,
                setLastPeriod
            )
        }
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
    requestChartData: (market: String) -> Unit,
    market: String,
    setLastPeriod: (period: String) -> Unit,
) {
    val buttonColor = if (selectedButton.value == period) {
        commonTextColor()
    } else {
        commonHintTextColor()
    }

    val modifierResult = if (selectedButton.value == period) {
        modifier
            .border(1.dp, commonTextColor())
            .fillMaxHeight()
    } else {
        modifier
    }

    TextButton(onClick = {
        isChartLastData.value = false
        candleType.value = candleTypeValue
        minuteVisibility.value = false
        minuteText.value = "분"
        selectedButton.value = period
        requestChartData(market)
        setLastPeriod(candleTypeValue)
    }, modifier = modifierResult) {
        Text(
            text = buttonText,
            style = TextStyle(color = buttonColor, fontSize = DpToSp(dp = 14.dp))
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
    requestChartData: (market: String) -> Unit,
    market: String,
    setLastPeriod: (period: String) -> Unit,
) {
    TextButton(
        onClick = {
            isChartLastData.value = false
            candleType.value = candleTypeValue
            minuteVisibility.value = false
            minuteText.value = minuteTextValue
            selectedButton.value = MINUTE_SELECT
            requestChartData(market)
            setLastPeriod(candleTypeValue)
        }, modifier = Modifier
            .background(commonBackground())
            .fillMaxHeight()
            .weight(1f)
    ) {
        if (autoSizeText) {
            AutoSizeText(
                text = minuteTextValue,
                textStyle = MaterialTheme.typography.body1.copy(
                    fontSize = DpToSp(14.dp)
                ),
                modifier = Modifier
                    .fillMaxHeight()
                    .wrapContentHeight(),
                color = commonTextColor()
            )
        } else {
            Text(
                text = minuteTextValue,
                style = TextStyle(color = commonTextColor(), fontSize = DpToSp(14.dp)),
            )
        }
    }
}