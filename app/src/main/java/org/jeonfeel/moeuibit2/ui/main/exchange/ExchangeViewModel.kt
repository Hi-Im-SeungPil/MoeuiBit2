package org.jeonfeel.moeuibit2.ui.main.exchange

import androidx.annotation.Keep
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import org.jeonfeel.moeuibit2.GlobalState
import org.jeonfeel.moeuibit2.constants.EXCHANGE_BITTHUMB
import org.jeonfeel.moeuibit2.constants.EXCHANGE_UPBIT
import org.jeonfeel.moeuibit2.constants.ioDispatcher
import org.jeonfeel.moeuibit2.data.local.preferences.PreferencesManager
import org.jeonfeel.moeuibit2.data.network.retrofit.model.upbit.CommonExchangeModel
import org.jeonfeel.moeuibit2.ui.base.BaseViewModel
import org.jeonfeel.moeuibit2.ui.main.exchange.ExchangeViewModel.Companion.TRADE_CURRENCY_KRW
import org.jeonfeel.moeuibit2.ui.main.exchange.component.SortOrder
import org.jeonfeel.moeuibit2.ui.main.exchange.component.SortType
import org.jeonfeel.moeuibit2.ui.main.exchange.root_exchange.BitThumbExchange
import org.jeonfeel.moeuibit2.ui.main.exchange.root_exchange.UpBitExchange
import java.math.BigDecimal
import javax.inject.Inject

@Keep
enum class TickerAskBidState {
    ASK, BID, NONE
}

@Keep
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
    private val bitThumbExchange: BitThumbExchange,
    private val preferenceManager: PreferencesManager,
) : BaseViewModel(preferenceManager) {

    private val state = ExchangeViewModelState()

    val isUpdateExchange: State<Boolean> get() = state.isUpdateExchange
    val tradeCurrencyState: State<Int> get() = state.tradeCurrencyState
    val selectedSortType: State<SortType> get() = state.selectedSortType
    val sortOrder: State<SortOrder> get() = state.sortOrder
    val textFieldValue: State<String> get() = state.textFieldValue
    var isStarted = false
        private set

    private var realTimeUpdateJob: Job? = null
    private var collectTickerJob: Job? = null
    private var marketChangeJob: Job? = null

    init {
        upBitExchange.initUpBit(
            tradeCurrencyState = tradeCurrencyState,
            isUpdateExchange = isUpdateExchange
        )

        bitThumbExchange.initBitThumb(
            tradeCurrencyState = tradeCurrencyState,
            isUpdateExchange = isUpdateExchange
        )
    }

    fun onStart() {
        realTimeUpdateJob?.cancel()
        collectTickerJob?.cancel()
        isStarted = true

        realTimeUpdateJob = viewModelScope.launch(ioDispatcher) {
            when (GlobalState.globalExchangeState.value) {
                EXCHANGE_UPBIT -> {
                    upBitExchange.onStart(
                        updateLoadingState = ::updateLoadingState
                    )
                }

                EXCHANGE_BITTHUMB -> {
                    bitThumbExchange.onStart()
                }
            }
        }.also { it.start() }

        collectTickerJob = viewModelScope.launch {
            when (GlobalState.globalExchangeState.value) {
                EXCHANGE_UPBIT -> {
                    upBitExchange.exchangeCollectTicker()
                }

                EXCHANGE_BITTHUMB -> {
                    bitThumbExchange.collectTicker2()
                }
            }
        }.also { it.start() }
    }

    fun onStop() {
        isStarted = false
        viewModelScope.launch {
            when (GlobalState.globalExchangeState.value) {
                EXCHANGE_UPBIT -> {
                    upBitExchange.onStop()
                    collectTickerJob?.cancel()
                    realTimeUpdateJob?.cancel()
                    collectTickerJob = null
                    realTimeUpdateJob = null
                }

                EXCHANGE_BITTHUMB -> {

                }
            }
        }
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
        return when (GlobalState.globalExchangeState.value) {
            EXCHANGE_UPBIT -> {
                upBitExchange.getExchangeModelList(tradeCurrencyState)
            }

            EXCHANGE_BITTHUMB -> {
                bitThumbExchange.getExchangeModelList(tradeCurrencyState)
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
                when (GlobalState.globalExchangeState.value) {
                    EXCHANGE_UPBIT -> {
                        upBitExchange.changeTradeCurrencyAction(
                            sortType = selectedSortType.value,
                            sortOrder = sortOrder.value
                        )
                    }

                    EXCHANGE_BITTHUMB -> {
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
        return when (GlobalState.globalExchangeState.value) {
            EXCHANGE_UPBIT -> {
                upBitExchange.getBtcPrice()
            }

            EXCHANGE_BITTHUMB -> {
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
        const val TRADE_CURRENCY_KRW = 0
        const val TRADE_CURRENCY_BTC = 1
        const val TRADE_CURRENCY_FAV = 2
    }
}