package org.jeonfeel.moeuibit2.view.activity.main

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import org.jeonfeel.moeuibit2.util.NetworkMonitorUtil

class MainLifecycleObserver(private val networkMonitorUtil: NetworkMonitorUtil) :
    LifecycleEventObserver {

    override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
//        when (event) {
//            Lifecycle.Event.ON_RESUME -> {
//                networkMonitorUtil.register()
//                UpBitWebSocket.onResume()
//            }
//            Lifecycle.Event.ON_STOP -> {
//                networkMonitorUtil.unregister()
//            }
//            Lifecycle.Event.ON_PAUSE -> {
//                UpBitWebSocket.onPause()
//            }
//            else -> {}
//        }
    }
}
