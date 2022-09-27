package org.jeonfeel.moeuibit2.data.remote.retrofit.api

import com.google.gson.JsonObject
import retrofit2.Response
import retrofit2.http.GET

interface BinanceService {
    @GET("https://api.binance.com/api/v3/ticker/24hr")
    suspend fun getExchangeInfo(): Response<JsonObject>
}