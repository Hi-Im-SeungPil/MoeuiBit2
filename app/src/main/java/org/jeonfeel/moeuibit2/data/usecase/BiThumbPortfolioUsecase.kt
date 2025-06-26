package org.jeonfeel.moeuibit2.data.usecase

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.jeonfeel.moeuibit2.constants.EXCHANGE_BITTHUMB
import org.jeonfeel.moeuibit2.data.local.room.dao.UserDAO
import org.jeonfeel.moeuibit2.data.local.room.entity.MyCoin
import org.jeonfeel.moeuibit2.data.network.retrofit.ApiResult
import org.jeonfeel.moeuibit2.data.network.retrofit.response.bitthumb.BitThumbMarketCodeRes
import org.jeonfeel.moeuibit2.data.network.retrofit.response.bitthumb.BitThumbTickerRes
import org.jeonfeel.moeuibit2.data.network.websocket.manager.bithumb.BiThumbPortfolioWebsocketManager
import org.jeonfeel.moeuibit2.data.network.websocket.model.bitthumb.BithumbSocketTickerRes
import org.jeonfeel.moeuibit2.data.repository.local.LocalRepository
import org.jeonfeel.moeuibit2.data.repository.network.BiThumbRepository
import org.jeonfeel.moeuibit2.ui.base.BaseUseCase
import org.jeonfeel.moeuibit2.ui.common.ResultState
import javax.inject.Inject

class BiThumbPortfolioUsecase @Inject constructor(
    private val biThumbRepository: BiThumbRepository,
    private val localRepository: LocalRepository,
) : BaseUseCase() {
    private val portfolioWebsocketManager = BiThumbPortfolioWebsocketManager()

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
    suspend fun fetchMarketCode(): Flow<ResultState<List<BitThumbMarketCodeRes>>> {
        return biThumbRepository.fetchBitThumbMarketCodeList().map { res ->
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

    suspend fun fetchMarketTicker(
        marketCodes: String,
    ): Flow<ResultState<List<BitThumbTickerRes>>> {
        return biThumbRepository.fetchBitThumbTicker(marketCodes).map { res ->
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

    fun observeTickerResponse(): Flow<BithumbSocketTickerRes?> {
        return portfolioWebsocketManager.tickerFlow
    }

    suspend fun getUserSeedMoney(): Double {
        return localRepository.getUserDao().getUserByExchange(EXCHANGE_BITTHUMB)?.krw ?: 0.0
    }

    suspend fun getMyCoins(): List<MyCoin?> {
        com.orhanobut.logger.Logger.e("mycoins" + localRepository.getMyCoinDao().getAllByExchange(EXCHANGE_BITTHUMB).toString())
        return localRepository.getMyCoinDao().getAllByExchange(EXCHANGE_BITTHUMB) ?: emptyList()
    }

    suspend fun getUserDao(): UserDAO {
        return localRepository.getUserDao()
    }

    suspend fun removeCoin(market: String) {
        localRepository.getFavoriteDao().delete(market, EXCHANGE_BITTHUMB)
        localRepository.getMyCoinDao().delete(market, EXCHANGE_BITTHUMB)
        localRepository.getTransactionInfoDao().delete(market, EXCHANGE_BITTHUMB)
    }

    suspend fun insertCoin(myCoin: MyCoin) {
        localRepository.getMyCoinDao().insert(myCoin)
    }
}