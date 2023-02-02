package org.jeonfeel.moeuibit2.ui.coindetail.chart

import android.annotation.SuppressLint
import android.graphics.Color
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
import org.jeonfeel.moeuibit2.constants.SELECTED_BTC_MARKET
import org.jeonfeel.moeuibit2.ui.theme.decrease_candle_color
import org.jeonfeel.moeuibit2.ui.theme.increase_candle_color
import org.jeonfeel.moeuibit2.utils.calculator.CurrentCalculator
import kotlin.math.round
import kotlin.math.roundToInt

class ChartHelper {
    fun defaultChartSettings(
        combinedChart: MBitCombinedChart,
        chartCanvas: ChartCanvas,
        marketState: Int,
        requestOldData: suspend (String, String, Float, Float, IBarDataSet, IBarDataSet, Float) -> Unit
    ) {
        combinedChart.apply {
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
            setLabelCount(5, true)
            textColor = Color.BLACK
            setDrawAxisLine(true)
            setDrawGridLines(false)
            axisLineColor = Color.GRAY
            this.minWidth = if (marketState == SELECTED_BTC_MARKET) {
                chartCanvas.getTextPaint().measureText("0.00000000")
            } else {
                50f
            }
            spaceBottom = 40f
        }

        combinedChart.apply {
            val legendEntry1 = LegendEntry()
            legendEntry1.label = if (MoeuiBitDataStore.isKor) "단순 MA" else "MA"
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
            val legend = combinedChart.legend
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

        chartCanvas.canvasInit(
            textSize = combinedChart.rendererRightYAxis.paintAxisLabels.textSize,
            textMarginLeft = combinedChart.axisRight.xOffset,
            width = combinedChart.rendererRightYAxis
                .paintAxisLabels
                .measureText(combinedChart.axisRight.longestLabel.plus('0')),
            x = combinedChart.measuredWidth - combinedChart.axisRight.getRequiredWidthSpace(
                combinedChart.rendererRightYAxis.paintAxisLabels
            )
        )
        combinedChart.addView(chartCanvas)
    }
}

@SuppressLint("ClickableViewAccessibility")
fun MBitCombinedChart.setMBitChartTouchListener(
    isLoadingMoreData: MutableState<Boolean>,
    minuteVisibility: MutableState<Boolean>,
    isUpdateChart: MutableState<Boolean>,
    marketState: Int
) {
    val rightAxis = this.axisRight
    val xAxis = this.xAxis
    val leftAxis = this.axisLeft
    val chartCanvas = this.getChartCanvas()

    this.setOnTouchListener { _, me ->
        if (isLoadingMoreData.value) return@setOnTouchListener true
        if (minuteVisibility.value) minuteVisibility.value = false
        me?.let {
            val action = me.action
            val x = me.x
            val y = me.y - 160f
            val highlight = this.getHighlightByTouchPoint(x, y)
            val valueByTouchPoint = this.getValuesByTouchPoint(
                x,
                y,
                rightAxis.axisDependency
            )
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
                    xAxis.removeAllLimitLines()
                    rightAxis.removeLimitLine(rightAxis.limitLines.last())
                    highestVisibleCandle?.let {
                        val tradePrice = highestVisibleCandle.close
                        val openPrice = highestVisibleCandle.open
                        val color = if (tradePrice - openPrice >= 0.0) increase_candle_color
                        else decrease_candle_color
                        val yp = this.getPosition(
                            Entry(0f, tradePrice), rightAxis.axisDependency
                        ).y
                        leftAxis.removeAllLimitLines()
//                        this.addAccAmountLimitLine(
//                            highestVisibleCandle.x,
//                            coinDetailViewModel,
//                            color,
//                            marketState
//                        )
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
//                        this.addAccAmountLimitLine(
//                            highestVisibleCandle.x,
//                            coinDetailViewModel,
//                            color, marketState
//                        )

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
                    if (this.lowestVisibleX <= this.data.candleData.xMin + 2f && !isLoadingMoreData.value && !isUpdateChart.value) {
                        requestOldData(
                            market = "",
                            startPosition = this.lowestVisibleX,
                            currentVisible = this.visibleXRange,
                            positiveBarDataSet = this.barData.dataSets[POSITIVE_BAR],
                            negativeBarDataSet = this.barData.dataSets[NEGATIVE_BAR],
                            candleXMin = this.data.candleData.xMin
                        )
                    }
                }
            }
        }
        false
    }
}