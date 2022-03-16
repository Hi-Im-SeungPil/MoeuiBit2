package org.jeonfeel.moeuibit2.data.remote.retrofit.api

import com.google.gson.JsonArray
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.Call

class RetrofitUpBit {
    private val TAG = RetrofitUpBit::class.java.simpleName
    private val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl("https://api.upbit.com/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    //Call 객체를 반환.
    fun getMarketCodeCall(): Call<JsonArray> {
        val upBitService =
            retrofit.create(UpBitService::class.java)
        return upBitService.getMarketCode()
    }

    fun getKrwTickerCall(markets: String): Call<JsonArray> {
        val upBitService =
            retrofit.create(UpBitService::class.java)
        return upBitService.getKrwTicker(markets)
    }
}