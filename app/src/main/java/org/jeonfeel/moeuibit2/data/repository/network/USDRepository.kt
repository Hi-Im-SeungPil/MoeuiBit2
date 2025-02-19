package org.jeonfeel.moeuibit2.data.repository.network

import kotlinx.coroutines.flow.Flow
import org.jeonfeel.moeuibit2.data.network.retrofit.ApiResult
import org.jeonfeel.moeuibit2.data.network.retrofit.api.USDService
import org.jeonfeel.moeuibit2.data.network.retrofit.networkCall
import org.jeonfeel.moeuibit2.data.network.retrofit.response.usd.USDToKRWPriceRes
import org.json.JSONObject
import retrofit2.Response

class USDRepository(
    private val usdService: USDService
) {
    suspend fun fetchUSDToKRWPrice(): Flow<ApiResult<USDToKRWPriceRes>> {
        return networkCall { usdService.getUSDTPrice() }
    }
}