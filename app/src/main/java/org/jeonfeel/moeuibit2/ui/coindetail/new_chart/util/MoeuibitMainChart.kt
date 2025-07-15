package org.jeonfeel.moeuibit2.ui.coindetail.new_chart.util

import android.content.Context
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.MotionEvent
import androidx.core.content.ContextCompat
import com.github.mikephil.charting.charts.Chart
import com.github.mikephil.charting.charts.CombinedChart
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.LegendEntry
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.listener.ChartTouchListener
import com.github.mikephil.charting.listener.OnChartGestureListener
import org.jeonfeel.moeuibit2.R
import org.jeonfeel.moeuibit2.constants.darkMovingAverageLineColorArray
import org.jeonfeel.moeuibit2.constants.movingAverageLineArray

class MoeuibitMainChart(
    private val context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : CombinedChart(context, attrs, defStyleAttr) {

    init {
        setupChart()
    }

    private fun setupChart() {
        val legendArray = ArrayList<LegendEntry>()
        legendArray.add(
            LegendEntry().apply {
                label = "단순 MA"
            }
        )
        for (i in movingAverageLineArray.indices) {
            legendArray.add(
                LegendEntry().apply {
                    label = movingAverageLineArray[i].toString()
                    formColor = Color.parseColor(darkMovingAverageLineColorArray[i])
                }
            )
        }

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
        legend.xOffset = -20f
        legend.horizontalAlignment = Legend.LegendHorizontalAlignment.LEFT
        legend.verticalAlignment = Legend.LegendVerticalAlignment.TOP

        xAxis.apply {
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

        axisLeft.apply {
            setDrawGridLines(false)
            setLabelCount(3, true)
            setDrawLabels(false)
            setDrawAxisLine(false)
            spaceTop = 400f
            axisMinimum = 0f
        }

        axisRight.apply {
            this.minWidth = Paint().apply {
                textSize = this.textSize
            }.measureText("100,000,000")

            setLabelCount(5, true)
            textColor = ContextCompat.getColor(context, R.color.text_color)
            granularity = 1f
            isGranularityEnabled = true
            setDrawAxisLine(true)
            setDrawGridLines(false)
            axisLineColor = Color.GRAY
        }

        legend.apply {
            setCustom(legendArray)
            textColor = ContextCompat.getColor(context, R.color.text_color)
            isWordWrapEnabled = true
            verticalAlignment = Legend.LegendVerticalAlignment.TOP
            horizontalAlignment = Legend.LegendHorizontalAlignment.LEFT
            orientation = Legend.LegendOrientation.HORIZONTAL
            xOffset = -10f
            setDrawInside(true)
            xEntrySpace = 2f
            yEntrySpace = 2f
            formToTextSpace = 2f
            formSize = 8f
        }
    }

    fun syncWith(otherChart: Chart<*>) {
        onChartGestureListener = object : OnChartGestureListener {
            override fun onChartTranslate(me: MotionEvent?, dX: Float, dY: Float) {
                otherChart.viewPortHandler.refresh(viewPortHandler.matrixTouch, otherChart, true)
            }

            override fun onChartScale(me: MotionEvent?, scaleX: Float, scaleY: Float) {
                otherChart.viewPortHandler.refresh(viewPortHandler.matrixTouch, otherChart, true)
            }

            override fun onChartGestureStart(
                me: MotionEvent?,
                lastPerformedGesture: ChartTouchListener.ChartGesture?
            ) {
            }

            override fun onChartGestureEnd(
                me: MotionEvent?,
                lastPerformedGesture: ChartTouchListener.ChartGesture?
            ) {
            }

            override fun onChartLongPressed(me: MotionEvent?) {}
            override fun onChartDoubleTapped(me: MotionEvent?) {}
            override fun onChartSingleTapped(me: MotionEvent?) {}
            override fun onChartFling(
                me1: MotionEvent?,
                me2: MotionEvent?,
                velocityX: Float,
                velocityY: Float
            ) {
            }
        }
    }
}