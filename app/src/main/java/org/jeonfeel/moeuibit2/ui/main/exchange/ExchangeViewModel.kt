package org.jeonfeel.moeuibit2.ui.main.exchange

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import org.jeonfeel.moeuibit2.data.network.retrofit.model.upbit.CommonExchangeModel
import org.jeonfeel.moeuibit2.data.repository.local.LocalRepository
import org.jeonfeel.moeuibit2.ui.base.BaseViewModel
import org.jeonfeel.moeuibit2.ui.main.exchange.ExchangeViewModel.Companion.TRADE_CURRENCY_KRW
import org.jeonfeel.moeuibit2.ui.main.exchange.component.SortOrder
import org.jeonfeel.moeuibit2.ui.main.exchange.component.SortType
import org.jeonfeel.moeuibit2.ui.main.exchange.root_exchange.UpBit
import org.jeonfeel.moeuibit2.ui.main.exchange.root_exchange.ExchangeInitState
import org.jeonfeel.moeuibit2.utils.manager.PreferenceManager
import javax.inject.Inject

//enum class TradeCurrency {
//    KRW, BTC, FAV
//}

enum class TradeCurrency {
    KRW, BTC, FAV
}

class ExchangeViewModelState {
    val isUpdateExchange = mutableStateOf(true)
    val tradeCurrencyState = mutableStateOf(TRADE_CURRENCY_KRW)
}

@HiltViewModel
class ExchangeViewModel @Inject constructor(
    private val upBit: UpBit,
    private val localRepository: LocalRepository,
    private val preferenceManager: PreferenceManager
) : BaseViewModel(preferenceManager) {
    private val state = ExchangeViewModelState()
    val isUpdateExchange: State<Boolean> get() = state.isUpdateExchange
    val tradeCurrencyState: State<Int> get() = state.tradeCurrencyState

    init {
        rootExchangeCoroutineBranch(
            upbitAction = {
                upBit.initUpBit(
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

            }

            is ExchangeInitState.Error -> {
                // 에러 처리
            }

            is ExchangeInitState.Wait -> {

            }
        }
    }

    fun getTickerList(): List<CommonExchangeModel> {
        return when (rootExchange) {
            ROOT_EXCHANGE_UPBIT -> {
                upBit.getExchangeModelList(tradeCurrencyState)
            }

            ROOT_EXCHANGE_BITTHUMB -> {
                upBit.getExchangeModelList(tradeCurrencyState)
            }

            else -> {
                upBit.getExchangeModelList(tradeCurrencyState)
            }
        }
    }

    fun sortTickerList(sortType: SortType, sortOrder: SortOrder) {
        state.isUpdateExchange.value = false
        upBit.sortTickerList(
            tradeCurrencyState = tradeCurrencyState,
            sortType = sortType,
            sortOrder = sortOrder
        )
        state.isUpdateExchange.value = true
    }

    fun changeTradeCurrency(tradeCurrency: Int) {
        state.tradeCurrencyState.value = tradeCurrency
    }

    fun onPause() {
        viewModelScope.launch {
            upBit.onPause()
        }
    }

    fun onResume() {
        viewModelScope.launch {
            upBit.onResume()
        }
    }

    companion object {
        const val ROOT_EXCHANGE_UPBIT = "upbit"
        const val ROOT_EXCHANGE_BITTHUMB = "bitthumb"

        const val TRADE_CURRENCY_KRW = 0
        const val TRADE_CURRENCY_BTC = 1
        const val TRADE_CURRENCY_FAV = 2
    }
}