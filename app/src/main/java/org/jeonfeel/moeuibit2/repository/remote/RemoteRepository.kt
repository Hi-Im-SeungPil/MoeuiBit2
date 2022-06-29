package org.jeonfeel.moeuibit2.repository.remote

import com.google.gson.JsonArray
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import org.jeonfeel.moeuibit2.data.remote.retrofit.ApiResult
import org.jeonfeel.moeuibit2.data.remote.retrofit.api.UpBitService
import retrofit2.Response

class RemoteRepository(private val upBitService: UpBitService) {

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
    suspend fun getMinuteCandleService(minute: String, market: String, count: String = "200", time: String = "") : Response<JsonArray> {
        return upBitService.getMinuteCandle(minute,market,count,time)
    }

    /**
     * 일,주,월봉 요청
     */
    suspend fun getOtherCandleService(candleType: String, market: String, count: String = "200", time: String = "") : Response<JsonArray> {
        return upBitService.getOtherCandle(candleType,market,count,time)
    }

    /**
     * 마켓코드 요청
     */
    suspend fun getMarketCodeService(): Flow<ApiResult<JsonArray>> {
        return flow {
            emit(ApiResult.loading())
            try {
                emit(call(upBitService.getMarketCode()))
            } catch (e: Exception) {
                emit(ApiResult.error(e))
            }
        }
    }

    /**
     * 원화 거래소 데이터
     */
    suspend fun getKrwTickerService(markets: String): Flow<ApiResult<JsonArray>> {
        return flow {
            emit(ApiResult.loading())
            try {
                emit(call(upBitService.getKrwTicker(markets)))
            } catch (e: Exception) {
                emit(ApiResult.error(e))
            }
        }
    }
}