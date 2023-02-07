package org.jeonfeel.moeuibit2.ui.coindetail.chart

import android.annotation.SuppressLint
import android.graphics.Color
import android.util.Log
import android.view.MotionEvent
import androidx.compose.runtime.MutableState
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.LegendEntry
import com.github.mikephil.charting.components.LimitLine
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.CandleEntry
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet
import org.jeonfeel.moeuibit2.MoeuiBitDataStore
import org.jeonfeel.moeuibit2.R
import org.jeonfeel.moeuibit2.constants.SELECTED_BTC_MARKET
import org.jeonfeel.moeuibit2.constants.movingAverageLineArray
import org.jeonfeel.moeuibit2.constants.movingAverageLineColorArray
import org.jeonfeel.moeuibit2.ui.coindetail.chart.marker.ChartMarkerView
import org.jeonfeel.moeuibit2.ui.theme.decrease_candle_color
import org.jeonfeel.moeuibit2.ui.theme.increase_candle_color
import org.jeonfeel.moeuibit2.utils.addAccAmountLimitLine
import org.jeonfeel.moeuibit2.utils.calculator.CurrentCalculator
import kotlin.math.round
import kotlin.math.roundToInt
import kotlin.reflect.KFunction6

class ChartHelper {
    /**
     * 차트 초기 세팅
     */
    fun defaultChartSettings(
        combinedChart: MBitCombinedChart,
        marketState: Int,
        requestOldData: (IBarDataSet, IBarDataSet, Float) -> Unit,
        loadingOldData: MutableState<Boolean>,
        minuteVisibility: MutableState<Boolean>,
        accData: HashMap<Int, Double>,
        kstDateHashMap: HashMap<Int, String>
    ) {
        combinedChart.apply {
            marker = ChartMarkerView(
                context = context,
                layoutResource = R.layout.candle_info_marker,
                dateHashMap = kstDateHashMap,
                chartData = accData,
                marketState = marketState
            )
            description.isEnabled = false
            isScaleYEnabled = false
            isDoubleTapToZoomEnabled = false
            isDragDecelerationEnabled = false
            isDragEnabled = true
            isAutoScaleMinMaxEnabled = true
            isDragYEnabled = false
            isHighlightPerTapEnabled = false
            isHighlightPerDragEnabled = false
            legend.isEnabled = true
            setPinchZoom(false)
            setDrawGridBackground(false)
            setDrawBorders(false)
            fitScreen()
        }

        combinedChart.xAxis.apply {
            textColor = Color.BLACK
            position = XAxis.XAxisPosition.BOTTOM
            setDrawGridLines(false)
            setAvoidFirstLastClipping(true)
            setLabelCount(3, true)
            setDrawLabels(true)
            setDrawAxisLine(false)
            axisLineColor = Color.GRAY
            granularity = 3f
            isGranularityEnabled = true
        }

        combinedChart.axisLeft.apply {
            setDrawGridLines(false)
            setLabelCount(3, true)
            setDrawLabels(false)
            setDrawAxisLine(false)
            spaceTop = 400f
            axisMinimum = 0f
        }

        combinedChart.axisRight.apply {
            this.minWidth = 50f
            setLabelCount(5, true)
            textColor = Color.BLACK
            setDrawAxisLine(true)
            setDrawGridLines(false)
            axisLineColor = Color.GRAY
            spaceBottom = 40f
        }

        val legendArray = ArrayList<LegendEntry>()
        legendArray.add(
            LegendEntry().apply {
                label = if (MoeuiBitDataStore.isKor) "단순 MA" else "MA"
            }
        )
        for (i in movingAverageLineArray.indices) {
            legendArray.add(
                LegendEntry().apply {
                    label = movingAverageLineArray[i].toString()
                    formColor = Color.parseColor(movingAverageLineColorArray[i])
                }
            )
        }

        combinedChart.legend.apply {
            setCustom(legendArray)
            textColor = Color.BLACK
            isWordWrapEnabled = true
            verticalAlignment = Legend.LegendVerticalAlignment.TOP
            horizontalAlignment = Legend.LegendHorizontalAlignment.LEFT
            orientation = Legend.LegendOrientation.HORIZONTAL
            xOffset = 10f
            setDrawInside(true)
        }

        combinedChart.setMBitChartTouchListener(
            loadingOldData = loadingOldData,
            minuteVisibility = minuteVisibility,
            marketState = marketState,
            accData = accData,
            requestOldData = requestOldData
        )
        combinedChart.invalidate()
    }
}

/**
 * 차트 터치 리스터
 */
@SuppressLint("ClickableViewAccessibility")
fun MBitCombinedChart.setMBitChartTouchListener(
    loadingOldData: MutableState<Boolean>,
    minuteVisibility: MutableState<Boolean>,
    marketState: Int,
    accData: HashMap<Int, Double>,
    requestOldData: (IBarDataSet, IBarDataSet, Float) -> Unit
) {
    val rightAxis = this.axisRight
    val xAxis = this.xAxis
    val leftAxis = this.axisLeft
    val chartCanvas = this.getChartCanvas()

    this.setOnTouchListener { _, me ->
        if (loadingOldData.value) {
            Log.e("return!!!!!!","")
            return@setOnTouchListener true
        }
        if (minuteVisibility.value) minuteVisibility.value = false
        me?.let {
            val action = me.action
            val x = me.x
            val y = me.y - 160f
            val valueByTouchPoint = this.getValuesByTouchPoint(
                x,
                y,
                rightAxis.axisDependency
            )
            val horizontalLine = LimitLine(valueByTouchPoint.y.toFloat()).apply {
                lineColor = Color.BLACK
                lineWidth = 0.5f
            }
            val selectedPrice =
                CurrentCalculator.tradePriceCalculator(valueByTouchPoint.y, marketState)
            val highestVisibleCandle: CandleEntry? =
                if (this.candleData.xMax > this.highestVisibleX) {
                    this.data.candleData.dataSets[0].getEntriesForXValue(
                        round(this.highestVisibleX)
                    ).first()
                } else {
                    null
                }

            /**
             * 액션
             */
            when (action) {
                MotionEvent.ACTION_DOWN -> {
                    val highlight = this.getHighlightByTouchPoint(x, y)
                    val verticalLine = try {
                        if (highlight != null) {
                            this.highlightValue(highlight, true)
                            LimitLine(highlight.x)
                        } else {
                            LimitLine(valueByTouchPoint.x.roundToInt().toFloat())
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                        LimitLine(0f)
                    }.apply {
                        lineColor = Color.BLACK
                        lineWidth = 0.5f
                    }
                    xAxis.removeAllLimitLines()
                    if (rightAxis.limitLines.isNotEmpty()) {
                        rightAxis.removeLimitLine(rightAxis.limitLines.last())
                    }
                    highestVisibleCandle?.let {
                        val tradePrice = highestVisibleCandle.close
                        val openPrice = highestVisibleCandle.open
                        val color = if (tradePrice - openPrice >= 0.0) increase_candle_color
                        else decrease_candle_color
                        val yp = this.getPosition(
                            Entry(0f, tradePrice), rightAxis.axisDependency
                        ).y
                        leftAxis.removeAllLimitLines()
                        this.addAccAmountLimitLine(
                            lastX = highestVisibleCandle.x,
                            color = color,
                            marketState = marketState,
                            accData = accData
                        )
                        chartCanvas?.realTimeLastCandleClose(
                            yp,
                            CurrentCalculator.tradePriceCalculator(tradePrice, marketState),
                            color
                        )
                    }
                    chartCanvas?.actionDownInvalidate(y, selectedPrice)
                    xAxis.addLimitLine(verticalLine)
                    rightAxis.addLimitLine(horizontalLine)
                }

                MotionEvent.ACTION_MOVE -> {
                    highestVisibleCandle?.let {
                        val tradePrice = highestVisibleCandle.close
                        val openPrice = highestVisibleCandle.open
                        val color = if (tradePrice - openPrice >= 0.0) increase_candle_color
                        else decrease_candle_color
                        val yp = this.getPosition(
                            Entry(0f, tradePrice), rightAxis.axisDependency
                        ).y
                        this.addAccAmountLimitLine(
                            lastX = highestVisibleCandle.x,
                            color = color,
                            marketState = marketState,
                            accData = accData
                        )

                        chartCanvas?.realTimeLastCandleClose(
                            yp,
                            CurrentCalculator.tradePriceCalculator(tradePrice, marketState),
                            color
                        )
                    }
                    chartCanvas?.actionMoveInvalidate(y, selectedPrice)
                    rightAxis.limitLines[rightAxis.limitLines.lastIndex] = horizontalLine
                }

                MotionEvent.ACTION_UP -> {
                    if (this.lowestVisibleX <= this.data.candleData.xMin + 2f) {
                        loadingOldData.value = true
                        requestOldData(
                            this.barData.dataSets[POSITIVE_BAR],
                            this.barData.dataSets[NEGATIVE_BAR],
                            this.data.candleData.xMin
                        )
                    }
                }
            }
        }
        false
    }
}