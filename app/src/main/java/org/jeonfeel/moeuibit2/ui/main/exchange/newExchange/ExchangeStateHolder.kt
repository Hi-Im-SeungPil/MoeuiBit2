package org.jeonfeel.moeuibit2.ui.main.exchange.newExchange

import android.content.Context
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.PagerState
import com.google.accompanist.pager.rememberPagerState
import org.jeonfeel.moeuibit2.data.network.retrofit.model.upbit.CommonExchangeModel
import org.jeonfeel.moeuibit2.ui.main.exchange.component.SortOrder
import org.jeonfeel.moeuibit2.ui.main.exchange.component.SortType
import org.jeonfeel.moeuibit2.utils.Utils

class ExchangeStateHolder @OptIn(ExperimentalPagerApi::class) constructor(
    val pagerState: PagerState,
    val lazyScrollState: LazyListState,
    val context: Context,
    val isUpdateExchange: Boolean,
    val sortTickerList: (
        sortType: SortType,
        sortOrder: SortOrder
    ) -> Unit
) {
    val textFieldValueState = mutableStateOf("")
    private var sortType = mutableStateOf(SortType.DEFAULT)
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
            this.sortType.value != sortType -> {
                this.sortType.value = sortType
                this.sortOrder.value = SortOrder.DESCENDING
            }

            else -> {
                this.sortOrder.value = when (this.sortOrder.value) {
                    SortOrder.DESCENDING -> SortOrder.ASCENDING
                    SortOrder.ASCENDING -> {
                        this.sortType.value = SortType.DEFAULT
                        SortOrder.NONE
                    }

                    SortOrder.NONE -> SortOrder.DESCENDING
                }
            }
        }

        sortTickerList(this.sortType.value, this.sortOrder.value)
    }
}

@OptIn(ExperimentalPagerApi::class)
@Composable
fun rememberExchangeStateHolder(
    pagerState: PagerState = rememberPagerState(),
    listScrollState: LazyListState = rememberLazyListState(),
    context: Context = LocalContext.current,
    isUpdateExchange: Boolean,
    sortTickerList: (
        sortType: SortType,
        sortOrder: SortOrder
    ) -> Unit
) = remember {
    ExchangeStateHolder(
        pagerState = pagerState,
        lazyScrollState = listScrollState,
        context = context,
        isUpdateExchange = isUpdateExchange,
        sortTickerList = sortTickerList
    )
}
