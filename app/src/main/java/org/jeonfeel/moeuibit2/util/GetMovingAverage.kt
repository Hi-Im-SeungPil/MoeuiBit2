package org.jeonfeel.moeuibit2.util

import android.graphics.Color
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.CandleEntry
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet

class GetMovingAverage(private val candleEntries: ArrayList<CandleEntry>?) {
    var count = 0
    val line1Entries = ArrayList<Entry>()
    val line2Entries = ArrayList<Entry>()
    val line3Entries = ArrayList<Entry>()
    val line4Entries = ArrayList<Entry>()
    val line5Entries = ArrayList<Entry>()

    var sumLine1 = 0.0f
    var sumLine2 = 0.0f
    var sumLine3 = 0.0f
    var sumLine4 = 0.0f
    var sumLine5 = 0.0f

    val Line1 = 5
    val Line2 = 10
    val Line3 = 20
    val Line4 = 60
    val Line5 = 120
    fun createLineData(): LineData {
        val lineData = LineData()
        count = 0
        sumLine1 = 0.0f
        sumLine2 = 0.0f
        sumLine3 = 0.0f
        sumLine4 = 0.0f
        sumLine5 = 0.0f

        line1Entries.clear()
        line2Entries.clear()
        line3Entries.clear()
        line4Entries.clear()
        line5Entries.clear()

        for (i in candleEntries!!.indices) {
            count++
            sumLine1 += candleEntries[i].close
            sumLine2 += candleEntries[i].close
            sumLine3 += candleEntries[i].close
            sumLine4 += candleEntries[i].close
            sumLine5 += candleEntries[i].close
            if (count >= Line1) {
                line1Entries.add(Entry(candleEntries[i].x, sumLine1 / Line1))
                sumLine1 -= candleEntries[count - 1 - (Line1 - 1)].close
            }
            if (count >= Line2) {
                line2Entries.add(Entry(candleEntries[i].x, sumLine2 / Line2))
                sumLine2 -= candleEntries[count - 1 - (Line2 - 1)].close
            }
            if (count >= Line3) {
                line3Entries.add(Entry(candleEntries[i].x, sumLine3 / Line3))
                sumLine3 -= candleEntries[count - 1 - (Line3 - 1)].close
            }
            if (count >= Line4) {
                line4Entries.add(Entry(candleEntries[i].x, sumLine4 / Line4))
                sumLine4 -= candleEntries[count - 1 - (Line4 - 1)].close
            }
            if (count >= Line5) {
                line5Entries.add(Entry(candleEntries[i].x, sumLine5 / Line5))
                sumLine5 -= candleEntries[count - 1 - (Line5 - 1)].close
            }
        }
        val lineDataSet1 = LineDataSet(line1Entries, "")
        lineDataSet1.defaultSet("#B3FF36FF")
        val lineDataSet2 = LineDataSet(line2Entries, "")
        lineDataSet2.defaultSet("#B30000B7")
        val lineDataSet3 = LineDataSet(line3Entries, "")
        lineDataSet3.defaultSet("#B3DBC000")
        val lineDataSet4 = LineDataSet(line4Entries, "")
        lineDataSet4.defaultSet("#B3FF4848")
        val lineDataSet5 = LineDataSet(line5Entries, "")
        lineDataSet5.defaultSet("#B3BDBDBD")
        lineData.addDataSet(lineDataSet1)
        lineData.addDataSet(lineDataSet2)
        lineData.addDataSet(lineDataSet3)
        lineData.addDataSet(lineDataSet4)
        lineData.addDataSet(lineDataSet5)

        return lineData
    }
}

class GetMovingAverage2(
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