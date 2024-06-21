package org.jeonfeel.moeuibit2.ui.coindetail.newScreen.order

import androidx.compose.runtime.mutableStateListOf
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import org.jeonfeel.moeuibit2.data.network.retrofit.model.upbit.UpbitOrderBookModel
import org.jeonfeel.moeuibit2.data.network.websocket.model.upbit.UpbitSocketOrderBookRes
import org.jeonfeel.moeuibit2.data.usecase.UpbitCoinOrderUseCase
import org.jeonfeel.moeuibit2.ui.main.exchange.root_exchange.BaseCommunicationModule
import javax.inject.Inject

class UpbitCoinOrder @Inject constructor(private val upbitCoinOrderUseCase: UpbitCoinOrderUseCase) :
    BaseCommunicationModule() {
    private val orderBookList = mutableStateListOf<UpbitOrderBookModel>()
    private val _tickerResponse = MutableStateFlow<UpbitSocketOrderBookRes?>(null)

    /**
     * 호가 요청
     */
    suspend fun requestOrderBook(market: String) {
        executeUseCase<List<UpbitOrderBookModel>>(
            target = upbitCoinOrderUseCase.getOrderBook(market),
            onComplete = {
                orderBookList.addAll(it)
            }
        )
    }

    /**
     * 호가 구독 요청
     */
    suspend fun requestSubscribeOrderBook(market: String) {
        upbitCoinOrderUseCase.getSocketOrderBook(listOf(market))
    }

    /**
     * 호가 수집
     */
    suspend fun collectOrderBook() {
        upbitCoinOrderUseCase.observeOrderBook().onEach { result ->
            _tickerResponse.update {
                result
            }
        }.collect { upbitSocketOrderBookRes ->
            val realTimeOrderBook = upbitSocketOrderBookRes.mapTo()
            for (i in orderBookList.indices) {
                orderBookList[i] = realTimeOrderBook[i]
            }
        }
    }



}