package org.jeonfeel.moeuibit2.util

import android.annotation.SuppressLint
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
import org.jeonfeel.moeuibit2.data.remote.retrofit.model.ChartModel

class ChartSetting {

    companion object {
        fun CandleDataSet.initCandleDataSet() {
            this.axisDependency = YAxis.AxisDependency.RIGHT
            this.shadowColorSameAsCandle = true
            this.shadowWidth = 1f
            this.decreasingColor = Color.BLUE
            this.decreasingPaintStyle = Paint.Style.FILL
            this.increasingColor = Color.RED
            this.increasingPaintStyle = Paint.Style.FILL
            this.neutralColor = Color.GRAY
            this.highLightColor = Color.BLACK
            this.setDrawHorizontalHighlightIndicator(false)
            this.setDrawVerticalHighlightIndicator(false)
            this.isHighlightEnabled = true
            this.setDrawValues(false)
        }

        @SuppressLint("ClickableViewAccessibility")
        fun CombinedChart.initCombinedChart() {
            this.description.isEnabled = false
            this.isScaleYEnabled = false
            this.isDoubleTapToZoomEnabled = false
            this.isDragDecelerationEnabled = false
            this.isDragEnabled = true
            this.isAutoScaleMinMaxEnabled = true
            this.setPinchZoom(false)
            this.setDrawGridBackground(false)
            this.setDrawBorders(false)
            this.fitScreen()
            this.isDragYEnabled = false
            this.isHighlightPerTapEnabled = false
            this.setOnTouchListener { _, me ->
                if (me!!.action == MotionEvent.ACTION_DOWN) {
                    val highlight = getHighlightByTouchPoint(me.x, me.y)
                    if (highlight != null) {
                        this.highlightValue(highlight, true)
                    }

                    if (this@initCombinedChart.xAxis.limitLines.size != 0) {
                        this@initCombinedChart.xAxis.removeAllLimitLines()
                        this@initCombinedChart.axisRight.removeAllLimitLines()
                    }
                    val value = this@initCombinedChart.getValuesByTouchPoint(me.x,
                        me.y,
                        this@initCombinedChart.axisRight.axisDependency)

                    val verticalLine = LimitLine(value.x.toInt().toFloat())
                    verticalLine.apply {
                        lineColor = Color.BLACK;
                        lineWidth = 0.5f
                    }

                    val horizontalLine = LimitLine(value.y.toFloat())
                    horizontalLine.apply {
                        lineColor = Color.BLACK;
                        lineWidth = 0.5f
                    }
                    this@initCombinedChart.xAxis.addLimitLine(verticalLine)
                    this@initCombinedChart.axisRight.addLimitLine(horizontalLine)
                } else if (me.action == MotionEvent.ACTION_MOVE) {
                    if (this@initCombinedChart.xAxis.limitLines.size != 0) {
                        val value = this@initCombinedChart.getValuesByTouchPoint(me.x,
                            me.y,
                            this@initCombinedChart.axisRight.axisDependency)
                        val horizontalLine = LimitLine(value.y.toFloat())
                        horizontalLine.apply {
                            lineColor = Color.BLACK;
                            lineWidth = 0.5f
                        }
                        this@initCombinedChart.axisRight.limitLines[0] = horizontalLine
                    }
                }
                false
            }
            //bottom Axis
            val xAxis = this.xAxis
            xAxis.textColor = Color.parseColor("#000000")
            xAxis.position = XAxis.XAxisPosition.BOTTOM
            xAxis.setDrawGridLines(false)
            xAxis.setAvoidFirstLastClipping(true)
            xAxis.setLabelCount(3, true)
            xAxis.setDrawLabels(true)
            xAxis.setDrawAxisLine(false)
            xAxis.axisLineColor = Color.GRAY
            xAxis.granularity = 3f
            xAxis.isGranularityEnabled = true
            //left Axis
            val leftAxis = this.axisLeft
            leftAxis.setDrawGridLines(false)
            leftAxis.setDrawLabels(false)
            leftAxis.setDrawAxisLine(false)
            leftAxis.axisMinimum = 0f
            //right Axis
            val rightAxis = this.axisRight
            rightAxis.setLabelCount(5, true)
            rightAxis.textColor = Color.BLACK
            rightAxis.setDrawAxisLine(true)
            rightAxis.setDrawGridLines(false)
            rightAxis.axisLineColor = Color.GRAY
            rightAxis.minWidth = 50f
        }

        fun CombinedChart.chartRefreshSetting(chartData: ArrayList<ChartModel>,candleEntries: ArrayList<CandleEntry>) {
            this.zoom(4f,0f,0f,0f)
            this.moveViewToX(candleEntries.size.toFloat())
            if(candleEntries.size >= 20) {
                this.setVisibleXRangeMinimum(20f)
            } else {
                this.setVisibleXRangeMinimum(candleEntries.size.toFloat())
            }
            this.xAxis.axisMaximum = this.candleData.xMax + 3f
            this.xAxis.axisMinimum = this.candleData.xMin - 3f
            if(chartData.size != 0){
                var a: MyValueFormatter? = MyValueFormatter(chartData)
                this.xAxis.valueFormatter = a
                a = null
            }
        }
    }
}

class MyValueFormatter(var mValue: ArrayList<ChartModel>) :
    ValueFormatter() {
    var size: Int = mValue.size
    override fun getFormattedValue(value: Float): String {
        if (value.toInt() < 0 || value.toInt() >= size) {
            return ""
        } else if (value.toInt()<size) {
            val fullyDate = mValue[value.toInt()].candleDateTimeKst.split("T").toTypedArray()
            val date = fullyDate[0].split("-").toTypedArray()
            val time = fullyDate[1].split(":").toTypedArray()
            return date[1] + "-" + date[2] + " " + time[0] + ":" + time[1]
        }
        return ""
    }
}