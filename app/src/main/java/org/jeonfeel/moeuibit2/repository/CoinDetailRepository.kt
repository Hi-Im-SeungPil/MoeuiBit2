package org.jeonfeel.moeuibit2.repository

import com.google.gson.JsonArray
import org.jeonfeel.moeuibit2.data.remote.retrofit.api.UpBitService
import retrofit2.Response

class CoinDetailRepository(private val upBitService: UpBitService) {
    suspend fun getOrderBookService(market: String): Response<JsonArray> {
        return upBitService.getKrwOrderBook(market)
    }

    suspend fun getCandleService(minute: String, market: String, count: String = "200", time: String = "") : Response<JsonArray> {
        return upBitService.getCandle(minute,market,count,time)
    }

}