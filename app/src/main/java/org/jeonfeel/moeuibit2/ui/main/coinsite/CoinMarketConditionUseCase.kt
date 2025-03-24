package org.jeonfeel.moeuibit2.ui.main.coinsite

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.jeonfeel.moeuibit2.data.network.retrofit.ApiResult
import org.jeonfeel.moeuibit2.data.network.retrofit.response.alternative.GetFearAndGreedyIndexRes
import org.jeonfeel.moeuibit2.data.network.retrofit.service.AlternativeService
import org.jeonfeel.moeuibit2.data.repository.network.AlternativeRepository
import org.jeonfeel.moeuibit2.ui.base.BaseUseCase
import org.jeonfeel.moeuibit2.ui.common.ResultState

class CoinMarketConditionUseCase(private val alternativeRepository: AlternativeRepository) :
    BaseUseCase() {
    suspend fun getFearAndGreedyIndex(): Flow<ResultState<GetFearAndGreedyIndexRes?>> {
        return alternativeRepository.getFearAndGreedyIndex().map { res ->
            when (res.status) {
                ApiResult.Status.SUCCESS -> {
                    ResultState.Success(res.data)
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
}