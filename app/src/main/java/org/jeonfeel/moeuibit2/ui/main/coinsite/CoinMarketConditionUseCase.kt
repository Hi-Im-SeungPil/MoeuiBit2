package org.jeonfeel.moeuibit2.ui.main.coinsite

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.jeonfeel.moeuibit2.data.network.retrofit.ApiResult
import org.jeonfeel.moeuibit2.data.repository.network.AlternativeRepository
import org.jeonfeel.moeuibit2.ui.base.BaseUseCase
import org.jeonfeel.moeuibit2.ui.common.ResultState
import org.jeonfeel.moeuibit2.ui.main.coinsite.ui_model.FearAndGreedyUIModel

class CoinMarketConditionUseCase(private val alternativeRepository: AlternativeRepository) :
    BaseUseCase() {

    suspend fun fetchFearAndGreedyIndex(): Flow<ResultState<FearAndGreedyUIModel>> {
        return alternativeRepository.fetchFearAndGreedyIndex().map { res ->
            when (res.status) {
                ApiResult.Status.SUCCESS -> {
                    ResultState.Success(
                        res.data?.data?.get(0)?.parseFearAndGreedyUIModel()
                            ?: FearAndGreedyUIModel(
                                index = 0,
                                indexDescription = "ERROR"
                            )
                    )
                }

                ApiResult.Status.API_ERROR, ApiResult.Status.NETWORK_ERROR -> {
                    ResultState.Error(res.message ?: "Unknown error")
                }

                ApiResult.Status.LOADING -> {
                    ResultState.Loading
                }
            }
        }
    }

//    suspend fun fetchExchangeRate(): Flow<ResultState<ExchangeRateUIModel>> {
//
//    }
}