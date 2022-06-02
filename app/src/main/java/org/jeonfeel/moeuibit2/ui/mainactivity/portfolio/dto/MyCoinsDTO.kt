package org.jeonfeel.moeuibit2.ui.mainactivity.portfolio.dto

data class UserHoldCoinDTO(
    var myCoinsKoreanName: String,
    var myCoinsSymbol: String,
    var myCoinsQuantity: Double,
    var myCoinsBuyingAverage: Double = 0.0,
    var currentPrice: Double = myCoinsBuyingAverage
)