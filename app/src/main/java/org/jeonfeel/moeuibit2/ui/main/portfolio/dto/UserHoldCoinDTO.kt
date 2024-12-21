package org.jeonfeel.moeuibit2.ui.main.portfolio.dto

import androidx.annotation.Keep

@Keep
data class UserHoldCoinDTO(
    var myCoinKoreanName: String,
    var myCoinEngName: String,
    var myCoinsSymbol: String,
    var myCoinsQuantity: Double,
    var myCoinsBuyingAverage: Double = 0.0,
    var currentPrice: Double = myCoinsBuyingAverage,
    var openingPrice: Double,
    var warning: String,
    var isFavorite: Int? = null,
    var market: String,
    var purchaseAverageBtcPrice: Double = 0.0,
    var initialConstant: String = ""
)