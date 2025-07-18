package org.jeonfeel.moeuibit2.ui.coindetail.new_chart.util

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.github.mikephil.charting.charts.CombinedChart
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.LegendEntry
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.CandleData
import com.github.mikephil.charting.data.CandleEntry
import com.github.mikephil.charting.data.CombinedData
import com.github.mikephil.charting.data.Entry
import org.jeonfeel.moeuibit2.R
import org.jeonfeel.moeuibit2.constants.darkMovingAverageLineColorArray
import org.jeonfeel.moeuibit2.constants.movingAverageLineArray
import org.jeonfeel.moeuibit2.utils.Utils.dpToPx
import kotlin.math.round

class MoeuibitMainChart(
    private val context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : CombinedChart(context, attrs, defStyleAttr) {

    var initialRightViewPort = 0f

    init {
        setupChart()
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setupChart() {
        val customRenderer = MainChartYAxisRenderer(
            context,
            viewPortHandler,
            axisRight,
            getTransformer(YAxis.AxisDependency.RIGHT)
        )

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

        getInitialViewport()
        description.isEnabled = false
        isScaleYEnabled = false
        isDoubleTapToZoomEnabled = false
        isDragDecelerationEnabled = true
        setDragDecelerationFrictionCoef(0.95f)
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
        extraTopOffset = 0f
        extraBottomOffset = 0f
        setViewPortOffsets(0f, 0f, initialRightViewPort, 0f)
        rendererRightYAxis = customRenderer

        xAxis.apply {
            textColor = Color.WHITE
            position = XAxis.XAxisPosition.BOTTOM
            setDrawGridLines(false)
            setAvoidFirstLastClipping(true)
            setLabelCount(3, true)
            setDrawLabels(true)
            setDrawAxisLine(true)
            axisLineColor = Color.GRAY
            granularity = 3f
            isGranularityEnabled = true
            yOffset = 0.4f.dpToPx(context = context)
        }

        axisLeft.apply {
            setDrawGridLines(false)
            setLabelCount(3, true)
            setDrawLabels(false)
            setDrawAxisLine(false)
            axisMinimum = 0f
        }

        axisRight.apply {
            spaceTop = 0f
            spaceBottom = 0f
            xOffset = 0f
            setLabelCount(5, true)
            textColor = ContextCompat.getColor(context, R.color.text_color)
            granularity = 1f
            isGranularityEnabled = true
            setDrawAxisLine(true)
            setDrawGridLines(true)
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

    fun chartAddAll(context: Context, candleEntries: List<CandleEntry>) {
        val candleDataSet = NewChartUtils.createCandleDataSet(context, candleEntries)
        val combinedData = CombinedData().apply {
            setData(CandleData(candleDataSet))
        }
        data = combinedData
        candleData.notifyDataChanged()

        xAxis.setAxisMaximum(candleData.xMax + 20f)
        xAxis.setAxisMinimum(candleData.xMin - 20f)

        invalidate()
    }

    fun getYpositionByTradePrice(price: Float, descent: Float, ascent: Float): Float {
        val chartTopY = this.viewPortHandler.contentTop() - ascent
        val chartBottomY = this.viewPortHandler.contentBottom() - descent
        val yPosition = this.getPosition(Entry(0f, price), axisRight.axisDependency).y
        val centerOffset = (ascent + descent) / 2
        return if (yPosition in chartTopY..chartBottomY) {
            yPosition - centerOffset
        } else if (yPosition < chartTopY) {
            chartTopY
        } else if (yPosition > chartBottomY) {
            chartBottomY
        } else {
            yPosition - centerOffset
        }
    }

    fun getHighestVisibleCandle(): CandleEntry? {
        return try {
            val highestVisibleXPosition = if (highestVisibleX > candleData.xMax) {
                round(candleData.xMax)
            } else {
                round(highestVisibleX)
            }
            data.candleData.dataSets[0].getEntriesForXValue(highestVisibleXPosition).first()
        } catch (e: Exception) {
            null
        }
    }

    fun getInitialViewport() {
        axisRight.minWidth = Paint().apply {
            textSize = axisRight.textSize
        }.measureText("100,000,000")
        initialRightViewPort = axisRight.minWidth
    }

    fun getRightViewport(): Float {
        return axisRight.minWidth
    }

    fun getRightXoffset(): Float {
        return axisRight.xOffset
    }

    fun getRightAxisTextSize(): Float {
        return axisRight.textSize
    }
}