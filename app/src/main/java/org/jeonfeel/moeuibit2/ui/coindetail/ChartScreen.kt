package org.jeonfeel.moeuibit2.ui.coindetail

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
import com.github.mikephil.charting.charts.CombinedChart
import org.jeonfeel.moeuibit2.util.OnLifecycleEvent
import org.jeonfeel.moeuibit2.util.initCombinedChart
import org.jeonfeel.moeuibit2.viewmodel.CoinDetailViewModel

@Composable
fun ChartScreen(coinDetailViewModel: CoinDetailViewModel) {

    val context = LocalContext.current

    val combinedChart = remember {
        CombinedChart(context)
    }

    OnLifecycleEvent { _, event ->
        when (event) {
            Lifecycle.Event.ON_STOP -> {
                coinDetailViewModel.candlePosition = 0f
                combinedChart.xAxis.valueFormatter = null
            }
            Lifecycle.Event.ON_START -> {
                combinedChart.initCombinedChart(coinDetailViewModel)
                coinDetailViewModel.requestChartData("1",combinedChart)
            }
            else -> {}
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {

        Row(modifier = Modifier
            .fillMaxWidth()
            .height(35.dp)) {
            Button(onClick = {}, modifier = Modifier.weight(1f)) { Text(text = "분") }
            Button(onClick = {}, modifier = Modifier.weight(1f)) { Text(text = "일") }
            Button(onClick = {}, modifier = Modifier.weight(1f)) { Text(text = "주") }
            Button(onClick = {}, modifier = Modifier.weight(1f)) { Text(text = "월") }
        }

        Row(modifier = Modifier
            .fillMaxWidth()
            .height(35.dp)) {
            Button(onClick = {}, modifier = Modifier.weight(1f)) { Text(text = "1분") }
            Button(onClick = {}, modifier = Modifier.weight(1f)) { Text(text = "3분") }
            Button(onClick = {}, modifier = Modifier.weight(1f)) { Text(text = "5분") }
            Button(onClick = {}, modifier = Modifier.weight(1f)) { Text(text = "10분") }
            Button(onClick = {}, modifier = Modifier.weight(1f)) { Text(text = "15분") }
            Button(onClick = {}, modifier = Modifier.weight(1f)) { Text(text = "30분") }
            Button(onClick = {}, modifier = Modifier.weight(1f)) { Text(text = "60분") }
            Button(onClick = {}, modifier = Modifier.weight(1f)) { Text(text = "240분") }
        }

        AndroidView(factory = { context ->
            combinedChart
        },
            modifier = Modifier.fillMaxSize()
        )
    }
}
