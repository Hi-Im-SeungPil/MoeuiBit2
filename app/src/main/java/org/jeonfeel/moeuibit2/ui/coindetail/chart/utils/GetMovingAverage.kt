package org.jeonfeel.moeuibit2.ui.coindetail.chart.utils

import android.graphics.Color
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.CandleEntry
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineDataSet

class GetMovingAverage(
    private val number: Int
) {
    private var beforeLastCandleClose = 0f
    val lineEntry = ArrayList<Entry>()
    var sum = 0.0f
    var count = 0

    fun createLineData(candleEntries: ArrayList<CandleEntry>) {
        lineEntry.clear()
        sum = 0.0f
        count = 0
        beforeLastCandleClose = candleEntries.last().close
        for (i in candleEntries.indices) {
            count++
            sum += candleEntries[i].close
            if (count >= number) {
                lineEntry.add(Entry(candleEntries[i].x, sum / number))
                sum -= candleEntries[count - number].close
            }
        }
        sum += beforeLastCandleClose
    }

    fun modifyLineData(lastCandle: CandleEntry) {
        sum -= beforeLastCandleClose
        sum += lastCandle.close
        val average = sum / number
        beforeLastCandleClose = lastCandle.close
        lineEntry.removeLast()
        lineEntry.add(Entry(lastCandle.x,average))
    }

    fun addLineData(lastCandle: CandleEntry) {
        count++
        sum = sum - lineEntry[lineEntry.lastIndex - number - 1].y + lastCandle.close
        lineEntry.add(Entry(lastCandle.x,lastCandle.close))
    }
}

fun LineDataSet.defaultSet(lineColor: String) {
    val lineDataset = this
    lineDataset.apply {
        setDrawCircles(false)
        color = Color.parseColor(lineColor)
        lineWidth = 1f
        axisDependency = YAxis.AxisDependency.RIGHT
        isHighlightEnabled = false
        valueTextSize = 0f
    }
}