package org.jeonfeel.moeuibit2.data.network.retrofit.api

import com.google.gson.JsonObject
import retrofit2.Response
import retrofit2.http.GET

interface USDTService {
    @GET("https://cdn.jsdelivr.net/gh/fawazahmed0/currency-api@1/latest/currencies/usd/krw.min.json")
    suspend fun getUSDTPrice(): Response<JsonObject>
}