package org.jeonfeel.moeuibit2.ui.coindetail.chart

import android.content.Context
import android.util.AttributeSet
import com.github.mikephil.charting.charts.CombinedChart
import com.github.mikephil.charting.data.BarEntry
import org.jeonfeel.moeuibit2.data.remote.retrofit.model.ChartModel

class MBitCombinedChart(context: Context?, attrs: AttributeSet?) : CombinedChart(context, attrs) {

    private val chartHelper = ChartHelper()

    init {
        chartHelper.defaultChartSettings(this)
    }

    fun addCandle(model: ChartModel, candlePosition: Float) {
        if (model.tradePrice - model.openingPrice >= 0.0) {
            this.barData.dataSets[0].addEntry(
                BarEntry(
                    candlePosition,
                    model.candleAccTradePrice.toFloat()
                )
            )
        } else {
            this.barData.dataSets[1].addEntry(
                BarEntry(
                    candlePosition,
                    model.candleAccTradePrice.toFloat()
                )
            )
        }

        try {
//            addLineData()
        } catch (e: Exception) {
            e.printStackTrace()
        }

        this.apply {
            xAxis.axisMaximum += 1f
            barData.notifyDataChanged()
            lineData.notifyDataChanged()
            data.notifyDataChanged()
            notifyDataSetChanged()
            invalidate()
        }
    }
}