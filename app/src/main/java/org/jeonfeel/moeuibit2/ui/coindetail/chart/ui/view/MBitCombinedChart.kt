package org.jeonfeel.moeuibit2.ui.coindetail.chart.ui.view

import android.content.Context
import android.graphics.Color
import androidx.compose.runtime.MutableState
import androidx.core.content.ContextCompat
import com.github.mikephil.charting.charts.CombinedChart
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.CandleDataSet
import com.github.mikephil.charting.data.CandleEntry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet
import org.jeonfeel.moeuibit2.MoeuiBitApp
import org.jeonfeel.moeuibit2.R
import org.jeonfeel.moeuibit2.data.remote.retrofit.model.ChartModel
import org.jeonfeel.moeuibit2.ui.coindetail.chart.*
import org.jeonfeel.moeuibit2.ui.coindetail.chart.utils.ChartHelper
import org.jeonfeel.moeuibit2.ui.coindetail.chart.utils.addAccAmountLimitLine
import org.jeonfeel.moeuibit2.ui.coindetail.chart.utils.chartRefreshSettings
import org.jeonfeel.moeuibit2.ui.coindetail.chart.utils.XAxisValueFormatter
import org.jeonfeel.moeuibit2.utils.calculator.CurrentCalculator

class MBitCombinedChart(
    context: Context?
) : CombinedChart(context) {

    private val chartHelper = ChartHelper(context)
    private val chartCanvas = ChartCanvas(context)
    private var xAxisValueFormatter: XAxisValueFormatter? = null
    private var marketState = 0

    fun initChart(
        requestOldData: (IBarDataSet, IBarDataSet, Float) -> Unit,
        marketState: Int,
        loadingOldData: MutableState<Boolean>,
        minuteVisibility: MutableState<Boolean>,
        accData: HashMap<Int, Double>,
        kstDateHashMap: HashMap<Int, String>,
        isChartLastData: MutableState<Boolean>
    ) {
        val context = MoeuiBitApp.mBitApplicationContext()
        this.marketState = marketState
        this.removeAllViews()
        this.addView(chartCanvas)
        this.axisRight.removeAllLimitLines()
        this.xAxis.removeAllLimitLines()
        this.xAxis.textColor = context?.let {
            ContextCompat.getColor(it,R.color.text_color)
        } ?: Color.BLACK
        xAxisValueFormatter = XAxisValueFormatter()
        xAxisValueFormatter?.setItem(kstDateHashMap)
        chartHelper.defaultChartSettings(
            combinedChart = this,
            marketState = marketState,
            requestOldData = requestOldData,
            loadingOldData = loadingOldData,
            isChartLastData = isChartLastData,
            minuteVisibility = minuteVisibility,
            accData = accData,
            kstDateHashMap = kstDateHashMap
        )
    }

    /**
     * 차트 컴포넌트 추가
     */
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

    /**
     * 차트 컴포넌트 세팅 (시간대 같을 때)
     */
    fun chartSet(
        marketState: Int,
        lastCandleEntry: CandleEntry,
        candleEntriesIsEmpty: Boolean,
        candleUpdateLiveDataValue: Int,
        isUpdateChart: MutableState<Boolean>,
        accData: HashMap<Int, Double>,
        candlePosition: Float
    ) {
        if (isUpdateChart.value && this.barData != null) {
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
            val color = if (tradePrice - openPrice >= 0.0) ContextCompat.getColor(
                this.context,
                R.color.increase_color
            )
            else ContextCompat.getColor(
                this.context,
                R.color.decrease_color
            )
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
                    this.addAccAmountLimitLine(
                        lastX = lastBar.x,
                        color = color,
                        marketState = marketState,
                        accData = accData
                    )
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

    /**
     * 차트 데이터 초기화
     */
    fun chartDataInit(
        candleEntries: ArrayList<CandleEntry>,
        candleDataSet: CandleDataSet,
        positiveBarDataSet: BarDataSet,
        negativeBarDataSet: BarDataSet,
        lineData: LineData,
        purchaseAveragePrice: Float? = null
    ) {
        this.chartRefreshSettings(
            candleEntries = candleEntries,
            candleDataSet = candleDataSet,
            positiveBarDataSet = positiveBarDataSet,
            negativeBarDataSet = negativeBarDataSet,
            lineData = lineData,
            valueFormatter = xAxisValueFormatter,
            purchaseAveragePrice = purchaseAveragePrice,
            marketState = marketState
        )
    }

    fun getChartCanvas(): ChartCanvas? {
        return chartCanvas
    }

    fun getChartXValueFormatter(): XAxisValueFormatter? {
        return xAxisValueFormatter
    }

    /**
     * 캔버스 초기화
     */
    fun initCanvas() {
        chartCanvas.canvasInit(
            textSize = this.rendererRightYAxis.paintAxisLabels.textSize,
            textMarginLeft = this.axisRight.xOffset,
            width = this.rendererRightYAxis
                .paintAxisLabels
                .measureText(this.axisRight.longestLabel.plus('0')),
            x = this.measuredWidth - this.axisRight.getRequiredWidthSpace(
                this.rendererRightYAxis.paintAxisLabels
            )
        )
    }

    /**
     * 현재가 Y 포지션 가져오기
     */
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