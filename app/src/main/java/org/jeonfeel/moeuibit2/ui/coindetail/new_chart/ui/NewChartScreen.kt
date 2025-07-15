package org.jeonfeel.moeuibit2.ui.coindetail.new_chart.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.charts.CombinedChart
import org.jeonfeel.moeuibit2.ui.coindetail.new_chart.NewChartViewModel
import org.jeonfeel.moeuibit2.ui.coindetail.new_chart.util.MoeuibitMainChart

@Composable
fun ChartScreen(viewModel: NewChartViewModel = hiltViewModel()) {
    Column(modifier = Modifier.fillMaxSize()) {

        AndroidView(
            modifier = Modifier
//                .weight(combinedWeight)
                .fillMaxWidth(),
            factory = { context ->
                MoeuibitMainChart(context).apply {
                    // 초기 설정 및 스타일링
                }
            },
            update = { chart ->
                // 데이터 갱신 또는 제스처 리스너 설정
            }
        )

        Box(
            modifier = Modifier
                .height(8.dp)
                .fillMaxWidth()
                .background(Color.Gray)
                .pointerInput(Unit) {
                    detectVerticalDragGestures { _, dragAmount ->
                        // dragAmount로 weight 조절
                    }
                }
        )

        AndroidView(
            modifier = Modifier
//                .weight(barWeight)
                .fillMaxWidth(),
            factory = { context ->
                BarChart(context).apply {
                    // 초기 설정
                }
            },
            update = { chart ->
                // 데이터 갱신
            }
        )
    }
}