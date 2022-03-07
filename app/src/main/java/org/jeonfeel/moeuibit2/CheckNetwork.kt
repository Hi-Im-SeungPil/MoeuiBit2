package org.jeonfeel.moeuibit2

import android.content.Context
import android.net.ConnectivityManager

//class CheckNetwork() {
//    companion object {
//        var TYPE_NOT_CONNECTED = 0
//        var TYPE_WIFI = 1
//        var TYPE_MOBILE = 2
//
//        fun checkNetWork(context: Context): Int {
//            val manager =
//                context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
//            val networkInfo = manager.activeNetworkInfo
//            if (networkInfo != null) {
//                val type = networkInfo.type
//                if (type == ConnectivityManager.TYPE_MOBILE) { //모바일
//                    return TYPE_MOBILE
//                } else if (type == ConnectivityManager.TYPE_WIFI) { //와이파이 연결된것
//                    return TYPE_WIFI
//                }
//            }
//            return TYPE_NOT_CONNECTED //연결이 되지않은 상태
//        }
//    }
//}