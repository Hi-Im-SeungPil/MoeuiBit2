package org.jeonfeel.moeuibit2.ui.viewmodels

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.jeonfeel.moeuibit2.constants.*
import org.jeonfeel.moeuibit2.data.local.room.entity.TransactionInfo
import org.jeonfeel.moeuibit2.data.remote.websocket.UpBitCoinDetailWebSocket
import org.jeonfeel.moeuibit2.data.remote.websocket.listener.OnCoinDetailMessageReceiveListener
import org.jeonfeel.moeuibit2.data.remote.websocket.model.CoinDetailTickerModel
import org.jeonfeel.moeuibit2.data.repository.local.LocalRepository
import org.jeonfeel.moeuibit2.ui.base.BaseViewModel
import org.jeonfeel.moeuibit2.ui.coindetail.chart.utils.Chart
import org.jeonfeel.moeuibit2.ui.coindetail.coininfo.utils.CoinInfo
import org.jeonfeel.moeuibit2.ui.coindetail.order.CoinOrder
import org.jeonfeel.moeuibit2.utils.Utils
import javax.inject.Inject
import kotlin.collections.set

@HiltViewModel
class CoinDetailViewModel @Inject constructor(
    val coinOrder: CoinOrder,
    val chart: Chart,
    val coinInfo: CoinInfo
) : BaseViewModel(),
    OnCoinDetailMessageReceiveListener {
    private var name = ""
    private var marketState = -999
    var market = ""
    var preClosingPrice = 0.0
    val favoriteMutableState = mutableStateOf(false)

    fun initViewModel(market: String, preClosingPrice: Double, isFavorite: Boolean) {
        this.market = market
        this.preClosingPrice = preClosingPrice
        this.favoriteMutableState.value = isFavorite
        this.marketState = Utils.getSelectedMarket(market)
        chart.market = market
        coinOrder.initAdjustCommission()
    }

    private fun setCoinDetailWebSocketMessageListener() {
        UpBitCoinDetailWebSocket.getListener().setTickerMessageListener(this)
    }

    private fun updateTicker() {
        viewModelScope.launch {
            while (coinOrder.isTickerSocketRunning) {
                val tradPrice = coinOrder.coinDetailModel.tradePrice
                coinOrder.state.currentTradePriceState.value = tradPrice
                chart.updateCandleTicker(tradPrice)
                delay(100)
            }
        }
    }

    // 주문 화면
    fun initOrderScreen() {
        if (coinOrder.state.currentTradePriceState.value == 0.0 && coinOrder.state.orderBookMutableStateList.isEmpty()) {
            viewModelScope.launch(ioDispatcher) {
                setCoinDetailWebSocketMessageListener()
                coinOrder.setOrderBookWebSocketMessageListener()
                coinOrder.initOrderScreen(market)
                updateTicker()
            }
        } else {
            setCoinDetailWebSocketMessageListener()
            coinOrder.setOrderBookWebSocketMessageListener()
            UpBitCoinDetailWebSocket.requestCoinDetailData(market)
        }
        for (i in 0 until 4) {
            coinOrder.state.commissionStateList.add(mutableStateOf(0f))
        }
    }

    /**
     * 매수
     */
    fun bidRequest(
        currentPrice: Double,
        quantity: Double,
        totalPrice: Long = 0L,
        btcTotalPrice: Double = 0.0,
        currentBtcPrice: Double = 0.0
    ): Job {
        return viewModelScope.launch(ioDispatcher) {
            coinOrder.bidRequest(
                market,
                name,
                currentPrice,
                quantity,
                totalPrice,
                btcTotalPrice,
                marketState = marketState,
                currentBtcPrice
            )
        }
    }

    /**
     * 매도
     */
    fun askRequest(
        quantity: Double,
        totalPrice: Long,
        currentPrice: Double,
        btcTotalPrice: Double = 0.0,
    ): Job {
        return viewModelScope.launch(ioDispatcher) {
            coinOrder.askRequest(
                market,
                quantity,
                totalPrice,
                btcTotalPrice,
                currentPrice,
                marketState
            )
        }
    }

    /**
     * 수수료 조정
     */
    fun adjustCommission() {
        viewModelScope.launch(ioDispatcher) {
            coinOrder.adjustCommission()
        }
    }

    /**
     * 수수료 조정 초기화
     */
    fun initAdjustCommission() {
        coinOrder.initAdjustCommission()
    }

    /**
     * 수수료 얻어오기
     */
    fun getCommission(key: String): Float {
        return coinOrder.getCommission(key)
    }

    /**
     * 거래내역 조회
     */
    fun getTransactionInfoList() {
        viewModelScope.launch(ioDispatcher) {
            coinOrder.getTransactionInfoList(market)
        }
    }

    // 코인 정보 화면
    fun getCoinInfo() {
        coinInfo.getCoinInfo(market)
    }

    // 차트 화면
    fun requestOldData(
        positiveBarDataSet: IBarDataSet,
        negativeBarDataSet: IBarDataSet,
        candleXMin: Float
    ) {
        viewModelScope.launch {
            chart.requestOldData(
                positiveBarDataSet = positiveBarDataSet,
                negativeBarDataSet = negativeBarDataSet,
                candleXMin = candleXMin
            )
        }
    }

    fun requestChartData() {
        viewModelScope.launch {
            chart.requestChartData(market = market)
        }
    }

    /**
     * 웹소켓 리스너
     */
    override fun onCoinDetailMessageReceiveListener(tickerJsonObject: String) {
        if (coinOrder.isTickerSocketRunning) {
            val model = gson.fromJson(tickerJsonObject, CoinDetailTickerModel::class.java)
            if (marketState == SELECTED_BTC_MARKET && model.code.startsWith(SYMBOL_KRW)) {
                coinOrder.state.currentBTCPrice.value = model.tradePrice
            } else {
                coinOrder.coinDetailModel = model
                coinOrder.state.currentTradePriceStateForOrderBook.value =
                    coinOrder.coinDetailModel.tradePrice
            }
        }
    }
}