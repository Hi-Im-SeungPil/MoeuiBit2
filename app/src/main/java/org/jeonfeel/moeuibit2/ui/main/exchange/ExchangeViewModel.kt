package org.jeonfeel.moeuibit2.ui.main.exchange

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.launch
import org.jeonfeel.moeuibit2.data.network.retrofit.model.upbit.CommonExchangeModel
import org.jeonfeel.moeuibit2.data.repository.local.LocalRepository
import org.jeonfeel.moeuibit2.ui.base.BaseViewModel
import org.jeonfeel.moeuibit2.ui.main.exchange.ExchangeViewModel.Companion.TRADE_CURRENCY_KRW
import org.jeonfeel.moeuibit2.ui.main.exchange.component.SortOrder
import org.jeonfeel.moeuibit2.ui.main.exchange.component.SortType
import org.jeonfeel.moeuibit2.ui.main.exchange.root_exchange.UpBit
import org.jeonfeel.moeuibit2.ui.main.exchange.root_exchange.ExchangeInitState
import org.jeonfeel.moeuibit2.utils.manager.PreferencesManager
import java.math.BigDecimal
import javax.inject.Inject

enum class TickerAskBidState {
    ASK, BID, NONE
}

class ExchangeViewModelState {
    val isUpdateExchange = mutableStateOf(true)
    val tradeCurrencyState = mutableIntStateOf(TRADE_CURRENCY_KRW)
}

@HiltViewModel
class ExchangeViewModel @Inject constructor(
    private val upBit: UpBit,
    private val localRepository: LocalRepository,
    private val preferenceManager: PreferencesManager
) : BaseViewModel(preferenceManager) {
    private val state = ExchangeViewModelState()
    val isUpdateExchange: State<Boolean> get() = state.isUpdateExchange
    val tradeCurrencyState: State<Int> get() = state.tradeCurrencyState

    private var realTimeUpdateJob: Job? = null
    private var marketChangeJob: Job? = null

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

    fun onPause() {
        rootExchangeCoroutineBranch(
            upbitAction = {
                upBit.onPause()
                realTimeUpdateJob?.cancelAndJoin()
            },
            bitthumbAction = {

            }
        )
    }

    fun onResume() {
        realTimeUpdateJob = viewModelScope.launch {
            when (rootExchange) {
                ROOT_EXCHANGE_UPBIT -> {
                    upBit.onResume()
                }

                ROOT_EXCHANGE_BITTHUMB -> {

                }
            }
        }.also { it.start() }
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

    fun sortTickerList(targetTradeCurrency: Int? = null, sortType: SortType, sortOrder: SortOrder) {
        state.isUpdateExchange.value = false
        upBit.sortTickerList(
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
            marketChangeJob = viewModelScope.launch {
                when (rootExchange) {
                    ROOT_EXCHANGE_UPBIT -> {
                        upBit.changeTradeCurrencyAction()
                    }

                    ROOT_EXCHANGE_BITTHUMB -> {
                        upBit.changeTradeCurrencyAction()
                    }

                    else -> {
                        upBit.changeTradeCurrencyAction()
                    }
                }
            }.also { it.start() }
        }
    }

    fun addFavorite(market: String) {
        viewModelScope.launch {
            when (rootExchange) {
                ROOT_EXCHANGE_UPBIT -> {
                    upBit.addFavorite(market)
                }

                ROOT_EXCHANGE_BITTHUMB -> {
                    upBit.addFavorite(market)
                }

                else -> {
                    upBit.addFavorite(market)
                }
            }
        }
    }

    fun removeFavorite(market: String) {
        viewModelScope.launch {
            when (rootExchange) {
                ROOT_EXCHANGE_UPBIT -> {
                    upBit.removeFavorite(market)
                }

                ROOT_EXCHANGE_BITTHUMB -> {
                    upBit.removeFavorite(market)
                }

                else -> {
                    upBit.removeFavorite(market)
                }
            }
        }
    }

    fun getBtcPrice(): BigDecimal {
        return when (rootExchange) {
            ROOT_EXCHANGE_UPBIT -> {
                upBit.getBtcPrice()
            }

            ROOT_EXCHANGE_BITTHUMB -> {
                upBit.getBtcPrice()
            }

            else -> {
                upBit.getBtcPrice()
            }
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