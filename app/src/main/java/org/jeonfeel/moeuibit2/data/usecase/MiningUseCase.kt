package org.jeonfeel.moeuibit2.data.usecase

import com.tradingview.lightweightcharts.Logger
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.jeonfeel.moeuibit2.data.network.retrofit.ApiResult
import org.jeonfeel.moeuibit2.data.network.retrofit.response.gitjson.GitJsonReferralItem
import org.jeonfeel.moeuibit2.data.repository.network.GitJsonRepository
import org.jeonfeel.moeuibit2.ui.base.BaseUseCase
import org.jeonfeel.moeuibit2.ui.common.ResultState

class MiningUseCase(private val miningRepository: GitJsonRepository): BaseUseCase() {

    suspend fun fetchAppMiningInfo(): Flow<ResultState<List<GitJsonReferralItem>>> {
        return miningRepository.fetchAppMiningInfo().map { res ->
            when (res.status) {
                ApiResult.Status.SUCCESS -> {
                    ResultState.Success(
                        res.data ?: emptyList()
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
}