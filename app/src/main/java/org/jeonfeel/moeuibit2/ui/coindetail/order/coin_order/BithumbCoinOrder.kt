package org.jeonfeel.moeuibit2.ui.coindetail.order.coin_order

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import kotlinx.coroutines.flow.MutableStateFlow
import org.jeonfeel.moeuibit2.data.local.room.entity.MyCoin
import org.jeonfeel.moeuibit2.data.network.retrofit.model.upbit.OrderBookModel
import org.jeonfeel.moeuibit2.data.network.websocket.model.upbit.UpbitSocketOrderBookRes

class BithumbCoinOrder(
//    bithumbCoinOrderUseCase: BithumbCoinOrderUseCase
) {
    private val _orderBookList = mutableStateListOf<OrderBookModel>()
    val orderBookList: List<OrderBookModel> get() = _orderBookList

    private val _userSeedMoney = mutableDoubleStateOf(0.0)
    val userSeedMoney: State<Double> get() = _userSeedMoney

    private val _userCoin = mutableStateOf(MyCoin())
    val userCoin: State<MyCoin> get() = _userCoin

    private val _userBtcCoin = mutableStateOf(MyCoin())
    val userBtcCoin: State<MyCoin> get() = _userBtcCoin

    private val _tickerResponse = MutableStateFlow<UpbitSocketOrderBookRes?>(null)
    private val _maxOrderBookSize = mutableDoubleStateOf(0.0)
    val maxOrderBookSize: State<Double> get() = _maxOrderBookSize

    private val _orderBookInitSuccess = mutableStateOf(false)
    val orderBookInitSuccess: State<Boolean> get() = _orderBookInitSuccess
}