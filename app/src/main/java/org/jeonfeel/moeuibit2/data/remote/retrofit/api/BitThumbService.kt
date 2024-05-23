package org.jeonfeel.moeuibit2.data.remote.retrofit.api

import com.google.gson.JsonObject
import org.jeonfeel.moeuibit2.constants.bitthumbCoinNameUrl
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface BitThumbService {

    @GET("https://api.bithumb.com/public/ticker/ALL_KRW")
    suspend fun getKRWMarketCode(): Response<JsonObject>

    @GET("https://api.bithumb.com/public/ticker/ALL_BTC")
    suspend fun getBTCMarketCode(): Response<JsonObject>

    @GET(bitthumbCoinNameUrl)
    suspend fun getCoinName(): Response<JsonObject>

    @GET("https://api.bithumb.com/public/ticker/{market}")
    suspend fun getTickerUnit(@Path(value = "market") market: String): Response<JsonObject>

}