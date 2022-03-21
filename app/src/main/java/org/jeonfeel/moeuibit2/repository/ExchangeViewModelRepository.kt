package org.jeonfeel.moeuibit2.repository

import com.google.gson.JsonArray
import org.jeonfeel.moeuibit2.R
import org.jeonfeel.moeuibit2.data.remote.retrofit.api.RetrofitUpBit
import org.jeonfeel.moeuibit2.data.remote.retrofit.api.UpBitService
import org.jeonfeel.moeuibit2.viewmodel.ExchangeViewModel
import retrofit2.Call

class ExchangeViewModelRepository(private val upBitService: UpBitService) {

    private val TAG = ExchangeViewModel::class.java.simpleName

    suspend fun getMarketCodeService(): JsonArray {
        return upBitService.getMarketCode()
    }

    suspend fun getKrwTickerService(markets: String): JsonArray {
        return upBitService.getKrwTicker(markets)
    }
}