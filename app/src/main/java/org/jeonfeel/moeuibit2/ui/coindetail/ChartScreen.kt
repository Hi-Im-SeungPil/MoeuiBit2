package org.jeonfeel.moeuibit2.ui.coindetail

import android.content.Context
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.Observer
import com.github.mikephil.charting.charts.CombinedChart
import org.jeonfeel.moeuibit2.util.OnLifecycleEvent
import org.jeonfeel.moeuibit2.util.initCombinedChart
import org.jeonfeel.moeuibit2.viewmodel.CoinDetailViewModel

@Composable
fun ChartScreen(coinDetailViewModel: CoinDetailViewModel) {

    val context = LocalContext.current
    val applicationContext = context.applicationContext

    val combinedChart = remember {
        CombinedChart(context)
    }

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
                    if(it == "add") {
                        combinedChart.candleData.notifyDataChanged()
                        combinedChart.xAxis.axisMaximum = combinedChart.xAxis.axisMaximum + 1f
                        combinedChart.data.notifyDataChanged()
                        combinedChart.invalidate()
                    } else {
                        if(combinedChart.candleData != null) {
                            combinedChart.candleData.notifyDataChanged()
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
            Button(onClick = {}, modifier = Modifier.weight(1f)) { Text(text = "분") }
            Button(onClick = {
                coinDetailViewModel.candleType.value = "days"
                coinDetailViewModel.requestChartData(
                    coinDetailViewModel.candleType.value,
                    combinedChart
                )
            }, modifier = Modifier.weight(1f)) { Text(text = "일") }
            Button(onClick = {
                coinDetailViewModel.candleType.value = "weeks"
                coinDetailViewModel.requestChartData(
                    coinDetailViewModel.candleType.value,
                    combinedChart
                )
            }, modifier = Modifier.weight(1f)) { Text(text = "주") }
            Button(onClick = {
                coinDetailViewModel.candleType.value = "months"
                coinDetailViewModel.requestChartData(
                    coinDetailViewModel.candleType.value,
                    combinedChart
                )
            }, modifier = Modifier.weight(1f)) { Text(text = "월") }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(35.dp)
        ) {
            Button(onClick = {
                coinDetailViewModel.candleType.value = "1"
                coinDetailViewModel.requestChartData(
                    coinDetailViewModel.candleType.value,
                    combinedChart
                )
            }, modifier = Modifier.weight(1f)) { Text(text = "1분") }
            Button(onClick = {
                coinDetailViewModel.candleType.value = "3"
                coinDetailViewModel.requestChartData(
                    coinDetailViewModel.candleType.value,
                    combinedChart
                )
            }, modifier = Modifier.weight(1f)) { Text(text = "3분") }
            Button(onClick = {
                coinDetailViewModel.candleType.value = "5"
                coinDetailViewModel.requestChartData(
                    coinDetailViewModel.candleType.value,
                    combinedChart
                )
            }, modifier = Modifier.weight(1f)) { Text(text = "5분") }
            Button(onClick = {
                coinDetailViewModel.candleType.value = "10"
                coinDetailViewModel.requestChartData(
                    coinDetailViewModel.candleType.value,
                    combinedChart
                )
            }, modifier = Modifier.weight(1f)) { Text(text = "10분") }
            Button(onClick = {
                coinDetailViewModel.candleType.value = "15"
                coinDetailViewModel.requestChartData(
                    coinDetailViewModel.candleType.value,
                    combinedChart
                )
            }, modifier = Modifier.weight(1f)) { Text(text = "15분") }
            Button(onClick = {
                coinDetailViewModel.candleType.value = "30"
                coinDetailViewModel.requestChartData(
                    coinDetailViewModel.candleType.value,
                    combinedChart
                )
            }, modifier = Modifier.weight(1f)) { Text(text = "30분") }
            Button(onClick = {
                coinDetailViewModel.candleType.value = "60"
                coinDetailViewModel.requestChartData(
                    coinDetailViewModel.candleType.value,
                    combinedChart
                )
            }, modifier = Modifier.weight(1f)) { Text(text = "60분") }
            Button(onClick = {
                coinDetailViewModel.candleType.value = "240"
                coinDetailViewModel.requestChartData(
                    coinDetailViewModel.candleType.value,
                    combinedChart
                )
            }, modifier = Modifier.weight(1f)) { Text(text = "240분") }
        }

        AndroidView(
            factory = { context ->
                combinedChart
            },
            modifier = Modifier.fillMaxSize()
        )
    }
}

@Composable
fun DrawPractice2(
    context: Context,
    chart: CombinedChart,
    coinDetailViewModel: CoinDetailViewModel
) {
    val drawPractice2 = DrawPractice2(context)
//    val canvasXPosition =
//        chart.measuredWidth - chart.axisRight.getRequiredWidthSpace(
//            chart.rendererRightYAxis.paintAxisLabels
//        )
//    drawPractice2.initCanvas(canvasXPosition)
//    val highestVisibleCandlePosition = chart.highestVisibleX
//    val xMax = chart.data.candleData.xMax
//    if(highestVisibleCandlePosition > xMax) {
//        val text = coinDetailViewModel.currentTradePriceState.toString()
//        drawPractice2.invalidate1(text)
//    } else {
//        val text = coinDetailViewModel.highestVisibleXPrice.value.toString()
//        drawPractice2.invalidate2(text,)
//    }
}