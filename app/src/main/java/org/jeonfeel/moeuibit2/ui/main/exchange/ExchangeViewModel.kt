package org.jeonfeel.moeuibit2.ui.main.exchange

import androidx.compose.runtime.MutableIntState
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.viewModelScope
import com.orhanobut.logger.Logger
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import org.jeonfeel.moeuibit2.constants.*
import org.jeonfeel.moeuibit2.data.remote.retrofit.model.upbit.CommonExchangeModel
import org.jeonfeel.moeuibit2.data.remote.websocket.UpBitOrderBookWebSocket
import org.jeonfeel.moeuibit2.data.remote.websocket.UpBitTickerWebSocket
import org.jeonfeel.moeuibit2.data.repository.local.LocalRepository
import org.jeonfeel.moeuibit2.data.repository.remote.RemoteRepository
import org.jeonfeel.moeuibit2.ui.base.BaseViewModel
import org.jeonfeel.moeuibit2.ui.main.exchange.root_exchange.BaseRootExchange
import org.jeonfeel.moeuibit2.ui.main.exchange.root_exchange.BitThumb
import org.jeonfeel.moeuibit2.ui.main.exchange.root_exchange.UpBit
import org.jeonfeel.moeuibit2.utils.NetworkMonitorUtil
import org.jeonfeel.moeuibit2.utils.manager.PreferenceManager
import javax.inject.Inject

class ExchangeViewModelState {
    val _loadingFavorite = mutableStateOf(true)
    val _loadingExchange = mutableStateOf(true)
    val _krwExchangeModelMutableStateList = mutableStateListOf<CommonExchangeModel>()
    val _btcExchangeModelMutableStateList = mutableStateListOf<CommonExchangeModel>()
    val _favoriteExchangeModelMutableStateList = mutableStateListOf<CommonExchangeModel>()
    val _selectedMarketState = mutableIntStateOf(SELECTED_KRW_MARKET)
    val _isUpdateExchange = mutableStateOf(false)
    val _currentRootExchange = mutableStateOf("")
}

@HiltViewModel
class ExchangeViewModel @Inject constructor(
    private val remoteRepository: RemoteRepository,
    private val localRepository: LocalRepository,
    private val preferenceManager: PreferenceManager
) : BaseViewModel() {
    private val state = ExchangeViewModelState()
    val isUpdateExchange: State<Boolean> get() = state._isUpdateExchange
    val loadingFavorite: State<Boolean> get() = state._loadingFavorite
    val loadingExchange: State<Boolean> get() = state._loadingExchange
    val selectedMarketState: State<Int> get() = state._selectedMarketState
    val currentRootExchange: State<String> get() = state._currentRootExchange
    private val krwExchangeModelMutableStateList: List<CommonExchangeModel> get() = state._krwExchangeModelMutableStateList
    private val btcExchangeModelMutableStateList: List<CommonExchangeModel> get() = state._btcExchangeModelMutableStateList
    private val favoriteExchangeModelMutableStateList: List<CommonExchangeModel> get() = state._favoriteExchangeModelMutableStateList

    private val upBit = UpBit(
        remoteRepository = remoteRepository,
        localRepository = localRepository,
        gson = gson,
        changeNetworkState = ::changeNetworkErrorState,
        selectedMarketState = selectedMarketState,
        isUpdateExchange = isUpdateExchange,
        swapList = ::swapList,
        changeIsUpdate = ::changeIsUpdate,
        removeStateList = ::removeStateList,
        updateExchange = ::updateExchange
    )
    private val bitThumb = BitThumb(
        remoteRepository = remoteRepository,
        localRepository = localRepository,
        gson = gson,
        changeNetworkState = ::changeNetworkErrorState,
        selectedMarketState = selectedMarketState,
        isUpdateExchange = isUpdateExchange,
        swapList = ::swapList,
        changeIsUpdate = ::changeIsUpdate,
        removeStateList = ::removeStateList,
        updateExchange = ::updateExchange
    )
    private var rootExchange: BaseRootExchange = bitThumb

    private var exchangeUpdateJob: Job? = null

    /**
     * 거래소 데이터 초기화
     */
    fun initExchangeData() {
        viewModelScope.launch {
            state._loadingExchange.value = true
            val rootExchangeValue = preferenceManager.getString(PREF_KEY_ROOT_EXCHANGE)
            rootExchange = if (rootExchangeValue == ROOT_EXCHANGE_UPBIT
                || rootExchangeValue == "-999"
            ) {
//                upBit
                bitThumb
            } else {
                bitThumb
            }
            requestExchangeData()
//            updateExchange()
            state._loadingExchange.value = false
        }
    }

    /**
     * 거래소 데이터 요청
     */
    private suspend fun requestExchangeData() {
        state._loadingExchange.value = true
        when (NetworkMonitorUtil.currentNetworkState) {
            INTERNET_CONNECTION -> {
                when (rootExchange) {
                    is UpBit -> {
                        (rootExchange as UpBit).initUpBitData()
                    }

                    is BitThumb -> {
                        (rootExchange as BitThumb).initBitThumbData()
                    }
                }
                _networkErrorState.value = INTERNET_CONNECTION
                state._loadingExchange.value = false
            }

            else -> {
                state._loadingExchange.value = false
                state._isUpdateExchange.value = false
                _networkErrorState.value = NetworkMonitorUtil.currentNetworkState
            }
        }
    }

    /**
     * 거래소 화면 업데이트
     */
    private fun updateExchange() {
        if (!isUpdateExchange.value) state._isUpdateExchange.value = true
        if (exchangeUpdateJob == null) {
            exchangeUpdateJob = viewModelScope.launch {
                while (true) {
                    when (selectedMarketState.value) {
                        SELECTED_KRW_MARKET -> {
                            swapList(SELECTED_KRW_MARKET)
                        }

                        SELECTED_BTC_MARKET -> {
                            swapList(SELECTED_BTC_MARKET)
                        }

                        SELECTED_FAVORITE -> {
                            swapList(SELECTED_FAVORITE)
                        }
                    }
                    delay(300)
                }
            }
            exchangeUpdateJob?.start()
        }
    }

    /**
     * 사용자 관심코인 변경사항 업데이트
     */
    fun updateFavorite(
        market: String = "",
        isFavorite: Boolean = false,
    ) {
        viewModelScope.launch(ioDispatcher) {
            when (rootExchange) {
                is UpBit -> {
                    (rootExchange as UpBit).updateFavorite(
                        market = market,
                        isFavorite = isFavorite
                    )
                }

                is BitThumb -> {

                }
            }
        }
    }

    fun marketChangeAction(
        marketState: Int,
        sortButtonState: MutableIntState
    ) {
        if (UpBitTickerWebSocket.currentMarket != marketState) {
            if (marketState == SELECTED_FAVORITE) {
                state._favoriteExchangeModelMutableStateList.clear()
            }
            when (rootExchange) {
                is UpBit -> {
                    (rootExchange as UpBit).marketChangeAction(
                        marketState = marketState,
                        sortButtonState = sortButtonState,
                        viewModelScope
                    )
                }

                is BitThumb -> {
//                    (rootExchange as BitThumb).marketChangeAction(
//                        marketState = marketState,
//                        sortButtonState = sortButtonState,
//                        viewModelScope
//                    )
                }
            }
        }

    }

    fun sortList(
        marketState: Int,
        sortButtonState: MutableIntState
    ) {
        if (isUpdateExchange.value || selectedMarketState.value == SELECTED_FAVORITE) {
            Logger.e("init")
            state._isUpdateExchange.value = false
            viewModelScope.launch(defaultDispatcher) {
                Logger.e("init2")
                state._selectedMarketState.intValue = marketState
                when (rootExchange) {
                    is UpBit -> {
                        Logger.e("init3")
                        (rootExchange as UpBit).sortList(
                            sortButtonState = sortButtonState
                        )
                    }

                    is BitThumb -> {

                    }
                }
                state._isUpdateExchange.value = true
            }
        }
    }

    fun getFilteredCoinList(
        textFieldValueState: MutableState<String>,
    ): List<CommonExchangeModel> {
        return when { //검색 X 관심코인 X
            textFieldValueState.value.isEmpty() -> {
                when (selectedMarketState.value) {
                    SELECTED_KRW_MARKET -> {
                        krwExchangeModelMutableStateList
                    }

                    SELECTED_BTC_MARKET -> {
                        btcExchangeModelMutableStateList
                    }

                    else -> {
                        favoriteExchangeModelMutableStateList
                    }
                }
            }

            else -> {
                val resultList = mutableStateListOf<CommonExchangeModel>()
                val targetList = when (selectedMarketState.value) {
                    SELECTED_KRW_MARKET -> {
                        krwExchangeModelMutableStateList
                    }

                    SELECTED_BTC_MARKET -> {
                        btcExchangeModelMutableStateList
                    }

                    else -> {
                        favoriteExchangeModelMutableStateList
                    }
                }
                for (element in targetList) {
                    if (element.koreanName.contains(textFieldValueState.value) || element.englishName.uppercase()
                            .contains(textFieldValueState.value.uppercase()) || element.symbol.uppercase()
                            .contains(textFieldValueState.value.uppercase())
                    ) {
                        resultList.add(element)
                    }
                }
                resultList
            }
        }
    }

    fun checkErrorScreen() {
        val preItemList = when (rootExchange) {
            is UpBit -> {
                (rootExchange as UpBit).preItemList()
            }

            is BitThumb -> {
//                (rootExchange as BitThumb).preItemList()
                Pair(emptyList<CommonExchangeModel>(), emptyList<CommonExchangeModel>())
            }

            else -> {
                Pair(emptyList<CommonExchangeModel>(), emptyList<CommonExchangeModel>())
            }
        }
        if (preItemList.first.isEmpty() || preItemList.second.isEmpty()) {
            initExchangeData()
        } else if (NetworkMonitorUtil.currentNetworkState == INTERNET_CONNECTION || NetworkMonitorUtil.currentNetworkState == NETWORK_ERROR) {
            _networkErrorState.value = NetworkMonitorUtil.currentNetworkState
            UpBitTickerWebSocket.onlyRebuildSocket()
            UpBitOrderBookWebSocket.onlyRebuildSocket()
            initExchangeData()
        }
    }

    fun getPreCoinListAndPosition(): Pair<ArrayList<CommonExchangeModel>, HashMap<String, Int>> {
        return when (rootExchange) {
            is UpBit -> {
                (rootExchange as UpBit).getPreCoinListAndPosition(marketState = selectedMarketState.value)
            }

            is BitThumb -> {
                (rootExchange as BitThumb).getPreCoinListAndPosition(marketState = selectedMarketState.value)
            }

            else -> {
                (rootExchange as UpBit).getPreCoinListAndPosition(marketState = selectedMarketState.value)
            }
        }
    }

    fun getFavoriteLoadingState(): MutableState<Boolean>? {
        return if (selectedMarketState.value == SELECTED_FAVORITE) {
            state._loadingFavorite
        } else {
            null
        }
    }

    private fun changeIsUpdate(isUpdate: Boolean) {
        state._isUpdateExchange.value = isUpdate
    }

    private fun removeStateList(index: Int) {
        state._favoriteExchangeModelMutableStateList.removeAt(index)
    }

    fun getBtcPrice(): State<Double> {
        return when (rootExchange) {
            is UpBit -> {
                Logger.e((rootExchange as UpBit).getBtcPrice().toString())
                (rootExchange as UpBit).getBtcPrice()
            }

            is BitThumb -> {
                mutableDoubleStateOf(0.0)
            }

            else -> {
                mutableDoubleStateOf(0.0)
            }
        }
    }

    fun changeSelectedMarketState(value: Int) {
        state._selectedMarketState.intValue = value
    }

    /**
     * btc, krw, 관심 코인 목록 불러오기
     */
    private fun swapList(marketState: Int) {
        val tempList = when (rootExchange) {
            is UpBit -> {
                (rootExchange as UpBit).getSwapTempList(marketState)
            }

            is BitThumb -> {
                (rootExchange as BitThumb).getSwapTempList(marketState)
            }

            else -> {
                emptyList()
            }
        }
        if (marketState == SELECTED_KRW_MARKET) {
            swapListAction(state._krwExchangeModelMutableStateList, tempList)
            return
        }

        if (marketState == SELECTED_BTC_MARKET) {
            swapListAction(state._btcExchangeModelMutableStateList, tempList)
            return
        }

        if (marketState == SELECTED_FAVORITE) {
            swapListAction(state._favoriteExchangeModelMutableStateList, tempList)
            return
        }
    }

    private fun swapListAction(
        targetList: SnapshotStateList<CommonExchangeModel>,
        fromList: List<CommonExchangeModel>
    ) {
        if (targetList.isEmpty()) {
            targetList.addAll(fromList)
        } else {
            for (i in fromList.indices) {
                targetList[i] = fromList[i]
            }
        }
    }

    fun stopExchangeUpdateCoroutine() {
        viewModelScope.launch {
            exchangeUpdateJob?.cancelAndJoin()
            exchangeUpdateJob = null
        }
    }

    fun updateIsExchangeUpdateState(value: Boolean) {
        state._isUpdateExchange.value = value
    }

    fun changeRootExchangeAction(rootExchange: String) {
        if (currentRootExchange.value != rootExchange) {
            state._currentRootExchange.value = rootExchange

            stopExchangeUpdateCoroutine()
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