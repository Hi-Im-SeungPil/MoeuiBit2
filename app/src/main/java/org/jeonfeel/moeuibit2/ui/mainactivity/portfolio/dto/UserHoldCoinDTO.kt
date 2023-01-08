package org.jeonfeel.moeuibit2.ui.mainactivity.portfolio.dto

import androidx.annotation.Keep

@Keep
data class UserHoldCoinDTO(
    var myCoinsKoreanName: String,
    var myCoinsEngName:String,
    var myCoinsSymbol: String,
    var myCoinsQuantity: Double,
    var myCoinsBuyingAverage: Double = 0.0,
    var currentPrice: Double = myCoinsBuyingAverage,
    var openingPrice: Double,
    var warning: String,
    var isFavorite: Int? = null,
    var market: String,
    var purchaseAverageBtcPrice: Double = 0.0
)