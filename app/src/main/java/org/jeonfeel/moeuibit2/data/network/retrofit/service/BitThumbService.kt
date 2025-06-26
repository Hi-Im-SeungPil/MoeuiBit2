package org.jeonfeel.moeuibit2.data.network.retrofit.service

import org.jeonfeel.moeuibit2.data.network.retrofit.request.bithumb.BiThumbDayCandleReq
import org.jeonfeel.moeuibit2.data.network.retrofit.request.bithumb.BiThumbMinuteCandleReq
import org.jeonfeel.moeuibit2.data.network.retrofit.response.bitthumb.BiThumbDayCandleRes
import org.jeonfeel.moeuibit2.data.network.retrofit.response.bitthumb.BiThumbMinuteCandleRes
import org.jeonfeel.moeuibit2.data.network.retrofit.response.bitthumb.BiThumbMonthCandleRes
import org.jeonfeel.moeuibit2.data.network.retrofit.response.bitthumb.BiThumbOrderBookRes
import org.jeonfeel.moeuibit2.data.network.retrofit.response.bitthumb.BiThumbWarningRes
import org.jeonfeel.moeuibit2.data.network.retrofit.response.bitthumb.BiThumbWeekCandleRes
import org.jeonfeel.moeuibit2.data.network.retrofit.response.bitthumb.BitThumbMarketCodeRes
import org.jeonfeel.moeuibit2.data.network.retrofit.response.bitthumb.BitThumbTickerRes
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import retrofit2.http.QueryMap

interface BitThumbService {
    @GET("v1/market/all")
    suspend fun fetchBitThumbMarketCodeList(@Query("isDetails") isDetails: Boolean = true): Response<List<BitThumbMarketCodeRes>>

    @GET("v1/ticker")
    suspend fun fetchBitThumbTicker(@Query("markets") markets: String): Response<List<BitThumbTickerRes>>

    @GET("v1/market/virtual_asset_warning")
    suspend fun fetchBiThumbWarning(): Response<List<BiThumbWarningRes>>

    @GET("v1/candles/minutes/{unit}")
    suspend fun fetchBiThumbMinuteCandle(
        @Path("unit") unit: String,
        @Query("market") market: String,
        @Query("count") count: String,
        @Query("to") to: String
    ): Response<List<BiThumbMinuteCandleRes>>

    @GET("v1/candles/days")
    suspend fun fetchBiThumbDaysCandle(
        @Query("market") market: String,
        @Query("count") count: String,
        @Query("to") to: String,
        @Query("convertingPriceUnit") convertingPriceUnit: String = "KRW"
    ): Response<List<BiThumbDayCandleRes>>

    @GET("v1/candles/weeks")
    suspend fun fetchBiThumbWeeksCandle(
        @Query("market") market: String,
        @Query("count") count: String,
        @Query("to") to: String,
    ): Response<List<BiThumbWeekCandleRes>>

    @GET("v1/candles/months")
    suspend fun fetchBiThumbMonthsCandle(
        @Query("market") market: String,
        @Query("count") count: String,
        @Query("to") to: String,
    ): Response<List<BiThumbMonthCandleRes>>

    @GET("v1/orderbook")
    suspend fun fetchBiThumbOrderBook(
        @Query("markets") market: String
    ): Response<List<BiThumbOrderBookRes>>
}