package org.jeonfeel.moeuibit2.data.repository.network

import org.jeonfeel.moeuibit2.data.network.retrofit.service.BitThumbService

class BitthumbRepository(private val bitthumbService: BitThumbService) {
//    /**
//     * 빗썸 마켓코드 요청
//     */
//    suspend fun getBitThumbKRWMarketCodeService(): Flow<ApiResult<JsonObject>> {
//        return flow {
//            emit(ApiResult.loading())
//            try {
//                emit(networkCall(bitthumbService.getKRWMarketCode()))
//            } catch (e: Exception) {
//                emit(ApiResult.error(e))
//            }
//        }
//    }
//
//    /**
//     * 빗썸 마켓코드 요청
//     */
//    suspend fun getBitThumbBTCMarketCodeService(): Flow<ApiResult<JsonObject>> {
//        return flow {
//            emit(ApiResult.loading())
//            try {
//                emit(networkCall(bitthumbService.getBTCMarketCode()))
//            } catch (e: Exception) {
//                emit(ApiResult.error(e))
//            }
//        }
//    }
//
//    /**
//     * 빗썸 마켓코드 요청
//     */
//    suspend fun getBitthumbTickerUnit(market: String): Flow<ApiResult<JsonObject>> {
//        return flow {
//            emit(ApiResult.loading())
//            try {
//                emit(networkCall(bitthumbService.getTickerUnit(Utils.upbitMarketToBitthumbMarket(market))))
//            } catch (e: Exception) {
//                emit(ApiResult.error(e))
//            }
//        }
//    }
//
//    /**
//     * 빗썸 코인 이름 요청
//     */
//    suspend fun getBitthumbCoinNameService(): Flow<ApiResult<JsonObject>> {
//        return flow {
//            emit(ApiResult.loading())
//            try {
//                emit(networkCall(bitthumbService.getCoinName()))
//            } catch (e: Exception) {
//                emit(ApiResult.error(e))
//            }
//        }
//    }
//
//    /**
//     * 일,주,월봉 요청
//     */
//    suspend fun getBitthumbChart(candleType: String, market: String ): Response<BitthumbChartModel> {
//        return bitthumbService.getChartData(market = market, candleType = candleType)
//    }
}