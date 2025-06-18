package org.jeonfeel.moeuibit2.data.repository.network

import kotlinx.coroutines.flow.Flow
import org.jeonfeel.moeuibit2.data.network.retrofit.ApiResult
import org.jeonfeel.moeuibit2.data.network.retrofit.networkCall
import org.jeonfeel.moeuibit2.data.network.retrofit.request.bithumb.BiThumbMinuteCandleReq
import org.jeonfeel.moeuibit2.data.network.retrofit.response.bitthumb.BiThumbMinuteCandleRes
import org.jeonfeel.moeuibit2.data.network.retrofit.response.bitthumb.BiThumbOrderBookRes
import org.jeonfeel.moeuibit2.data.network.retrofit.response.bitthumb.BiThumbWarningRes
import org.jeonfeel.moeuibit2.data.network.retrofit.response.bitthumb.BitThumbMarketCodeRes
import org.jeonfeel.moeuibit2.data.network.retrofit.response.bitthumb.BitThumbTickerRes
import org.jeonfeel.moeuibit2.data.network.retrofit.service.BitThumbService

class BiThumbRepository(private val bitThumbService: BitThumbService) {
    /**
     * 빗썸 market codes 요청
     */
    fun fetchBitThumbMarketCodeList(): Flow<ApiResult<List<BitThumbMarketCodeRes>>> {
        return networkCall { bitThumbService.fetchBitThumbMarketCodeList() }
    }

    /**
     * 빗썸 Ticker 데이터
     */
    fun fetchBitThumbTicker(marketCodes: String): Flow<ApiResult<List<BitThumbTickerRes>>> {
        return networkCall { bitThumbService.fetchBitThumbTicker(marketCodes) }
    }

    fun fetchBiThumbWarning(): Flow<ApiResult<List<BiThumbWarningRes>>> {
        return networkCall { bitThumbService.fetchBiThumbWarning() }
    }

    /**
     * 분봉 요청
     */
    fun fetchMinuteCandle(
        biThumbMinuteCandleReq: BiThumbMinuteCandleReq,
        unit: String
    ): Flow<ApiResult<List<BiThumbMinuteCandleRes>>> {
        return networkCall {
            bitThumbService.fetchBiThumbMinuteCandle(
                unit = unit,
                market = biThumbMinuteCandleReq.market,
                count = biThumbMinuteCandleReq.count,
            )
        }
    }

    fun fetchBiThumbOrderBook(market: String): Flow<ApiResult<BiThumbOrderBookRes>> {
        return networkCall {
            bitThumbService.fetchBiThumbOrderBook(market)
        }
    }
}