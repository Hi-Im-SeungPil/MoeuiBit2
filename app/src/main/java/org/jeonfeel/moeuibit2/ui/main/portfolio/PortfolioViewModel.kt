package org.jeonfeel.moeuibit2.ui.main.portfolio

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.jeonfeel.moeuibit2.GlobalState
import org.jeonfeel.moeuibit2.constants.EXCHANGE_BITTHUMB
import org.jeonfeel.moeuibit2.constants.EXCHANGE_UPBIT
import org.jeonfeel.moeuibit2.constants.defaultDispatcher
import org.jeonfeel.moeuibit2.constants.ioDispatcher
import org.jeonfeel.moeuibit2.data.local.preferences.PreferencesManager
import org.jeonfeel.moeuibit2.data.local.room.entity.MyCoin
import org.jeonfeel.moeuibit2.data.network.retrofit.response.upbit.UpbitMarketCodeRes
import org.jeonfeel.moeuibit2.ui.base.BaseViewModel
import org.jeonfeel.moeuibit2.ui.main.portfolio.dto.UserHoldCoinDTO
import org.jeonfeel.moeuibit2.ui.main.portfolio.root_exchange.BiThumbPortfolio
import org.jeonfeel.moeuibit2.ui.main.portfolio.root_exchange.UpBitPortfolio
import org.jeonfeel.moeuibit2.utils.manager.AdMobManager
import org.jeonfeel.moeuibit2.utils.manager.CacheManager
import java.math.BigDecimal
import javax.inject.Inject

@HiltViewModel
class PortfolioViewModel @Inject constructor(
    preferenceManager: PreferencesManager,
    private val upBitPortfolio: UpBitPortfolio,
    private val biThumbPortfolio: BiThumbPortfolio
    val adMobManager: AdMobManager,
) : BaseViewModel(preferenceManager) {

    private val rootExchange = GlobalState.globalExchangeState.value

    val userSeedMoney
        get() = run {
            when (rootExchange) {
                EXCHANGE_UPBIT -> {
                    upBitPortfolio.userSeedMoney
                }

                EXCHANGE_BITTHUMB -> {
                    biThumbPortfolio.userSeedMoney
                }

                else -> {
                    upBitPortfolio.userSeedMoney
                }
            }
        }

    val totalPurchase: State<BigDecimal>
        get() = run {
            when (rootExchange) {
                EXCHANGE_UPBIT -> {
                    upBitPortfolio.totalPurchase
                }

                EXCHANGE_BITTHUMB -> {
                    biThumbPortfolio.totalPurchase
                }

                else -> {
                    upBitPortfolio.totalPurchase
                }
            }
        }

    val userHoldCoinDtoList: State<List<UserHoldCoinDTO>>
        get() = run {
            when (rootExchange) {
                EXCHANGE_UPBIT -> {
                    upBitPortfolio.userHoldCoinDtoListState
                }

                EXCHANGE_BITTHUMB -> {
                    biThumbPortfolio.userHoldCoinDtoListState
                }

                else -> {
                    upBitPortfolio.userHoldCoinDtoListState
                }
            }
        }

    val totalValuedAssets: State<BigDecimal>
        get() = run {
            when (rootExchange) {
                EXCHANGE_UPBIT -> {
                    upBitPortfolio.totalValuedAssets
                }

                EXCHANGE_BITTHUMB -> {
                    biThumbPortfolio.totalValuedAssets
                }

                else -> {
                    upBitPortfolio.totalValuedAssets
                }
            }
        }

    val portfolioOrderState: State<Int>
        get() = run {
            when (rootExchange) {
                EXCHANGE_UPBIT -> {
                    upBitPortfolio.portfolioOrderState
                }

                EXCHANGE_BITTHUMB -> {
                    biThumbPortfolio.portfolioOrderState
                }

                else -> {
                    upBitPortfolio.portfolioOrderState
                }
            }
        }

    val btcTradePrice: State<Double>
        get() = run {
            when (rootExchange) {
                EXCHANGE_UPBIT -> {
                    upBitPortfolio.btcTradePrice
                }

                EXCHANGE_BITTHUMB -> {
                    biThumbPortfolio.btcTradePrice
                }

                else -> {
                    upBitPortfolio.btcTradePrice
                }
            }
        }

    val removeCoinInfo: List<Pair<String, String>>
        get() = run {
            when (rootExchange) {
                EXCHANGE_UPBIT -> {
                    upBitPortfolio.removeCoinInfo
                }

                EXCHANGE_BITTHUMB -> {
                    biThumbPortfolio.removeCoinInfo
                }

                else -> {
                    upBitPortfolio.removeCoinInfo
                }
            }
        }

    val removeCoinCheckedState: List<Boolean>
        get() = run {
            when (rootExchange) {
                EXCHANGE_UPBIT -> {
                    upBitPortfolio.removeCoinCheckedState
                }

                EXCHANGE_BITTHUMB -> {
                    biThumbPortfolio.removeCoinCheckedState
                }

                else -> {
                    upBitPortfolio.removeCoinCheckedState
                }
            }
        }

    val showRemoveCoinDialogState: State<Boolean>
        get() = run {
            when (rootExchange) {
                EXCHANGE_UPBIT -> {
                    upBitPortfolio.showRemoveCoinDialogState
                }

                EXCHANGE_BITTHUMB -> {
                    biThumbPortfolio.showRemoveCoinDialogState
                }

                else -> {
                    upBitPortfolio.showRemoveCoinDialogState
                }
            }
        }

    val loading: StateFlow<Boolean>
        get() = run {
            when (rootExchange) {
                EXCHANGE_UPBIT -> {
                    upBitPortfolio.loading
                }

                EXCHANGE_BITTHUMB -> {
                    biThumbPortfolio.loading
                }

                else -> {
                    upBitPortfolio.loading
                }
            }
        }

    val portfolioSearchTextState: MutableState<String> = mutableStateOf("")
    private var realTimeUpdateJob: Job? = null
    private var collectTickerJob: Job? = null
    var isStarted = false

    fun onStart() {
        realTimeUpdateJob?.cancel()
        collectTickerJob?.cancel()
        isStarted = true

        realTimeUpdateJob = viewModelScope.launch(ioDispatcher) {
            when (rootExchange) {
                EXCHANGE_UPBIT -> {
                    upBitPortfolio.onStart()
                }

                EXCHANGE_BITTHUMB -> {
                    biThumbPortfolio.onStart()
                }

                else -> {

                }
            }
        }.also { it.start() }

        collectTickerJob = viewModelScope.launch {
            when (rootExchange) {
                EXCHANGE_UPBIT -> {
                    upBitPortfolio.collectTicker()
                }

                EXCHANGE_BITTHUMB -> {
                    biThumbPortfolio.collectTicker()
                }

                else -> {

                }
            }
        }.also { it.start() }
    }

    fun onStop() {
        isStarted = false
        viewModelScope.launch {
            when (rootExchange) {
                EXCHANGE_UPBIT -> {
                    upBitPortfolio.portfolioStop()
                }

                EXCHANGE_BITTHUMB -> {
                    biThumbPortfolio.portfolioStop()
                }

                else -> {

                }
            }
            realTimeUpdateJob?.cancel()
            collectTickerJob?.cancel()
            realTimeUpdateJob = null
            collectTickerJob = null
            when (rootExchange) {
                EXCHANGE_UPBIT -> {
                    upBitPortfolio.onStop()
                }

                EXCHANGE_BITTHUMB -> {
                    biThumbPortfolio.onStop()
                }

                else -> {

                }
            }
        }
    }

    fun sortUserHoldCoin(sortStandard: Int) {
        viewModelScope.launch(defaultDispatcher) {
            when (rootExchange) {
                EXCHANGE_UPBIT -> {
                    upBitPortfolio.sortUserHoldCoin(sortStandard)
                }

                EXCHANGE_BITTHUMB -> {
                    biThumbPortfolio.sortUserHoldCoin(sortStandard)
                }

                else -> {

                }
            }
        }
    }

    fun hideBottomSheet() {
        when (rootExchange) {
            EXCHANGE_UPBIT -> {
                upBitPortfolio.hideBottomSheet()
            }

            EXCHANGE_BITTHUMB -> {
                biThumbPortfolio.hideBottomSheet()
            }

            else -> {

            }
        }
    }

    fun updateRemoveCoinCheckState(index: Int = 0) { //-1 은 selectAll
        when (rootExchange) {
            EXCHANGE_UPBIT -> {
                upBitPortfolio.updateRemoveCoinCheckState(index)
            }

            EXCHANGE_BITTHUMB -> {
                biThumbPortfolio.updateRemoveCoinCheckState(index)
            }

            else -> {

            }
        }
    }

    fun findWrongCoin() {
        viewModelScope.launch {
            when (rootExchange) {
                EXCHANGE_UPBIT -> {
                    upBitPortfolio.findWrongCoin()
                }

                EXCHANGE_BITTHUMB -> {
                    biThumbPortfolio.findWrongCoin()
                }

                else -> {

                }
            }
        }
    }

    fun editUserHoldCoin() {
        viewModelScope.launch(ioDispatcher) {
            when (rootExchange) {
                EXCHANGE_UPBIT -> {
                    upBitPortfolio.editUserHoldCoin()
                }

                EXCHANGE_BITTHUMB -> {
                    biThumbPortfolio.editUserHoldCoin()
                }

                else -> {

                }
            }
        }
    }

    fun earnReward() {
        viewModelScope.launch(ioDispatcher) {
            when (rootExchange) {
                EXCHANGE_UPBIT -> {
                    upBitPortfolio.earnReward()
                }

                EXCHANGE_BITTHUMB -> {
                    biThumbPortfolio.earnReward()
                }

                else -> {

                }
            }
        }
    }

    fun errorReward() {
        viewModelScope.launch(ioDispatcher) {
            when (rootExchange) {
                EXCHANGE_UPBIT -> {
                    upBitPortfolio.errorReward()
                }

                EXCHANGE_BITTHUMB -> {
                    biThumbPortfolio.errorReward()
                }

                else -> {

                }
            }
        }
    }

    companion object {
        const val SORT_NONE = -2
        const val SORT_DEFAULT = -1
        const val SORT_NAME_DEC = 0
        const val SORT_NAME_ASC = 1
        const val SORT_RATE_DEC = 2
        const val SORT_RATE_ASC = 3
    }
}