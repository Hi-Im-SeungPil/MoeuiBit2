package org.jeonfeel.moeuibit2.data.network.websocket.manager

import com.orhanobut.logger.Logger
import io.ktor.client.HttpClient
import io.ktor.client.plugins.websocket.WebSockets
import io.ktor.client.plugins.websocket.webSocket
import io.ktor.http.HttpMethod
import io.ktor.websocket.Frame
import io.ktor.websocket.WebSocketSession
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.serialization.json.Json
import org.jeonfeel.moeuibit2.constants.upbitTickerWebSocketMessage
import org.jeonfeel.moeuibit2.data.network.websocket.model.upbit.UpbitSocketTickerRes
import java.util.concurrent.atomic.AtomicReference

enum class WebSocketState {
    CONNECTED, CONNECTING, DISCONNECTED
}

class PortfolioWebsocketManager {
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
                session = this
                socketState.set(WebSocketState.CONNECTED)
                println("WebSocket 연결 성공!")
                isCancel = false

                val message = upbitTickerWebSocketMessage(marketCodes)
                session!!.send(Frame.Text(message))

                try {
                    // 메시지를 Flow로 방출
                    for (frame in incoming) {
                        when (frame) {
                            is Frame.Text -> {

                            }

                            is Frame.Binary -> {
                                val message =
                                    json.decodeFromString<UpbitSocketTickerRes>(frame.data.decodeToString())
                                _tickerFlow.emit(message)
                            }

                            else -> Unit
                        }
                    }
                } catch (e: Exception) {
                    Logger.e(e.localizedMessage.toString())
                    if (e.localizedMessage == "StandaloneCoroutine was cancelled") {
                        println("코루틴이 취소된 상태입니다. 추가 작업을 진행하지 않습니다.")
                        isCancel = true
                        return@webSocket
                    }

                    println("수신 처리 중 예외 발생: ${e.localizedMessage}")
                    handleDisconnection(marketCodes)
                } finally {
                    disConnectionSocket()
                    println("WebSocket 세션 종료")
                }
            }
        } catch (e: Exception) {
            println("WebSocket 연결 실패: ${e.localizedMessage}")
            // 취소된 상태에서 더 이상 처리하지 않도록 할 수 있습니다.
            if (e.localizedMessage == "StandaloneCoroutine was cancelled") {
                println("코루틴이 취소된 상태입니다. 추가 작업을 진행하지 않습니다.")
                isCancel = true
                return
            }

            handleDisconnection(marketCodes)
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
                session!!.send(Frame.Text(message))

                println("메시지 전송 성공: $marketCodes")
            } catch (e: Exception) {
                println("메시지 전송 실패: ${e.localizedMessage}")
                if (e.localizedMessage == "StandaloneCoroutine was cancelled") {
                    println("코루틴이 취소된 상태입니다. 추가 작업을 진행하지 않습니다.")
                    isCancel = true
                    return
                }
                handleDisconnection(marketCodes) // 연결 끊김 처리
            }
        } else {
            println("WebSocket이 연결되지 않았습니다. 재연결 시도 중...")
            handleDisconnection(marketCodes) // 연결 끊김 처리
        }
    }

    suspend fun onStop() {
        disConnectionSocket()
    }

    private suspend fun handleDisconnection(marketCodes: String) {
        socketState.set(WebSocketState.DISCONNECTED)
        session = null
        println("WebSocket 연결이 끊겼습니다. 재연결 시도 중...")

        var attempt = 0
        while (attempt < 5 && socketState.get() == WebSocketState.DISCONNECTED) {
            println("WebSocket 연결이 끊겼습니다. 재연결 시도 중... ($attempt)")

            try {
                delay(2000) // 재연결 대기
                connectWebSocketFlow(marketCodes)
                _showSnackBarState.emit(true) // 연결 성공 알림
                return
            } catch (e: Exception) {
                println("재연결 실패: ${e.localizedMessage}")
                attempt++
            }
        }

        if (socketState.get() == WebSocketState.DISCONNECTED) {
            _showSnackBarState.emit(false) // 연결 실패 알림
            println("재연결 시도 횟수 초과, 사용자에게 알림")
        }
    }

    fun getIsSocketConnected(): Boolean {
        return session != null && socketState.get() == WebSocketState.CONNECTED
    }
}