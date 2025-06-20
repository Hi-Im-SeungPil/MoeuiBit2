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
import org.jeonfeel.moeuibit2.ui.main.portfolio.root_exchange.UpBitPortfolio
import org.jeonfeel.moeuibit2.utils.manager.AdMobManager
import org.jeonfeel.moeuibit2.utils.manager.CacheManager
import java.math.BigDecimal
import javax.inject.Inject

@HiltViewModel
class PortfolioViewModel @Inject constructor(
    private val preferenceManager: PreferencesManager,
    private val upBitPortfolio: UpBitPortfolio,
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
                    upBitPortfolio.userSeedMoney
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
                    upBitPortfolio.totalPurchase
                }

                else -> {
                    upBitPortfolio.totalPurchase
                }
            }
        }

    val userHoldCoinDtoList: State<List<UserHoldCoinDTO>> get() = run {
        when (rootExchange) {
            EXCHANGE_UPBIT -> {
                upBitPortfolio.userHoldCoinDtoListState
            }

            EXCHANGE_BITTHUMB -> {
                upBitPortfolio.userHoldCoinDtoListState
            }

            else -> {
                upBitPortfolio.userHoldCoinDtoListState
            }
        }
    }

    val marketCodeRes: Map<String, UpbitMarketCodeRes> get() = run {
        when (rootExchange) {
            EXCHANGE_UPBIT -> {
                upBitPortfolio.marketCodeRes
            }

            EXCHANGE_BITTHUMB -> {
                upBitPortfolio.marketCodeRes
            }

            else -> {
                upBitPortfolio.marketCodeRes
            }
        }
    }

    val totalValuedAssets: State<BigDecimal> get() = run {
        when (rootExchange) {
            EXCHANGE_UPBIT -> {
                upBitPortfolio.totalValuedAssets
            }

            EXCHANGE_BITTHUMB -> {
                upBitPortfolio.totalValuedAssets
            }

            else -> {
                upBitPortfolio.totalValuedAssets
            }
        }
    }

    val portfolioOrderState: State<Int> get() = run {
        when (rootExchange) {
            EXCHANGE_UPBIT -> {
                upBitPortfolio.portfolioOrderState
            }
            EXCHANGE_BITTHUMB -> {
                upBitPortfolio.portfolioOrderState
            }
            else -> {
                upBitPortfolio.portfolioOrderState
            }
        }
    }

    val portfolioSearchTextState: MutableState<String> = mutableStateOf("")

    private val _btcTradePrice = mutableDoubleStateOf(0.0)
    val btcTradePrice: State<Double> get() = _btcTradePrice

    private val _isPortfolioSocketRunning = mutableStateOf(true)
    private val isPortfolioSocketRunning: State<Boolean> get() = _isPortfolioSocketRunning

    private val _koreanCoinNameMap = mutableMapOf<String, String>()
    private val _engCoinNameMap = mutableMapOf<String, String>()

    private val userHoldCoinsMarkets = StringBuilder()
    private var myCoinList = ArrayList<MyCoin?>()

    private val myCoinHashMap = HashMap<String, MyCoin>()

    private val userHoldCoinDtoListPositionHashMap = HashMap<String, Int>()

    private val _removeCoinInfo = SnapshotStateList<Pair<String, String>>()
    val removeCoinInfo: List<Pair<String, String>> get() = _removeCoinInfo // market reason

    private val _removeCoinCheckedList = SnapshotStateList<Boolean>()
    val removeCoinCheckedState: List<Boolean> get() = _removeCoinCheckedList

    private val showRemoveCoinDialog = mutableStateOf(false)
    val showRemoveCoinDialogState: State<Boolean> get() = showRemoveCoinDialog

    private var realTimeUpdateJob: Job? = null
    private var collectTickerJob: Job? = null
    var isStarted = false

    val loading = MutableStateFlow<Boolean>(false)

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

                }

                else -> {

                }
            }
        }.also { it.start() }
    }

    fun onStop() {
        isStarted = false
        viewModelScope.launch {
            _isPortfolioSocketRunning.value = false
            realTimeUpdateJob?.cancel()
            collectTickerJob?.cancel()
            realTimeUpdateJob = null
            collectTickerJob = null
            when (rootExchange) {
                EXCHANGE_UPBIT -> {
                    upBitPortfolio.onStop()
                }

                EXCHANGE_BITTHUMB -> {

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