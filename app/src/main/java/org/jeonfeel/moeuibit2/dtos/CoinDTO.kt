//package org.jeonfeel.moeuibit2.DTOS
//
//import org.jeonfeel.moeuibit2.DTOS.CoinDTO
//import org.jeonfeel.moeuibit2.DTOS.MyCoinsDTO
//
//class CoinDTO(
//    var market: String,
//    var koreanName: String,
//    var englishName: String,
//    var currentPrice: Double,
//    var dayToDay: Double,
//    var transactionAmount: Double,
//    var symbol: String
//) : Comparable<CoinDTO> {
//    var id = 0
//    override fun compareTo(c: CoinDTO): Int {
//        when (orderStatus) {
//            "currentPrice" -> return currentPrice.compareTo(c.currentPrice)
//            "dayToDay" -> return dayToDay.compareTo(c.dayToDay)
//            "transactionAmount" -> return transactionAmount.compareTo(c.transactionAmount)
//        }
//        return -1
//    }
//
//    companion object {
//        @JvmField
//        var orderStatus: String? = null
//    }
//}