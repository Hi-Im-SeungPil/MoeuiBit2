package org.jeonfeel.moeuibit2.utils

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch

interface ConnectivityObserver {

    fun getFlow(): Flow<Status>

    enum class Status {
        Available, Unavailable, Losing, Lost
    }
}

class NetworkConnectivityObserver(
    context: Context,
) : ConnectivityObserver {

    companion object {
        var isNetworkAvailable = false
    }

    private val connectivityManager =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    override fun getFlow(): Flow<ConnectivityObserver.Status> {
        return callbackFlow {
            val callback = object : ConnectivityManager.NetworkCallback() {
                override fun onAvailable(network: Network) {
                    super.onAvailable(network)
                    launch {
                        isNetworkAvailable = true
                        send(ConnectivityObserver.Status.Available)
                    }
                }

                override fun onLosing(network: Network, maxMsToLive: Int) {
                    super.onLosing(network, maxMsToLive)
                    launch {
                        isNetworkAvailable = false
                        send(ConnectivityObserver.Status.Losing)
                    }
                }

                override fun onLost(network: Network) {
                    super.onLost(network)
                    launch {
                        isNetworkAvailable = false
                        send(ConnectivityObserver.Status.Lost)
                    }
                }

                override fun onUnavailable() {
                    super.onUnavailable()
                    launch {
                        isNetworkAvailable = false
                        send(ConnectivityObserver.Status.Unavailable)
                    }
                }
            }

            connectivityManager.registerDefaultNetworkCallback(callback)

            awaitClose {
                connectivityManager.unregisterNetworkCallback(callback)
            }

        }.distinctUntilChanged()
    }
}