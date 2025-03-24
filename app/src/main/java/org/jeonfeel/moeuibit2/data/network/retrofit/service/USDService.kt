package org.jeonfeel.moeuibit2.data.network.retrofit.service

import org.jeonfeel.moeuibit2.data.network.retrofit.response.usd.USDToKRWPriceRes
import retrofit2.Response
import retrofit2.http.GET

interface USDService {
    @GET("npm/@fawazahmed0/currency-api@latest/v1/currencies/usd.json")
    suspend fun getUSDTPrice(): Response<USDToKRWPriceRes>
}