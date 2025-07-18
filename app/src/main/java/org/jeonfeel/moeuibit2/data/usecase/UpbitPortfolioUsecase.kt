package org.jeonfeel.moeuibit2.data.usecase

import com.tradingview.lightweightcharts.Logger
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.jeonfeel.moeuibit2.constants.EXCHANGE_UPBIT
import org.jeonfeel.moeuibit2.data.local.room.dao.UserDAO
import org.jeonfeel.moeuibit2.data.local.room.entity.MyCoin
import org.jeonfeel.moeuibit2.data.network.retrofit.ApiResult
import org.jeonfeel.moeuibit2.data.network.retrofit.request.upbit.GetUpbitMarketTickerReq
import org.jeonfeel.moeuibit2.data.network.retrofit.response.upbit.GetUpbitMarketTickerRes
import org.jeonfeel.moeuibit2.data.network.retrofit.response.upbit.UpbitMarketCodeRes
import org.jeonfeel.moeuibit2.data.network.websocket.model.upbit.UpbitSocketTickerRes
import org.jeonfeel.moeuibit2.data.repository.local.LocalRepository
import org.jeonfeel.moeuibit2.data.repository.network.UpbitRepository
import org.jeonfeel.moeuibit2.ui.base.BaseUseCase
import org.jeonfeel.moeuibit2.data.network.websocket.manager.upbit.PortfolioWebsocketManager
import org.jeonfeel.moeuibit2.ui.common.ResultState
import javax.inject.Inject

class UpbitPortfolioUsecase @Inject constructor(
    private val upbitRepository: UpbitRepository,
    private val localRepository: LocalRepository,
) : BaseUseCase() {
    private val portfolioWebsocketManager = PortfolioWebsocketManager()

    suspend fun onStart(marketCodes: List<String>) {
        portfolioWebsocketManager.updateIsBackground(false)
        if (portfolioWebsocketManager.getIsSocketConnected()) {
            portfolioWebsocketManager.sendMessage(marketCodes.joinToString(separator = ","))
        } else {
            portfolioWebsocketManager.connectWebSocketFlow(marketCodes.joinToString(separator = ","))
        }
    }

    suspend fun onStop() {
        portfolioWebsocketManager.onStop()
    }

    /**
     * 업비트 마켓 코드 조회
     */
    suspend fun getMarketCode(): Flow<Any> {
        return requestApiResult(
            result = upbitRepository.getUpbitMarketCodeList(),
            onSuccess = { result ->
                result
            }
        )
    }

    /**
     * 업비트 마켓 코드 조회
     */
    suspend fun fetchMarketCode(): Flow<ResultState<List<UpbitMarketCodeRes>>> {
        return upbitRepository.fetchUpbitMarketCodeList().map { res ->
            when (res.status) {
                ApiResult.Status.SUCCESS -> {
                    if (res.data != null) {
                        ResultState.Success(res.data)
                    } else {
                        ResultState.Error(res.message.toString())
                    }
                }

                ApiResult.Status.API_ERROR,
                ApiResult.Status.NETWORK_ERROR -> {
                    ResultState.Error(res.message.toString())
                }

                ApiResult.Status.LOADING -> {
                    ResultState.Loading
                }
            }
        }
    }

    suspend fun getMarketTicker(
        getUpbitMarketTickerReq: GetUpbitMarketTickerReq,
        isList: Boolean = false
    ): Flow<Any> {
        return requestApiResult(
            result = upbitRepository.getMarketTicker(getUpbitMarketTickerReq),
            onSuccess = { ticker ->
                if (isList) {
                    ticker
                } else {
                    ticker[0]
                }
            }
        )
    }

    suspend fun fetchMarketTicker(
        getUpbitMarketTickerReq: GetUpbitMarketTickerReq,
    ): Flow<ResultState<List<GetUpbitMarketTickerRes>>> {
        return upbitRepository.fetchMarketTicker(getUpbitMarketTickerReq).map { res ->
            when (res.status) {
                ApiResult.Status.SUCCESS -> {
                    if (res.data != null) {
                        ResultState.Success(res.data)
                    } else {
                        ResultState.Error(res.message.toString())
                    }
                }

                ApiResult.Status.API_ERROR,
                ApiResult.Status.NETWORK_ERROR -> {
                    ResultState.Error(res.message.toString())
                }

                ApiResult.Status.LOADING -> {
                    ResultState.Loading
                }
            }

        }
    }

    /**
     * 업비트 Ticker 구독 요청
     */
    fun observeTickerResponse(): Flow<UpbitSocketTickerRes?> {
        return portfolioWebsocketManager.tickerFlow
    }

    suspend fun getUserSeedMoney(): Double {
        return localRepository.getUserDao().getUserByExchange(EXCHANGE_UPBIT)?.krw ?: 0.0
    }

    suspend fun getMyCoins(): List<MyCoin?> {
        return localRepository.getMyCoinDao().getAllByExchange(EXCHANGE_UPBIT) ?: emptyList()
    }

    suspend fun getUserDao(): UserDAO {
        return localRepository.getUserDao()
    }

    suspend fun removeCoin(market: String) {
        localRepository.getFavoriteDao().delete(market, EXCHANGE_UPBIT)
        localRepository.getMyCoinDao().delete(market, EXCHANGE_UPBIT)
        localRepository.getTransactionInfoDao().delete(market, EXCHANGE_UPBIT)
    }

    suspend fun insertCoin(myCoin: MyCoin) {
        localRepository.getMyCoinDao().insert(myCoin)
    }
}
