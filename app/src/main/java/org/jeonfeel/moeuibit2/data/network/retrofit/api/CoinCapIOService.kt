package org.jeonfeel.moeuibit2.data.network.retrofit.api

import org.jeonfeel.moeuibit2.data.network.retrofit.response.coincapio.FetchCoinInfoRes
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

interface CoinCapIOService {
    @GET("v2/assets")
    suspend fun fetchCoinInfo(
        @Header("Authorization") token: String = "Bearer ac6fa4e0-57a3-4983-aa0b-672fb0dc2a76",
        @Query("search") engName: String,
    ): Response<FetchCoinInfoRes>
}