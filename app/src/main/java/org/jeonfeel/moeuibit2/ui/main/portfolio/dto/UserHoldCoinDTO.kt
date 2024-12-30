package org.jeonfeel.moeuibit2.ui.main.portfolio.dto

import androidx.annotation.Keep
import org.jeonfeel.moeuibit2.data.network.retrofit.response.upbit.Caution

@Keep
data class UserHoldCoinDTO(
    var myCoinKoreanName: String,
    var myCoinEngName: String,
    var myCoinsSymbol: String,
    var myCoinsQuantity: Double,
    var myCoinsBuyingAverage: Double = 0.0,
    var currentPrice: Double = myCoinsBuyingAverage,
    var openingPrice: Double,
    var warning: Boolean,
    var caution: Caution?,
    var isFavorite: Int? = null,
    var market: String,
    var purchaseAverageBtcPrice: Double = 0.0,
    var initialConstant: String = ""
)