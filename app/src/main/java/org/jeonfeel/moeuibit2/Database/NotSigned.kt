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
//class NotSigned {
//    @PrimaryKey
//    var market: String = null
//
//    @ColumnInfo
//    var koreanCoinName: String? = null
//
//    @ColumnInfo
//    var symbol: String? = null
//
//    @ColumnInfo
//    var notSignedPrice: Double? = null
//
//    @ColumnInfo
//    var quantity: Double? = null
//}