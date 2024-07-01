package org.jeonfeel.moeuibit2.ui.coindetail.newScreen.order

import dagger.hilt.android.lifecycle.HiltViewModel
import org.jeonfeel.moeuibit2.data.local.room.entity.MyCoin
import org.jeonfeel.moeuibit2.data.network.retrofit.model.upbit.CommonExchangeModel
import org.jeonfeel.moeuibit2.data.network.retrofit.model.upbit.UpbitOrderBookModel
import org.jeonfeel.moeuibit2.data.network.retrofit.request.upbit.GetUpbitMarketTickerReq
import org.jeonfeel.moeuibit2.data.network.websocket.thunder.service.UpBitSocketService
import org.jeonfeel.moeuibit2.data.repository.network.UpbitRepository
import org.jeonfeel.moeuibit2.data.usecase.UpbitUseCase
import org.jeonfeel.moeuibit2.ui.base.BaseViewModel
import org.jeonfeel.moeuibit2.ui.main.exchange.ExchangeViewModel.Companion.ROOT_EXCHANGE_BITTHUMB
import org.jeonfeel.moeuibit2.ui.main.exchange.ExchangeViewModel.Companion.ROOT_EXCHANGE_UPBIT
import org.jeonfeel.moeuibit2.utils.isTradeCurrencyKrw
import org.jeonfeel.moeuibit2.utils.manager.PreferenceManager
import javax.inject.Inject

@HiltViewModel
class NewCoinDetailViewModel @Inject constructor(
    preferenceManager: PreferenceManager,
    private val upbitCoinOrder: UpbitCoinOrder,
    private val upbitUseCase: UpbitUseCase
) : BaseViewModel(preferenceManager) {

    fun init(market: String) {
        rootExchangeCoroutineBranch(
            upbitAction = {
                val markets = when (market.isTradeCurrencyKrw()) {
                    true -> {
                        market
                    }
                    false -> {
                        "$market,KRW-BTC"
                    }
                }
                executeUseCase<CommonExchangeModel>(
                    target = upbitUseCase.getMarketTicker(markets),
                    onComplete = {

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
            }
        )
    }

    /**
     * orderBookList 받아옴
     */
    fun getOrderBookList(): List<UpbitOrderBookModel> {
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