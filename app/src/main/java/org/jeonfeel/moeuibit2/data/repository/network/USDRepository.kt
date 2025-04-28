package org.jeonfeel.moeuibit2.data.repository.network

import kotlinx.coroutines.flow.Flow
import org.jeonfeel.moeuibit2.data.network.retrofit.ApiResult
import org.jeonfeel.moeuibit2.data.network.retrofit.service.USDService
import org.jeonfeel.moeuibit2.data.network.retrofit.networkCall
import org.jeonfeel.moeuibit2.data.network.retrofit.response.exchange_rate.USDToKRWPriceRes

class USDRepository(
    private val usdService: USDService
) {
    suspend fun fetchUSDToKRWPrice(): Flow<ApiResult<USDToKRWPriceRes>> {
        return networkCall { usdService.getUSDTPrice() }
    }
}