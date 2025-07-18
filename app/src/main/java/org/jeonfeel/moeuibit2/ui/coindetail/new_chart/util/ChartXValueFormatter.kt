package org.jeonfeel.moeuibit2.ui.coindetail.new_chart.util

import com.github.mikephil.charting.formatter.ValueFormatter

class ChartXValueFormatter(private val labels: List<String>) : ValueFormatter() {
    override fun getFormattedValue(value: Float): String {
        val index = value.toInt()
        return if (index in labels.indices) labels[index] else if (index >= labels.size) labels.last() else if (index <= 0) labels.first() else ""
    }
}