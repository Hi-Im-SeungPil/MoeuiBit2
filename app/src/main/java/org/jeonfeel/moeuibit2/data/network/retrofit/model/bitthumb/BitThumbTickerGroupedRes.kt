package org.jeonfeel.moeuibit2.data.network.retrofit.model.bitthumb

import org.jeonfeel.moeuibit2.data.network.retrofit.model.upbit.CommonExchangeModel

data class BitThumbTickerGroupedRes(
    val krwCommonExchangeModelList: List<CommonExchangeModel>,
    val btcCommonExchangeModelList: List<CommonExchangeModel>,
    val krwModelPosition: Map<String, Int>,
    val btcModelPosition: Map<String, Int>,
)
