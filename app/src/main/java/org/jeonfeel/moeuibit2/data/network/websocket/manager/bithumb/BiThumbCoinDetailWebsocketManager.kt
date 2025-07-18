package org.jeonfeel.moeuibit2.data.network.websocket.manager.bithumb

import com.orhanobut.logger.Logger
import io.ktor.client.HttpClient
import io.ktor.client.plugins.websocket.WebSockets
import io.ktor.client.plugins.websocket.webSocket
import io.ktor.websocket.Frame
import io.ktor.websocket.WebSocketSession
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import org.jeonfeel.moeuibit2.constants.upbitTickerWebSocketMessage
import org.jeonfeel.moeuibit2.data.network.websocket.manager.upbit.WebSocketState
import org.jeonfeel.moeuibit2.data.network.websocket.model.bitthumb.BithumbSocketTickerRes
import org.jeonfeel.moeuibit2.utils.Utils
import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.atomic.AtomicReference

class BiThumbCoinDetailWebsocketManager {
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

    private val _tickerFlow = MutableStateFlow<BithumbSocketTickerRes?>(null)
    val tickerFlow: Flow<BithumbSocketTickerRes?> = _tickerFlow

    private val _showSnackBarState = MutableStateFlow(false)
    val showSnackBarState: Flow<Boolean> = _showSnackBarState

    // WebSocket 메시지 Flow 생성
    suspend fun connectWebSocketFlow(marketCodes: String) {
        if (socketState.get() == WebSocketState.CONNECTED || socketState.get() == WebSocketState.CONNECTING) return

        socketState.set(WebSocketState.CONNECTING)
        try {
            client.webSocket(
                urlString = "wss://ws-api.bithumb.com/websocket/v1"
            ) {
                session = this
                receiveChannel = this.incoming
                socketState.set(WebSocketState.CONNECTED)
                isCancel = false

                val message = upbitTickerWebSocketMessage(marketCodes)
                session!!.send(Frame.Text(message))

                for (frame in receiveChannel!!) {
                    when (frame) {
                        is Frame.Text -> {
                        }

                        is Frame.Binary -> {
                            val receivedMessage =
                                Utils.json.decodeFromString<BithumbSocketTickerRes>(frame.data.decodeToString())
                            _tickerFlow.emit(receivedMessage)
                        }

                        else -> Unit
                    }
                }
            }
        } catch (e: Exception) {
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
                            Utils.json.decodeFromString<BithumbSocketTickerRes>(frame.data.decodeToString())
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
        val message = upbitTickerWebSocketMessage(marketCodes)
        try {
            if (session != null && socketState.get() == WebSocketState.CONNECTED) {
                session!!.send(Frame.Text(message))
            } else {
                disConnectionSocket()
                if (!isCancel && !isBackGround) {
                    retry(marketCodes)
                }
            }
        } catch (e: Exception) {
            // 소켓 연결부터 다시
//            println("send message 오류")
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
                    urlString = "wss://ws-api.bithumb.com/websocket/v1"
                ) {
//                    println("WebSocket 연결 성공!")
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
                                    Utils.json.decodeFromString<BithumbSocketTickerRes>(frame.data.decodeToString())
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
//                println("catch")

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
        isCancel = true // 이걸로 정상종료 FLAG 해도될듯???
        disConnectionSocket()
    }

    fun updateIsBackground(value: Boolean) {
        isBackGround = value
    }

    fun getIsSocketConnected(): Boolean {
        return session != null && socketState.get() == WebSocketState.CONNECTED
    }
}