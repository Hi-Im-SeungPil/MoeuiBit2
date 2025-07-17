package org.jeonfeel.moeuibit2.ui.coindetail.new_chart.util

import android.content.Context
import android.graphics.Canvas
import android.graphics.Path
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.renderer.YAxisRenderer
import com.github.mikephil.charting.utils.ViewPortHandler
import org.jeonfeel.moeuibit2.utils.Utils.dpToPx

class MainChartYAxisRenderer(
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
        val descent = paint.fontMetrics.descent
        val ascent = paint.fontMetrics.ascent

        val centerOffset = (ascent + descent) / 2

        for (i in 0 until yAxis.mEntryCount) {
            val text = yAxis.getFormattedLabel(i)
            val xPos = fixedPosition + offset
            val baseY = positions[i * 2 + 1]

            val yPos = when (i) {
                0 -> baseY - descent
                yAxis.mEntryCount - 1 -> baseY - ascent
                else -> baseY - centerOffset
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