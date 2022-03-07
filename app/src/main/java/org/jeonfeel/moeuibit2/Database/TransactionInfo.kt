//package org.jeonfeel.moeuibit2.Database
//
//import androidx.room.*
//import org.jeonfeel.moeuibit2.Database.MyCoin
//import org.jeonfeel.moeuibit2.Database.Favorite
//import org.jeonfeel.moeuibit2.Database.NotSigned
//import org.jeonfeel.moeuibit2.Database.TransactionInfo
//import org.jeonfeel.moeuibit2.Database.UserDAO
//import org.jeonfeel.moeuibit2.Database.MyCoinDAO
//import org.jeonfeel.moeuibit2.Database.FavoriteDAO
//import org.jeonfeel.moeuibit2.Database.TransactionInfoDAO
//import org.jeonfeel.moeuibit2.Database.MoEuiBitDatabase
//import kotlin.jvm.Synchronized
//
//@Entity
//class TransactionInfo(
//    var market: String,
//    var price: Double,
//    var quantity: Double,
//    var transactionAmount: Long,
//    var transactionStatus: String,
//    var transactionTime: Long
//) {
//    @JvmField
//    @PrimaryKey(autoGenerate = true)
//    var id: Long = 0
//
//}