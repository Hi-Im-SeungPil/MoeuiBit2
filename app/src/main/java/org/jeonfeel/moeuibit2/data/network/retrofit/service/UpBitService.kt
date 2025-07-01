package org.jeonfeel.moeuibit2.data.network.retrofit.service

import org.jeonfeel.moeuibit2.data.network.retrofit.response.upbit.GetChartCandleRes
import org.jeonfeel.moeuibit2.data.network.retrofit.response.upbit.GetUpbitMarketTickerRes
import org.jeonfeel.moeuibit2.data.network.retrofit.response.upbit.GetUpbitOrderBookRes
import org.jeonfeel.moeuibit2.data.network.retrofit.response.upbit.UpbitMarketCodeRes
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface UpBitService {
    @GET("v1/market/all?isDetails=true")
    suspend fun getMarketCodeList(): Response<List<UpbitMarketCodeRes>>

    @GET("v1/ticker")
    suspend fun getMarketTicker(@Query("markets") markets: String): Response<List<GetUpbitMarketTickerRes>>

    @GET("v1/orderbook")
    suspend fun getOrderBook(@Query("markets") market: String): Response<List<GetUpbitOrderBookRes>>

    @GET("v1/candles/minutes/{minute}")
    suspend fun getMinuteCandle(
        @Path("minute") minute: String,
        @Query("market") market: String,
        @Query("count") count: String,
        @Query("to") time: String? = null
    ): Response<List<GetChartCandleRes>>

    @GET("v1/candles/{period}")
    suspend fun getOtherCandle(
        @Path("period") period: String,
        @Query("market") market: String,
        @Query("count") count: String,
        @Query("to") time: String
    ): Response<List<GetChartCandleRes>>
}