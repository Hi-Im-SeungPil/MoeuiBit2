package org.jeonfeel.moeuibit2.data.repository.network

import org.jeonfeel.moeuibit2.data.network.retrofit.api.USDService
import org.json.JSONObject
import retrofit2.Response

class USDRepository(
    private val usdService: USDService
) {
    suspend fun fetchUSDToKRWPrice(): Response<JSONObject>
}