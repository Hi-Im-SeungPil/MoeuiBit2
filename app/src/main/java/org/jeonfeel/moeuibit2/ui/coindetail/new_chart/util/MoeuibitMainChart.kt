package org.jeonfeel.moeuibit2.ui.coindetail.new_chart.util

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
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
import com.github.mikephil.charting.renderer.YAxisRenderer
import com.github.mikephil.charting.utils.ViewPortHandler
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

    init {
        setupChart()
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setupChart() {
        val customRenderer = CustomYAxisRenderer(
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
        rendererRightYAxis = customRenderer

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

        xAxis.setAxisMaximum(candleData.getXMax() + 20f)
        xAxis.setAxisMinimum(candleData.getXMin() - 20f)

        invalidate()
    }

    fun getYpositionByTradePrice(price: Float, decent: Float, accent: Float): Float {
        val chartTopY = this.viewPortHandler.contentTop() - accent
        val chartBottomY = this.viewPortHandler.contentBottom() + decent
        val yPosition = this.getPosition(Entry(0f, price), axisRight.axisDependency).y
        return if (yPosition in chartTopY..chartBottomY) {
            yPosition
        } else if (yPosition < chartTopY) {
            chartTopY
        } else if (yPosition > chartBottomY) {
            chartBottomY
        } else {
            yPosition
        }

//        if(yPosition)
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
}

class CustomYAxisRenderer(
    private val context: Context,
    viewPortHandler: ViewPortHandler,
    yAxis: YAxis,
    trans: com.github.mikephil.charting.utils.Transformer
) : YAxisRenderer(viewPortHandler, yAxis, trans) {

    private val gridLinePath = Path()

    override fun drawYLabels(
        c: Canvas,
        fixedPosition: Float,
        positions: FloatArray,
        offset: Float
    ) {
        val paint = mAxisLabelPaint
        val yAxis = mYAxis
        val topFix = 11f.dpToPx(context)    // 가장 위 레이블 아래로
        val bottomFix = (-8f).dpToPx(context) // 가장 아래 레이블 위로

        for (i in 0 until yAxis.mEntryCount) {
            val text = yAxis.getFormattedLabel(i)
            val xPos = fixedPosition + offset
            val baseY = positions[i * 2 + 1]

            val yPos = when (i) {
                0 -> baseY + bottomFix
                yAxis.mEntryCount - 1 -> baseY + topFix
                else -> baseY
            }

            c.drawText(text, xPos, yPos, paint)
        }
    }

    override fun renderGridLines(c: Canvas) {
        if (!mYAxis.isEnabled || !mYAxis.isDrawGridLinesEnabled) return

        val positions = FloatArray(mYAxis.mEntryCount * 2)

        for (i in 0 until mYAxis.mEntryCount) {
            positions[i * 2 + 1] = mYAxis.mEntries[i]
        }

        mTrans.pointValuesToPixel(positions)

        for (i in 0 until mYAxis.mEntryCount) {
            // 상단/하단 제외
            if (i == 0 || i == mYAxis.mEntryCount - 1) continue

            val y = positions[i * 2 + 1]
            gridLinePath.reset()
            gridLinePath.moveTo(mViewPortHandler.contentLeft(), y)
            gridLinePath.lineTo(mViewPortHandler.contentRight(), y)

            c.drawPath(gridLinePath, mGridPaint)
        }
    }
}