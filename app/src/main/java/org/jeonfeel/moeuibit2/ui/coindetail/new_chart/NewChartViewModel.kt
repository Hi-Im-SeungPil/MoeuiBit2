package org.jeonfeel.moeuibit2.ui.coindetail.new_chart

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import org.jeonfeel.moeuibit2.GlobalState
import org.jeonfeel.moeuibit2.constants.EXCHANGE_UPBIT
import org.jeonfeel.moeuibit2.constants.ioDispatcher
import javax.inject.Inject

@HiltViewModel
class NewChartViewModel @Inject constructor(
    private val newUpbitChart: NewUpbitChart
) : ViewModel() {
    val commonEntries = when (GlobalState.globalExchangeState.value) {
        EXCHANGE_UPBIT -> newUpbitChart.commonEntries
        else -> emptyList()
    }

    val candleEntries = when (GlobalState.globalExchangeState.value) {
        EXCHANGE_UPBIT -> newUpbitChart.commonEntries
        else -> emptyList()
    }

    val volumeEntries = when (GlobalState.globalExchangeState.value) {
        EXCHANGE_UPBIT -> newUpbitChart.volumeEntries
        else -> emptyList()
    }

    val chartUpdates = when (GlobalState.globalExchangeState.value) {
        EXCHANGE_UPBIT -> newUpbitChart.chartUpdates
        else -> newUpbitChart.chartUpdates
    }

    fun init(market: String) {
        viewModelScope.launch(ioDispatcher) {
            when (GlobalState.globalExchangeState.value) {
                EXCHANGE_UPBIT -> newUpbitChart.init(
                    market = market,
                    chartReqType = ChartReqType.CHART_GET
                )

                else -> {}
            }
        }
    }
}