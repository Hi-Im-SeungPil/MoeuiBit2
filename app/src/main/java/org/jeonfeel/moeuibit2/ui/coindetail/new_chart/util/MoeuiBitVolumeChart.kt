package org.jeonfeel.moeuibit2.ui.coindetail.new_chart.util

import android.content.Context
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import androidx.core.content.ContextCompat
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarEntry
import org.jeonfeel.moeuibit2.R
import org.jeonfeel.moeuibit2.ui.coindetail.chart.utils.bithumb.CommonChartModel

class MoeuiBitVolumeChart(
    private val context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : BarChart(context, attrs, defStyleAttr) {

    init {
        setUpChart()
    }

    fun setUpChart() {
        axisRight.minWidth = Paint().apply {
            textSize = this.textSize
        }.measureText("100,000,000")

        val paint = Paint().apply {
            textSize = axisRight.textSize
            typeface = axisRight.typeface
        }
        val measured = paint.measureText("100,000,000")
        val extraMargin = 12f

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
        setViewPortOffsets(0f, 0f, measured + extraMargin, 0f)
        legend.isEnabled = false

        xAxis.apply {
            textColor = Color.BLACK
            position = XAxis.XAxisPosition.BOTTOM
            setDrawGridLines(false)
            setAvoidFirstLastClipping(true)
            setDrawLabels(true)
            setDrawAxisLine(false)
            axisLineColor = Color.GRAY
            granularity = 3f
            isGranularityEnabled = true
        }

        axisLeft.apply {
            setDrawGridLines(true)
            setLabelCount(3, true)
            setDrawLabels(false)
            setDrawAxisLine(false)
            axisMinimum = 0f
        }

        axisRight.apply {
            spaceTop = 0f
            spaceBottom = 0f
            this.minWidth = Paint().apply {
                textSize = this.textSize
            }.measureText("100,000,000")
            setLabelCount(5, true)
            textColor = ContextCompat.getColor(context, R.color.text_color)
            granularity = 1f
            isGranularityEnabled = true
            setDrawAxisLine(true)
            setDrawGridLines(true)
            axisLineColor = Color.GRAY
        }
    }

    fun chartAddAll(
        context: Context,
        volumeEntries: List<BarEntry>,
        commonChartModelList: List<CommonChartModel>
    ) {
        val volumeDataSet =
            NewChartUtils.createVolumeDataSet(context, volumeEntries, commonChartModelList)
        val volumeData = BarData(volumeDataSet)
        data = volumeData
        barData.notifyDataChanged()

        xAxis.setAxisMaximum(barData.getXMax() + 20f)
        xAxis.setAxisMinimum(barData.getXMin() - 20f)

        invalidate()
    }
}