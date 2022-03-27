package org.jeonfeel.moeuibit2.util

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities

enum class ConnectionType {
    Wifi, Cellular
}

const val INTERNET_CONNECTION = 0
const val NO_INTERNET_CONNECTION = -1
const val NETWORK_ERROR = -2

class NetworkMonitorUtil(context: Context) {
    private var mContext = context
    private lateinit var networkCallback: ConnectivityManager.NetworkCallback
    lateinit var result: ((isAvailable: Boolean, type: ConnectionType?) -> Unit)

    companion object {
        var currentNetworkState = INTERNET_CONNECTION
    }

    fun register() {
        val connectivityManager =
            mContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (connectivityManager.activeNetwork == null) {
            result(false, null)
        }
        networkCallback = object : ConnectivityManager.NetworkCallback() {
            override fun onLost(network: Network) {
                super.onLost(network)
                result(false, null)
            }

            override fun onCapabilitiesChanged(
                network: Network,
                networkCapabilities: NetworkCapabilities,
            ) {
                super.onCapabilitiesChanged(network, networkCapabilities)
                when {
                    networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> {
                        result(true, ConnectionType.Wifi)
                    }
                    else -> {
                        result(true, ConnectionType.Cellular)
                    }
                }
            }
        }
        connectivityManager.registerDefaultNetworkCallback(networkCallback)
    }

    fun unregister() {
        val connectivityManager =
            mContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        connectivityManager.unregisterNetworkCallback(networkCallback)
    }

    fun internetConnection(markets: String) {
        if (currentNetworkState != INTERNET_CONNECTION) {
            currentNetworkState = INTERNET_CONNECTION
        }
//        if(UpBitWebSocket.currentSocketState == SOCKET_IS_NO_CONNECTION) {
//            UpBitWebSocket.requestKrwCoinList(markets)
//        }
    }

    fun noInternetConnection() {
        if (currentNetworkState != NO_INTERNET_CONNECTION) {
            currentNetworkState = NO_INTERNET_CONNECTION
        }
    }
}