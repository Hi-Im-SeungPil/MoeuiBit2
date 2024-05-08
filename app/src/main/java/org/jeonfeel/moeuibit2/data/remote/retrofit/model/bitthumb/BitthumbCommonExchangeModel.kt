package org.jeonfeel.moeuibit2.data.remote.retrofit.model.bitthumb

import com.google.firebase.database.Exclude
import com.google.gson.annotations.SerializedName

data class BitthumbCommonExchangeModel(
    var koreanName: String,
    var englishName: String,
    var market: String,
    var symbol: String,
    val opening_price: Double,
    @SerializedName("closing_price")
    var tradePrice: Double,
    @SerializedName("fluctate_rate_24H")
    var signedChangeRate: Double,
    @SerializedName("acc_trade_value")
    var accTradePrice24h: Double,
    var warning: String
)
