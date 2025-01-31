package org.jeonfeel.moeuibit2.ui.coindetail.order.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import org.jeonfeel.moeuibit2.data.local.room.entity.MyCoin
import org.jeonfeel.moeuibit2.data.local.room.entity.TransactionInfo
import org.jeonfeel.moeuibit2.data.network.retrofit.model.upbit.CommonExchangeModel
import org.jeonfeel.moeuibit2.data.network.retrofit.model.upbit.OrderBookModel
import java.math.BigDecimal

@Composable
fun OrderScreenRoute(
    market: String,
    initCoinOrder: (String) -> Unit,
    coinOrderScreenOnStop: () -> Unit,
    coinOrderScreenOnStart: (String) -> Unit,
    commonExchangeModelState: State<CommonExchangeModel?>,
    maxOrderBookSize: State<Double>,
    orderBookList: List<OrderBookModel>,
    orderBookIndicationState: State<String>,
    saveOrderBookIndicationState: () -> Unit,
    changeOrderBookIndicationState: () -> Unit,
    requestBid: (String, Double, BigDecimal, Double) -> Unit,
    requestAsk: (String, Double, Long, BigDecimal, Double) -> Unit,
    btcPrice: State<BigDecimal>,
    userSeedMoney: State<Long>,
    userBTC: State<MyCoin>,
    userCoin: State<MyCoin>,
    transactionInfoList: List<TransactionInfo>,
    getTransactionInfoList: (String) -> Unit
) {
    OrderScreen(
        market = market,
        initCoinOrder = initCoinOrder,
        coinOrderScreenOnStop = coinOrderScreenOnStop,
        coinOrderScreenOnStart = coinOrderScreenOnStart,
        commonExchangeModelState = commonExchangeModelState,
        maxOrderBookSize = maxOrderBookSize,
        orderBookList = orderBookList,
        orderBookIndicationState = orderBookIndicationState,
        saveOrderBookIndicationState = saveOrderBookIndicationState,
        changeOrderBookIndicationState = changeOrderBookIndicationState,
        userSeedMoney = userSeedMoney,
        userBTC = userBTC,
        userCoin = userCoin,
        requestBid = requestBid,
        requestAsk = requestAsk,
        btcPrice = btcPrice,
        transactionInfoList = transactionInfoList,
        getTransactionInfoList = getTransactionInfoList
    )
}