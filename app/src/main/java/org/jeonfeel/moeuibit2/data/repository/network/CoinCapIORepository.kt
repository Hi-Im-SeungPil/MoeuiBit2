package org.jeonfeel.moeuibit2.data.repository.network

import kotlinx.coroutines.flow.Flow
import org.jeonfeel.moeuibit2.data.network.retrofit.ApiResult
import org.jeonfeel.moeuibit2.data.network.retrofit.api.CoinCapIOService
import org.jeonfeel.moeuibit2.data.network.retrofit.networkCall
import org.jeonfeel.moeuibit2.data.network.retrofit.response.coincapio.FetchCoinInfoRes

class CoinCapIORepository(
    private val coinCapIOService: CoinCapIOService,
) {
    suspend fun fetchCoinInfo(engName: String): Flow<ApiResult<FetchCoinInfoRes>> {
        return networkCall { coinCapIOService.fetchCoinInfo(engName = engName) }
    }
}