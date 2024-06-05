package org.jeonfeel.moeuibit2.ui.main.exchange.newExchange

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
import org.jeonfeel.moeuibit2.data.network.retrofit.model.upbit.CommonExchangeModel
import org.jeonfeel.moeuibit2.ui.main.exchange.component.SortOrder
import org.jeonfeel.moeuibit2.ui.main.exchange.component.SortType
import org.jeonfeel.moeuibit2.utils.Utils

class ExchangeStateHolder @OptIn(ExperimentalPagerApi::class) constructor(
    val pagerState: PagerState,
    val lazyScrollState: LazyListState,
    val context: Context,
    val isUpdateExchange: Boolean,
    val sortTickerList: (targetTradeCurrency: Int?, sortType: SortType, sortOrder: SortOrder) -> Unit,
    val focusManaManager: FocusManager,
    private val changeTradeCurrency: (tradeCurrency: Int) -> Unit,
    val coroutineScope: CoroutineScope
) {
    val textFieldValueState = mutableStateOf("")
    var selectedSortType = mutableStateOf(SortType.DEFAULT)
    var sortOrder = mutableStateOf(SortOrder.DESCENDING)

    fun getFilteredList(tickerList: List<CommonExchangeModel>): List<CommonExchangeModel> {
        return Utils.filterTickerList(
            exchangeModelList = tickerList,
            searchStr = textFieldValueState.value
        )
    }

    fun onSortClick(
        sortType: SortType,
    ) {
        if (!isUpdateExchange) return
        when {
            this.selectedSortType.value != sortType -> {
                this.selectedSortType.value = sortType
                this.sortOrder.value = SortOrder.DESCENDING
            }

            else -> {
                this.sortOrder.value = when (this.sortOrder.value) {
                    SortOrder.DESCENDING -> SortOrder.ASCENDING
                    SortOrder.ASCENDING -> {
                        this.selectedSortType.value = SortType.DEFAULT
                        SortOrder.NONE
                    }

                    SortOrder.NONE -> SortOrder.DESCENDING
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
    coroutineScope: CoroutineScope = rememberCoroutineScope()
) = remember {
    ExchangeStateHolder(
        pagerState = pagerState,
        lazyScrollState = listScrollState,
        context = context,
        isUpdateExchange = isUpdateExchange,
        sortTickerList = sortTickerList,
        focusManaManager = focusManaManager,
        changeTradeCurrency = changeTradeCurrency,
        coroutineScope = coroutineScope
    )
}
