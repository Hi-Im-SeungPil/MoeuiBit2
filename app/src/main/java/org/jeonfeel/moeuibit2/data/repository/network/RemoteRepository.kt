package org.jeonfeel.moeuibit2.data.repository.network

import com.google.gson.JsonArray
import com.google.gson.JsonObject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import org.jeonfeel.moeuibit2.data.network.retrofit.ApiResult
import org.jeonfeel.moeuibit2.data.network.retrofit.api.BinanceService
import org.jeonfeel.moeuibit2.data.network.retrofit.api.BitThumbService
import org.jeonfeel.moeuibit2.data.network.retrofit.api.USDTService
import org.jeonfeel.moeuibit2.data.network.retrofit.api.UpBitService
import org.jeonfeel.moeuibit2.data.network.retrofit.model.bitthumb.BitthumbChartModel
import org.jeonfeel.moeuibit2.data.network.retrofit.response.upbit.GetChartCandleRes
import org.jeonfeel.moeuibit2.utils.Utils
import retrofit2.Response

class RemoteRepository(
        private val upBitService: UpBitService,
        private val usdtService: USDTService,
        private val binanceService: BinanceService,
        private val bitThumbService: BitThumbService
) {

    private fun <T> call(response: Response<T>): ApiResult<T> {
        return try {
            if (response.isSuccessful) {
                ApiResult.success(response.body())
            } else {
                ApiResult.error(null, response.errorBody()?.string() ?: "")
            }
        } catch (e: Exception) {
            ApiResult.error(e)
        }
    }

    /**
     * 분봉 요청
     */
    suspend fun getMinuteCandleService(
            minute: String,
            market: String,
            count: String = "200",
            time: String = "",
    ): Response<List<GetChartCandleRes>> {
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
    ): Response<List<GetChartCandleRes>> {
        return upBitService.getOtherCandle(candleType, market, count, time)
    }

    /**
     * 빗썸 마켓코드 요청
     */
    suspend fun getBitThumbKRWMarketCodeService(): Flow<ApiResult<JsonObject>> {
        return flow {
            emit(ApiResult.loading())
            try {
                emit(call(bitThumbService.getKRWMarketCode()))
            } catch (e: Exception) {
                emit(ApiResult.error(e))
            }
        }
    }

    /**
     * 빗썸 마켓코드 요청
     */
    suspend fun getBitThumbBTCMarketCodeService(): Flow<ApiResult<JsonObject>> {
        return flow {
            emit(ApiResult.loading())
            try {
                emit(call(bitThumbService.getBTCMarketCode()))
            } catch (e: Exception) {
                emit(ApiResult.error(e))
            }
        }
    }

    /**
     * 빗썸 마켓코드 요청
     */
    suspend fun getBitthumbTickerUnit(market: String): Flow<ApiResult<JsonObject>> {
        return flow {
            emit(ApiResult.loading())
            try {
                emit(call(bitThumbService.getTickerUnit(Utils.upbitMarketToBitthumbMarket(market))))
            } catch (e: Exception) {
                emit(ApiResult.error(e))
            }
        }
    }

    /**
     * 빗썸 코인 이름 요청
     */
    suspend fun getBitthumbCoinNameService(): Flow<ApiResult<JsonObject>> {
        return flow {
            emit(ApiResult.loading())
            try {
                emit(call(bitThumbService.getCoinName()))
            } catch (e: Exception) {
                emit(ApiResult.error(e))
            }
        }
    }

    /**
     * 일,주,월봉 요청
     */
    suspend fun getBitthumbChart(candleType: String, market: String ): Response<BitthumbChartModel> {
        return bitThumbService.getChartData(market = market, candleType = candleType)
    }

    /**
     * 금일 usdt 가격
     */
    suspend fun getUSDTPrice(): Flow<ApiResult<JsonObject>> {
        return flow {
            emit(ApiResult.loading())
            try {
                emit(call(usdtService.getUSDTPrice()))
            } catch (e: Exception) {
                emit(ApiResult.error(e))
            }
        }
    }
}