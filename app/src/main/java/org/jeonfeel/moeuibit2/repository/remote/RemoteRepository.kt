package org.jeonfeel.moeuibit2.repository.remote

import com.google.gson.JsonArray
import org.jeonfeel.moeuibit2.data.remote.retrofit.api.UpBitService
import retrofit2.Response

class RemoteRepository(private val upBitService: UpBitService) {
    suspend fun getOrderBookService(market: String): Response<JsonArray> {
        return upBitService.getKrwOrderBook(market)
    }

    suspend fun getMinuteCandleService(minute: String, market: String, count: String = "200", time: String = "") : Response<JsonArray> {
        return upBitService.getMinuteCandle(minute,market,count,time)
    }

    suspend fun getOtherCandleService(candleType: String, market: String, count: String = "200", time: String = "") : Response<JsonArray> {
        return upBitService.getOtherCandle(candleType,market,count,time)
    }

    suspend fun getMarketCodeService(): Response<JsonArray> {
        return upBitService.getMarketCode()
    }

    suspend fun getKrwTickerService(markets: String): Response<JsonArray> {
        return upBitService.getKrwTicker(markets)
    }

}