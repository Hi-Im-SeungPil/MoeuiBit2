package org.jeonfeel.moeuibit2.ui.main.exchange

import android.content.Context
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.PagerState
import com.google.accompanist.pager.rememberPagerState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import me.onebone.toolbar.CollapsingToolbarScaffoldState
import me.onebone.toolbar.ExperimentalToolbarApi
import me.onebone.toolbar.rememberCollapsingToolbarScaffoldState
import org.jeonfeel.moeuibit2.data.network.retrofit.model.upbit.CommonExchangeModel
import org.jeonfeel.moeuibit2.ui.main.exchange.ExchangeViewModel.Companion.TRADE_CURRENCY_FAV
import org.jeonfeel.moeuibit2.ui.main.exchange.ExchangeViewModel.Companion.TRADE_CURRENCY_KRW
import org.jeonfeel.moeuibit2.ui.main.exchange.component.SortOrder
import org.jeonfeel.moeuibit2.ui.main.exchange.component.SortType
import org.jeonfeel.moeuibit2.utils.Utils
import kotlin.reflect.KFunction1

class ExchangeStateHolder @OptIn(ExperimentalPagerApi::class) constructor(
    val pagerState: PagerState,
    val lazyScrollState: LazyListState,
    val context: Context,
    val isUpdateExchange: Boolean,
    val sortTickerList: (targetTradeCurrency: Int?, sortType: SortType, sortOrder: SortOrder) -> Unit,
    val focusManaManager: FocusManager,
    val coroutineScope: CoroutineScope,
    val toolbarState: CollapsingToolbarScaffoldState,
    val selectedSortType: State<SortType>,
    val sortOrder: State<SortOrder>,
    private val changeTradeCurrency: (tradeCurrency: Int) -> Unit,
    private val tradeCurrencyState: State<Int>,
    private val updateSortType: KFunction1<SortType, Unit>,
    private val updateSortOrder: KFunction1<SortOrder, Unit>,
    val textFieldValueState: State<String>,
) {

    fun getFilteredList(tickerList: List<CommonExchangeModel>): List<CommonExchangeModel> {
        val list = Utils.filterTickerList(
            exchangeModelList = tickerList,
            searchStr = textFieldValueState.value
        )
        return list
    }

    fun onSortClick(
        sortType: SortType,
        isChangeTradeCurrency: Boolean = false
    ) {
        if (!isUpdateExchange) return

        when {
            isChangeTradeCurrency -> {
                sortTickerList(null, this.selectedSortType.value, this.sortOrder.value)
                return
            }

            this.selectedSortType.value != sortType -> {
                updateSortType(sortType)
                updateSortOrder(SortOrder.DESCENDING)
            }

            else -> {
                when (this.sortOrder.value) {
                    SortOrder.DESCENDING -> {
                        updateSortOrder(SortOrder.ASCENDING)
                    }

                    SortOrder.ASCENDING -> {
                        updateSortType(SortType.DEFAULT)
                        updateSortOrder(SortOrder.NONE)
                    }

                    SortOrder.NONE -> {
                        updateSortOrder(SortOrder.DESCENDING)
                    }
                }
            }
        }

        sortTickerList(null, this.selectedSortType.value, this.sortOrder.value)
    }

    fun changeTradeCurrencyAction(tradeCurrency: Int) {
        sortTickerList(tradeCurrency, this.selectedSortType.value, this.sortOrder.value)
        coroutineScope.launch {
            lazyScrollState.scrollToItem(0)
        }
        changeTradeCurrency(tradeCurrency)
    }

    @OptIn(ExperimentalPagerApi::class, ExperimentalToolbarApi::class)
    fun coinTickerListSwipeAction(isSwipeLeft: Boolean) {
        if (isSwipeLeft) {
            coroutineScope.launch {
                if (tradeCurrencyState.value != TRADE_CURRENCY_KRW) {
                    changeTradeCurrency(tradeCurrencyState.value - 1)
                    onSortClick(sortType = selectedSortType.value, isChangeTradeCurrency = true)
                    toolbarState.toolbarState.expand(50)
                    lazyScrollState.scrollToItem(0)
                    pagerState.animateScrollToPage(tradeCurrencyState.value)
                }
            }
        } else {
            coroutineScope.launch {
                if (tradeCurrencyState.value != TRADE_CURRENCY_FAV) {
                    changeTradeCurrency(tradeCurrencyState.value + 1)
                    onSortClick(sortType = selectedSortType.value, isChangeTradeCurrency = true)
                    toolbarState.toolbarState.expand(50)
                    lazyScrollState.scrollToItem(0)
                    pagerState.animateScrollToPage(tradeCurrencyState.value)
                }
            }
        }
    }
}

@OptIn(ExperimentalPagerApi::class)
@Composable
fun rememberExchangeStateHolder(
    pagerState: PagerState = rememberPagerState(),
    listScrollState: LazyListState = rememberLazyListState(),
    context: Context = LocalContext.current,
    focusManaManager: FocusManager = LocalFocusManager.current,
    isUpdateExchange: Boolean,
    sortTickerList: (targetTradeCurrency: Int?, sortType: SortType, sortOrder: SortOrder) -> Unit,
    changeTradeCurrency: (tradeCurrency: Int) -> Unit,
    coroutineScope: CoroutineScope = rememberCoroutineScope(),
    selectedSortType: State<SortType>,
    sortOrder: State<SortOrder>,
    tradeCurrencyState: State<Int>,
    toolbarState: CollapsingToolbarScaffoldState = rememberCollapsingToolbarScaffoldState(),
    updateSortType: KFunction1<SortType, Unit>,
    updateSortOrder: KFunction1<SortOrder, Unit>,
    textFieldValueState: State<String>
) = remember {
    ExchangeStateHolder(
        pagerState = pagerState,
        lazyScrollState = listScrollState,
        context = context,
        isUpdateExchange = isUpdateExchange,
        sortTickerList = sortTickerList,
        focusManaManager = focusManaManager,
        changeTradeCurrency = changeTradeCurrency,
        coroutineScope = coroutineScope,
        selectedSortType = selectedSortType,
        sortOrder = sortOrder,
        tradeCurrencyState = tradeCurrencyState,
        toolbarState = toolbarState,
        updateSortType = updateSortType,
        updateSortOrder = updateSortOrder,
        textFieldValueState = textFieldValueState
    )
}
