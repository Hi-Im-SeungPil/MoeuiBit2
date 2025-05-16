package org.jeonfeel.moeuibit2.ui.coindetail.order.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.hilt.navigation.compose.hiltViewModel
import org.jeonfeel.moeuibit2.data.network.retrofit.model.upbit.CommonExchangeModel
import org.jeonfeel.moeuibit2.ui.coindetail.order.CoinOrderViewModel
import java.math.BigDecimal

@Composable
fun OrderScreenRoute(
    viewModel: CoinOrderViewModel = hiltViewModel(),
    market: String,
    commonExchangeModelState: State<CommonExchangeModel?>,
    btcPrice: State<BigDecimal>,
    koreanCoinName: State<String>,
) {
    OrderScreen(
        market = market,
        commonExchangeModelState = commonExchangeModelState,
        maxOrderBookSize = viewModel.getMaxOrderBookSize(),
        initCoinOrder = viewModel::initCoinOrder,
        coinOrderScreenOnStop = viewModel::coinOrderScreenOnStop,
        coinOrderScreenOnStart = viewModel::coinOrderScreenOnStart,
        orderBookList = viewModel.getOrderBookList() ,
        orderBookIndicationState = viewModel.orderBookIndication,
        saveOrderBookIndicationState = viewModel::saveOrderBookIndication,
        changeOrderBookIndicationState = viewModel::changeOrderBookIndication,
        userSeedMoney = viewModel.getUserSeedMoney(),
        userBTC = viewModel.getUserBtcCoin(),
        userCoin = viewModel.getUserCoin(),
        requestBid = viewModel::requestBid,
        requestAsk = viewModel::requestAsk,
        transactionInfoList = viewModel.transactionInfo,
        getTransactionInfoList = viewModel::getTransactionInfoList,
        isCoinOrderStarted = viewModel.isCoinOrderStarted,
        koreanName = koreanCoinName,
        btcPrice = btcPrice,
        orderBookInitSuccess = viewModel.getOrderBookInitSuccess()
    )
}