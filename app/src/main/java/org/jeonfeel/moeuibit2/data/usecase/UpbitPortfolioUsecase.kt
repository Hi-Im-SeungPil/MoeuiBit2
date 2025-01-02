package org.jeonfeel.moeuibit2.data.usecase

import android.content.Context
import com.jeremy.thunder.Thunder
import com.jeremy.thunder.event.converter.ConverterType
import com.jeremy.thunder.makeWebSocketCore
import kotlinx.coroutines.flow.Flow
import okhttp3.OkHttpClient
import org.jeonfeel.moeuibit2.data.local.room.dao.UserDAO
import org.jeonfeel.moeuibit2.data.local.room.entity.MyCoin
import org.jeonfeel.moeuibit2.data.network.retrofit.request.upbit.GetUpbitMarketTickerReq
import org.jeonfeel.moeuibit2.data.network.websocket.model.upbit.UpbitSocketTickerRes
import org.jeonfeel.moeuibit2.data.network.websocket.thunder.request.upbit.RequestFormatField
import org.jeonfeel.moeuibit2.data.network.websocket.thunder.request.upbit.RequestTicketField
import org.jeonfeel.moeuibit2.data.network.websocket.thunder.request.upbit.RequestTypeField
import org.jeonfeel.moeuibit2.data.network.websocket.thunder.service.upbit.UpbitPortfolioSocketService
import org.jeonfeel.moeuibit2.data.repository.local.LocalRepository
import org.jeonfeel.moeuibit2.data.repository.network.UpbitRepository
import org.jeonfeel.moeuibit2.ui.base.BaseUseCase
import java.util.UUID
import javax.inject.Inject

class UpbitPortfolioUsecase @Inject constructor(
    private val upbitRepository: UpbitRepository,
    private val localRepository: LocalRepository,
    private val okHttpClient: OkHttpClient,
    private val context: Context
) : BaseUseCase() {

    private var socketService: UpbitPortfolioSocketService? = null

    fun onStart() {
        socketService = Thunder.Builder()
            .setWebSocketFactory(okHttpClient.makeWebSocketCore("wss://api.upbit.com/websocket/v1"))
            .setApplicationContext(context)
            .setConverterType(ConverterType.Serialization)
            .build()
            .create()
    }

    fun onStop() {
        socketService = null
    }

    /**
     * 업비트 마켓 코드 조회
     */
    suspend fun getMarketCode(): Flow<Any> {
        return requestApiResult(
            result = upbitRepository.getUpbitMarketCodeList(),
            onSuccess = { result ->
                result
            }
        )
    }

    suspend fun getMarketTicker(
        getUpbitMarketTickerReq: GetUpbitMarketTickerReq,
        isList: Boolean = false
    ): Flow<Any> {
        return requestApiResult(
            result = upbitRepository.getMarketTicker(getUpbitMarketTickerReq),
            onSuccess = { ticker ->
                if (isList) {
                    ticker
                } else {
                    ticker[0]
                }
            }
        )
    }

    /**
     * 업비트 Ticker 구독 요청
     */
    suspend fun requestSubscribeTicker(
        marketCodes: List<String>,
    ) {
        socketService?.requestUpbitTickerRequest(
            listOf(
                RequestTicketField(ticket = UUID.randomUUID().toString()),
                RequestTypeField(
                    type = "ticker",
                    codes = marketCodes,
                ),
                RequestFormatField()
            )
        )
    }

    fun observeTickerResponse(): Flow<UpbitSocketTickerRes>? {
        return socketService?.collectUpbitTrade()
    }

    suspend fun getUserSeedMoney(): Long {
        return localRepository.getUserDao().all?.krw ?: 0L
    }

    suspend fun getMyCoins(): List<MyCoin?> {
        return localRepository.getMyCoinDao().all ?: emptyList()
    }

    suspend fun getUserDao(): UserDAO {
        return localRepository.getUserDao()
    }

    suspend fun removeCoin(market: String) {
        localRepository.getFavoriteDao().delete(market)
        localRepository.getMyCoinDao().delete(market)
        localRepository.getTransactionInfoDao().delete(market)
    }
}
