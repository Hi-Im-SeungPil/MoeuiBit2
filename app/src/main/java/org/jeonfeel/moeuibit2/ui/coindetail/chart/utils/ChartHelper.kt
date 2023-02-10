package org.jeonfeel.moeuibit2.ui.coindetail.chart.utils

import android.annotation.SuppressLint
import android.graphics.Color
import android.graphics.Paint
import android.util.Log
import android.view.MotionEvent
import androidx.compose.runtime.MutableState
import androidx.core.view.get
import com.github.mikephil.charting.charts.CombinedChart
import com.github.mikephil.charting.components.*
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet
import org.jeonfeel.moeuibit2.MoeuiBitDataStore
import org.jeonfeel.moeuibit2.R
import org.jeonfeel.moeuibit2.constants.movingAverageLineArray
import org.jeonfeel.moeuibit2.constants.movingAverageLineColorArray
import org.jeonfeel.moeuibit2.ui.coindetail.chart.NEGATIVE_BAR
import org.jeonfeel.moeuibit2.ui.coindetail.chart.POSITIVE_BAR
import org.jeonfeel.moeuibit2.ui.coindetail.chart.ui.marker.ChartMarkerView
import org.jeonfeel.moeuibit2.ui.coindetail.chart.ui.view.ChartCanvas
import org.jeonfeel.moeuibit2.ui.coindetail.chart.ui.view.MBitCombinedChart
import org.jeonfeel.moeuibit2.ui.theme.decrease_bar_color
import org.jeonfeel.moeuibit2.ui.theme.decrease_candle_color
import org.jeonfeel.moeuibit2.ui.theme.increase_bar_color
import org.jeonfeel.moeuibit2.ui.theme.increase_candle_color
import org.jeonfeel.moeuibit2.utils.XAxisValueFormatter
import org.jeonfeel.moeuibit2.utils.calculator.CurrentCalculator
import kotlin.math.round
import kotlin.math.roundToInt

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
        kstDateHashMap: HashMap<Int, String>,
        isChartLastData: MutableState<Boolean>
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
            requestOldData = requestOldData,
            isChartLastData = isChartLastData
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
    requestOldData: (IBarDataSet, IBarDataSet, Float) -> Unit,
    isChartLastData: MutableState<Boolean>
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
                    if (this.lowestVisibleX <= this.data.candleData.xMin + 2f && !isChartLastData.value) {
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

fun CandleDataSet.initCandleDataSet() {
    val candleDataSet = this
    candleDataSet.apply {
        axisDependency = YAxis.AxisDependency.RIGHT
        shadowColorSameAsCandle = true
        shadowWidth = 1f
        decreasingColor = decrease_candle_color
        decreasingPaintStyle = Paint.Style.FILL
        increasingColor = increase_candle_color
        increasingPaintStyle = Paint.Style.FILL
        neutralColor = Color.DKGRAY
        highLightColor = Color.BLACK
        setDrawHorizontalHighlightIndicator(false)
        setDrawVerticalHighlightIndicator(false)
        isHighlightEnabled = true
        setDrawValues(false)
    }
}

fun BarDataSet.initPositiveBarDataSet() {
    val barDataSet = this
    barDataSet.apply {
        axisDependency = YAxis.AxisDependency.LEFT
        isHighlightEnabled = false
        color = increase_bar_color
        setDrawIcons(false)
        setDrawValues(false)
    }
}

fun BarDataSet.initNegativeBarDataSet() {
    val barDataSet = this
    barDataSet.apply {
        axisDependency = YAxis.AxisDependency.LEFT
        isHighlightEnabled = false
        color = decrease_bar_color
        setDrawIcons(false)
        setDrawValues(false)
    }
}

fun CombinedChart.chartRefreshSettings(
    candleEntries: ArrayList<CandleEntry>,
    candleDataSet: CandleDataSet,
    positiveBarDataSet: BarDataSet,
    negativeBarDataSet: BarDataSet,
    lineData: LineData,
    valueFormatter: XAxisValueFormatter? = null,
    purchaseAveragePrice: Float?,
    marketState: Int
) {
    if (candleDataSet.entryCount > 0 && positiveBarDataSet.entryCount >= 0 && negativeBarDataSet.entryCount >= 0) {
        val chart = this
        val xAxis = chart.xAxis
        candleDataSet.initCandleDataSet()
        positiveBarDataSet.initPositiveBarDataSet()
        negativeBarDataSet.initNegativeBarDataSet()

        val candleData = CandleData(candleDataSet)
        val barData = BarData(listOf(positiveBarDataSet, negativeBarDataSet))
        val combinedData = CombinedData()
        combinedData.setData(candleData)
        combinedData.setData(barData)
        combinedData.setData(lineData)
        chart.data = combinedData

        chart.candleData.notifyDataChanged()
        chart.barData.notifyDataChanged()
        if (candleEntries.size >= 20) {
            xAxis.axisMaximum = chart.candleData.xMax + 3f
            xAxis.axisMinimum = chart.candleData.xMin - 3f
            chart.fitScreen()
            chart.setVisibleXRangeMinimum(20f)
            chart.setVisibleXRangeMaximum(160f)
            chart.data.notifyDataChanged()
            xAxis?.valueFormatter = valueFormatter
            addPurchaseLimitLine(purchaseAveragePrice, marketState)
            chart.zoom(4f, 0f, 0f, 0f)
            chart.moveViewToX(candleEntries.size.toFloat())
        } else {
            xAxis.axisMaximum = chart.candleData.xMax
            xAxis.axisMinimum = chart.candleData.xMin - 0.5f
            chart.fitScreen()
            chart.setVisibleXRangeMinimum(candleEntries.size.toFloat())
            chart.setVisibleXRangeMaximum(candleEntries.size.toFloat())
            chart.data.notifyDataChanged()
            xAxis?.valueFormatter = valueFormatter
            addPurchaseLimitLine(purchaseAveragePrice, marketState)
            chart.invalidate()
        }
    }
}

fun CombinedChart.addPurchaseLimitLine(purchaseAveragePrice: Float?, marketState: Int) {
    val chart = this
    purchaseAveragePrice?.let {
        val purchaseAverageLimitLine = LimitLine(it, "매수평균")
        val purchaseAverageLimitLine2 =
            LimitLine(it, CurrentCalculator.tradePriceCalculator(purchaseAveragePrice, marketState))
        purchaseAverageLimitLine.apply {
            labelPosition = LimitLine.LimitLabelPosition.LEFT_BOTTOM
            textColor = Color.parseColor("#2F9D27")
            lineColor = Color.parseColor("#2F9D27")
        }
        purchaseAverageLimitLine2.apply {
            labelPosition = LimitLine.LimitLabelPosition.LEFT_TOP
            textColor = Color.parseColor("#2F9D27")
            lineColor = Color.parseColor("#2F9D27")
        }
        chart.axisRight.addLimitLine(purchaseAverageLimitLine)
        chart.axisRight.addLimitLine(purchaseAverageLimitLine2)
    }
}

fun CombinedChart.chartRefreshLoadMoreData(
    candleDataSet: CandleDataSet,
    positiveBarDataSet: BarDataSet,
    negativeBarDataSet: BarDataSet,
    lineData: LineData,
    startPosition: Float,
    currentVisible: Float,
    loadingOldData: MutableState<Boolean>
) {
    val chart = this
    candleDataSet.initCandleDataSet()
    positiveBarDataSet.initPositiveBarDataSet()
    negativeBarDataSet.initNegativeBarDataSet()

    val candleData = CandleData(candleDataSet)
    val barData = BarData(listOf(positiveBarDataSet, negativeBarDataSet))
    val combinedData = CombinedData()
    combinedData.setData(candleData)
    combinedData.setData(barData)
    combinedData.setData(lineData)
    chart.data = combinedData
    chart.data.notifyDataChanged()

    chart.xAxis.axisMinimum = (chart.data.candleData.xMin - 3f)
    chart.fitScreen()
    chart.setVisibleXRangeMaximum(currentVisible)
    chart.data.notifyDataChanged()
    chart.setVisibleXRangeMinimum(20f)
    chart.setVisibleXRangeMaximum(160f)
    chart.notifyDataSetChanged()
    chart.moveViewToX(startPosition)
    loadingOldData.value = false
}


fun CombinedChart.initCanvas() {
    val combinedChart = this
    val canvasXPosition =
        combinedChart.measuredWidth - combinedChart.axisRight.getRequiredWidthSpace(
            combinedChart.rendererRightYAxis.paintAxisLabels
        )
    val length = combinedChart.rendererRightYAxis
        .paintAxisLabels
        .measureText(combinedChart.axisRight.longestLabel.plus('0'))
    val textMarginLeft = combinedChart.axisRight.xOffset
    val textSize = combinedChart.rendererRightYAxis.paintAxisLabels.textSize
    (combinedChart[0] as ChartCanvas).canvasInit(textSize, textMarginLeft, length, canvasXPosition)
}

fun CombinedChart.addAccAmountLimitLine(
    lastX: Float,
    color: Int,
    marketState: Int,
    accData: HashMap<Int,Double>
) {
    val chart = this
    if (chart.axisLeft.limitLines.isNotEmpty()) {
        chart.axisLeft.removeAllLimitLines()
    }
    val lastBar = if (chart.barData.dataSets[0].getEntriesForXValue(lastX).isEmpty()) {
        try {
            chart.barData.dataSets[1].getEntriesForXValue(lastX).first()
        } catch (e: Exception) {
            BarEntry(0f, 1f)
        }
    } else {
        try {
            chart.barData.dataSets[0].getEntriesForXValue(lastX).first()
        } catch (e: Exception) {
            BarEntry(0f, 1f)
        }
    }
    val barPrice = lastBar.y
    val lastBarLimitLine = LimitLine(
        barPrice,
        CurrentCalculator.accTradePrice24hCalculator(
            accData[lastX.toInt()]!!,
            marketState
        )
    ).apply {
        lineColor = color
        textColor = color
        lineWidth = 0f
        textSize = 11f
        labelPosition = LimitLine.LimitLabelPosition.RIGHT_TOP
    }
    chart.axisLeft.addLimitLine(lastBarLimitLine)
}