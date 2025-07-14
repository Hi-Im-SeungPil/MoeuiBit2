package org.jeonfeel.moeuibit2.data.network.retrofit.model.bitthumb

import androidx.annotation.Keep
import org.jeonfeel.moeuibit2.data.network.retrofit.response.bitthumb.BitThumbMarketCodeRes

@Keep
data class BitThumbMarketCodeGroupedRes(
    val krwList: List<BitThumbMarketCodeRes>,
    val btcList: List<BitThumbMarketCodeRes>,
    val krwMarketCodeMap: Map<String, BitThumbMarketCodeRes>,
    val btcMarketCodeMap: Map<String, BitThumbMarketCodeRes>,
)
