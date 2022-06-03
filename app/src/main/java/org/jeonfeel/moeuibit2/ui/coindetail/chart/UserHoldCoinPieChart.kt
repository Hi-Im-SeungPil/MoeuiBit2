package org.jeonfeel.moeuibit2.ui.coindetail.chart

import android.content.Context
import android.graphics.Color
import android.icu.number.Precision.currency
import android.text.TextPaint
import android.util.Log
import androidx.core.view.size
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.LegendEntry
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.utils.ViewPortHandler
import org.jeonfeel.moeuibit2.R
import org.jeonfeel.moeuibit2.data.local.room.entity.MyCoin


class UserHoldCoinPieChart(
    context: Context,
    private val userSeedMoney: Long,
    private val userHoldCoinList: List<MyCoin?>
) : PieChart(context) {
    private val pieChart = this
    private val symbolArray = ArrayList<String>()
    private val eachCoinAmountArray = ArrayList<Double>()
    private var totalAssets = 0.0
    private val colorArray = resources.obtainTypedArray(R.array.pieChart_color)

    init {
        this.initPieChart()
        getEachCoinAmount()
        val legendEntryArrayList = ArrayList<LegendEntry>()
        var position = 0
        val colors = IntArray(eachCoinAmountArray.size)
        for (i in eachCoinAmountArray.indices) {
            colors[i] = colorArray.getColor(position, 0)
            position++
            if (position >= 46) {
                position = 0
            }
        }

        for (i in eachCoinAmountArray.indices) {
            val labelString =
                String.format("%.1f", (eachCoinAmountArray[i] / totalAssets.toFloat()) * 100)
            val legendEntry = LegendEntry().apply {
                label = symbolArray[i].plus(" $labelString%")
                formColor = colors[i]
            }
            legendEntryArrayList.add(legendEntry)
        }

        legend.apply {
            setCustom(legendEntryArrayList)
            isWordWrapEnabled = true
        }

        val data: ArrayList<PieEntry> = ArrayList()
        for (i in eachCoinAmountArray.indices) {
            data.add(PieEntry(eachCoinAmountArray[i].toFloat()))
        }

        val pieDataSet = PieDataSet(data, "").apply {
            setColors(*colors)
            isHighlightEnabled = false
            valueTextColor = Color.parseColor("#FFFFFFFF")
        }
        val pieData = PieData(pieDataSet)
        pieData.setValueTextSize(10f)
        pieData.setValueFormatter(object : ValueFormatter(){
            override fun getFormattedValue(value: Float): String {
                return if(value >= 3f) {
                    String.format("%.1f", value)
                } else {
                    ""
                }
            }
        })
        pieChart.data = pieData
        pieChart.invalidate()
    }

    private fun getEachCoinAmount() {
        if (userSeedMoney != 0L) {
            val krw = userSeedMoney
            eachCoinAmountArray.add(krw.toDouble())
            symbolArray.add("KRW")
            totalAssets += krw
        }
        for (i in userHoldCoinList.indices) {
            symbolArray.add(userHoldCoinList[i]?.symbol ?: "")
            val eachCoinAmount =
                userHoldCoinList[i]!!.quantity * userHoldCoinList[i]!!.purchasePrice
            eachCoinAmountArray.add(eachCoinAmount)
            totalAssets += eachCoinAmount
        }
    }
}

fun PieChart.initPieChart() {
    val pieChart = this
    pieChart.apply {
        setDrawEntryLabels(true)
        setUsePercentValues(true)
        isHighlightPerTapEnabled = false
        centerText = "보유 현황 %"
        setCenterTextSize(15f)
        legend.isEnabled = true
        isHighlightPerTapEnabled = false
        isRotationEnabled = false
        description.isEnabled = false
    }
}