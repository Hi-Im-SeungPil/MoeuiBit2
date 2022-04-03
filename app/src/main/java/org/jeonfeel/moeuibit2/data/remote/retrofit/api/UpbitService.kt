package org.jeonfeel.moeuibit2.data.remote.retrofit.api

import com.google.gson.JsonArray
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface UpBitService {
    @GET("https://api.upbit.com/v1/market/all?isDetails=true")
    suspend fun getMarketCode(): Response<JsonArray>

    @GET("https://api.upbit.com/v1/ticker")
    suspend fun getKrwTicker(@Query("markets") markets: String): Response<JsonArray>

    @GET("https://api.upbit.com/v1/orderbook")
    suspend fun getKrwOrderBook(@Query("markets") market: String): Response<JsonArray>
}