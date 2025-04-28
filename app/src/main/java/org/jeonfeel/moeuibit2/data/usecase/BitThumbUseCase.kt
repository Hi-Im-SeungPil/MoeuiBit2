package org.jeonfeel.moeuibit2.data.usecase

import com.tradingview.lightweightcharts.Logger
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.jeonfeel.moeuibit2.data.network.retrofit.ApiResult
import org.jeonfeel.moeuibit2.data.network.retrofit.response.bitthumb.BitThumbMarketCodeRes
import org.jeonfeel.moeuibit2.data.repository.local.LocalRepository
import org.jeonfeel.moeuibit2.data.repository.network.BitThumbRepository
import org.jeonfeel.moeuibit2.ui.base.BaseUseCase

class BitThumbUseCase(
    localRepository: LocalRepository,
    private val bitThumbRepository: BitThumbRepository,
) : BaseUseCase() {

    suspend fun callTest(): Flow<Unit> {
        Logger.e("calltest usecase")
        return bitThumbRepository.getUpbitMarketCodeList().map { res ->
            when (res.status) {
                ApiResult.Status.SUCCESS -> {
                    Logger.e("bitthumb -> " + res.data.toString())
                }

                ApiResult.Status.API_ERROR -> {
                    Logger.e("bitthumb error -> " + res.message.toString())
                }

                else -> {
                    Logger.e(res.message.toString())
                }
            }
        }
//        bitThumbRepository.getMarketTicker()
    }
}