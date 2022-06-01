package org.jeonfeel.moeuibit2.helper//package org.jeonfeel.moeuibit2.Fragment.Chart
//
import android.graphics.Color
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.CandleEntry
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet

class GetMovingAverage(private val candleEntries: ArrayList<CandleEntry>?) {

    var isOk = true
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
        isOk = false
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
        lineDataSet1.setDrawCircles(false)
        lineDataSet1.color = Color.parseColor("#B3FF36FF")
        lineDataSet1.lineWidth = 1f
        lineDataSet1.axisDependency = YAxis.AxisDependency.RIGHT
        lineDataSet1.isHighlightEnabled = false
        lineDataSet1.valueTextSize = 0f
        val lineDataSet2 = LineDataSet(line2Entries, "")
        lineDataSet2.setDrawCircles(false)
        lineDataSet2.color = Color.parseColor("#B30000B7")
        lineDataSet2.lineWidth = 1f
        lineDataSet2.axisDependency = YAxis.AxisDependency.RIGHT
        lineDataSet2.isHighlightEnabled = false
        lineDataSet2.valueTextSize = 0f
        val lineDataSet3 = LineDataSet(line3Entries, "")
        lineDataSet3.setDrawCircles(false)
        lineDataSet3.color = Color.parseColor("#B3DBC000")
        lineDataSet3.lineWidth = 1f
        lineDataSet3.axisDependency = YAxis.AxisDependency.RIGHT
        lineDataSet3.isHighlightEnabled = false
        lineDataSet3.valueTextSize = 0f
        val lineDataSet4 = LineDataSet(line4Entries, "")
        lineDataSet4.setDrawCircles(false)
        lineDataSet4.color = Color.parseColor("#B3FF4848")
        lineDataSet4.lineWidth = 1f
        lineDataSet4.axisDependency = YAxis.AxisDependency.RIGHT
        lineDataSet4.isHighlightEnabled = false
        lineDataSet4.valueTextSize = 0f
        val lineDataSet5 = LineDataSet(line5Entries, "")
        lineDataSet5.setDrawCircles(false)
        lineDataSet5.color = Color.parseColor("#B3BDBDBD")
        lineDataSet5.lineWidth = 1f
        lineDataSet5.axisDependency = YAxis.AxisDependency.RIGHT
        lineDataSet5.isHighlightEnabled = false
        lineDataSet5.valueTextSize = 0f
        lineData.addDataSet(lineDataSet1)
        lineData.addDataSet(lineDataSet2)
        lineData.addDataSet(lineDataSet3)
        lineData.addDataSet(lineDataSet4)
        lineData.addDataSet(lineDataSet5)
        return lineData
    }
}