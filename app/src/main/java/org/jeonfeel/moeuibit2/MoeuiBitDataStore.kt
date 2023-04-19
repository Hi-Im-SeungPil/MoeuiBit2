package org.jeonfeel.moeuibit2

import org.jeonfeel.moeuibit2.data.remote.retrofit.model.CommonExchangeModel

object MoeuiBitDataStore {
    var isKor = true
    var usdPrice = 1250.0
    val coinName = HashMap<String,Pair<String,String>>()
    val favoriteHashMap = HashMap<String,Int>()
    var krwMarkets = HashMap<String,Int>()
    var btcMarkets = HashMap<String,Int>()
}