package org.jeonfeel.moeuibit2.data.repository.network

import kotlinx.coroutines.flow.Flow
import org.jeonfeel.moeuibit2.data.network.retrofit.ApiResult
import org.jeonfeel.moeuibit2.data.network.retrofit.service.UpBitService
import org.jeonfeel.moeuibit2.data.network.retrofit.networkCall
import org.jeonfeel.moeuibit2.data.network.retrofit.request.upbit.GetUpbitMarketTickerReq
import org.jeonfeel.moeuibit2.data.network.retrofit.request.upbit.GetUpbitOrderBookReq
import org.jeonfeel.moeuibit2.data.network.retrofit.response.upbit.GetChartCandleRes
import org.jeonfeel.moeuibit2.data.network.retrofit.response.upbit.GetUpbitMarketTickerRes
import org.jeonfeel.moeuibit2.data.network.retrofit.response.upbit.GetUpbitOrderBookRes
import org.jeonfeel.moeuibit2.data.network.retrofit.response.upbit.UpbitMarketCodeRes
import org.jeonfeel.moeuibit2.ui.common.ResultState

class UpbitRepository(
    private val upBitService: UpBitService
) {
    /**
     * 업비트 market codes 요청
     */
    suspend fun fetchUpbitMarketCodeList(): Flow<ApiResult<List<UpbitMarketCodeRes>>> {
        return networkCall { upBitService.getMarketCodeList() }
    }

    /**
     * NEW 업비트 market codes 요청
     */
    suspend fun getUpbitMarketCodeList(): ApiResult<List<UpbitMarketCodeRes>> {
        return networkCall(upBitService.getMarketCodeList())
    }

    /**
     * Ticker 데이터
     */
    suspend fun getMarketTicker(getUpbitMarketTickerReq: GetUpbitMarketTickerReq): ApiResult<List<GetUpbitMarketTickerRes>> {
        return networkCall(upBitService.getMarketTicker(getUpbitMarketTickerReq.marketCodes))
    }

    /**
     * Ticker 데이터 NEW
     */
    suspend fun fetchMarketTicker(getUpbitMarketTickerReq: GetUpbitMarketTickerReq): Flow<ApiResult<List<GetUpbitMarketTickerRes>>> {
        return networkCall { upBitService.getMarketTicker(getUpbitMarketTickerReq.marketCodes) }
    }

    /**
     * OrderBook 데이터
     */
    suspend fun getOrderBook(getUpbitOrderBookReq: GetUpbitOrderBookReq): ApiResult<List<GetUpbitOrderBookRes>> {
        return networkCall(upBitService.getOrderBook(getUpbitOrderBookReq.market))
    }

    /**
     * NEW 분봉 요청
     */
    suspend fun fetchMinuteCandle(
        minute: String,
        market: String,
        count: String = "200",
        time: String = "",
    ): Flow<ApiResult<List<GetChartCandleRes>>> {
        return networkCall { upBitService.getMinuteCandle(minute, market, count, time) }
    }

    /**
     * 분봉 요청
     */
    suspend fun getMinuteCandle(
        minute: String,
        market: String,
        count: String = "200",
        time: String = "",
    ): ApiResult<List<GetChartCandleRes>> {
        return networkCall(upBitService.getMinuteCandle(minute, market, count, time))
    }

    /**
     * 일,주,월봉 요청
     */
    suspend fun getOtherCandle(
        candleType: String,
        market: String,
        count: String = "200",
        time: String = "",
    ): ApiResult<List<GetChartCandleRes>> {
        return networkCall(upBitService.getOtherCandle(candleType, market, count, time))
    }
}