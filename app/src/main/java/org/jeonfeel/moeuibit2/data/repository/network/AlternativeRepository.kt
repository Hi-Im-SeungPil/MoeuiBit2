package org.jeonfeel.moeuibit2.data.repository.network

import kotlinx.coroutines.flow.Flow
import org.jeonfeel.moeuibit2.data.network.retrofit.ApiResult
import org.jeonfeel.moeuibit2.data.network.retrofit.networkCall
import org.jeonfeel.moeuibit2.data.network.retrofit.response.alternative.GetFearAndGreedyIndexRes
import org.jeonfeel.moeuibit2.data.network.retrofit.service.AlternativeService

class AlternativeRepository(private val alternativeService: AlternativeService) {
    suspend fun fetchFearAndGreedyIndex(): Flow<ApiResult<GetFearAndGreedyIndexRes>> {
        return networkCall { alternativeService.fecthFearAndGreedyIndex() }
    }
}