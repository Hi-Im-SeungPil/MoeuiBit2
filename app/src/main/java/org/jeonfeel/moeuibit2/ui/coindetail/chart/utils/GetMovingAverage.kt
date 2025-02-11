package org.jeonfeel.moeuibit2.ui.coindetail.chart.utils

import android.graphics.Color
import android.os.Build
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.CandleEntry
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineDataSet
import com.orhanobut.logger.Logger

class GetMovingAverage(
    private val number: Int,
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
        if (lastCandle.x < number) return
        sum -= beforeLastCandleClose
        sum += lastCandle.close
        val average = sum / number
        beforeLastCandleClose = lastCandle.close
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.VANILLA_ICE_CREAM) {
            lineEntry.removeLast()
        } else {
            val lastIndex = lineEntry.lastIndex
            lineEntry.removeAt(lastIndex)
        }
        lineEntry.add(Entry(lastCandle.x, average))
    }

    fun addLineData(lastCandle: CandleEntry, yValue: Float) {
        if (lastCandle.x < number) return
        Logger.e("last -> ${lineEntry.lastIndex - number - 1} $number ${lineEntry.count()} $yValue")
        sum = sum - yValue + lastCandle.close
        count++
        lineEntry.add(Entry(lastCandle.x, sum / number))
    }

    fun getNumber(): Int {
        return number
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