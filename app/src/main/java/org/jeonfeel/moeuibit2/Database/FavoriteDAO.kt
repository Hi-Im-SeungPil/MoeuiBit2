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
//interface FavoriteDAO {
//    @get:Query("SELECT * FROM Favorite")
//    val all: List<Favorite?>?
//
//    @Query("INSERT INTO Favorite values(:market)")
//    fun insert(market: String?)
//
//    @Query("DELETE FROM Favorite WHERE market = :market ")
//    fun delete(market: String?)
//
//    @Query("SELECT * FROM Favorite WHERE market = :market")
//    fun select(market: String?): Favorite?
//
//    @Query("DELETE FROM Favorite ")
//    fun deleteAll()
//}