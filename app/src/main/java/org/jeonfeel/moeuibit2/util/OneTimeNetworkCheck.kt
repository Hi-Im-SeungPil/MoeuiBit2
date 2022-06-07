package org.jeonfeel.moeuibit2.util

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network

object OneTimeNetworkCheck {

    fun networkCheck(context: Context): Network? {
        val cm = context.getSystemService(ConnectivityManager::class.java)
        return cm.activeNetwork
    }

}