//package org.jeonfeel.moeuibit2.DTOS

//import org.jeonfeel.moeuibit2.DTOS.CoinDTO
//import org.jeonfeel.moeuibit2.DTOS.MyCoinsDTO

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
//
//    override fun compareTo(o: MyCoinsDTO): Int {
//        when (orderStatus) {
//            "name" -> return myCoinsKoreanName.compareTo(o.myCoinsKoreanName)
//            "yield" -> return yield!!.compareTo(o.yield!!)
//        }
//        return -1
//    }
//
//    companion object {
//        @JvmField
//        var orderStatus: String? = null
//    }
//}