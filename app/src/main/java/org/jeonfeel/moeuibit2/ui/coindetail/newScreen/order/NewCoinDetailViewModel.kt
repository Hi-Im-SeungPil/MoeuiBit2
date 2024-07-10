package org.jeonfeel.moeuibit2.ui.coindetail.newScreen.order

import androidx.compose.runtime.mutableStateOf
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import org.jeonfeel.moeuibit2.data.local.room.entity.MyCoin
import org.jeonfeel.moeuibit2.data.network.retrofit.model.upbit.OrderBookModel
import org.jeonfeel.moeuibit2.data.network.retrofit.request.upbit.GetUpbitMarketTickerReq
import org.jeonfeel.moeuibit2.data.network.retrofit.response.upbit.GetUpbitMarketTickerRes
import org.jeonfeel.moeuibit2.data.usecase.UpbitUseCase
import org.jeonfeel.moeuibit2.ui.base.BaseViewModel
import org.jeonfeel.moeuibit2.ui.main.exchange.ExchangeViewModel.Companion.ROOT_EXCHANGE_BITTHUMB
import org.jeonfeel.moeuibit2.ui.main.exchange.ExchangeViewModel.Companion.ROOT_EXCHANGE_UPBIT
import org.jeonfeel.moeuibit2.utils.Utils.coinOrderIsKrwMarket
import org.jeonfeel.moeuibit2.utils.manager.CacheManager
import org.jeonfeel.moeuibit2.utils.manager.PreferencesManager
import javax.inject.Inject

@HiltViewModel
class NewCoinDetailViewModel @Inject constructor(
    preferenceManager: PreferencesManager,
    private val upbitCoinOrder: UpbitCoinOrder,
    private val upbitUseCase: UpbitUseCase,
    private val cacheManager: CacheManager
) : BaseViewModel(preferenceManager) {
    private val _coinTicker = mutableStateOf<GetUpbitMarketTickerRes?>(null)
    val coinTicker get() = _coinTicker
    private val _koreanCoinName = mutableStateOf("")
    val koreanCoinName get() = _koreanCoinName

    fun init(market: String) {
        rootExchangeCoroutineBranch(
            upbitAction = {
                _koreanCoinName.value =
                    cacheManager.readKoreanCoinNameMap()[market.substring(4)] ?: ""
                val getUpbitTickerReq = GetUpbitMarketTickerReq(
                    market.coinOrderIsKrwMarket()
                )
                executeUseCase<GetUpbitMarketTickerRes>(
                    target = upbitUseCase.getMarketTicker(getUpbitTickerReq),
                    onComplete = { ticker ->
                        _coinTicker.value = ticker
                    }
                )
            },
            bitthumbAction = {

            }
        )
    }

    /**
     * coin Order 화면 초기화
     */
    fun initCoinOrder(market: String) {
        rootExchangeCoroutineBranch(
            upbitAction = {
                upbitCoinOrder.initCoinOrder(market)
            },
            bitthumbAction = {
            },
            dispatcher = Dispatchers.IO
        )
    }

    /**
     * 코인 주문 화면 pause
     */
    fun coinOrderScreenOnPause() {
        rootExchangeCoroutineBranch(
            upbitAction = {
                upbitCoinOrder.onPause()
            },
            bitthumbAction = {

            }
        )
    }

    /**
     * orderBookList 받아옴
     */
    fun getOrderBookList(): List<OrderBookModel> {
        return when (rootExchange) {
            ROOT_EXCHANGE_UPBIT -> {
                upbitCoinOrder.orderBookList
            }

            ROOT_EXCHANGE_BITTHUMB -> {
                upbitCoinOrder.orderBookList
            }

            else -> {
                upbitCoinOrder.orderBookList
            }
        }
    }

    fun getMaxOrderBookSize(): Double {
        return when (rootExchange) {
            ROOT_EXCHANGE_UPBIT -> {
                upbitCoinOrder.maxOrderBookSize
            }

            ROOT_EXCHANGE_BITTHUMB -> {
                upbitCoinOrder.maxOrderBookSize
            }

            else -> {
                upbitCoinOrder.maxOrderBookSize
            }
        }
    }

    /**
     * 사용자 시드머니 받아옴
     */
    fun getUserSeedMoney(): Long {
        return when (rootExchange) {
            ROOT_EXCHANGE_UPBIT -> {
                upbitCoinOrder.userSeedMoney
            }

            ROOT_EXCHANGE_BITTHUMB -> {
                upbitCoinOrder.userSeedMoney
            }

            else -> {
                0L
            }
        }
    }

    /**
     * 사용자 코인 받아옴
     */
    fun getUserCoin(): MyCoin {
        return when (rootExchange) {
            ROOT_EXCHANGE_UPBIT -> {
                upbitCoinOrder.userCoin
            }

            ROOT_EXCHANGE_BITTHUMB -> {
                upbitCoinOrder.userCoin
            }

            else -> {
                upbitCoinOrder.userCoin
            }
        }
    }

    /**
     * BTC마켓 일 때 필요한데, 사용자 BTC 코인 받아옴
     */
    fun getUserBtcCoin(): MyCoin {
        return when (rootExchange) {
            ROOT_EXCHANGE_UPBIT -> {
                upbitCoinOrder.userBtcCoin
            }

            ROOT_EXCHANGE_BITTHUMB -> {
                upbitCoinOrder.userBtcCoin
            }

            else -> {
                upbitCoinOrder.userBtcCoin
            }
        }
    }
}