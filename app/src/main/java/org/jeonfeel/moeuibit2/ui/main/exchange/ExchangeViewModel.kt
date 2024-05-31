package org.jeonfeel.moeuibit2.ui.main.exchange

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import dagger.hilt.android.lifecycle.HiltViewModel
import org.jeonfeel.moeuibit2.data.network.retrofit.model.upbit.CommonExchangeModel
import org.jeonfeel.moeuibit2.data.repository.local.LocalRepository
import org.jeonfeel.moeuibit2.ui.base.BaseViewModel
import org.jeonfeel.moeuibit2.ui.main.exchange.root_exchange.UpBit
import org.jeonfeel.moeuibit2.ui.main.exchange.root_exchange.ExchangeInitState
import org.jeonfeel.moeuibit2.utils.manager.PreferenceManager
import javax.inject.Inject

class ExchangeViewModelState {
    val krwExchangeModelMutableStateList = mutableStateListOf<CommonExchangeModel>()
    val btcExchangeModelMutableStateList = mutableStateListOf<CommonExchangeModel>()
    val favoriteExchangeModelMutableStateList = mutableStateListOf<CommonExchangeModel>()
    val isUpdateExchange = mutableStateOf(false)
}

@HiltViewModel
class ExchangeViewModel @Inject constructor(
    private val upBit: UpBit,
    private val localRepository: LocalRepository,
    private val preferenceManager: PreferenceManager
) : BaseViewModel(preferenceManager) {
    private val state = ExchangeViewModelState()
    val isUpdateExchange: State<Boolean> get() = state.isUpdateExchange
    private val krwExchangeModelStateList: List<CommonExchangeModel> get() = state.krwExchangeModelMutableStateList
    private val btcExchangeModelStateList: List<CommonExchangeModel> get() = state.btcExchangeModelMutableStateList
    private val favoriteExchangeModelStateList: List<CommonExchangeModel> get() = state.favoriteExchangeModelMutableStateList

    init {
        rootExchangeCoroutineBranch(
            upbitAction = {
                upBit.initUpBit().collect { upBitInitState ->
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

            is ExchangeInitState.Wait -> {

            }

            is ExchangeInitState.Error -> {
                // 에러 처리
            }
        }
    }

    fun getTickerList(): List<CommonExchangeModel> {
        return when (rootExchange) {
            ROOT_EXCHANGE_UPBIT -> {
                upBit.krwExchangeModelList
            }
            ROOT_EXCHANGE_BITTHUMB -> {
                upBit.krwExchangeModelList
            }
            else -> {
                upBit.krwExchangeModelList
            }
        }
    }

    companion object {
        const val SORT_DEFAULT = -1
        const val SORT_PRICE_DEC = 0
        const val SORT_PRICE_ASC = 1
        const val SORT_RATE_DEC = 2
        const val SORT_RATE_ASC = 3
        const val SORT_AMOUNT_DEC = 4
        const val SORT_AMOUNT_ASC = 5

        const val ROOT_EXCHANGE_UPBIT = "upbit"
        const val ROOT_EXCHANGE_BITTHUMB = "bitthumb"
    }
}