package org.jeonfeel.moeuibit2.data.network.retrofit.model.bitthumb

import androidx.annotation.Keep
import org.jeonfeel.moeuibit2.data.network.retrofit.model.upbit.CommonExchangeModel

@Keep
data class BitThumbTickerGroupedRes(
    val krwCommonExchangeModelList: List<CommonExchangeModel>,
    val btcCommonExchangeModelList: List<CommonExchangeModel>,
    val krwModelPosition: Map<String, Int>,
    val btcModelPosition: Map<String, Int>,
)
