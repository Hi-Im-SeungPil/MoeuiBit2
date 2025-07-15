package org.jeonfeel.moeuibit2.ui.coindetail.new_chart.model

import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.CandleEntry
import org.jeonfeel.moeuibit2.ui.coindetail.chart.utils.bithumb.CommonChartModel

data class ChartCombineModel(
    val candleEntries: List<CandleEntry>,
    val volumeEntries: List<BarEntry>,
    val commonChartDataList: List<CommonChartModel>
)
