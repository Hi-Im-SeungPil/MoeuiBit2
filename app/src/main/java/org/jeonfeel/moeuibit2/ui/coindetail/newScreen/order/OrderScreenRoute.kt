package org.jeonfeel.moeuibit2.ui.coindetail.newScreen.order

import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import org.jeonfeel.moeuibit2.data.network.retrofit.model.upbit.CommonExchangeModel
import org.jeonfeel.moeuibit2.data.network.retrofit.model.upbit.OrderBookModel
import kotlin.reflect.KFunction0

@Composable
fun OrderScreenRoute(
    market: String,
    initCoinOrder: (String) -> Unit,
    coinOrderScreenOnPause: () -> Unit,
    coinOrderScreenOnResume: (String) -> Unit,
    preClosedPrice: State<CommonExchangeModel?>,
    maxOrderBookSize: State<Double>,
    orderBookList: List<OrderBookModel>,
    orderBookIndication: State<String>,
    saveOrderBookIndicationState: () -> Unit,
    changeOrderBookIndicationState: () -> Unit,
    getUserSeedMoney: () -> Long
) {
    NewOrderScreen(
        initCoinOrder = initCoinOrder,
        coinOrderScreenOnPause = coinOrderScreenOnPause,
        coinOrderScreenOnResume = coinOrderScreenOnResume,
        market = market,
        commonExchangeModelState = preClosedPrice,
        orderBookList = orderBookList,
        maxOrderBookSize = maxOrderBookSize,
        orderBookIndicationState = orderBookIndication,
        saveOrderBookIndicationState = saveOrderBookIndicationState,
        changeOrderBookIndicationState =  changeOrderBookIndicationState,
        getUserSeedMoney = getUserSeedMoney
    )
}