package org.jeonfeel.moeuibit2.repository

import com.google.gson.JsonArray
import org.jeonfeel.moeuibit2.data.remote.retrofit.api.RetrofitUpBit
import org.jeonfeel.moeuibit2.viewmodel.ExchangeViewModel
import retrofit2.Call

class ExchangeViewModelRepository {

    private val TAG = ExchangeViewModel::class.java.simpleName

    fun getMarketCodeCall(): Call<JsonArray> {
        return RetrofitUpBit().getMarketCodeCall()
    }

    fun getKrwTickerCall(markets: String): Call<JsonArray> {
        return RetrofitUpBit().getKrwTickerCall(markets)
    }
}