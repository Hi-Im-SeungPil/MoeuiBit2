package org.jeonfeel.moeuibit2.repository

import com.google.gson.JsonArray
import org.jeonfeel.moeuibit2.data.remote.retrofit.api.UpBitService
import retrofit2.Response

class OrderBookRepository(private val upBitService: UpBitService) {
    suspend fun getOrderBookService(market: String): Response<JsonArray> {
        return upBitService.getKrwOrderBook(market)
    }
}