package org.jeonfeel.moeuibit2.ui.main.exchange

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import org.jeonfeel.moeuibit2.constants.ioDispatcher
import org.jeonfeel.moeuibit2.data.network.retrofit.model.upbit.CommonExchangeModel
import org.jeonfeel.moeuibit2.ui.base.BaseViewModel
import org.jeonfeel.moeuibit2.ui.main.exchange.ExchangeViewModel.Companion.TRADE_CURRENCY_KRW
import org.jeonfeel.moeuibit2.ui.main.exchange.component.SortOrder
import org.jeonfeel.moeuibit2.ui.main.exchange.component.SortType
import org.jeonfeel.moeuibit2.ui.main.exchange.root_exchange.UpBitExchange
import org.jeonfeel.moeuibit2.ui.main.exchange.root_exchange.ExchangeInitState
import org.jeonfeel.moeuibit2.data.local.preferences.PreferencesManager
import java.math.BigDecimal
import javax.inject.Inject

enum class TickerAskBidState {
    ASK, BID, NONE
}

class ExchangeViewModelState {
    val isUpdateExchange = mutableStateOf(true)

    val tradeCurrencyState = mutableIntStateOf(TRADE_CURRENCY_KRW)

    val selectedSortType: MutableState<SortType> = mutableStateOf(SortType.DEFAULT)

    val sortOrder: MutableState<SortOrder> = mutableStateOf(SortOrder.NONE)

    val textFieldValue: MutableState<String> = mutableStateOf("")
}

@HiltViewModel
class ExchangeViewModel @Inject constructor(
    private val upBitExchange: UpBitExchange,
    private val preferenceManager: PreferencesManager
) : BaseViewModel(preferenceManager) {

    private val state = ExchangeViewModelState()

    val isUpdateExchange: State<Boolean> get() = state.isUpdateExchange

    val tradeCurrencyState: State<Int> get() = state.tradeCurrencyState

    val selectedSortType: State<SortType> get() = state.selectedSortType

    val sortOrder: State<SortOrder> get() = state.sortOrder

    val textFieldValue: State<String> get() = state.textFieldValue

    private var realTimeUpdateJob: Job? = null
    private var collectTickerJob: Job? = null
    private var marketChangeJob: Job? = null

    init {
        _loadingState.value = true
        rootExchangeCoroutineBranch(
            upbitAction = {
                upBitExchange.initUpBit(
                    tradeCurrencyState = tradeCurrencyState,
                    isUpdateExchange = isUpdateExchange
                ).collect { upBitInitState ->
                    processData(upBitInitState)
                }
            },
            bitthumbAction = {

            }
        )
    }

    private suspend fun processData(exchangeInitState: ExchangeInitState) {
        when (exchangeInitState) {
            is ExchangeInitState.Loading -> {
                // 로딩
            }

            is ExchangeInitState.Success -> {
                _loadingState.value = false
            }

            is ExchangeInitState.Error -> {
                _loadingState.value = false
            }

            is ExchangeInitState.Wait -> {
            }
        }
    }

    fun onStart() {
        realTimeUpdateJob?.cancel()
        collectTickerJob?.cancel()

        realTimeUpdateJob = viewModelScope.launch(ioDispatcher) {
            when (rootExchange) {
                ROOT_EXCHANGE_UPBIT -> {
                    upBitExchange.onStart(
                        updateLoadingState = ::updateLoadingState
                    )
                }

                ROOT_EXCHANGE_BITTHUMB -> {

                }
            }
        }.also { it.start() }

        collectTickerJob = viewModelScope.launch {
            upBitExchange.exchangeCollectTicker()
        }.also { it.start() }
    }

    fun onStop() {
        rootExchangeCoroutineBranch(
            upbitAction = {
                upBitExchange.onStop()
                collectTickerJob?.cancel()
                realTimeUpdateJob?.cancel()
                collectTickerJob = null
                realTimeUpdateJob = null
            },
            bitthumbAction = {

            }
        )
    }

    private fun updateLoadingState(state: Boolean) {
        _loadingState.value = state
    }

    fun updateSortType(sortType: SortType) {
        state.selectedSortType.value = sortType
    }

    fun updateSortOrder(sortOrder: SortOrder) {
        state.sortOrder.value = sortOrder
    }

    fun getTickerList(): List<CommonExchangeModel> {
        return when (rootExchange) {
            ROOT_EXCHANGE_UPBIT -> {
                upBitExchange.getExchangeModelList(tradeCurrencyState)
            }

            ROOT_EXCHANGE_BITTHUMB -> {
                upBitExchange.getExchangeModelList(tradeCurrencyState)
            }

            else -> {
                upBitExchange.getExchangeModelList(tradeCurrencyState)
            }
        }
    }

    fun sortTickerList(targetTradeCurrency: Int? = null, sortType: SortType, sortOrder: SortOrder) {
        state.isUpdateExchange.value = false
        upBitExchange.sortTickerList(
            tradeCurrency = targetTradeCurrency ?: tradeCurrencyState.value,
            sortType = sortType,
            sortOrder = sortOrder
        )
        state.isUpdateExchange.value = true
    }

    fun changeTradeCurrency(tradeCurrency: Int) {
        if (tradeCurrency in TRADE_CURRENCY_KRW..TRADE_CURRENCY_FAV) {
            state.tradeCurrencyState.intValue = tradeCurrency
            marketChangeJob?.cancel()
            marketChangeJob = viewModelScope.launch(ioDispatcher) {
                when (rootExchange) {
                    ROOT_EXCHANGE_UPBIT -> {
                        upBitExchange.changeTradeCurrencyAction(
                            sortType = selectedSortType.value,
                            sortOrder = sortOrder.value
                        )
                    }

                    ROOT_EXCHANGE_BITTHUMB -> {
                        upBitExchange.changeTradeCurrencyAction()
                    }

                    else -> {
                        upBitExchange.changeTradeCurrencyAction()
                    }
                }
            }.also { it.start() }
        }
    }

    fun getBtcPrice(): BigDecimal {
        return when (rootExchange) {
            ROOT_EXCHANGE_UPBIT -> {
                upBitExchange.getBtcPrice()
            }

            ROOT_EXCHANGE_BITTHUMB -> {
                upBitExchange.getBtcPrice()
            }

            else -> {
                upBitExchange.getBtcPrice()
            }
        }
    }

    fun updateTextFieldValue(value: String) {
        state.textFieldValue.value = value
    }

    companion object {
        const val ROOT_EXCHANGE_UPBIT = "upbit"
        const val ROOT_EXCHANGE_BITTHUMB = "bitthumb"

        const val TRADE_CURRENCY_KRW = 0
        const val TRADE_CURRENCY_BTC = 1
        const val TRADE_CURRENCY_FAV = 2
    }
}