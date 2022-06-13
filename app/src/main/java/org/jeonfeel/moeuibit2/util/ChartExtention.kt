package org.jeonfeel.moeuibit2.util

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.graphics.Paint
import android.util.Log
import android.view.MotionEvent
import androidx.core.view.get
import com.github.mikephil.charting.charts.CombinedChart
import com.github.mikephil.charting.components.*
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.formatter.ValueFormatter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.jeonfeel.moeuibit2.R
import org.jeonfeel.moeuibit2.ui.coindetail.chart.marker.ChartMarkerView
import org.jeonfeel.moeuibit2.ui.coindetail.chart.ChartCanvas
import org.jeonfeel.moeuibit2.activity.coindetail.viewmodel.CoinDetailViewModel
import kotlin.math.round
import kotlin.math.roundToInt

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

fun BarDataSet.initPositiveBarDataSet() {
    val barDataSet = this
    barDataSet.apply {
        axisDependency = YAxis.AxisDependency.LEFT
        isHighlightEnabled = false
        color = Color.parseColor("#4DFF0000")
        setDrawIcons(false)
        setDrawValues(false)
    }
}

fun BarDataSet.initNegativeBarDataSet() {
    val barDataSet = this
    barDataSet.apply {
        axisDependency = YAxis.AxisDependency.LEFT
        isHighlightEnabled = false
        color = Color.parseColor("#4D0100FF")
        setDrawIcons(false)
        setDrawValues(false)
    }
}

@SuppressLint("ClickableViewAccessibility")
fun CombinedChart.initCombinedChart(context: Context, coinDetailViewModel: CoinDetailViewModel) {
    val chart = this@initCombinedChart
    chart.removeAllViews()

    val canvasView = ChartCanvas(context)
    chart.addView(canvasView)

    var purchaseAveragePrice = -1.0f

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
        isHighlightPerDragEnabled = false
        legend.isEnabled = true
        marker = ChartMarkerView(
            context,
            R.layout.candle_info_marker,
            coinDetailViewModel.kstDateHashMap,
            coinDetailViewModel.accData
        )
        setPinchZoom(false)
        setDrawGridBackground(false)
        setDrawBorders(false)
        fitScreen()
    }
    //legend
    chart.apply {
        val legendEntry1 = LegendEntry()
        legendEntry1.label = "단순 MA"
        val legendEntry2 = LegendEntry().apply {
            label = "5"
            formColor = Color.parseColor("#B3FF36FF")
        }
        val legendEntry3 = LegendEntry().apply {
            label = "10"
            formColor = Color.parseColor("#B30000B7")
        }
        val legendEntry4 = LegendEntry().apply {
            label = "20"
            formColor = Color.parseColor("#B3DBC000")
        }
        val legendEntry5 = LegendEntry().apply {
            label = "60"
            formColor = Color.parseColor("#B3FF4848")
        }
        val legendEntry6 = LegendEntry().apply {
            label = "120"
            formColor = Color.parseColor("#B3BDBDBD")
        }
        val legend = chart.legend
        legend.setCustom(
            arrayOf(
                legendEntry1,
                legendEntry2,
                legendEntry3,
                legendEntry4,
                legendEntry5,
                legendEntry6
            )
        )
        legend.textColor = Color.BLACK
        legend.isWordWrapEnabled = true
        legend.verticalAlignment = Legend.LegendVerticalAlignment.TOP
        legend.horizontalAlignment = Legend.LegendHorizontalAlignment.LEFT
        legend.orientation = Legend.LegendOrientation.HORIZONTAL
        legend.setDrawInside(true)
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
        setLabelCount(3, true)
        setDrawLabels(false)
        setDrawAxisLine(false)
        spaceTop = 400f
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
        spaceBottom = 40f
        CoroutineScope(Dispatchers.Main).launch {
            val myCoin = coinDetailViewModel.getChartCoinPurchaseAverage()
            myCoin?.let {
                val purchaseAverage = it.purchasePrice.toFloat()
                purchaseAveragePrice = purchaseAverage
                val purchaseAverageLimitLine = LimitLine(purchaseAverage, "매수평균")
                purchaseAverageLimitLine.labelPosition = LimitLine.LimitLabelPosition.LEFT_BOTTOM
                purchaseAverageLimitLine.lineColor = Color.parseColor("#2F9D27")
                purchaseAverageLimitLine.textColor = Color.parseColor("#2F9D27")
                addLimitLine(purchaseAverageLimitLine)
                canvasView.purchaseAverageSetPosition(
                    chart.getPosition(
                        Entry(
                            0f,
                            purchaseAverage
                        ), rightAxis.axisDependency
                    ).y
                )
                canvasView.purchaseAverageSetPrice(
                    Calculator.tradePriceCalculatorForChart(
                        purchaseAverage
                    )
                )
                Log.d(
                    "purchaseAveragePrice", chart.getPosition(
                        CandleEntry(
                            0f,
                            0f,
                            0f,
                            purchaseAverage,
                            purchaseAverage
                        ), axisRight.axisDependency
                    ).y.toString()
                )
            }
        }
    }

    chart.setOnTouchListener { _, me ->
        if (coinDetailViewModel.loadingMoreChartData) {
            return@setOnTouchListener true
        }

        if (coinDetailViewModel.minuteVisible) {
            coinDetailViewModel.minuteVisible = false
        }

        val action = me!!.action
        val x = me.x
        val y = me.y - 160f
        if (action == MotionEvent.ACTION_DOWN) {
            val highlight = getHighlightByTouchPoint(x, y)
            val value = chart.getValuesByTouchPoint(
                x,
                y,
                axisRight.axisDependency
            )

            if (purchaseAveragePrice > -1.0f) {
                canvasView.purchaseAverageSetPosition(
                    chart.getPosition(
                        CandleEntry(
                            0f,
                            0f,
                            0f,
                            purchaseAveragePrice,
                            purchaseAveragePrice
                        ), rightAxis.axisDependency
                    ).y
                )
            }

            val verticalLine = LimitLine(value.x.roundToInt().toFloat())
            val horizontalLine = LimitLine(value.y.toFloat())
            val text = Calculator.tradePriceCalculatorForChart(
                value.y
            )

            if (highlight != null) {
                chart.highlightValue(highlight, true)
            }
            if (xAxis.limitLines.size != 0) {
                xAxis.removeAllLimitLines()
                rightAxis.removeLimitLine(rightAxis.limitLines.last())
            }
            horizontalLine.apply {
                lineColor = Color.BLACK
                lineWidth = 0.5f
            }
            verticalLine.apply {
                lineColor = Color.BLACK
                lineWidth = 0.5f
            }

            if (chart.candleData.xMax > highestVisibleX) {
                if (chart.axisLeft.limitLines.isNotEmpty()) {
                    chart.axisLeft.removeAllLimitLines()
                }
                val highestVisibleCandle =
                    chart.data.candleData.dataSets[0].getEntriesForXValue(round(chart.highestVisibleX))
                        .first()

                val tradePrice = highestVisibleCandle.close
                val openPrice = highestVisibleCandle.open
                val color = if (tradePrice - openPrice >= 0.0) {
                    Color.RED
                } else {
                    Color.BLUE
                }
                chart.addAccAmountLimitLine(highestVisibleCandle.x, coinDetailViewModel, color)
                val yp = chart.getPosition(
                    CandleEntry(
                        tradePrice,
                        tradePrice,
                        tradePrice,
                        tradePrice,
                        tradePrice
                    ), rightAxis.axisDependency
                ).y
                canvasView.realTimeLastCandleClose(
                    yp,
                    Calculator.tradePriceCalculatorForChart(tradePrice),
                    color
                )
            }
            xAxis.addLimitLine(verticalLine)
            rightAxis.addLimitLine(horizontalLine)
            canvasView.actionDownInvalidate(y, text)
        } else if (action == MotionEvent.ACTION_MOVE) {
            if (purchaseAveragePrice > -1.0f) {
                canvasView.purchaseAverageSetPosition(
                    chart.getPosition(
                        CandleEntry(
                            0f,
                            0f,
                            0f,
                            purchaseAveragePrice,
                            purchaseAveragePrice
                        ), rightAxis.axisDependency
                    ).y
                )
            }
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
                    lineColor = Color.BLACK
                    lineWidth = 0.5f
                }
                if (chart.candleData.xMax > highestVisibleX) {
                    val highestVisibleCandle =
                        chart.data.candleData.dataSets[0].getEntriesForXValue(round(chart.highestVisibleX))
                            .first()

                    val tradePrice = highestVisibleCandle.close
                    val openPrice = highestVisibleCandle.open
                    val color = if (tradePrice - openPrice >= 0.0) {
                        Color.RED
                    } else {
                        Color.BLUE
                    }
                    chart.addAccAmountLimitLine(highestVisibleCandle.x, coinDetailViewModel, color)
                    val yp = chart.getPosition(
                        CandleEntry(
                            0f,
                            0f,
                            0f,
                            tradePrice,
                            tradePrice
                        ), rightAxis.axisDependency
                    ).y
                    canvasView.realTimeLastCandleClose(
                        yp,
                        Calculator.tradePriceCalculatorForChart(tradePrice),
                        color
                    )
                }
                rightAxis.limitLines[rightAxis.limitLines.lastIndex] = horizontalLine
                canvasView.actionMoveInvalidate(y, text)
            }
        } else if (action == MotionEvent.ACTION_UP && chart.lowestVisibleX <= chart.data.candleData.xMin + 2f && !coinDetailViewModel.loadingMoreChartData && coinDetailViewModel.isUpdateChart) {
            coinDetailViewModel.requestMoreData(chart)
        }
        false
    }
}

fun CombinedChart.chartRefreshSetting(
    candleEntries: ArrayList<CandleEntry>,
    candleDataSet: CandleDataSet,
    positiveBarDataSet: BarDataSet,
    negativeBarDataSet: BarDataSet,
    lineData: LineData,
    valueFormatter: XAxisValueFormatter
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
            chart.data.notifyDataChanged()
            xAxis.valueFormatter = valueFormatter
            chart.zoom(4f, 0f, 0f, 0f)
            chart.moveViewToX(candleEntries.size.toFloat())
        } else {
            xAxis.axisMaximum = chart.candleData.xMax
            xAxis.axisMinimum = chart.candleData.xMin - 0.5f
            chart.fitScreen()
            chart.setVisibleXRangeMinimum(candleEntries.size.toFloat())
            chart.setVisibleXRangeMaximum(candleEntries.size.toFloat())
            chart.data.notifyDataChanged()
            xAxis.valueFormatter = valueFormatter
            chart.invalidate()
        }
    }
}

fun CombinedChart.chartRefreshLoadMoreData(
    candleDataSet: CandleDataSet,
    positiveBarDataSet: BarDataSet,
    negativeBarDataSet: BarDataSet,
    lineData: LineData,
    startPosition: Float,
    currentVisible: Float,
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
    chart.setVisibleXRangeMaximum(190f)
    chart.notifyDataSetChanged()
    chart.moveViewToX(startPosition)
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
    coinDetailViewModel: CoinDetailViewModel,
    color: Int
) {
    val chart = this
    if (chart.axisLeft.limitLines.isNotEmpty()) {
        chart.axisLeft.removeAllLimitLines()
    }
    val lastBar = if (chart.barData.dataSets[0].getEntriesForXValue(lastX).isEmpty()) {
        chart.barData.dataSets[1].getEntriesForXValue(lastX).first()
    } else {
        chart.barData.dataSets[0].getEntriesForXValue(lastX).first()
    }
    val barPrice = lastBar.y
    val lastBarLimitLine = LimitLine(
        barPrice,
        Calculator.accTradePrice24hCalculatorForChart(coinDetailViewModel.accData[lastX.toInt()]!!)
    )
    lastBarLimitLine.lineColor = color
    lastBarLimitLine.textColor = color
    lastBarLimitLine.lineWidth = 0f
    lastBarLimitLine.textSize = 11f
    lastBarLimitLine.labelPosition = LimitLine.LimitLabelPosition.RIGHT_TOP
    chart.axisLeft.addLimitLine(lastBarLimitLine)
}

class XAxisValueFormatter :
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

    fun addItem(newDateString: String, position: Int) {
        this.dateHashMap[position] = newDateString
    }
}