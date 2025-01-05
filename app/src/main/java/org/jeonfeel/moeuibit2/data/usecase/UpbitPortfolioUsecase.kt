package org.jeonfeel.moeuibit2.data.usecase

import android.content.Context
import com.jeremy.thunder.Thunder
import com.jeremy.thunder.event.converter.ConverterType
import com.jeremy.thunder.makeWebSocketCore
import com.orhanobut.logger.Logger
import io.ktor.client.HttpClient
import io.ktor.client.plugins.websocket.WebSockets
import io.ktor.client.plugins.websocket.webSocket
import io.ktor.http.HttpMethod
import io.ktor.websocket.Frame
import kotlinx.coroutines.flow.Flow
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.jeonfeel.moeuibit2.constants.upbitTickerWebSocketMessage
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
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class UpbitPortfolioUsecase @Inject constructor(
    private val upbitRepository: UpbitRepository,
    private val localRepository: LocalRepository,
    private val okHttpClient: OkHttpClient,
    private val context: Context
) : BaseUseCase() {
    private var socketService: UpbitPortfolioSocketService? = null

    val client = HttpClient {
        install(WebSockets) // WebSocket 모듈 설치
    }

    fun onStart() {
//        socketService = Thunder.Builder()
//            .setWebSocketFactory(okHttpClient.makeWebSocketCore("wss://api.upbit.com/websocket/v1"))
//            .setApplicationContext(context)
//            .setConverterType(ConverterType.Serialization)
//            .build()
//            .create()
    }

    fun onStop() {
//        Logger.e("portfolio onStop")
//        socketService = null
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
        try {
            client.webSocket(
                method = HttpMethod.Get,
                host = "api.upbit.com",
                path = "/websocket/v1"
            ) {
                println("WebSocket 연결 성공!")

                // 요청 JSON으로 변환 후 서버에 전송
                send(Frame.Text(upbitTickerWebSocketMessage(marketCodes.joinToString(separator = ","))))
                Logger.e(upbitTickerWebSocketMessage(marketCodes.joinToString (separator = ",")))

                // 메시지 수신
                for (frame in incoming) {
                    when (frame) {
                        is Frame.Text -> {
//                            println("받은 메시지: ${frame.readText()}")
                        }

                        is Frame.Binary -> {
                            println("받은 바이너리 데이터: ${frame.data.decodeToString()}")
                        }

                        else -> Unit
                    }
                }
            }
        } catch (e: Exception) {
            println("WebSocket 에러: ${e.localizedMessage}")
        } finally {
//            client.close()
//            println("클라이언트 종료")
        }

//        socketService?.requestUpbitTickerRequest(
//            listOf(
//                RequestTicketField(ticket = UUID.randomUUID().toString()),
//                RequestTypeField(
//                    type = "ticker",
//                    codes = marketCodes,
//                ),
//                RequestFormatField()
//            )
//        )
    }

    fun observeTickerResponse(): Flow<UpbitSocketTickerRes>? {
        return socketService?.collectUpbitTicker()
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
