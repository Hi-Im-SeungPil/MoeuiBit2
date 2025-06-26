package org.jeonfeel.moeuibit2.ui.coindetail.chart

import androidx.lifecycle.viewModelScope
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet
import kotlinx.coroutines.launch
import org.jeonfeel.moeuibit2.GlobalState
import org.jeonfeel.moeuibit2.constants.EXCHANGE_BITTHUMB
import org.jeonfeel.moeuibit2.constants.EXCHANGE_UPBIT
import org.jeonfeel.moeuibit2.data.local.preferences.PreferencesManager
import org.jeonfeel.moeuibit2.ui.base.BaseViewModel
import org.jeonfeel.moeuibit2.ui.coindetail.chart.utils.bithumb.BiThumbChart
import org.jeonfeel.moeuibit2.ui.coindetail.chart.utils.upbit.UpbitChart
import javax.inject.Inject

class ChartViewModel @Inject constructor(
    private val upbitChart: UpbitChart,
    private val biThumbChart: BiThumbChart,
    preferenceManager: PreferencesManager
) : BaseViewModel(preferenceManager) {

    val state
        get() = run {
            when (GlobalState.globalExchangeState.value) {
                EXCHANGE_UPBIT -> upbitChart.state
                EXCHANGE_BITTHUMB -> biThumbChart.state
                else -> upbitChart.state
            }
        }


    val chartUpdateLiveData
        get() = run {
            when (GlobalState.globalExchangeState.value) {
                EXCHANGE_UPBIT -> upbitChart.chartUpdateLiveData
                EXCHANGE_BITTHUMB -> biThumbChart.chartUpdateLiveData
                else -> upbitChart.chartUpdateLiveData
            }
        }

    val kstDateHashMap
        get() = run {
            when (GlobalState.globalExchangeState.value) {
                EXCHANGE_UPBIT -> upbitChart.kstDateHashMap
                EXCHANGE_BITTHUMB -> biThumbChart.kstDateHashMap
                else -> upbitChart.kstDateHashMap
            }
        }

    val accData
        get() = run {
            when (GlobalState.globalExchangeState.value) {
                EXCHANGE_UPBIT -> upbitChart.accData
                EXCHANGE_BITTHUMB -> biThumbChart.accData
                else -> upbitChart.accData

            }
        }

    val candleEntries
        get() = run {
            when (GlobalState.globalExchangeState.value) {
                EXCHANGE_UPBIT -> upbitChart.candleEntries
                EXCHANGE_BITTHUMB -> biThumbChart.candleEntries
                else -> upbitChart.candleEntries
            }
        }

    val candleDataSet
        get() = run {
            when (GlobalState.globalExchangeState.value) {
                EXCHANGE_UPBIT -> upbitChart.candleDataSet
                EXCHANGE_BITTHUMB -> biThumbChart.candleDataSet
                else -> upbitChart.candleDataSet
            }
        }

    val positiveBarDataSet
        get() = run {
            when (GlobalState.globalExchangeState.value) {
                EXCHANGE_UPBIT -> upbitChart.positiveBarDataSet
                EXCHANGE_BITTHUMB -> biThumbChart.positiveBarDataSet
                else -> upbitChart.positiveBarDataSet
            }
        }

    val negativeBarDataSet
        get() = run {
            when (GlobalState.globalExchangeState.value) {
                EXCHANGE_UPBIT -> upbitChart.negativeBarDataSet
                EXCHANGE_BITTHUMB -> biThumbChart.negativeBarDataSet
                else -> upbitChart.negativeBarDataSet
            }
        }

    val purchaseAveragePrice
        get() = run {
            when (GlobalState.globalExchangeState.value) {
                EXCHANGE_UPBIT -> upbitChart.purchaseAveragePrice
                EXCHANGE_BITTHUMB -> biThumbChart.purchaseAveragePrice
                else -> upbitChart.purchaseAveragePrice
            }
        }

    val addModel
        get() = run {
            when (GlobalState.globalExchangeState.value) {
                EXCHANGE_UPBIT -> upbitChart.addModel
                EXCHANGE_BITTHUMB -> biThumbChart.addModel
                else -> upbitChart.addModel
            }
        }

    val candlePosition
        get() = run {
            when (GlobalState.globalExchangeState.value) {
                EXCHANGE_UPBIT -> upbitChart.candlePosition
                EXCHANGE_BITTHUMB -> biThumbChart.candlePosition
                else -> upbitChart.candlePosition
            }
        }

    fun refresh(market: String) {
        viewModelScope.launch {
            when (GlobalState.globalExchangeState.value) {
                EXCHANGE_UPBIT -> upbitChart.refresh(market = market)
                EXCHANGE_BITTHUMB -> biThumbChart.refresh(market = market)
            }
        }
    }

    fun saveLastPeriod(period: String) {
        viewModelScope.launch {
            when (GlobalState.globalExchangeState.value) {
                EXCHANGE_UPBIT -> upbitChart.saveLastPeriod(period = period)
            }
        }
    }

    fun newRequestOldData(
        market: String,
        positiveBarDataSet: IBarDataSet,
        negativeBarDataSet: IBarDataSet,
        candleXMin: Float
    ) {
        viewModelScope.launch {
            when (GlobalState.globalExchangeState.value) {
                EXCHANGE_UPBIT -> upbitChart.newRequestOldData(
                    positiveBarDataSet = positiveBarDataSet,
                    negativeBarDataSet = negativeBarDataSet,
                    candleXMin = candleXMin,
                    market = market
                )
            }
        }
    }

    fun updateCandleTicker(tradePrice: Double) {
        viewModelScope.launch {
            when (GlobalState.globalExchangeState.value) {
                EXCHANGE_UPBIT -> upbitChart.updateCandleTicker(tradePrice)
            }
        }
    }

    fun updateCandlePosition(position: Float) {
        when (GlobalState.globalExchangeState.value) {
            EXCHANGE_UPBIT -> upbitChart.updateCandlePosition(position)
        }
    }

    fun createLineData(): LineData {
        return when (GlobalState.globalExchangeState.value) {
            EXCHANGE_UPBIT -> upbitChart.createLineData()
            else -> upbitChart.createLineData()
        }
    }

    fun addLineData() {
        when (GlobalState.globalExchangeState.value) {
            EXCHANGE_UPBIT -> upbitChart.addLineData()
        }
    }

    fun isCandleEntryEmpty() = when (GlobalState.globalExchangeState.value) {
        EXCHANGE_UPBIT -> upbitChart.isCandleEntryEmpty()
        else -> upbitChart.isCandleEntryEmpty()
    }

    fun getLastCandleEntry() = when (GlobalState.globalExchangeState.value) {
        EXCHANGE_UPBIT -> upbitChart.getLastCandleEntry()
        else -> upbitChart.getLastCandleEntry()
    }
}