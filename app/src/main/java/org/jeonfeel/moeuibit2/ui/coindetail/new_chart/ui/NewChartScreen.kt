package org.jeonfeel.moeuibit2.ui.coindetail.new_chart.ui

import android.annotation.SuppressLint
import android.graphics.Paint
import android.graphics.Typeface
import android.view.MotionEvent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.gestures.waitForUpOrCancellation
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
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
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
import com.github.mikephil.charting.data.CandleEntry
import com.tradingview.lightweightcharts.Logger
import org.jeonfeel.moeuibit2.ui.coindetail.chart.utils.setMBitChartTouchListener
import org.jeonfeel.moeuibit2.ui.coindetail.new_chart.ChartUpdateEvent
import org.jeonfeel.moeuibit2.ui.coindetail.new_chart.NewChartViewModel
import org.jeonfeel.moeuibit2.ui.coindetail.new_chart.util.MoeuiBitVolumeChart
import org.jeonfeel.moeuibit2.ui.coindetail.new_chart.util.MoeuibitMainChart
import org.jeonfeel.moeuibit2.utils.AddLifecycleEvent
import org.jeonfeel.moeuibit2.utils.NetworkConnectivityObserver
import org.jeonfeel.moeuibit2.utils.Utils.dpToPx
import kotlin.math.round
import kotlin.math.roundToInt
import kotlin.math.sqrt

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

    var downTime by remember { mutableStateOf(0L) }
    var downX by remember { mutableStateOf(0f) }
    var downY by remember { mutableStateOf(0f) }

    var crosshairEnabled by remember { mutableStateOf(false) }
    var crossX by remember { mutableStateOf(-1f) }
    var crossY by remember { mutableStateOf(-1f) }


    Box(modifier = Modifier) {
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
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
//                    .height(with(density) { combinedHeightPx.toDp() })
            ) {
                ChartWithRightPriceLabel(moeuibitMainChart)
                AndroidView(
                    modifier = Modifier
                        .fillMaxSize()
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
                                volumeChart.onTouchEvent(MotionEvent.obtain(event))

                                when (event.action) {
                                    MotionEvent.ACTION_DOWN -> {
                                        downTime = event.eventTime
                                        downX = event.x
                                        downY = event.y
                                    }

                                    MotionEvent.ACTION_UP -> {
                                        val deltaTime = event.eventTime - downTime
                                        val dx = event.x - downX
                                        val dy = event.y - downY
                                        val distance = sqrt(dx * dx + dy * dy)

                                        if (deltaTime < 200 && distance < 20f) {
                                            crosshairEnabled = true
                                            crossX = event.x - 25f.dpToPx(context)
                                            crossY = event.y - 25f.dpToPx(context)
                                        }
                                    }

                                    else -> {}
                                }

                                false
                            }
                        }
                    }
                )
            }

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
                    .weight(1f),
//                    .height(with(density) { barHeightPx.toDp() }),
                factory = { context ->
                    volumeChart.apply {
                        setOnTouchListener { _, event ->
                            moeuibitMainChart.onTouchEvent(MotionEvent.obtain(event))

                            when (event.action) {
                                MotionEvent.ACTION_DOWN -> {
                                    downTime = event.eventTime
                                    downX = event.x
                                    downY = event.y
                                }

                                MotionEvent.ACTION_UP -> {
                                    val deltaTime = event.eventTime - downTime
                                    val dx = event.x - downX
                                    val dy = event.y - downY
                                    val distance = sqrt(dx * dx + dy * dy)

                                    if (deltaTime < 200 && distance < 20f) {
                                        // click event
                                    }
                                }

                                else -> {}
                            }

                            false
                        }
                    }
                }
            )
        }

        if (crosshairEnabled) {
            CrosshairOverlay(
                modifier = Modifier
                    .fillMaxSize()
                    .pointerInput(Unit) {
                        awaitEachGesture {
                            val down = awaitFirstDown(requireUnconsumed = false)
                            val downTime = down.uptimeMillis
                            val downPosition = down.position

                            // 이전 포지션 저장 (절대 좌표 아님)
                            var lastPosition = down.position

                            var pointer = down
                            while (true) {
                                val event = awaitPointerEvent()
                                val change =
                                    event.changes.firstOrNull { it.id == pointer.id } ?: break

                                if (change.pressed) {
                                    val currentPosition = change.position
                                    val delta = currentPosition - lastPosition

                                    // ✨ 이동한 거리만큼 크로스헤어 이동
                                    crossX += delta.x
                                    crossY += delta.y

                                    lastPosition = currentPosition
                                    change.consume()
                                } else {
                                    val upTime = change.uptimeMillis
                                    val upPosition = change.position

                                    val duration = upTime - downTime
                                    val distance = (upPosition - downPosition).getDistance()

                                    // 클릭 판정: 빠르게 터치하고 거의 안 움직였을 때
                                    if (duration < 200 && distance < 20f) {
                                        crosshairEnabled = false
                                        crossX = -1f
                                        crossY = -1f
                                    }

                                    break
                                }
                            }
                        }
                    },
                combinedHeightPx = combinedHeightPx,
                moeuibitMainChart = moeuibitMainChart,
                volumeChart = volumeChart,
                crosshairEnabled = crosshairEnabled,
                crossX = crossX,
                crossY = crossY
            )
        }
    }
}

@Composable
fun BoxScope.CrosshairOverlay(
    modifier: Modifier,
    combinedHeightPx: Float,
    moeuibitMainChart: CombinedChart,
    volumeChart: BarChart,
    crosshairEnabled: Boolean,
    crossX: Float,
    crossY: Float,
) {
    Box(
        modifier = modifier
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
}

@Composable
fun ChartWithRightPriceLabel(moeuibitMainChart: MoeuibitMainChart) {
    val density = LocalDensity.current
    val paint = remember {
        Paint().apply {
            color = android.graphics.Color.WHITE
            textSize = with(density) { 14.sp.toPx() }
            isAntiAlias = true
            typeface = Typeface.DEFAULT_BOLD
        }
    }

    Canvas(modifier = Modifier.fillMaxSize()) {
        val chartTopY = moeuibitMainChart.viewPortHandler.contentTop()
        val chartBottomY = moeuibitMainChart.viewPortHandler.contentBottom()
        val highestVisibleCandle = moeuibitMainChart.getHighestVisibleCandle()

        highestVisibleCandle?.let { entry ->
            val pixel = moeuibitMainChart.getYpositionByTradePrice(
                price = entry.close,
                decent = paint.fontMetrics.descent,
                accent = paint.fontMetrics.ascent
            )

            Logger.e("chartTopY: $chartTopY, chartBottomY: $chartBottomY highestVisibleCandle: $pixel")
            val label = "₩${entry.close.toInt()}" // 정수 변환 등 포매팅은 여기서

            drawIntoCanvas { canvas ->
                val textX = size.width - with(density) { 48.dp.toPx() }
                canvas.nativeCanvas.drawText(label, textX, pixel, paint)
            }
        }
    }
}