package org.jeonfeel.moeuibit2.utils

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.OnLifecycleEvent
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch

interface ConnectivityObserver {

    fun startObserving()
    fun stopObserving()
}

class NetworkConnectivityObserver(
    context: Context,
) : ConnectivityObserver {

    companion object {
        private val _isNetworkAvailable = mutableStateOf(false)
        val isNetworkAvailable: State<Boolean> get() = _isNetworkAvailable
    }

    private val connectivityManager =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    private var networkCallback: ConnectivityManager.NetworkCallback? = null

    private var isObserving = false

    // 네트워크 상태 감지 시작
    override fun startObserving() {
        if (!isObserving) {
            isObserving = true
            networkCallback = object : ConnectivityManager.NetworkCallback() {
                override fun onAvailable(network: Network) {
                    super.onAvailable(network)
                    _isNetworkAvailable.value = true
                }

                override fun onLosing(network: Network, maxMsToLive: Int) {
                    super.onLosing(network, maxMsToLive)
                    _isNetworkAvailable.value = false
                }

                override fun onLost(network: Network) {
                    super.onLost(network)
                    _isNetworkAvailable.value = false
                }

                override fun onUnavailable() {
                    super.onUnavailable()
                    _isNetworkAvailable.value = false
                }
            }

            connectivityManager.registerDefaultNetworkCallback(networkCallback!!)
        }
    }

    // 네트워크 상태 감지 중지
    override fun stopObserving() {
        if (isObserving) {
            isObserving = false  // 네트워크 감지가 중지되었음을 표시

            networkCallback?.let { callback ->
                connectivityManager.unregisterNetworkCallback(callback)  // 콜백 해제
            }
            networkCallback = null  // 콜백 인스턴스를 null로 초기화
        }
    }
}

class NetworkObserverLifecycle(
    private val networkConnectivityObserver: NetworkConnectivityObserver,
) : DefaultLifecycleObserver {

    override fun onStart(owner: LifecycleOwner) {
        networkConnectivityObserver.startObserving()  // 네트워크 감지 시작
    }

    override fun onStop(owner: LifecycleOwner) {
        networkConnectivityObserver.stopObserving()  // 네트워크 감지 중지
    }
}
