package org.jeonfeel.moeuibit2.data.network.websocket.manager

import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.orhanobut.logger.Logger
import io.ktor.client.HttpClient
import io.ktor.client.plugins.websocket.WebSockets
import io.ktor.client.plugins.websocket.webSocket
import io.ktor.http.HttpMethod
import io.ktor.websocket.Frame
import io.ktor.websocket.WebSocketSession
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.serialization.json.Json
import org.jeonfeel.moeuibit2.constants.upbitTickerWebSocketMessage
import org.jeonfeel.moeuibit2.data.network.websocket.model.upbit.UpbitSocketTickerRes
import java.util.concurrent.atomic.AtomicReference

class ExchangeWebsocketManager() : DefaultLifecycleObserver {
    private val client = HttpClient {
        install(WebSockets)
    }
    private var isCancel = false

    private var session: WebSocketSession? = null
    private val socketState = AtomicReference(WebSocketState.DISCONNECTED)

    private val _tickerFlow = MutableStateFlow<UpbitSocketTickerRes?>(null)
    val tickerFlow: Flow<UpbitSocketTickerRes?> = _tickerFlow

    private val _showSnackBarState = MutableStateFlow(false)
    val showSnackBarState: Flow<Boolean> = _showSnackBarState

    private val json = Json {
        ignoreUnknownKeys = true
        explicitNulls = false
    }

    override fun onStart(owner: LifecycleOwner) {
        super.onStart(owner)
        Logger.e("onStart!!!!")
    }


    override fun onStop(owner: LifecycleOwner) {
        super.onStop(owner)
        Logger.e("onStop!!!!")
    }

    // WebSocket 메시지 Flow 생성
    suspend fun connectWebSocketFlow(marketCodes: String) {
        if (socketState.get() == WebSocketState.CONNECTED || socketState.get() == WebSocketState.CONNECTING) return

        socketState.set(WebSocketState.CONNECTING)

        try {
            client.webSocket(
                method = HttpMethod.Get,
                host = "api.upbit.com",
                path = "/websocket/v1"
            ) {
                println("WebSocket 연결 성공!")
                session = this
                socketState.set(WebSocketState.CONNECTED)
                isCancel = false

                val message = upbitTickerWebSocketMessage(marketCodes)
                session!!.send(Frame.Text(message))

                for (frame in incoming) {
                    when (frame) {
                        is Frame.Text -> {

                        }

                        is Frame.Binary -> {
                            val message =
                                json.decodeFromString<UpbitSocketTickerRes>(frame.data.decodeToString())
                            Logger.e(message.code)
                            _tickerFlow.emit(message)
                        }

                        else -> Unit
                    }
                }

                disConnectionSocket()
            }
        } catch (e: Exception) {
            println("${isCancel} WebSocket catch: ${e.localizedMessage}")
            disConnectionSocket()
        }
    }

    private fun disConnectionSocket() {
        socketState.set(WebSocketState.DISCONNECTED)
        session?.cancel()
        session = null
    }

    suspend fun onStart(marketCodes: String) {
        val message = upbitTickerWebSocketMessage(marketCodes)
        if (session != null && socketState.get() == WebSocketState.CONNECTED) {
            try {
                println("메시지 전송 성공: $marketCodes")
                session!!.send(Frame.Text(message))
            } catch (e: Exception) {
                println("메시지 전송 실패: ${e.localizedMessage}")
                disConnectionSocket()
            }
        } else {
            println("WebSocket이 연결되지 않았습니다. 재연결 시도 중...")
            disConnectionSocket()
        }
    }

    suspend fun onStop() {
        Logger.e(isCancel.toString())
        isCancel = true // 이걸로 정상종료 FLAG 해도될듯???
        disConnectionSocket()
    }

    fun getIsSocketConnected(): Boolean {
        return session != null && socketState.get() == WebSocketState.CONNECTED
    }
}