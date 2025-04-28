package org.jeonfeel.moeuibit2.data.network.retrofit.service

import org.jeonfeel.moeuibit2.data.network.retrofit.response.bitthumb.BitThumbMarketCodeRes
import org.jeonfeel.moeuibit2.data.network.retrofit.response.bitthumb.BitThumbTickerRes
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface BitThumbService {
    @GET("v1/market/all")
    suspend fun getMarketList(@Query("isDetails") isDetails: Boolean = true): Response<List<BitThumbMarketCodeRes>>

    @GET("ticker")
    suspend fun getMarketTicker(@Query("markets") markets: String): Response<List<BitThumbTickerRes>>
}