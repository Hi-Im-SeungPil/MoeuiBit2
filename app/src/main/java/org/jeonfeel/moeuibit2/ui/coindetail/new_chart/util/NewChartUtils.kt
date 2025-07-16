package org.jeonfeel.moeuibit2.ui.coindetail.new_chart.util

import android.content.Context
import android.graphics.Color
import android.graphics.Paint
import androidx.core.content.ContextCompat
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.CandleDataSet
import com.github.mikephil.charting.data.CandleEntry
import org.jeonfeel.moeuibit2.R
import org.jeonfeel.moeuibit2.ui.coindetail.chart.utils.bithumb.CommonChartModel

object NewChartUtils {
    fun createEntryLists(data: List<CommonChartModel>): Pair<List<CandleEntry>, List<BarEntry>> {
        val candleEntries = mutableListOf<CandleEntry>()
        val barEntries = mutableListOf<BarEntry>()

        data.forEachIndexed { index, item ->
            candleEntries += CandleEntry(
                index.toFloat(),
                item.highPrice.toFloat(),
                item.lowPrice.toFloat(),
                item.openingPrice.toFloat(),
                item.tradePrice.toFloat()
            )

            barEntries += BarEntry(
                index.toFloat(),
                item.candleAccTradePrice.toFloat()
            )
        }

        return candleEntries to barEntries
    }

    fun createCandleDataSet(context: Context, entries: List<CandleEntry>): CandleDataSet {
        return CandleDataSet(entries, "Price").apply {
            axisDependency = YAxis.AxisDependency.RIGHT
            shadowColorSameAsCandle = true
            shadowWidth = 1f
            decreasingColor = ContextCompat.getColor(context, R.color.decrease_color)
            decreasingPaintStyle = Paint.Style.FILL
            increasingColor = ContextCompat.getColor(context, R.color.increase_color)
            increasingPaintStyle = Paint.Style.FILL
            neutralColor = ContextCompat.getColor(context, R.color.natural_color)
            highLightColor = Color.BLACK
            setDrawHorizontalHighlightIndicator(false)
            setDrawVerticalHighlightIndicator(false)
            isHighlightEnabled = true
            setDrawValues(false)
        }
    }

    fun createVolumeDataSet(
        context: Context,
        entries: List<BarEntry>,
        commonData: List<CommonChartModel>
    ): BarDataSet {
        val barColors = commonData.map { item ->
            val isBearish = item.tradePrice < item.openingPrice
            if (isBearish) ContextCompat.getColor(
                context,
                R.color.decrease_color
            ) else ContextCompat.getColor(context, R.color.increase_color)
        }

        return BarDataSet(entries, "Volume").apply {
            setColors(barColors)
            axisDependency = YAxis.AxisDependency.LEFT
            setDrawValues(false)
        }
    }

    fun compareChartTime() {

    }
}