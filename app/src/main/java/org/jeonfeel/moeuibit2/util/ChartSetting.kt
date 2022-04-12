package org.jeonfeel.moeuibit2.util

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.graphics.Paint
import android.view.MotionEvent
import com.github.mikephil.charting.charts.CombinedChart
import com.github.mikephil.charting.components.LimitLine
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.CandleDataSet
import com.github.mikephil.charting.data.CandleEntry
import com.github.mikephil.charting.formatter.ValueFormatter
import org.jeonfeel.moeuibit2.ui.coindetail.DrawPractice
import org.jeonfeel.moeuibit2.viewmodel.CoinDetailViewModel

fun CandleDataSet.initCandleDataSet() {
    val candleDataSet = this
    candleDataSet.apply {
        axisDependency = YAxis.AxisDependency.RIGHT
        shadowColorSameAsCandle = true
        shadowWidth = 1f
        decreasingColor = Color.BLUE
        decreasingPaintStyle = Paint.Style.FILL
        increasingColor = Color.RED
        increasingPaintStyle = Paint.Style.FILL
        neutralColor = Color.DKGRAY
        highLightColor = Color.BLACK
        setDrawHorizontalHighlightIndicator(false)
        setDrawVerticalHighlightIndicator(false)
        isHighlightEnabled = true
        setDrawValues(false)
    }
}

@SuppressLint("ClickableViewAccessibility")
fun CombinedChart.initCombinedChart(context: Context, coinDetailViewModel: CoinDetailViewModel) {
    val chart = this@initCombinedChart
    chart.removeAllViews()
    //chart
    chart.apply {
        description.isEnabled = false
        isScaleYEnabled = false
        isDoubleTapToZoomEnabled = false
        isDragDecelerationEnabled = false
        isDragEnabled = true
        isAutoScaleMinMaxEnabled = true
        isDragYEnabled = false
        isHighlightPerTapEnabled = false
        legend.isEnabled = false
        setPinchZoom(false)
        setDrawGridBackground(false)
        setDrawBorders(false)
        fitScreen()
    }
    //bottom Axis
    val xAxis = chart.xAxis
    xAxis.apply {
        textColor = Color.parseColor("#000000")
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
    //left Axis
    val leftAxis = chart.axisLeft
    leftAxis.apply {
        setDrawGridLines(false)
        setDrawLabels(false)
        setDrawAxisLine(false)
        axisMinimum = 0f
    }
    //right Axis
    val rightAxis = chart.axisRight
    rightAxis.apply {
        setLabelCount(5, true)
        textColor = Color.BLACK
        setDrawAxisLine(true)
        setDrawGridLines(false)
        axisLineColor = Color.GRAY
        minWidth = 50f
    }

    val canvasView = DrawPractice(context)
    canvasView.cInit(chart.rendererRightYAxis.paintAxisLabels.textSize)
    chart.addView(canvasView)
    chart.setOnTouchListener { _, me ->
        val action = me!!.action
        val x = me.x
        val y = me.y
        if (action == MotionEvent.ACTION_DOWN) {
            val highlight = getHighlightByTouchPoint(x, y)
            val value = chart.getValuesByTouchPoint(
                x,
                y,
                axisRight.axisDependency
            )
            val verticalLine = LimitLine(value.x.toInt().toFloat())
            val horizontalLine = LimitLine(value.y.toFloat())
            val canvasXPosition =
                chart.measuredWidth - rightAxis.getRequiredWidthSpace(
                    chart.rendererRightYAxis.paintAxisLabels
                )
            val text = Calculator.tradePriceCalculatorForChart(
                chart.getValuesByTouchPoint(
                    x,
                    y,
                    axisRight.axisDependency
                ).y
            )
            val length = chart.rendererRightYAxis
                .paintAxisLabels
                .measureText(axisRight.longestLabel)
            val textMarginLeft = axisRight.xOffset

            if (coinDetailViewModel.loadingMoreChartData) {
                coinDetailViewModel.loadingMoreChartData = false
            }
            if (highlight != null) {
                chart.highlightValue(highlight, true)
            }
            if (xAxis.limitLines.size != 0) {
                xAxis.removeAllLimitLines()
                rightAxis.removeAllLimitLines()
            }
            horizontalLine.apply {
                lineColor = Color.BLACK;
                lineWidth = 0.5f
            }
            verticalLine.apply {
                lineColor = Color.BLACK;
                lineWidth = 0.5f
            }
            xAxis.addLimitLine(verticalLine)
            rightAxis.addLimitLine(horizontalLine)
            canvasView.actionDownInvalidate(canvasXPosition, y, text, textMarginLeft, length)
        } else if (action == MotionEvent.ACTION_MOVE) {
            if (xAxis.limitLines.size != 0) {
                val value = chart.getValuesByTouchPoint(
                    x,
                    y,
                    rightAxis.axisDependency
                )
                val horizontalLine = LimitLine(value.y.toFloat())
                val text = Calculator.tradePriceCalculatorForChart(
                    chart.getValuesByTouchPoint(
                        x,
                        y,
                        axisRight.axisDependency
                    ).y
                )
                horizontalLine.apply {
                    lineColor = Color.BLACK;
                    lineWidth = 0.5f
                }
                rightAxis.limitLines[0] = horizontalLine
                canvasView.actionMoveInvalidate(y, text)
                val highestVisibleX = chart.highestVisibleX
                val chartXMax = chart.candleData.xMax
                if(highestVisibleX < chartXMax) {
                    coinDetailViewModel.highestVisibleXPrice.value =
                        chart.candleData.getDataSetByIndex(0)
                            .getEntryForXValue(highestVisibleX, 0f).close
                }
            }
        } else if (action == MotionEvent.ACTION_UP && chart.lowestVisibleX <= chart.data.candleData.xMin + 2f && !coinDetailViewModel.loadingMoreChartData) {
            coinDetailViewModel.requestMoreData("1", chart)
        }
        false
    }
}

fun CombinedChart.chartRefreshSetting(
    candleEntries: ArrayList<CandleEntry>,
) {
    this.zoom(4f, 0f, 0f, 0f)
    this.moveViewToX(candleEntries.size.toFloat())
    if (candleEntries.size >= 20) {
        this.setVisibleXRangeMinimum(20f)
    } else {
        this.setVisibleXRangeMinimum(candleEntries.size.toFloat())
    }
    this.xAxis.axisMaximum = this.candleData.xMax + 3f
    this.xAxis.axisMinimum = this.candleData.xMin - 3f
}

class MyValueFormatter :
    ValueFormatter() {
    private var dateHashMap = HashMap<Int, String>()
    override fun getFormattedValue(value: Float): String {
        if (dateHashMap[value.toInt()] == null) {
            return ""
        } else if (dateHashMap[value.toInt()] != null) {
            val fullyDate = dateHashMap[value.toInt()]!!.split("T").toTypedArray()
            val date = fullyDate[0].split("-").toTypedArray()
            val time = fullyDate[1].split(":").toTypedArray()
            return date[1] + "-" + date[2] + " " + time[0] + ":" + time[1]
        }
        return ""
    }

    fun setItem(newDateHashMap: HashMap<Int, String>) {
        this.dateHashMap = newDateHashMap
    }
}