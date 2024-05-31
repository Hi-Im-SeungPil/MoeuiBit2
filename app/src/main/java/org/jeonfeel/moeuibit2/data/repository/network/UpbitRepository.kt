package org.jeonfeel.moeuibit2.data.repository.network

import com.google.gson.JsonArray
import org.jeonfeel.moeuibit2.data.network.retrofit.ApiResult
import org.jeonfeel.moeuibit2.data.network.retrofit.api.UpBitService
import org.jeonfeel.moeuibit2.data.network.retrofit.networkCall
import org.jeonfeel.moeuibit2.data.network.retrofit.request.upbit.GetUpbitMarketTickerReq
import org.jeonfeel.moeuibit2.data.network.retrofit.response.upbit.GetUpbitMarketTickerRes
import org.jeonfeel.moeuibit2.data.network.retrofit.response.upbit.UpbitMarketCodeRes
import retrofit2.Response

class UpbitRepository(
    private val upBitService: UpBitService
) {
    /**
     * 업비트 마켓코드 요청
     */
    suspend fun getUpbitMarketCodeList(): ApiResult<List<UpbitMarketCodeRes>> {
        return networkCall(upBitService.getMarketCodeList())
    }

    /**
     * 거래소 Ticker 데이터
     */
    suspend fun getMarketTicker(getUpbitMarketTickerReq: GetUpbitMarketTickerReq): ApiResult<List<GetUpbitMarketTickerRes>> {
        return networkCall(upBitService.getMarketTicker(getUpbitMarketTickerReq.marketCodes))
    }

    /**
     * 분봉 요청
     */
    suspend fun getMinuteCandleService(
        minute: String,
        market: String,
        count: String = "200",
        time: String = "",
    ): Response<JsonArray> {
        return upBitService.getMinuteCandle(minute, market, count, time)
    }

    /**
     * 일,주,월봉 요청
     */
    suspend fun getOtherCandleService(
        candleType: String,
        market: String,
        count: String = "200",
        time: String = "",
    ): Response<JsonArray> {
        return upBitService.getOtherCandle(candleType, market, count, time)
    }
}