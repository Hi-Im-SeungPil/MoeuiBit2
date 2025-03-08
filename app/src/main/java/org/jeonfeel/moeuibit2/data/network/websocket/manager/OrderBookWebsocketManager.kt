package org.jeonfeel.moeuibit2.data.network.websocket.manager

import com.orhanobut.logger.Logger
import io.ktor.client.HttpClient
import io.ktor.client.plugins.websocket.WebSockets
import io.ktor.client.plugins.websocket.webSocket
import io.ktor.http.HttpMethod
import io.ktor.websocket.Frame
import io.ktor.websocket.WebSocketSession
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import org.jeonfeel.moeuibit2.constants.upbitOrderBookWebSocketMessage
import org.jeonfeel.moeuibit2.constants.upbitTickerWebSocketMessage
import org.jeonfeel.moeuibit2.data.network.websocket.model.upbit.UpbitSocketOrderBookRes
import org.jeonfeel.moeuibit2.utils.Utils
import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.atomic.AtomicReference

class OrderBookWebsocketManager {
    private val client = HttpClient {
        install(WebSockets)
    }

    @Volatile
    private var isCancel = false

    @Volatile
    private var isBackGround = false

    private val retryCount = AtomicInteger(0)
    private var session: WebSocketSession? = null
    private var receiveChannel: ReceiveChannel<Frame>? = null
    private val socketState = AtomicReference(WebSocketState.DISCONNECTED)

    private val _tickerFlow = MutableStateFlow<UpbitSocketOrderBookRes?>(null)
    val tickerFlow: Flow<UpbitSocketOrderBookRes?> = _tickerFlow

    private val _showSnackBarState = MutableStateFlow(false)
    val showSnackBarState: Flow<Boolean> = _showSnackBarState

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
                receiveChannel = this.incoming
                socketState.set(WebSocketState.CONNECTED)
                isCancel = false

                val message = upbitOrderBookWebSocketMessage(marketCodes)
                session!!.send(Frame.Text(message))

                for (frame in receiveChannel!!) {
                    when (frame) {
                        is Frame.Text -> {

                        }

                        is Frame.Binary -> {
                            val receivedMessage =
                                Utils.json.decodeFromString<UpbitSocketOrderBookRes>(frame.data.decodeToString())
//                            Logger.e(receivedMessage.code)
                            _tickerFlow.emit(receivedMessage)
                        }

                        else -> Unit
                    }
                }
            }
        } catch (e: Exception) {
            println("${isCancel} WebSocket catch: ${e.localizedMessage}")
            disConnectionSocket()
            //소켓 연결부터 다시
            if (!isCancel && !isBackGround) {
                retry(marketCodes)
            }
        }
    }

    private fun disConnectionSocket() {
        socketState.set(WebSocketState.DISCONNECTED)
        session?.cancel()
        session = null
    }

    suspend fun receiveMessage(marketCodes: String) {
        if (receiveChannel != null) {
            for (frame in receiveChannel!!) {
                when (frame) {
                    is Frame.Text -> {

                    }

                    is Frame.Binary -> {
                        val receivedMessage =
                            Utils.json.decodeFromString<UpbitSocketOrderBookRes>(frame.data.decodeToString())
                        _tickerFlow.emit(receivedMessage)
                    }

                    else -> Unit
                }
            }
        } else {
            disConnectionSocket()
            // 소켓 연결부터 다시
            if (!isCancel && !isBackGround) {
                retry(marketCodes)
            }
        }
    }

    suspend fun sendMessage(marketCodes: String) {
        val message = upbitOrderBookWebSocketMessage(marketCodes)
        try {
            if (session != null && socketState.get() == WebSocketState.CONNECTED) {
                println("메시지 전송 성공: $marketCodes")
                session!!.send(Frame.Text(message))
            } else {
                println("WebSocket이 연결되지 않았습니다.")
                disConnectionSocket()
                if (!isCancel && !isBackGround) {
                    retry(marketCodes)
                }
            }
        } catch (e: Exception) {
            // 소켓 연결부터 다시
            println("send message 오류")
            disConnectionSocket()
            if (!isCancel && !isBackGround) {
                retry(marketCodes)
            }
        }
    }

    private suspend fun retry(marketCodes: String) {
        while (retryCount.get() <= 5) {
            if (isCancel || isBackGround) {
                return
            }

            try {
                client.webSocket(
                    method = HttpMethod.Get,
                    host = "api.upbit.com",
                    path = "/websocket/v1"
                ) {
                    println("WebSocket 연결 성공!")
                    session = this
                    receiveChannel = this.incoming
                    socketState.set(WebSocketState.CONNECTED)

                    val message = upbitTickerWebSocketMessage(marketCodes)
                    session!!.send(Frame.Text(message))

                    retryCount.set(0)

                    for (frame in receiveChannel!!) {

                        if (isCancel || isBackGround) {
                            return@webSocket
                        }

                        when (frame) {
                            is Frame.Text -> {

                            }

                            is Frame.Binary -> {
                                val receivedMessage =
                                    Utils.json.decodeFromString<UpbitSocketOrderBookRes>(frame.data.decodeToString())
                                _tickerFlow.emit(receivedMessage)
                            }

                            else -> Unit
                        }
                    }
                    return@webSocket
                }
            } catch (e: Exception) {
                if (isCancel || isBackGround) {
                    return
                }

                disConnectionSocket()
                delay(3000L)

                if (retryCount.getAndIncrement() == 5) {
                    retryCount.set(0)
                    _showSnackBarState.value = true
                    return
                }
            }
        }
    }

    suspend fun onStop() {
        isCancel = true
        disConnectionSocket()
    }

    fun updateIsBackground(value: Boolean) {
        isBackGround = value
    }

    fun getIsSocketConnected(): Boolean {
        return session != null && socketState.get() == WebSocketState.CONNECTED
    }
}