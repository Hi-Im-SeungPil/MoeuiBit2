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
//@Dao
//interface TransactionInfoDAO {
//    @Query("INSERT INTO TransactionInfo values(null,:market,:price,:quantity,:transactionAmount,:transactionStatus,:transactionTime)")
//    fun insert(
//        market: String?,
//        price: Double?,
//        quantity: Double?,
//        transactionAmount: Long,
//        transactionStatus: String?,
//        transactionTime: Long
//    )
//
//    @Query("SELECT * FROM TransactionInfo where market=:market")
//    fun select(market: String?): List<TransactionInfo?>?
//
//    @Query("DELETE FROM TransactionInfo ")
//    fun deleteAll()
//}