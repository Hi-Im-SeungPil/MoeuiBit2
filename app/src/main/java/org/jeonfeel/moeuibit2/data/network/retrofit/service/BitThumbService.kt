package org.jeonfeel.moeuibit2.data.network.retrofit.service

import org.jeonfeel.moeuibit2.data.network.retrofit.response.bitthumb.BiThumbWarningRes
import org.jeonfeel.moeuibit2.data.network.retrofit.response.bitthumb.BitThumbMarketCodeRes
import org.jeonfeel.moeuibit2.data.network.retrofit.response.bitthumb.BitThumbTickerRes
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface BitThumbService {
    @GET("v1/market/all")
    suspend fun fetchBitThumbMarketCodeList(@Query("isDetails") isDetails: Boolean = true): Response<List<BitThumbMarketCodeRes>>

    @GET("v1/ticker")
    suspend fun fetchBitThumbTicker(@Query("markets") markets: String): Response<List<BitThumbTickerRes>>

    @GET("v1/market/virtual_asset_warning")
    suspend fun fetchBiThumbWarning(): Response<List<BiThumbWarningRes>>
}