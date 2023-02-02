package org.jeonfeel.moeuibit2.ui.coindetail.chart

import android.content.Context
import androidx.compose.runtime.MutableState
import com.github.mikephil.charting.charts.CombinedChart
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.CandleDataSet
import com.github.mikephil.charting.data.CandleEntry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet
import org.jeonfeel.moeuibit2.data.remote.retrofit.model.ChartModel
import org.jeonfeel.moeuibit2.ui.theme.decrease_candle_color
import org.jeonfeel.moeuibit2.ui.theme.increase_candle_color
import org.jeonfeel.moeuibit2.utils.XAxisValueFormatter
import org.jeonfeel.moeuibit2.utils.calculator.CurrentCalculator
import org.jeonfeel.moeuibit2.utils.chartRefreshSetting

class MBitCombinedChart(
    context: Context?
) : CombinedChart(context) {

    private val chartHelper = ChartHelper()
    private val chartCanvas = ChartCanvas(context)
    private var marketState = 0

    fun initChart(
        marketState: Int,
        requestOldData: suspend (String, String, Float, Float, IBarDataSet, IBarDataSet, Float) -> Unit
    ) {
        chartHelper.defaultChartSettings(this,chartCanvas,marketState,requestOldData)
    }

    fun chartAdd(model: ChartModel, candlePosition: Float, addLineData: () -> Unit) {
        if (model.tradePrice - model.openingPrice >= 0.0) {
            this.barData.dataSets[POSITIVE_BAR].addEntry(
                BarEntry(
                    candlePosition,
                    model.candleAccTradePrice.toFloat()
                )
            )
        } else {
            this.barData.dataSets[NEGATIVE_BAR].addEntry(
                BarEntry(
                    candlePosition,
                    model.candleAccTradePrice.toFloat()
                )
            )
        }

        try {
            addLineData()
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

    fun chartSet(
        marketState: Int,
        lastCandleEntry: CandleEntry,
        candleEntriesIsEmpty: Boolean,
        candleUpdateLiveDataValue: Int,
        isUpdateChart: MutableState<Boolean>,
        accData: HashMap<Int, Double>,
        candlePosition: Float
    ) {
        if (isUpdateChart.value) {
            val positiveBarLast =
                this.barData.dataSets[POSITIVE_BAR].getEntriesForXValue(candlePosition)
                    ?: emptyList()
            val negativeBarLast =
                this.barData.dataSets[NEGATIVE_BAR].getEntriesForXValue(candlePosition)
                    ?: emptyList()
            val positiveDataSet = this.barData.dataSets[POSITIVE_BAR]
            val negativeDataSet = this.barData.dataSets[NEGATIVE_BAR]
            val barEntry = BarEntry(candlePosition, accData[candlePosition.toInt()]!!.toFloat())
            if (lastCandleEntry.close - lastCandleEntry.open >= 0.0) {
                if (positiveBarLast.isNotEmpty()) {
                    positiveDataSet.removeLast()
                    positiveDataSet.addEntry(barEntry)
                } else {
                    positiveDataSet.addEntry(barEntry)
                    negativeDataSet.removeLast()
                }
            } else {
                if (negativeBarLast.isNotEmpty()) {
                    negativeDataSet.removeLast()
                    negativeDataSet.addEntry(barEntry)
                } else {
                    negativeDataSet.addEntry(barEntry)
                    positiveDataSet.removeLast()
                }
            }
        }

        if (this.candleData != null) {
            val tradePrice = lastCandleEntry.close
            val openPrice = lastCandleEntry.open
            val color = if (tradePrice - openPrice >= 0.0) increase_candle_color
            else decrease_candle_color
            val yp = this.getTradePriceYPosition(tradePrice)
            if (this.data.candleData.xMax <= this.highestVisibleX
                && !candleEntriesIsEmpty
            ) {
                this.getChartCanvas()?.realTimeLastCandleClose(
                    lastCandlePriceYPosition = yp,
                    lastCandlePrice = CurrentCalculator.tradePriceCalculator(
                        tradePrice,
                        marketState
                    ),
                    color = color
                )
                if (candleUpdateLiveDataValue == CHART_SET_ALL) {
                    val lastBar = try {
                        val position = if (this.getLastBarIsEmpty(0, lastCandleEntry.x)) {
                            NEGATIVE_BAR
                        } else {
                            POSITIVE_BAR
                        }
                        this.barData.dataSets[position].getEntriesForXValue(
                            lastCandleEntry.x
                        ).first()
                    } catch (e: Exception) {
                        CandleEntry(1f, 1f, 1f, 1f, 1f)
                    }
//                    this.addAccAmountLimitLine(
//                        lastBar.x,
//                        coinDetailViewModel,
//                        color,
//                        marketState
//                    )
                }
            }
            this.apply {
                barData.notifyDataChanged()
                data.notifyDataChanged()
                notifyDataSetChanged()
                lineData.notifyDataChanged()
                invalidate()
            }
        }
    }

    fun getChartCanvas(): ChartCanvas? {
        return chartCanvas
    }

    fun getChartXValueFormatter(): ValueFormatter? {
        return this.xAxis.valueFormatter
    }

    fun chartInit(
        candleEntries: ArrayList<CandleEntry>,
        candleDataSet: CandleDataSet,
        positiveBarDataSet: BarDataSet,
        negativeBarDataSet: BarDataSet,
        lineData: LineData,
        purchaseAveragePrice: Float? = null
    ) {
        this.chartRefreshSetting(
            candleEntries = candleEntries,
            candleDataSet = candleDataSet,
            positiveBarDataSet = positiveBarDataSet,
            negativeBarDataSet = negativeBarDataSet,
            lineData = lineData,
            valueFormatter = getChartXValueFormatter() as XAxisValueFormatter,
            purchaseAveragePrice = purchaseAveragePrice
        )
    }

    private fun getTradePriceYPosition(tradePrice: Float): Float {
        return this.getPosition(
            CandleEntry(
                tradePrice,
                tradePrice,
                tradePrice,
                tradePrice,
                tradePrice
            ), this.axisRight.axisDependency
        ).y
    }

    private fun getLastBarIsEmpty(barDataSetsPosition: Int, xValue: Float): Boolean {
        return this.barData.dataSets[barDataSetsPosition].getEntriesForXValue(
            xValue
        ).isEmpty()
    }
}