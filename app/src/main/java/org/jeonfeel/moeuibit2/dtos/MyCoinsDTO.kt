package org.jeonfeel.moeuibit2.dtos

//class MyCoinsDTO(
//    var myCoinsKoreanName: String,
//    var myCoinsSymbol: String,
//    var myCoinsQuantity: Double,
//    var myCoinsBuyingAverage: Double,
//    private var currentPrice: Double
//) : Comparable<MyCoinsDTO> {
//    var yield: Double? = null
//        private set
//
//    fun getCurrentPrice(): Double {
//        return currentPrice
//    }
//
//    fun setCurrentPrice(currentPrice: Double) {
//        this.currentPrice = currentPrice
//        yield = (currentPrice - myCoinsBuyingAverage) / myCoinsBuyingAverage * 100
//    }
//}

data class MyCoinDTO(
    var myCoinsKoreanName: String,
    var myCoinsSymbol: String,
    var myCoinsQuantity: Double,
    var myCoinsBuyingAverage: Double,
        )