package org.jeonfeel.moeuibit2.data.repository.network

import kotlinx.coroutines.flow.Flow
import org.jeonfeel.moeuibit2.data.network.retrofit.ApiResult
import org.jeonfeel.moeuibit2.data.network.retrofit.networkCall
import org.jeonfeel.moeuibit2.data.network.retrofit.request.bithumb.BiThumbDayCandleReq
import org.jeonfeel.moeuibit2.data.network.retrofit.request.bithumb.BiThumbMinuteCandleReq
import org.jeonfeel.moeuibit2.data.network.retrofit.request.bithumb.BiThumbMonthCandleReq
import org.jeonfeel.moeuibit2.data.network.retrofit.request.bithumb.BiThumbWeekCandleReq
import org.jeonfeel.moeuibit2.data.network.retrofit.response.bitthumb.BiThumbDayCandleRes
import org.jeonfeel.moeuibit2.data.network.retrofit.response.bitthumb.BiThumbMinuteCandleRes
import org.jeonfeel.moeuibit2.data.network.retrofit.response.bitthumb.BiThumbMonthCandleRes
import org.jeonfeel.moeuibit2.data.network.retrofit.response.bitthumb.BiThumbOrderBookRes
import org.jeonfeel.moeuibit2.data.network.retrofit.response.bitthumb.BiThumbWarningRes
import org.jeonfeel.moeuibit2.data.network.retrofit.response.bitthumb.BiThumbWeekCandleRes
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

    fun fetchBiThumbOrderBook(market: String): Flow<ApiResult<List<BiThumbOrderBookRes>>> {
        return networkCall {
            bitThumbService.fetchBiThumbOrderBook(market)
        }
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
                to = biThumbMinuteCandleReq.to.ifEmpty { null }
            )
        }
    }

    /**
     * 일봉 요청
     */
    fun fetchDayCandle(
        biThumbDayCandleReq: BiThumbDayCandleReq,
    ): Flow<ApiResult<List<BiThumbDayCandleRes>>> {
        return networkCall {
            bitThumbService.fetchBiThumbDaysCandle(
                market = biThumbDayCandleReq.market,
                count = biThumbDayCandleReq.count,
                to = biThumbDayCandleReq.to.ifEmpty { null }
            )
        }
    }

    /**
     * 주봉 요청
     */
    fun fetchWeekCandle(
        biThumbWeekCandleReq: BiThumbWeekCandleReq,
    ): Flow<ApiResult<List<BiThumbWeekCandleRes>>> {
        return networkCall {
            bitThumbService.fetchBiThumbWeeksCandle(
                market = biThumbWeekCandleReq.market,
                count = biThumbWeekCandleReq.count,
                to = biThumbWeekCandleReq.to.ifEmpty { null }
            )
        }
    }

    /**
     * 월봉 요청
     */
    fun fetchMonthCandle(
        biThumbMonthCandleReq: BiThumbMonthCandleReq,
    ): Flow<ApiResult<List<BiThumbMonthCandleRes>>> {
        return networkCall {
            bitThumbService.fetchBiThumbMonthsCandle(
                market = biThumbMonthCandleReq.market,
                count = biThumbMonthCandleReq.count,
                to = biThumbMonthCandleReq.to.ifEmpty { null }
            )
        }
    }
}