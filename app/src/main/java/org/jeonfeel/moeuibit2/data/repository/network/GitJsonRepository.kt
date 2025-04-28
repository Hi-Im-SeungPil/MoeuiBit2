package org.jeonfeel.moeuibit2.data.repository.network

import kotlinx.coroutines.flow.Flow
import org.jeonfeel.moeuibit2.data.network.retrofit.ApiResult
import org.jeonfeel.moeuibit2.data.network.retrofit.networkCall
import org.jeonfeel.moeuibit2.data.network.retrofit.response.gitjson.GitJsonReferralItem
import org.jeonfeel.moeuibit2.data.network.retrofit.service.GitJsonService

class GitJsonRepository(
    private val gitJsonService: GitJsonService
) {
    suspend fun fetchAppMiningInfo(): Flow<ApiResult<List<GitJsonReferralItem>>> {
        return networkCall { gitJsonService.getReferralJson() }
    }
}