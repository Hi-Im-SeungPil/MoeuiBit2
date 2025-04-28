package org.jeonfeel.moeuibit2.data.repository.network

import com.tradingview.lightweightcharts.Logger
import kotlinx.coroutines.flow.Flow
import org.jeonfeel.moeuibit2.data.network.retrofit.ApiResult
import org.jeonfeel.moeuibit2.data.network.retrofit.networkCall
import org.jeonfeel.moeuibit2.data.network.retrofit.request.upbit.GetUpbitMarketTickerReq
import org.jeonfeel.moeuibit2.data.network.retrofit.response.bitthumb.BitThumbMarketCodeRes
import org.jeonfeel.moeuibit2.data.network.retrofit.response.bitthumb.BitThumbTickerRes
import org.jeonfeel.moeuibit2.data.network.retrofit.service.BitThumbService

class BitThumbRepository(private val bitThumbService: BitThumbService) {
    /**
     * 빗썸 market codes 요청
     */
    suspend fun getUpbitMarketCodeList(): Flow<ApiResult<List<BitThumbMarketCodeRes>>> {
        Logger.e("calltest usecase2")
        return networkCall { bitThumbService.getMarketList() }
    }

    /**
     * 빗썸 Ticker 데이터
     */
    suspend fun getMarketTicker(marketCodes: String): Flow<ApiResult<List<BitThumbTickerRes>>> {
        return networkCall { bitThumbService.getMarketTicker(marketCodes) }
    }

//    /**
//     * OrderBook 데이터
//     */
//    suspend fun getOrderBook(getUpbitOrderBookReq: GetUpbitOrderBookReq): ApiResult<List<GetUpbitOrderBookRes>> {
//        return networkCall(upBitService.getOrderBook(getUpbitOrderBookReq.market))
//    }
//
//    /**
//     * 분봉 요청
//     */
//    suspend fun getMinuteCandle(
//        minute: String,
//        market: String,
//        count: String = "200",
//        time: String = "",
//    ): ApiResult<List<GetChartCandleRes>> {
//        return networkCall(upBitService.getMinuteCandle(minute, market, count, time))
//    }
//
//    /**
//     * 일,주,월봉 요청
//     */
//    suspend fun getOtherCandle(
//        candleType: String,
//        market: String,
//        count: String = "200",
//        time: String = "",
//    ): ApiResult<List<GetChartCandleRes>> {
//        return networkCall(upBitService.getOtherCandle(candleType, market, count, time))
//    }
}