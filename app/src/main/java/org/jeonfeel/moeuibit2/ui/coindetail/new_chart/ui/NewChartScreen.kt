package org.jeonfeel.moeuibit2.ui.coindetail.new_chart.ui

import android.annotation.SuppressLint
import android.view.MotionEvent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.charts.CombinedChart
import com.github.mikephil.charting.components.YAxis
import org.jeonfeel.moeuibit2.ui.coindetail.new_chart.ChartUpdateEvent
import org.jeonfeel.moeuibit2.ui.coindetail.new_chart.NewChartViewModel
import org.jeonfeel.moeuibit2.ui.coindetail.new_chart.util.MoeuiBitVolumeChart
import org.jeonfeel.moeuibit2.ui.coindetail.new_chart.util.MoeuibitMainChart
import org.jeonfeel.moeuibit2.utils.AddLifecycleEvent
import org.jeonfeel.moeuibit2.utils.NetworkConnectivityObserver
import kotlin.math.roundToInt

@SuppressLint("ClickableViewAccessibility")
@Composable
fun NewChartScreen(viewModel: NewChartViewModel = hiltViewModel(), market: String) {
    val context = LocalContext.current
    val moeuibitMainChart = remember {
        MoeuibitMainChart(context)
    }
    val volumeChart = remember {
        MoeuiBitVolumeChart(context)
    }
    AddLifecycleEvent(
        onCreateAction = {
            if (NetworkConnectivityObserver.isNetworkAvailable.value) {
                viewModel.init(market)
            }
        },
        onStartAction = {
            if (NetworkConnectivityObserver.isNetworkAvailable.value) {
//                if (!viewModel.isStarted.value) {
//                    viewModel.onStart(market)
//                }
            }
        },
        onStopAction = {
//            viewModel.onStop()
        }
    )

    LaunchedEffect(Unit) {
        viewModel.chartUpdates.collect { event ->
            when (event) {
                is ChartUpdateEvent.AddAll -> {
                    moeuibitMainChart.chartAddAll(context, event.candles)
                    volumeChart.chartAddAll(context, event.volumes, event.commonEntries)
                }

                is ChartUpdateEvent.UpdateLatest -> {

                }

                is ChartUpdateEvent.AddLatest -> {

                }

                ChartUpdateEvent.Clear -> {
                    // 차트 초기화 등
                }
            }
        }
    }

    val density = LocalDensity.current
    val handleHeightPx = with(density) { 13.dp.toPx() }

    var combinedHeightPx by remember { mutableStateOf(0f) }
    var barHeightPx by remember { mutableStateOf(0f) }
    var totalHeightPx by remember { mutableStateOf(0f) }

    var crosshairEnabled by remember { mutableStateOf(false) }
    var crossX by remember { mutableStateOf(-1f) }
    var crossY by remember { mutableStateOf(-1f) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectTapGestures { offset ->
                    crosshairEnabled = !crosshairEnabled
                    if (crosshairEnabled) {
                        crossX = offset.x
                        crossY = offset.y
                    } else {
                        crossX = -1f
                        crossY = -1f
                    }
                }
            }
    ) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .onGloballyPositioned { layoutCoordinates ->
                    if (totalHeightPx == 0f) {
                        totalHeightPx = layoutCoordinates.size.height.toFloat()
//                    val remaining = totalHeightPx - handleHeightPx
                        combinedHeightPx = (totalHeightPx * 0.75f) - handleHeightPx
                        barHeightPx = (totalHeightPx - combinedHeightPx) - handleHeightPx
                    }
                }
        ) {
            AndroidView(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(with(density) { combinedHeightPx.toDp() })
                    .border(
                        BorderStroke(1.dp, Color.DarkGray),
                        shape = object : Shape {
                            override fun createOutline(
                                size: androidx.compose.ui.geometry.Size,
                                layoutDirection: androidx.compose.ui.unit.LayoutDirection,
                                density: Density
                            ): Outline {
                                val path = Path().apply {
                                    moveTo(0f, 0f)
                                    lineTo(size.width, 0f)
                                    lineTo(size.width, 1f)
                                    lineTo(0f, 1f)
                                    close()
                                }
                                return Outline.Generic(path)
                            }
                        }
                    ),
                factory = { context ->
                    moeuibitMainChart.apply {
                        setOnTouchListener { _, event ->
                            volumeChart.onTouchEvent(MotionEvent.obtain(event)) // 이벤트 복제 전달
                            false // chartTop의 기본 동작도 계속하게 하려면 false
                        }
                    }
                }
            )

            Box(
                modifier = Modifier
                    .height(13.dp)
                    .fillMaxWidth()
                    .background(Color.Gray)
                    .pointerInput(Unit) {
                        detectVerticalDragGestures { _, dragAmount ->
                            val newCombined = (combinedHeightPx + dragAmount) // 손가락 아래로 = 커짐
                                .coerceIn(100f, totalHeightPx - handleHeightPx - 100f)
                            combinedHeightPx = newCombined
                            barHeightPx = totalHeightPx - handleHeightPx - combinedHeightPx
                        }
                    }
            )

            AndroidView(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(with(density) { barHeightPx.toDp() }),
                factory = { context ->
                    volumeChart.apply {
                        setOnTouchListener { _, event ->
                            moeuibitMainChart.onTouchEvent(MotionEvent.obtain(event)) // 이벤트 복제 전달
                            false // chartTop의 기본 동작도 계속하게 하려면 false
                        }
                    }
                }
            )
        }

        CrosshairOverlay(
            modifier = Modifier.fillMaxSize(),
            combinedHeightPx = combinedHeightPx,
            moeuibitMainChart = moeuibitMainChart,
            volumeChart = volumeChart,
            crossX = crossX,
            crossY = crossY,
            crosshairEnabled = crosshairEnabled
        )
    }
}

@Composable
fun BoxScope.CrosshairOverlay(
    modifier: Modifier,
    combinedHeightPx: Float,
    moeuibitMainChart: CombinedChart,
    volumeChart: BarChart,
    crossX: Float,
    crossY: Float,
    crosshairEnabled: Boolean
) {
    if (crosshairEnabled && crossX >= 0f && crossY >= 0f) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawLine(
                color = Color.Gray,
                start = Offset(crossX, 0f),
                end = Offset(crossX, size.height),
                strokeWidth = 1.5f
            )
            drawLine(
                color = Color.Gray,
                start = Offset(0f, crossY),
                end = Offset(size.width, crossY),
                strokeWidth = 1.5f
            )
        }

        val labelOffsetX = with(LocalDensity.current) { 12.dp.toPx() }
        val labelOffsetY = with(LocalDensity.current) { 12.dp.toPx() }

        val value = remember(crossX, crossY) {
            val targetChart =
                if (crossY <= combinedHeightPx) moeuibitMainChart else volumeChart
            val pt = targetChart
                .getTransformer(YAxis.AxisDependency.RIGHT)
                .getValuesByTouchPoint(crossX, crossY)
            pt.y
        }

        Text(
            text = if (crossY <= combinedHeightPx) "₩${value.roundToInt()}" else "Vol: ${value.roundToInt()}",
            color = Color.White,
            fontSize = 12.sp,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .offset(
                    x = with(LocalDensity.current) { (crossX - labelOffsetX).toDp() },
                    y = with(LocalDensity.current) { (crossY - labelOffsetY).toDp() }
                )
                .background(Color.Black, shape = RoundedCornerShape(4.dp))
                .padding(4.dp)
        )
    }
}