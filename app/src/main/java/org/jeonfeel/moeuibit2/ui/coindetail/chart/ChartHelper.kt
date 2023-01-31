package org.jeonfeel.moeuibit2.ui.coindetail.chart

import android.annotation.SuppressLint
import android.graphics.Color
import android.view.MotionEvent
import com.github.mikephil.charting.charts.CombinedChart
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.LegendEntry
import com.github.mikephil.charting.components.LimitLine
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import org.jeonfeel.moeuibit2.MoeuiBitDataStore
import org.jeonfeel.moeuibit2.ui.theme.decrease_candle_color
import org.jeonfeel.moeuibit2.ui.theme.increase_candle_color
import org.jeonfeel.moeuibit2.utils.addAccAmountLimitLine
import org.jeonfeel.moeuibit2.utils.calculator.CurrentCalculator
import kotlin.math.round
import kotlin.math.roundToInt

class ChartHelper {
    fun defaultChartSettings(combinedChart: CombinedChart) {
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
//            this.minWidth = if (marketState == SELECTED_BTC_MARKET) {
//                canvasView.getTextPaint().measureText("0.00000000")
//            } else {
//                50f
//            }
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
    }

    @SuppressLint("ClickableViewAccessibility")
    fun setOnTouchListener(combinedChart: CombinedChart) {

        val rightAxis = combinedChart.axisRight
        val xAxis = combinedChart.xAxis

        combinedChart.setOnTouchListener { _, me ->
            if (coinDetailViewModel.loadingMoreChartData) {
                return@setOnTouchListener true
            }
            if (coinDetailViewModel.minuteVisible) {
                coinDetailViewModel.minuteVisible = false
            }
            val action = me?.action
            val x = me.x
            val y = me.y - 160f

            if (action == MotionEvent.ACTION_DOWN) {
                val highlight = combinedChart.getHighlightByTouchPoint(x, y)
                val valueByTouchPoint = combinedChart.getValuesByTouchPoint(
                    x,
                    y,
                    rightAxis.axisDependency
                )
                val verticalLine = if (highlight != null) {
                    combinedChart.highlightValue(highlight, true)
                    LimitLine(highlight.x)
                } else {
                    try {
                        LimitLine(valueByTouchPoint.x.roundToInt().toFloat())
                    } catch (e: Exception) {
                        e.printStackTrace()
                        LimitLine(0f)
                    }
                }

                val horizontalLine = LimitLine(valueByTouchPoint.y.toFloat())
                val text = CurrentCalculator.tradePriceCalculator(valueByTouchPoint.y, marketState)
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

                if (combinedChart.candleData.xMax > combinedChart.highestVisibleX) {
                    if (combinedChart.axisLeft.limitLines.isNotEmpty()) {
                        combinedChart.axisLeft.removeAllLimitLines()
                    }
                    val highestVisibleCandle =
                        combinedChart.data.candleData.dataSets[0].getEntriesForXValue(round(combinedChart.highestVisibleX))
                            .first()

                    val tradePrice = highestVisibleCandle.close
                    val openPrice = highestVisibleCandle.open
                    val color = if (tradePrice - openPrice >= 0.0) {
                        increase_candle_color
                    } else {
                        decrease_candle_color
                    }
                    combinedChart.addAccAmountLimitLine(
                        highestVisibleCandle.x,
                        coinDetailViewModel,
                        color,
                        marketState
                    )
                    val yp = combinedChart.getPosition(
                        Entry(
                            0f,
                            tradePrice
                        ), rightAxis.axisDependency
                    ).y
                    canvasView.realTimeLastCandleClose(
                        yp,
                        CurrentCalculator.tradePriceCalculator(tradePrice, marketState),
                        color
                    )
                }
                canvasView.actionDownInvalidate(y, text)
                xAxis.addLimitLine(verticalLine)
                rightAxis.addLimitLine(horizontalLine)
            } else if (action == MotionEvent.ACTION_MOVE) {
                if (xAxis.limitLines.size != 0) {
                    val valueByTouchPoint = combinedChart.getValuesByTouchPoint(
                        x,
                        y,
                        rightAxis.axisDependency
                    )
                    val horizontalLine = LimitLine(valueByTouchPoint.y.toFloat())
                    val price = CurrentCalculator.tradePriceCalculator(
                        combinedChart.getValuesByTouchPoint(
                            x,
                            y,
                            rightAxis.axisDependency
                        ).y, marketState
                    )
                    horizontalLine.apply {
                        lineColor = Color.BLACK
                        lineWidth = 0.5f
                    }
                    if (combinedChart.candleData.xMax > combinedChart.highestVisibleX) {
                        val highestVisibleCandle =
                            combinedChart.data.candleData.dataSets[0].getEntriesForXValue(round(combinedChart.highestVisibleX))
                                .first()

                        val tradePrice = highestVisibleCandle.close
                        val openPrice = highestVisibleCandle.open
                        val color = if (tradePrice - openPrice >= 0.0) {
                            increase_candle_color
                        } else {
                            decrease_candle_color
                        }
                        combinedChart.addAccAmountLimitLine(
                            highestVisibleCandle.x,
                            coinDetailViewModel,
                            color, marketState
                        )
                        val yp = combinedChart.getPosition(
                            Entry(
                                0f,
                                tradePrice
                            ), rightAxis.axisDependency
                        ).y
                        canvasView.realTimeLastCandleClose(
                            yp,
                            CurrentCalculator.tradePriceCalculator(tradePrice, marketState),
                            color
                        )
                    }
                    canvasView.actionMoveInvalidate(y, price)
                    rightAxis.limitLines[rightAxis.limitLines.lastIndex] = horizontalLine
                }
            } else if (action == MotionEvent.ACTION_UP && chart.lowestVisibleX <= chart.data.candleData.xMin + 2f && !coinDetailViewModel.loadingMoreChartData && coinDetailViewModel.isUpdateChart) {
                coinDetailViewModel.requestMoreData(chart)
            }
            false
        }
    }
}